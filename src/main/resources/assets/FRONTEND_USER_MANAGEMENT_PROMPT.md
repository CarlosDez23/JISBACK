# Prompt: Implementación de Gestión de Usuarios en Panel de Administración

## Contexto

Debes implementar la gestión de usuarios en el panel de administración de una aplicación Angular. La funcionalidad permite a los administradores gestionar los usuarios de cada comunidad. Mantén el estilo moderno y simple que se ha utilizado en el resto de la aplicación.

---

## Flujo de la Pantalla

```
┌─────────────────────────────────────────────────────────────────┐
│                    FLUJO DE GESTIÓN DE USUARIOS                  │
└─────────────────────────────────────────────────────────────────┘

1. DASHBOARD DE COMUNIDADES
   ├── Se muestra una lista/grid de comunidades disponibles
   ├── El administrador selecciona una comunidad
   └── Se navega a la vista de usuarios de esa comunidad

2. LISTADO DE USUARIOS (por comunidad)
   ├── Botón "Crear Usuario" en la parte superior
   ├── Tabla con columnas: Nombre, Apellidos, Email, Es Admin, Acciones
   ├── Cada fila tiene botones: Editar | Eliminar
   └── Opción de volver al dashboard de comunidades

3. MODAL CREAR/EDITAR USUARIO
   ├── Campos: Nombre, Apellidos, Correo Electrónico, Es Admin (checkbox)
   ├── NO incluir campo de contraseña (el usuario la creará después)
   ├── Botones: Guardar | Cancelar
   └── Al guardar se cierra el modal y se refresca la tabla
```

---

## APIs Disponibles

### Base URL
```
http://localhost:8080
```

**Header requerido en todas las peticiones:**
```
Authorization: Bearer <TOKEN_JWT>
```

---

### API de Comunidades

#### Listar todas las comunidades
```
GET /api/comunidades
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "nombre": "ANDALUCÍA",
    "grupoOposicion": "A4-FARMACIA"
  },
  {
    "id": 2,
    "nombre": "ARAGÓN",
    "grupoOposicion": "FARMACÉUTICOS ADMINISTRACIÓN SANITARIA"
  },
  {
    "id": 3,
    "nombre": "CASTILLA-LA MANCHA",
    "grupoOposicion": "SANITARIOS LOCALES Y CUERPO SUPERIOR"
  }
]
```

---

### API de Usuarios

#### Listar usuarios por comunidad
```
GET /api/usuarios/comunidad/{comunidadId}
```

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellidos": "García López",
    "correoElectronico": "juan@ejemplo.com",
    "isAdmin": false,
    "comunidad": {
      "id": 1,
      "nombre": "ANDALUCÍA",
      "grupoOposicion": "A4-FARMACIA"
    }
  },
  {
    "id": 2,
    "nombre": "María",
    "apellidos": "Pérez Ruiz",
    "correoElectronico": "maria@ejemplo.com",
    "isAdmin": true,
    "comunidad": {
      "id": 1,
      "nombre": "ANDALUCÍA",
      "grupoOposicion": "A4-FARMACIA"
    }
  }
]
```

#### Obtener usuario por ID
```
GET /api/usuarios/{id}
```

**Response 200 OK:**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellidos": "García López",
  "correoElectronico": "juan@ejemplo.com",
  "isAdmin": false,
  "comunidad": {
    "id": 1,
    "nombre": "ANDALUCÍA",
    "grupoOposicion": "A4-FARMACIA"
  }
}
```

**Response 404 Not Found:** Usuario no existe

#### Crear usuario (usar endpoint de registro)
```
POST /api/auth/register
Content-Type: application/json
```

**Request:**
```json
{
  "nombre": "Pedro",
  "apellidos": "Martínez Sánchez",
  "correoElectronico": "pedro@ejemplo.com",
  "password": "temporal123",
  "comunidadId": 1
}
```

**IMPORTANTE sobre la contraseña:**
- El campo `password` es obligatorio en la API pero el usuario no lo conocerá
- Genera una contraseña temporal aleatoria (ej: `temp_` + 8 caracteres aleatorios)
- El usuario deberá cambiar su contraseña posteriormente (funcionalidad futura)
- Muestra un mensaje al admin indicando que el usuario deberá establecer su contraseña

