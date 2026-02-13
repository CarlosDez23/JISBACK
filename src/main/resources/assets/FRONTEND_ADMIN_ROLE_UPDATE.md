# Prompt: Actualización del Frontend - Campo isAdmin

## Contexto

Se ha añadido el campo `isAdmin` al sistema de autenticación. Este campo indica si el usuario tiene permisos de administrador. Debes actualizar el frontend para manejar este nuevo campo.

---

## Cambios en la API

### Endpoint de Login

```
POST /api/auth/login
```

**Response actualizada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 123,
  "correoElectronico": "usuario@ejemplo.com",
  "nombre": "Juan",
  "apellidos": "García López",
  "isAdmin": true
}
```

### Endpoint de Registro

```
POST /api/auth/register
```

**Response actualizada (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 124,
  "correoElectronico": "nuevo@ejemplo.com",
  "nombre": "María",
  "apellidos": "López Pérez",
  "isAdmin": false
}
```

---

## Tareas a Implementar

### 1. Actualizar la interfaz AuthResponse

```typescript
// models/auth.models.ts

export interface AuthResponse {
  token: string;
  userId: number;
  correoElectronico: string;
  nombre: string;
  apellidos: string;
  isAdmin: boolean;  // NUEVO CAMPO
}

export interface User {
  userId: number;
  correoElectronico: string;
  nombre: string;
  apellidos: string;
  isAdmin: boolean;  // NUEVO CAMPO
}
```

### 2. Actualizar AuthService

El servicio debe guardar el campo `isAdmin` en localStorage junto con los demás datos del usuario:

```typescript
// auth.service.ts

login(correoElectronico: string, password: string): Observable<AuthResponse> {
  return this.http.post<AuthResponse>(`${this.apiUrl}/login`, {
    correoElectronico,
    password
  }).pipe(
    tap(response => {
      localStorage.setItem('jwt_token', response.token);
      // Guardar todos los datos del usuario incluyendo isAdmin
      localStorage.setItem('user', JSON.stringify({
        userId: response.userId,
        correoElectronico: response.correoElectronico,
        nombre: response.nombre,
        apellidos: response.apellidos,
        isAdmin: response.isAdmin
      }));
      this.userSubject.next(response);
    })
  );
}

// Método para verificar si es admin
isAdmin(): boolean {
  const user = localStorage.getItem('user');
  if (user) {
    return JSON.parse(user).isAdmin === true;
  }
  return false;
}
```

### 3. Crear AdminGuard

Crear un guard para proteger rutas que solo pueden acceder administradores:

```typescript
// admin.guard.ts

import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from './auth.service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn() && authService.isAdmin()) {
    return true;
  }

  // Redirigir a home o mostrar "acceso denegado"
  router.navigate(['/']);
  return false;
};
```

### 4. Proteger Rutas de Administración

```typescript
// app.routes.ts

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],  // Requiere login + admin
    children: [
      { path: 'usuarios', component: UsuariosComponent },
      { path: 'preguntas', component: PreguntasComponent },
      // ... otras rutas de admin
    ]
  },
  {
    path: 'user',
    canActivate: [authGuard],  // Solo requiere login
    children: [
      { path: 'perfil', component: PerfilComponent },
      // ... rutas de usuario normal
    ]
  }
];
```

### 5. Mostrar/Ocultar Elementos según Rol

En el navbar o cualquier componente, mostrar opciones según el rol:

```html
<!-- navbar.component.html -->

<nav *ngIf="authService.isLoggedIn()">
  <span>Hola, {{ user?.nombre }}</span>

  <!-- Opciones para todos los usuarios logueados -->
  <a routerLink="/user/perfil">Mi Perfil</a>

  <!-- Opciones solo para administradores -->
  <ng-container *ngIf="authService.isAdmin()">
    <a routerLink="/admin/usuarios">Gestión Usuarios</a>
    <a routerLink="/admin/preguntas">Gestión Preguntas</a>
  </ng-container>

  <button (click)="logout()">Cerrar Sesión</button>
</nav>
```

```typescript
// navbar.component.ts

@Component({...})
export class NavbarComponent {
  user: User | null = null;

  constructor(public authService: AuthService) {
    this.authService.user$.subscribe(user => this.user = user);
  }

  logout() {
    this.authService.logout();
  }
}
```

---

## Lógica de Negocio

| isAdmin | Acceso |
|---------|--------|
| `true`  | Puede acceder a todas las rutas, incluyendo `/admin/**` |
| `false` | Solo puede acceder a rutas de usuario normal |

---

## Checklist de Implementación

- [ ] Actualizar interfaz `AuthResponse` con campos `userId` e `isAdmin`
- [ ] Actualizar interfaz `User` con campos `userId` e `isAdmin`
- [ ] Modificar `AuthService.login()` para guardar `isAdmin` en localStorage
- [ ] Añadir método `AuthService.isAdmin()` que lee de localStorage
- [ ] Crear `AdminGuard` para proteger rutas de administración
- [ ] Aplicar `AdminGuard` a rutas `/admin/**`
- [ ] Actualizar navbar para mostrar/ocultar opciones según rol
- [ ] Probar login con usuario admin (`isAdmin: true`)
- [ ] Probar login con usuario normal (`isAdmin: false`)
- [ ] Verificar que usuario normal no puede acceder a rutas admin

---

## Testing

Para probar, asegúrate de tener usuarios con diferentes roles en la base de datos:

```sql
-- Usuario administrador
UPDATE jis_training.usuarios SET is_admin = true WHERE correo_electronico = 'admin@ejemplo.com';

-- Usuario normal
UPDATE jis_training.usuarios SET is_admin = false WHERE correo_electronico = 'usuario@ejemplo.com';
```
