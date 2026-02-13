# Prompt: Implementación del Flujo de Cambio de Contraseña Obligatorio en Primera Sesión

## Contexto del Problema

El backend ahora implementa un sistema de seguridad que obliga a los usuarios a cambiar su contraseña en su primer inicio de sesión. Cuando un usuario es creado (ya sea por registro o por un administrador), tiene el flag `passwordChangeRequired: true`. Mientras este flag esté activo, el usuario NO puede acceder a ningún endpoint del sistema excepto el de cambio de contraseña.

## Comportamiento del Backend

### 1. Login Response
Cuando el usuario hace login exitoso, la respuesta incluye el campo `passwordChangeRequired`:

```typescript
// POST /api/auth/login
// Request
{
  "correoElectronico": "usuario@email.com",
  "password": "password123"
}

// Response 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "correoElectronico": "usuario@email.com",
  "nombre": "Juan",
  "apellidos": "García",
  "isAdmin": false,
  "passwordChangeRequired": true  // <-- CAMPO CRÍTICO
}
```

### 2. Bloqueo de Endpoints (403 Forbidden)
Si el usuario tiene `passwordChangeRequired: true` en su JWT e intenta acceder a CUALQUIER endpoint (excepto los de auth), recibirá:

```typescript
// Cualquier petición a /api/* (excepto /api/auth/*)
// Response 403 Forbidden
{
  "error": "PASSWORD_CHANGE_REQUIRED",
  "message": "Debe cambiar su contraseña antes de continuar",
  "redirectTo": "/change-password"
}
```

### 3. Endpoint de Cambio de Contraseña
```typescript
// POST /api/auth/change-password
// Headers
{
  "Authorization": "Bearer <token_actual>",
  "Content-Type": "application/json"
}

// Request Body
{
  "newPassword": "nuevaContraseña123"  // Mínimo 6 caracteres
}

// Response 200 OK - Devuelve nuevo token con pcr=false
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", // NUEVO TOKEN
  "userId": 1,
  "correoElectronico": "usuario@email.com",
  "nombre": "Juan",
  "apellidos": "García",
  "isAdmin": false,
  "passwordChangeRequired": false  // Ahora es false
}

// Response 400 Bad Request - Validación fallida
{
  "newPassword": "La contraseña debe tener al menos 6 caracteres"
}
```

## Implementación Requerida en Frontend

### 1. Actualizar el Modelo de AuthResponse

```typescript
// models/auth.model.ts
export interface LoginResponse {
  token: string;
  userId: number;
  correoElectronico: string;
  nombre: string;
  apellidos: string;
  isAdmin: boolean;
  passwordChangeRequired: boolean;  // Añadir este campo
}

export interface ChangePasswordRequest {
  newPassword: string;
}
```

### 2. Actualizar el AuthService

```typescript
// services/auth.service.ts
@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  // Estado reactivo para passwordChangeRequired
  private passwordChangeRequired$ = new BehaviorSubject<boolean>(false);

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          localStorage.setItem('token', response.token);
          localStorage.setItem('user', JSON.stringify(response));
          this.passwordChangeRequired$.next(response.passwordChangeRequired);
        })
      );
  }

  changePassword(newPassword: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.apiUrl}/change-password`,
      { newPassword }
    ).pipe(
      tap(response => {
        // CRÍTICO: Reemplazar el token antiguo con el nuevo
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response));
        this.passwordChangeRequired$.next(false);
      })
    );
  }

  isPasswordChangeRequired(): boolean {
    const user = this.getCurrentUser();
    return user?.passwordChangeRequired ?? false;
  }

  getPasswordChangeRequired$(): Observable<boolean> {
    return this.passwordChangeRequired$.asObservable();
  }

  getCurrentUser(): LoginResponse | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }
}
```

### 3. Crear el Guard de Cambio de Contraseña

```typescript
// guards/password-change.guard.ts
@Injectable({ providedIn: 'root' })
export class PasswordChangeGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    // Si el usuario debe cambiar contraseña, redirigir
    if (this.authService.isPasswordChangeRequired()) {
      this.router.navigate(['/change-password']);
      return false;
    }
    return true;
  }
}