**Response 201 Created:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 5,
  "correoElectronico": "pedro@ejemplo.com",
  "nombre": "Pedro",
  "apellidos": "Martínez Sánchez",
  "isAdmin": false
}
```

**Response 409 Conflict:**
```
"El correo electrónico ya está registrado"
```

#### Actualizar usuario
```
PUT /api/usuarios/{id}
Content-Type: application/json
```

**Request:**
```json
{
  "id": 1,
  "nombre": "Juan Carlos",
  "apellidos": "García López",
  "correoElectronico": "juancarlos@ejemplo.com",
  "isAdmin": true,
  "comunidad": {
    "id": 1
  }
}
```

**Response 200 OK:** Devuelve el usuario actualizado

#### Eliminar usuario
```
DELETE /api/usuarios/{id}
```

**Response 200 OK:** Sin contenido

---

## Estructura de Componentes

```
src/app/
├── pages/
│   └── admin/
│       └── usuarios/
│           ├── comunidades-dashboard/
│           │   ├── comunidades-dashboard.component.ts
│           │   ├── comunidades-dashboard.component.html
│           │   └── comunidades-dashboard.component.scss
│           ├── usuarios-list/
│           │   ├── usuarios-list.component.ts
│           │   ├── usuarios-list.component.html
│           │   └── usuarios-list.component.scss
│           └── usuario-modal/
│               ├── usuario-modal.component.ts
│               ├── usuario-modal.component.html
│               └── usuario-modal.component.scss
└── services/
    ├── usuario.service.ts
    └── comunidad.service.ts
```

---

## Tareas a Implementar

### 1. Crear Interfaces

```typescript
// models/usuario.models.ts

export interface Comunidad {
  id: number;
  nombre: string;
  grupoOposicion: string;
}

export interface Usuario {
  id?: number;
  nombre: string;
  apellidos: string;
  correoElectronico: string;
  isAdmin: boolean;
  comunidad?: Comunidad;
}

export interface CreateUsuarioRequest {
  nombre: string;
  apellidos: string;
  correoElectronico: string;
  password: string;
  comunidadId: number;
}

export interface UpdateUsuarioRequest {
  id: number;
  nombre: string;
  apellidos: string;
  correoElectronico: string;
  isAdmin: boolean;
  comunidad: { id: number };
}
```

### 2. Crear Servicios

```typescript
// services/comunidad.service.ts

@Injectable({ providedIn: 'root' })
export class ComunidadService {
  private apiUrl = 'http://localhost:8080/api/comunidades';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Comunidad[]> {
    return this.http.get<Comunidad[]>(this.apiUrl);
  }
}
```

```typescript
// services/usuario.service.ts

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private apiUrl = 'http://localhost:8080/api/usuarios';
  private authUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  getByComunidad(comunidadId: number): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.apiUrl}/comunidad/${comunidadId}`);
  }

  getById(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }

  create(request: CreateUsuarioRequest): Observable<any> {
    return this.http.post(`${this.authUrl}/register`, request);
  }

  update(id: number, request: UpdateUsuarioRequest): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Genera una contraseña temporal
  generateTempPassword(): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let password = 'temp_';
    for (let i = 0; i < 8; i++) {
      password += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return password;
  }
}
```

### 3. Dashboard de Comunidades

**Diseño:**
- Grid/Cards con las comunidades disponibles
- Cada card muestra: nombre de comunidad y grupo de oposición
- Al hacer clic en una card, navegar a `/admin/usuarios/{comunidadId}`

```html
<!-- comunidades-dashboard.component.html -->

<div class="dashboard-container">
  <h1>Gestión de Usuarios</h1>
  <p class="subtitle">Selecciona una comunidad para gestionar sus usuarios</p>

  <div class="comunidades-grid">
    <div
      *ngFor="let comunidad of comunidades"
      class="comunidad-card"
      (click)="selectComunidad(comunidad)">
      <h3>{{ comunidad.nombre }}</h3>
      <p>{{ comunidad.grupoOposicion }}</p>
    </div>
  </div>
</div>
```

```scss
/* comunidades-dashboard.component.scss */

.dashboard-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  font-size: 1.75rem;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #6b7280;
  margin-bottom: 2rem;
}

.comunidades-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.comunidad-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #e5e7eb;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-color: #3b82f6;
  }

  h3 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #1a1a2e;
    margin-bottom: 0.5rem;
  }

  p {
    font-size: 0.875rem;
    color: #6b7280;
  }
}
```

### 4. Listado de Usuarios

**Diseño:**
- Botón "Volver" para regresar al dashboard
- Título con nombre de la comunidad seleccionada
- Botón "Crear Usuario" alineado a la derecha
- Tabla con columnas: Nombre, Apellidos, Email, Admin, Acciones
- Columna Admin: mostrar badge/chip "Sí" o "No"
- Columna Acciones: iconos o botones de Editar y Eliminar
- Confirmación antes de eliminar

```html
<!-- usuarios-list.component.html -->

<div class="usuarios-container">
  <div class="header">
    <div class="header-left">
      <button class="btn-back" (click)="goBack()">
        ← Volver
      </button>
      <div>
        <h1>Usuarios de {{ comunidadNombre }}</h1>
        <p class="subtitle">{{ usuarios.length }} usuarios registrados</p>
      </div>
    </div>
    <button class="btn-primary" (click)="openCreateModal()">
      + Crear Usuario
    </button>
  </div>

  <div class="table-container">
    <table>
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Apellidos</th>
          <th>Correo Electrónico</th>
          <th>Admin</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let usuario of usuarios">
          <td>{{ usuario.nombre }}</td>
          <td>{{ usuario.apellidos }}</td>
          <td>{{ usuario.correoElectronico }}</td>
          <td>
            <span class="badge" [class.badge-admin]="usuario.isAdmin">
              {{ usuario.isAdmin ? 'Sí' : 'No' }}
            </span>
          </td>
          <td class="actions">
            <button class="btn-icon" (click)="openEditModal(usuario)" title="Editar">
              ✏️
            </button>
            <button class="btn-icon btn-danger" (click)="confirmDelete(usuario)" title="Eliminar">
              🗑️
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div *ngIf="usuarios.length === 0" class="empty-state">
      <p>No hay usuarios en esta comunidad</p>
      <button class="btn-primary" (click)="openCreateModal()">Crear el primero</button>
    </div>
  </div>
</div>

<!-- Modal -->
<app-usuario-modal
  *ngIf="showModal"
  [usuario]="selectedUsuario"
  [comunidadId]="comunidadId"
  [isEditMode]="isEditMode"
  (close)="closeModal()"
  (saved)="onUsuarioSaved()">
</app-usuario-modal>
```

```scss
/* usuarios-list.component.scss */

.usuarios-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
}

.header-left {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.btn-back {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  font-size: 0.875rem;
  padding: 0.5rem;

  &:hover {
    color: #1a1a2e;
  }
}

h1 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 0.25rem;
}

.subtitle {
  color: #6b7280;
  font-size: 0.875rem;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #2563eb;
  }
}

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;

  th, td {
    padding: 1rem;
    text-align: left;
    border-bottom: 1px solid #e5e7eb;
  }

  th {
    background: #f9fafb;
    font-weight: 600;
    color: #374151;
    font-size: 0.875rem;
  }

  td {
    color: #1a1a2e;
  }

  tbody tr:hover {
    background: #f9fafb;
  }
}

.badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
  background: #e5e7eb;
  color: #6b7280;

  &.badge-admin {
    background: #dbeafe;
    color: #1d4ed8;
  }
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  background: none;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 0.5rem;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f3f4f6;
  }

  &.btn-danger:hover {
    background: #fee2e2;
    border-color: #fecaca;
  }
}

.empty-state {
  padding: 3rem;
  text-align: center;
  color: #6b7280;

  p {
    margin-bottom: 1rem;
  }
}
```

### 5. Modal de Crear/Editar Usuario

**Diseño:**
- Overlay oscuro con modal centrado
- Título: "Crear Usuario" o "Editar Usuario"
- Formulario con campos: Nombre, Apellidos, Correo Electrónico
- Checkbox: "Es administrador"
- Botones: Cancelar (secundario) | Guardar (primario)
- Mostrar errores de validación
- Al crear, mostrar mensaje de que el usuario deberá establecer su contraseña