// guards/password-change-page.guard.ts
// Guard INVERSO para la página de cambio de contraseña
@Injectable({ providedIn: 'root' })
export class PasswordChangePageGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    // Solo permitir acceso si DEBE cambiar contraseña
    if (!this.authService.isPasswordChangeRequired()) {
      this.router.navigate(['/dashboard']);
      return false;
    }
    return true;
  }
}
```

### 4. Actualizar el Interceptor HTTP

```typescript
// interceptors/auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');

    let authReq = req;
    if (token) {
      authReq = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        // Manejar el 403 de PASSWORD_CHANGE_REQUIRED
        if (error.status === 403 && error.error?.error === 'PASSWORD_CHANGE_REQUIRED') {
          // Actualizar el estado local
          const user = this.authService.getCurrentUser();
          if (user) {
            user.passwordChangeRequired = true;
            localStorage.setItem('user', JSON.stringify(user));
          }
          // Redirigir a cambio de contraseña
          this.router.navigate(['/change-password']);
          return throwError(() => error);
        }

        // Manejar 401 (token expirado/inválido)
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }

        return throwError(() => error);
      })
    );
  }
}
```

### 5. Crear el Componente de Cambio de Contraseña

```typescript
// components/change-password/change-password.component.ts
@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent {
  changePasswordForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.changePasswordForm = this.fb.group({
      newPassword: ['', [
        Validators.required,
        Validators.minLength(6)
      ]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  // Validador personalizado para confirmar contraseña
  passwordMatchValidator(form: FormGroup) {
    const password = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    if (password !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.changePasswordForm.invalid) {
      this.changePasswordForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const newPassword = this.changePasswordForm.get('newPassword')?.value;

    this.authService.changePassword(newPassword).subscribe({
      next: (response) => {
        this.isLoading = false;
        // Redirigir al dashboard o página principal
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isLoading = false;
        if (error.error?.newPassword) {
          this.errorMessage = error.error.newPassword;
        } else {
          this.errorMessage = 'Error al cambiar la contraseña. Inténtelo de nuevo.';
        }
      }
    });
  }

  // Helpers para el template
  get newPasswordControl() { return this.changePasswordForm.get('newPassword'); }
  get confirmPasswordControl() { return this.changePasswordForm.get('confirmPassword'); }
}
```

```html
<!-- components/change-password/change-password.component.html -->
<div class="change-password-container">
  <div class="change-password-card">
    <div class="card-header">
      <div class="icon-container">
        <i class="fas fa-key"></i>
      </div>
      <h2>Cambio de Contraseña Requerido</h2>
      <p class="subtitle">
        Por seguridad, debe establecer una nueva contraseña antes de continuar.
        Esta acción solo se requiere en su primer inicio de sesión.
      </p>
    </div>

    <form [formGroup]="changePasswordForm" (ngSubmit)="onSubmit()">
      <!-- Nueva Contraseña -->
      <div class="form-group">
        <label for="newPassword">Nueva Contraseña</label>
        <div class="input-wrapper">
          <input
            [type]="showPassword ? 'text' : 'password'"
            id="newPassword"
            formControlName="newPassword"
            placeholder="Mínimo 6 caracteres"
            [class.error]="newPasswordControl?.invalid && newPasswordControl?.touched"
          />
          <button
            type="button"
            class="toggle-password"
            (click)="showPassword = !showPassword"
          >
            <i [class]="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
          </button>
        </div>
        <div class="error-message" *ngIf="newPasswordControl?.touched && newPasswordControl?.errors">
          <span *ngIf="newPasswordControl?.errors?.['required']">
            La contraseña es obligatoria
          </span>
          <span *ngIf="newPasswordControl?.errors?.['minlength']">
            La contraseña debe tener al menos 6 caracteres
          </span>
        </div>
      </div>

      <!-- Confirmar Contraseña -->
      <div class="form-group">
        <label for="confirmPassword">Confirmar Contraseña</label>
        <div class="input-wrapper">
          <input
            [type]="showConfirmPassword ? 'text' : 'password'"
            id="confirmPassword"
            formControlName="confirmPassword"
            placeholder="Repita la contraseña"
            [class.error]="confirmPasswordControl?.invalid && confirmPasswordControl?.touched"
          />
          <button
            type="button"
            class="toggle-password"
            (click)="showConfirmPassword = !showConfirmPassword"
          >
            <i [class]="showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
          </button>
        </div>
        <div class="error-message" *ngIf="confirmPasswordControl?.touched && confirmPasswordControl?.errors">
          <span *ngIf="confirmPasswordControl?.errors?.['required']">
            Debe confirmar la contraseña
          </span>
          <span *ngIf="confirmPasswordControl?.errors?.['passwordMismatch']">
            Las contraseñas no coinciden
          </span>
        </div>
      </div>

      <!-- Mensaje de error general -->
      <div class="alert alert-error" *ngIf="errorMessage">
        <i class="fas fa-exclamation-circle"></i>
        {{ errorMessage }}
      </div>

      <!-- Botón Submit -->
      <button
        type="submit"
        class="btn-submit"
        [disabled]="isLoading || changePasswordForm.invalid"
      >
        <span *ngIf="!isLoading">Cambiar Contraseña</span>
        <span *ngIf="isLoading">
          <i class="fas fa-spinner fa-spin"></i> Procesando...
        </span>
      </button>
    </form>

    <!-- Requisitos de contraseña -->
    <div class="password-requirements">
      <h4>Requisitos de la contraseña:</h4>
      <ul>
        <li [class.valid]="newPasswordControl?.value?.length >= 6">
          <i [class]="newPasswordControl?.value?.length >= 6 ? 'fas fa-check' : 'fas fa-times'"></i>
          Mínimo 6 caracteres
        </li>
      </ul>
    </div>
  </div>
</div>
```

### 6. Configurar las Rutas

```typescript
// app-routing.module.ts
const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [NoAuthGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [NoAuthGuard] },

  // Ruta de cambio de contraseña - Protegida por guard especial
  {
    path: 'change-password',
    component: ChangePasswordComponent,
    canActivate: [AuthGuard, PasswordChangePageGuard]  // Debe estar autenticado Y tener pcr=true
  },

  // Rutas protegidas - Requieren autenticación Y no tener pcr=true
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard, PasswordChangeGuard]
  },
  {
    path: 'simulacros',
    component: SimulacrosComponent,
    canActivate: [AuthGuard, PasswordChangeGuard]
  },
  // ... resto de rutas protegidas con ambos guards

  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
```

### 7. Modificar el Flujo de Login

```typescript
// components/login/login.component.ts
onSubmit(): void {
  if (this.loginForm.invalid) return;

  this.isLoading = true;

  this.authService.login(this.loginForm.value).subscribe({
    next: (response) => {
      this.isLoading = false;

      // CRÍTICO: Verificar si debe cambiar contraseña
      if (response.passwordChangeRequired) {
        this.router.navigate(['/change-password']);
      } else {
        this.router.navigate(['/dashboard']);
      }
    },
    error: (error) => {
      this.isLoading = false;
      this.errorMessage = 'Credenciales inválidas';
    }
  });
}
```

## Diagrama de Flujo

```
┌─────────────────────────────────────────────────────────────────────┐
│                         FLUJO COMPLETO                              │
└─────────────────────────────────────────────────────────────────────┘

Usuario nuevo/creado por admin
         │
         ▼
    ┌─────────┐
    │  LOGIN  │
    └────┬────┘
         │
         ▼
   ┌───────────────┐
   │ Backend envía │
   │ response con  │
   │ pcr = true    │
   └───────┬───────┘
           │
           ▼
   ┌───────────────────┐     NO      ┌─────────────┐
   │ passwordChange    ├────────────►│  Dashboard  │
   │ Required = true?  │             └─────────────┘
   └───────┬───────────┘
           │ SÍ
           ▼
   ┌───────────────────┐
   │   Redirigir a     │
   │ /change-password  │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │ Usuario ingresa   │
   │ nueva contraseña  │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │ POST /api/auth/   │
   │ change-password   │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │ Backend devuelve  │
   │ NUEVO TOKEN con   │
   │ pcr = false       │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │ Guardar nuevo     │
   │ token en storage  │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │   Redirigir a     │
   │    Dashboard      │
   └───────────────────┘


┌─────────────────────────────────────────────────────────────────────┐
│              CASO: Usuario intenta acceder sin cambiar              │
└─────────────────────────────────────────────────────────────────────┘

Usuario con pcr=true intenta acceder a /api/questions
         │
         ▼
   ┌───────────────────┐
   │ Backend responde  │
   │ 403 Forbidden     │
   │ error: "PASSWORD_ │
   │ CHANGE_REQUIRED"  │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │ Interceptor HTTP  │
   │ captura el error  │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │   Redirigir a     │
   │ /change-password  │
   └───────────────────┘
```

## Casos Edge a Considerar

1. **Token expirado durante cambio de contraseña**: Si el token expira mientras el usuario está en la página de cambio de contraseña, debe redirigir al login.

2. **Refresco de página**: Cuando el usuario refresca la página, leer el estado de `passwordChangeRequired` del localStorage y mantener la redirección.

3. **Manipulación de localStorage**: Aunque el usuario modifique el localStorage para poner `passwordChangeRequired: false`, el backend seguirá devolviendo 403 porque el JWT contiene el valor real. El interceptor actualizará el estado y redirigirá.

4. **Navegación con botón atrás**: El guard debe prevenir que el usuario vuelva atrás desde la página de cambio de contraseña.

5. **Múltiples pestañas**: Si el usuario cambia la contraseña en una pestaña, las otras pestañas detectarán el 403 en su próxima petición y redirigirán.

## Checklist de Implementación

- [ ] Actualizar interface `LoginResponse` con campo `passwordChangeRequired`
- [ ] Crear interface `ChangePasswordRequest`
- [ ] Añadir método `changePassword()` al AuthService
- [ ] Añadir método `isPasswordChangeRequired()` al AuthService
- [ ] Crear `PasswordChangeGuard` para rutas protegidas
- [ ] Crear `PasswordChangePageGuard` para la página de cambio
- [ ] Actualizar interceptor HTTP para manejar error 403 `PASSWORD_CHANGE_REQUIRED`
- [ ] Crear componente `ChangePasswordComponent` con formulario
- [ ] Añadir estilos al componente de cambio de contraseña
- [ ] Configurar ruta `/change-password` con guards apropiados
- [ ] Actualizar todas las rutas protegidas para incluir `PasswordChangeGuard`
- [ ] Modificar `LoginComponent` para redirigir según `passwordChangeRequired`
- [ ] Probar flujo completo con usuario nuevo
- [ ] Probar que usuario no puede navegar a otras rutas sin cambiar contraseña
- [ ] Probar que después de cambiar contraseña puede navegar normalmente