```html
<!-- usuario-modal.component.html -->

<div class="modal-overlay" (click)="onClose()">
  <div class="modal-content" (click)="$event.stopPropagation()">
    <div class="modal-header">
      <h2>{{ isEditMode ? 'Editar Usuario' : 'Crear Usuario' }}</h2>
      <button class="btn-close" (click)="onClose()">×</button>
    </div>

    <form [formGroup]="form" (ngSubmit)="onSubmit()">
      <div class="modal-body">
        <div class="form-group">
          <label for="nombre">Nombre *</label>
          <input
            type="text"
            id="nombre"
            formControlName="nombre"
            [class.error]="form.get('nombre')?.invalid && form.get('nombre')?.touched">
          <span class="error-message" *ngIf="form.get('nombre')?.hasError('required') && form.get('nombre')?.touched">
            El nombre es obligatorio
          </span>
        </div>

        <div class="form-group">
          <label for="apellidos">Apellidos *</label>
          <input
            type="text"
            id="apellidos"
            formControlName="apellidos"
            [class.error]="form.get('apellidos')?.invalid && form.get('apellidos')?.touched">
          <span class="error-message" *ngIf="form.get('apellidos')?.hasError('required') && form.get('apellidos')?.touched">
            Los apellidos son obligatorios
          </span>
        </div>

        <div class="form-group">
          <label for="correoElectronico">Correo Electrónico *</label>
          <input
            type="email"
            id="correoElectronico"
            formControlName="correoElectronico"
            [class.error]="form.get('correoElectronico')?.invalid && form.get('correoElectronico')?.touched">
          <span class="error-message" *ngIf="form.get('correoElectronico')?.hasError('required') && form.get('correoElectronico')?.touched">
            El correo es obligatorio
          </span>
          <span class="error-message" *ngIf="form.get('correoElectronico')?.hasError('email') && form.get('correoElectronico')?.touched">
            Formato de email inválido
          </span>
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input type="checkbox" formControlName="isAdmin">
            <span>Es administrador</span>
          </label>
        </div>

        <div class="info-message" *ngIf="!isEditMode">
          ℹ️ El usuario recibirá instrucciones para establecer su contraseña.
        </div>

        <div class="error-message api-error" *ngIf="apiError">
          {{ apiError }}
        </div>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn-secondary" (click)="onClose()">
          Cancelar
        </button>
        <button type="submit" class="btn-primary" [disabled]="form.invalid || loading">
          {{ loading ? 'Guardando...' : 'Guardar' }}
        </button>
      </div>
    </form>
  </div>
</div>
```

```scss
/* usuario-modal.component.scss */

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;

  h2 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #1a1a2e;
  }

  .btn-close {
    background: none;
    border: none;
    font-size: 1.5rem;
    color: #6b7280;
    cursor: pointer;

    &:hover {
      color: #1a1a2e;
    }
  }
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.25rem;

  label {
    display: block;
    font-size: 0.875rem;
    font-weight: 500;
    color: #374151;
    margin-bottom: 0.5rem;
  }

  input[type="text"],
  input[type="email"] {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    font-size: 1rem;
    transition: border-color 0.2s;

    &:focus {
      outline: none;
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }

    &.error {
      border-color: #ef4444;
    }
  }
}

.checkbox-group {
  .checkbox-label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;

    input[type="checkbox"] {
      width: 1.25rem;
      height: 1.25rem;
      cursor: pointer;
    }

    span {
      font-size: 0.875rem;
      color: #374151;
    }
  }
}

.error-message {
  color: #ef4444;
  font-size: 0.75rem;
  margin-top: 0.25rem;

  &.api-error {
    background: #fef2f2;
    padding: 0.75rem;
    border-radius: 8px;
    margin-top: 1rem;
  }
}

.info-message {
  background: #eff6ff;
  color: #1d4ed8;
  padding: 0.75rem;
  border-radius: 8px;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.btn-secondary {
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f9fafb;
  }
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;

  &:hover:not(:disabled) {
    background: #2563eb;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}
```

### 6. Lógica del Modal

```typescript
// usuario-modal.component.ts

@Component({...})
export class UsuarioModalComponent implements OnInit {
  @Input() usuario: Usuario | null = null;
  @Input() comunidadId!: number;
  @Input() isEditMode = false;
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  form: FormGroup;
  loading = false;
  apiError = '';

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService
  ) {
    this.form = this.fb.group({
      nombre: ['', Validators.required],
      apellidos: ['', Validators.required],
      correoElectronico: ['', [Validators.required, Validators.email]],
      isAdmin: [false]
    });
  }

  ngOnInit() {
    if (this.isEditMode && this.usuario) {
      this.form.patchValue({
        nombre: this.usuario.nombre,
        apellidos: this.usuario.apellidos,
        correoElectronico: this.usuario.correoElectronico,
        isAdmin: this.usuario.isAdmin
      });
    }
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.loading = true;
    this.apiError = '';

    if (this.isEditMode) {
      this.updateUsuario();
    } else {
      this.createUsuario();
    }
  }

  private createUsuario() {
    const request: CreateUsuarioRequest = {
      nombre: this.form.value.nombre,
      apellidos: this.form.value.apellidos,
      correoElectronico: this.form.value.correoElectronico,
      password: this.usuarioService.generateTempPassword(),
      comunidadId: this.comunidadId
    };

    this.usuarioService.create(request).subscribe({
      next: () => {
        this.loading = false;
        this.saved.emit();
      },
      error: (err) => {
        this.loading = false;
        this.apiError = err.error || 'Error al crear el usuario';
      }
    });
  }

  private updateUsuario() {
    const request: UpdateUsuarioRequest = {
      id: this.usuario!.id!,
      nombre: this.form.value.nombre,
      apellidos: this.form.value.apellidos,
      correoElectronico: this.form.value.correoElectronico,
      isAdmin: this.form.value.isAdmin,
      comunidad: { id: this.comunidadId }
    };

    this.usuarioService.update(this.usuario!.id!, request).subscribe({
      next: () => {
        this.loading = false;
        this.saved.emit();
      },
      error: (err) => {
        this.loading = false;
        this.apiError = err.error || 'Error al actualizar el usuario';
      }
    });
  }

  onClose() {
    this.close.emit();
  }
}
```

---

## Rutas

```typescript
// app.routes.ts

{
  path: 'admin',
  canActivate: [authGuard, adminGuard],
  children: [
    {
      path: 'usuarios',
      component: ComunidadesDashboardComponent
    },
    {
      path: 'usuarios/:comunidadId',
      component: UsuariosListComponent
    }
  ]
}
```

---

## Checklist de Implementación

- [ ] Crear interfaces `Usuario`, `Comunidad`, `CreateUsuarioRequest`, `UpdateUsuarioRequest`
- [ ] Crear `ComunidadService` con método `getAll()`
- [ ] Crear `UsuarioService` con métodos CRUD
- [ ] Implementar `ComunidadesDashboardComponent`
  - [ ] Cargar y mostrar comunidades
  - [ ] Navegar al seleccionar comunidad
- [ ] Implementar `UsuariosListComponent`
  - [ ] Cargar usuarios por comunidad
  - [ ] Mostrar tabla con datos
  - [ ] Botón volver al dashboard
  - [ ] Botón crear usuario
  - [ ] Acciones editar/eliminar en cada fila
  - [ ] Confirmación antes de eliminar
- [ ] Implementar `UsuarioModalComponent`
  - [ ] Formulario reactivo con validaciones
  - [ ] Modo crear (sin contraseña visible)
  - [ ] Modo editar (cargar datos existentes)
  - [ ] Manejo de errores de API
- [ ] Configurar rutas
- [ ] Probar flujo completo: crear, editar, eliminar
- [ ] Verificar estilos consistentes con el resto de la app

---

## Notas de Diseño

- Mantener el estilo minimalista y moderno
- Usar la paleta de colores existente (azul #3b82f6 como primario)
- Bordes redondeados (8px-12px)
- Sombras sutiles
- Transiciones suaves (0.2s)
- Estados hover claros
- Mensajes de error en rojo (#ef4444)
- Mensajes informativos en azul (#1d4ed8)
