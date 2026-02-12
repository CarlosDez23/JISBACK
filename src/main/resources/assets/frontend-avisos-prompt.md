# Prompt: Implementación del Sistema de Avisos/Notificaciones

## Contexto del Problema

El backend implementa un sistema de avisos/notificaciones que permite a los administradores crear mensajes para los usuarios. Los avisos pueden ser:
- **Generales**: Visibles para todos los usuarios (comunidad = null)
- **Segmentados**: Visibles solo para usuarios de una comunidad específica

Cuando un usuario entra a la aplicación, debe ver tanto los avisos generales como los específicos de su comunidad.

## Comportamiento del Backend

### 1. Modelo de Datos

```typescript
interface Aviso {
  id: number;
  tituloAviso: string;
  cuerpoAviso: string;
  comunidadId: number | null;      // null = aviso general
  comunidadNombre: string | null;
  fechaCreacion: string;           // ISO 8601: "2024-01-15T10:30:00"
  activo: boolean;
  esGeneral: boolean;              // true si comunidadId es null
}
```

### 2. Endpoints Disponibles

#### Para Usuarios (Vista de avisos)

```typescript
// GET /api/avisos/mis-avisos
// Obtiene avisos generales + avisos de la comunidad del usuario
// Headers: Authorization: Bearer <token>

// Response 200 OK
[
  {
    "id": 1,
    "tituloAviso": "Mantenimiento programado",
    "cuerpoAviso": "El sistema estará en mantenimiento el sábado...",
    "comunidadId": null,
    "comunidadNombre": null,
    "fechaCreacion": "2024-01-15T10:30:00",
    "activo": true,
    "esGeneral": true
  },
  {
    "id": 2,
    "tituloAviso": "Nueva convocatoria disponible",
    "cuerpoAviso": "Se ha publicado la convocatoria para tu comunidad...",
    "comunidadId": 1,
    "comunidadNombre": "Comunidad de Madrid",
    "fechaCreacion": "2024-01-14T09:00:00",
    "activo": true,
    "esGeneral": false
  }
]
```

#### Para Administradores (CRUD completo)

```typescript
// GET /api/avisos
// Listar todos los avisos (admin)
// Response: Array de Aviso

// GET /api/avisos/{id}
// Obtener un aviso específico
// Response: Aviso

// GET /api/avisos/comunidad/{comunidadId}
// Avisos de una comunidad específica
// Response: Array de Aviso

// GET /api/avisos/generales
// Solo avisos generales
// Response: Array de Aviso

// GET /api/avisos/activos
// Solo avisos activos
// Response: Array de Aviso

// POST /api/avisos
// Crear nuevo aviso
// Request Body:
{
  "tituloAviso": "Título del aviso",
  "cuerpoAviso": "Contenido del aviso...",
  "comunidadId": null,  // null para general, o ID de comunidad
  "activo": true
}
// Response 201 Created: Aviso

// PUT /api/avisos/{id}
// Actualizar aviso
// Request Body: igual que POST
// Response 200 OK: Aviso

// DELETE /api/avisos/{id}
// Eliminar aviso
// Response 204 No Content

// PATCH /api/avisos/{id}/toggle-activo
// Activar/Desactivar aviso
// Response 200 OK: Aviso (con activo invertido)
```

## Implementación Requerida en Frontend

### 1. Modelos TypeScript

```typescript
// models/aviso.model.ts
export interface Aviso {
  id: number;
  tituloAviso: string;
  cuerpoAviso: string;
  comunidadId: number | null;
  comunidadNombre: string | null;
  fechaCreacion: string;
  activo: boolean;
  esGeneral: boolean;
}

export interface CreateAvisoRequest {
  tituloAviso: string;
  cuerpoAviso: string;
  comunidadId: number | null;
  activo: boolean;
}
```

### 2. Servicio de Avisos

```typescript
// services/aviso.service.ts
@Injectable({ providedIn: 'root' })
export class AvisoService {
  private apiUrl = 'http://localhost:8080/api/avisos';

  constructor(private http: HttpClient) {}

  // Para usuarios - obtener sus avisos
  getMisAvisos(): Observable<Aviso[]> {
    return this.http.get<Aviso[]>(`${this.apiUrl}/mis-avisos`);
  }

  // Para admin - obtener todos
  getAll(): Observable<Aviso[]> {
    return this.http.get<Aviso[]>(this.apiUrl);
  }

  getById(id: number): Observable<Aviso> {
    return this.http.get<Aviso>(`${this.apiUrl}/${id}`);
  }

  getByComunidad(comunidadId: number): Observable<Aviso[]> {
    return this.http.get<Aviso[]>(`${this.apiUrl}/comunidad/${comunidadId}`);
  }

  getGenerales(): Observable<Aviso[]> {
    return this.http.get<Aviso[]>(`${this.apiUrl}/generales`);
  }

  getActivos(): Observable<Aviso[]> {
    return this.http.get<Aviso[]>(`${this.apiUrl}/activos`);
  }

  create(aviso: CreateAvisoRequest): Observable<Aviso> {
    return this.http.post<Aviso>(this.apiUrl, aviso);
  }

  update(id: number, aviso: CreateAvisoRequest): Observable<Aviso> {
    return this.http.put<Aviso>(`${this.apiUrl}/${id}`, aviso);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleActivo(id: number): Observable<Aviso> {
    return this.http.patch<Aviso>(`${this.apiUrl}/${id}/toggle-activo`, {});
  }
}
```

### 3. Componente de Avisos para Usuarios (Dashboard/Home)

Este componente muestra los avisos al usuario cuando entra a la aplicación.

```typescript
// components/avisos-usuario/avisos-usuario.component.ts
@Component({
  selector: 'app-avisos-usuario',
  templateUrl: './avisos-usuario.component.html',
  styleUrls: ['./avisos-usuario.component.scss']
})
export class AvisosUsuarioComponent implements OnInit {
  avisos: Aviso[] = [];
  isLoading = true;
  avisosLeidos: Set<number> = new Set();

  constructor(private avisoService: AvisoService) {
    // Cargar avisos leídos del localStorage
    const leidos = localStorage.getItem('avisosLeidos');
    if (leidos) {
      this.avisosLeidos = new Set(JSON.parse(leidos));
    }
  }

  ngOnInit(): void {
    this.cargarAvisos();
  }

  cargarAvisos(): void {
    this.isLoading = true;
    this.avisoService.getMisAvisos().subscribe({
      next: (avisos) => {
        this.avisos = avisos;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar avisos:', error);
        this.isLoading = false;
      }
    });
  }

  marcarComoLeido(avisoId: number): void {
    this.avisosLeidos.add(avisoId);
    localStorage.setItem('avisosLeidos', JSON.stringify([...this.avisosLeidos]));
  }

  esNuevo(aviso: Aviso): boolean {
    return !this.avisosLeidos.has(aviso.id);
  }

  get avisosNoLeidos(): Aviso[] {
    return this.avisos.filter(a => this.esNuevo(a));
  }

  get cantidadNoLeidos(): number {
    return this.avisosNoLeidos.length;
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-ES', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
```

```html
<!-- components/avisos-usuario/avisos-usuario.component.html -->
<div class="avisos-container" *ngIf="avisos.length > 0">
  <div class="avisos-header">
    <h3>
      <i class="fas fa-bell"></i>
      Avisos
      <span class="badge" *ngIf="cantidadNoLeidos > 0">{{ cantidadNoLeidos }}</span>
    </h3>
  </div>

  <div class="avisos-list">
    <div
      *ngFor="let aviso of avisos"
      class="aviso-card"
      [class.nuevo]="esNuevo(aviso)"
      [class.general]="aviso.esGeneral"
      (click)="marcarComoLeido(aviso.id)"
    >
      <div class="aviso-header">
        <span class="aviso-tipo">
          <i [class]="aviso.esGeneral ? 'fas fa-globe' : 'fas fa-users'"></i>
          {{ aviso.esGeneral ? 'General' : aviso.comunidadNombre }}
        </span>
        <span class="aviso-fecha">{{ formatearFecha(aviso.fechaCreacion) }}</span>
      </div>

      <h4 class="aviso-titulo">
        {{ aviso.tituloAviso }}
        <span class="nuevo-badge" *ngIf="esNuevo(aviso)">Nuevo</span>
      </h4>

      <p class="aviso-cuerpo">{{ aviso.cuerpoAviso }}</p>
    </div>
  </div>

  <div class="no-avisos" *ngIf="avisos.length === 0 && !isLoading">
    <i class="fas fa-check-circle"></i>
    <p>No hay avisos nuevos</p>
  </div>

  <div class="loading" *ngIf="isLoading">
    <i class="fas fa-spinner fa-spin"></i>
    <p>Cargando avisos...</p>
  </div>
</div>
```

```scss
// components/avisos-usuario/avisos-usuario.component.scss
.avisos-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.avisos-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #eee;

  h3 {
    margin: 0;
    display: flex;
    align-items: center;
    gap: 0.5rem;

    i {
      color: #f59e0b;
    }

    .badge {
      background: #ef4444;
      color: white;
      font-size: 0.75rem;
      padding: 0.125rem 0.5rem;
      border-radius: 999px;
    }
  }
}

.avisos-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-height: 400px;
  overflow-y: auto;
}

.aviso-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: #f3f4f6;
  }

  &.nuevo {
    border-left: 4px solid #3b82f6;
    background: #eff6ff;
  }

  &.general {
    .aviso-tipo {
      color: #059669;
    }
  }
}

.aviso-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
  font-size: 0.75rem;
}

.aviso-tipo {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: #6b7280;

  i {
    font-size: 0.875rem;
  }
}

.aviso-fecha {
  color: #9ca3af;
}

.aviso-titulo {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #111827;
  display: flex;
  align-items: center;
  gap: 0.5rem;

  .nuevo-badge {
    background: #3b82f6;
    color: white;
    font-size: 0.625rem;
    padding: 0.125rem 0.375rem;
    border-radius: 4px;
    font-weight: 500;
    text-transform: uppercase;
  }
}

.aviso-cuerpo {
  margin: 0;
  color: #4b5563;
  font-size: 0.875rem;
  line-height: 1.5;
}

.no-avisos,
.loading {
  text-align: center;
  padding: 2rem;
  color: #6b7280;

  i {
    font-size: 2rem;
    margin-bottom: 0.5rem;
  }

  p {
    margin: 0;
  }
}

.no-avisos i {
  color: #10b981;
}
```

### 4. Modal de Avisos (Alternativa con Modal)

Si prefieres mostrar los avisos en un modal al entrar:

```typescript
// components/avisos-modal/avisos-modal.component.ts
@Component({
  selector: 'app-avisos-modal',
  templateUrl: './avisos-modal.component.html',
  styleUrls: ['./avisos-modal.component.scss']
})
export class AvisosModalComponent implements OnInit {
  @Input() isOpen = false;
  @Output() close = new EventEmitter<void>();

  avisos: Aviso[] = [];
  currentIndex = 0;
  isLoading = true;

  constructor(private avisoService: AvisoService) {}

  ngOnInit(): void {
    this.cargarAvisos();
  }

  cargarAvisos(): void {
    this.avisoService.getMisAvisos().subscribe({
      next: (avisos) => {
        // Filtrar solo avisos no leídos
        const leidos = JSON.parse(localStorage.getItem('avisosLeidos') || '[]');
        this.avisos = avisos.filter(a => !leidos.includes(a.id));
        this.isLoading = false;

        if (this.avisos.length === 0) {
          this.close.emit();
        }
      },
      error: () => {
        this.isLoading = false;
        this.close.emit();
      }
    });
  }

  get avisoActual(): Aviso | null {
    return this.avisos[this.currentIndex] || null;
  }

  siguiente(): void {
    this.marcarComoLeido();
    if (this.currentIndex < this.avisos.length - 1) {
      this.currentIndex++;
    } else {
      this.close.emit();
    }
  }

  marcarComoLeido(): void {
    if (this.avisoActual) {
      const leidos = JSON.parse(localStorage.getItem('avisosLeidos') || '[]');
      leidos.push(this.avisoActual.id);
      localStorage.setItem('avisosLeidos', JSON.stringify(leidos));
    }
  }

  cerrar(): void {
    this.marcarComoLeido();
    this.close.emit();
  }
}
```

```html
<!-- components/avisos-modal/avisos-modal.component.html -->
<div class="modal-overlay" *ngIf="isOpen && avisoActual" (click)="cerrar()">
  <div class="modal-content" (click)="$event.stopPropagation()">
    <button class="close-btn" (click)="cerrar()">
      <i class="fas fa-times"></i>
    </button>

    <div class="modal-header">
      <div class="aviso-meta">
        <span class="aviso-tipo">
          <i [class]="avisoActual.esGeneral ? 'fas fa-globe' : 'fas fa-users'"></i>
          {{ avisoActual.esGeneral ? 'Aviso General' : avisoActual.comunidadNombre }}
        </span>
        <span class="aviso-contador">
          {{ currentIndex + 1 }} / {{ avisos.length }}
        </span>
      </div>
    </div>

    <div class="modal-body">
      <h2>{{ avisoActual.tituloAviso }}</h2>
      <p>{{ avisoActual.cuerpoAviso }}</p>
    </div>

    <div class="modal-footer">
      <button class="btn-primary" (click)="siguiente()">
        {{ currentIndex < avisos.length - 1 ? 'Siguiente' : 'Entendido' }}
        <i [class]="currentIndex < avisos.length - 1 ? 'fas fa-arrow-right' : 'fas fa-check'"></i>
      </button>
    </div>
  </div>
</div>
```

### 5. Panel de Administración de Avisos

```typescript
// components/admin/avisos-admin/avisos-admin.component.ts
@Component({
  selector: 'app-avisos-admin',
  templateUrl: './avisos-admin.component.html',
  styleUrls: ['./avisos-admin.component.scss']
})
export class AvisosAdminComponent implements OnInit {
  avisos: Aviso[] = [];
  comunidades: Comunidad[] = [];
  isLoading = true;
  showForm = false;
  editingAviso: Aviso | null = null;

  avisoForm: FormGroup;

  constructor(
    private avisoService: AvisoService,
    private comunidadService: ComunidadService,
    private fb: FormBuilder
  ) {
    this.avisoForm = this.fb.group({
      tituloAviso: ['', [Validators.required, Validators.maxLength(255)]],
      cuerpoAviso: ['', [Validators.required]],
      comunidadId: [null],
      activo: [true]
    });
  }

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.isLoading = true;

    forkJoin({
      avisos: this.avisoService.getAll(),
      comunidades: this.comunidadService.getAll()
    }).subscribe({
      next: ({ avisos, comunidades }) => {
        this.avisos = avisos;
        this.comunidades = comunidades;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar datos:', error);
        this.isLoading = false;
      }
    });
  }

  abrirFormulario(aviso?: Aviso): void {
    this.editingAviso = aviso || null;
    this.showForm = true;

    if (aviso) {
      this.avisoForm.patchValue({
        tituloAviso: aviso.tituloAviso,
        cuerpoAviso: aviso.cuerpoAviso,
        comunidadId: aviso.comunidadId,
        activo: aviso.activo
      });
    } else {
      this.avisoForm.reset({ activo: true });
    }
  }

  cerrarFormulario(): void {
    this.showForm = false;
    this.editingAviso = null;
    this.avisoForm.reset({ activo: true });
  }

  guardar(): void {
    if (this.avisoForm.invalid) {
      this.avisoForm.markAllAsTouched();
      return;
    }

    const avisoData: CreateAvisoRequest = this.avisoForm.value;

    const request$ = this.editingAviso
      ? this.avisoService.update(this.editingAviso.id, avisoData)
      : this.avisoService.create(avisoData);

    request$.subscribe({
      next: () => {
        this.cerrarFormulario();
        this.cargarDatos();
      },
      error: (error) => {
        console.error('Error al guardar:', error);
      }
    });
  }

  toggleActivo(aviso: Aviso): void {
    this.avisoService.toggleActivo(aviso.id).subscribe({
      next: (updated) => {
        const index = this.avisos.findIndex(a => a.id === aviso.id);
        if (index !== -1) {
          this.avisos[index] = updated;
        }
      },
      error: (error) => {
        console.error('Error al cambiar estado:', error);
      }
    });
  }

  eliminar(aviso: Aviso): void {
    if (confirm(`¿Estás seguro de eliminar el aviso "${aviso.tituloAviso}"?`)) {
      this.avisoService.delete(aviso.id).subscribe({
        next: () => {
          this.avisos = this.avisos.filter(a => a.id !== aviso.id);
        },
        error: (error) => {
          console.error('Error al eliminar:', error);
        }
      });
    }
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-ES', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
```

```html
<!-- components/admin/avisos-admin/avisos-admin.component.html -->
<div class="avisos-admin">
  <div class="page-header">
    <h1>Gestión de Avisos</h1>
    <button class="btn-primary" (click)="abrirFormulario()">
      <i class="fas fa-plus"></i> Nuevo Aviso
    </button>
  </div>

  <!-- Loading -->
  <div class="loading" *ngIf="isLoading">
    <i class="fas fa-spinner fa-spin"></i> Cargando...
  </div>

  <!-- Tabla de avisos -->
  <div class="table-container" *ngIf="!isLoading">
    <table class="avisos-table">
      <thead>
        <tr>
          <th>Estado</th>
          <th>Título</th>
          <th>Tipo</th>
          <th>Fecha</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let aviso of avisos" [class.inactivo]="!aviso.activo">
          <td>
            <button
              class="status-toggle"
              [class.activo]="aviso.activo"
              (click)="toggleActivo(aviso)"
              [title]="aviso.activo ? 'Desactivar' : 'Activar'"
            >
              <i [class]="aviso.activo ? 'fas fa-check-circle' : 'fas fa-times-circle'"></i>
            </button>
          </td>
          <td>
            <strong>{{ aviso.tituloAviso }}</strong>
            <p class="cuerpo-preview">{{ aviso.cuerpoAviso | slice:0:100 }}...</p>
          </td>
          <td>
            <span class="tipo-badge" [class.general]="aviso.esGeneral">
              <i [class]="aviso.esGeneral ? 'fas fa-globe' : 'fas fa-users'"></i>
              {{ aviso.esGeneral ? 'General' : aviso.comunidadNombre }}
            </span>
          </td>
          <td>{{ formatearFecha(aviso.fechaCreacion) }}</td>
          <td class="acciones">
            <button class="btn-icon" (click)="abrirFormulario(aviso)" title="Editar">
              <i class="fas fa-edit"></i>
            </button>
            <button class="btn-icon btn-danger" (click)="eliminar(aviso)" title="Eliminar">
              <i class="fas fa-trash"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="empty-state" *ngIf="avisos.length === 0">
      <i class="fas fa-bullhorn"></i>
      <p>No hay avisos creados</p>
      <button class="btn-primary" (click)="abrirFormulario()">
        Crear primer aviso
      </button>
    </div>
  </div>

  <!-- Modal de formulario -->
  <div class="modal-overlay" *ngIf="showForm" (click)="cerrarFormulario()">
    <div class="modal-content" (click)="$event.stopPropagation()">
      <div class="modal-header">
        <h2>{{ editingAviso ? 'Editar Aviso' : 'Nuevo Aviso' }}</h2>
        <button class="close-btn" (click)="cerrarFormulario()">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <form [formGroup]="avisoForm" (ngSubmit)="guardar()">
        <div class="form-group">
          <label for="tituloAviso">Título *</label>
          <input
            type="text"
            id="tituloAviso"
            formControlName="tituloAviso"
            placeholder="Título del aviso"
            [class.error]="avisoForm.get('tituloAviso')?.invalid && avisoForm.get('tituloAviso')?.touched"
          />
          <span class="error-msg" *ngIf="avisoForm.get('tituloAviso')?.touched && avisoForm.get('tituloAviso')?.errors?.['required']">
            El título es obligatorio
          </span>
        </div>

        <div class="form-group">
          <label for="cuerpoAviso">Contenido *</label>
          <textarea
            id="cuerpoAviso"
            formControlName="cuerpoAviso"
            rows="5"
            placeholder="Escribe el contenido del aviso..."
            [class.error]="avisoForm.get('cuerpoAviso')?.invalid && avisoForm.get('cuerpoAviso')?.touched"
          ></textarea>
          <span class="error-msg" *ngIf="avisoForm.get('cuerpoAviso')?.touched && avisoForm.get('cuerpoAviso')?.errors?.['required']">
            El contenido es obligatorio
          </span>
        </div>

        <div class="form-group">
          <label for="comunidadId">Comunidad</label>
          <select id="comunidadId" formControlName="comunidadId">
            <option [ngValue]="null">General (todos los usuarios)</option>
            <option *ngFor="let comunidad of comunidades" [ngValue]="comunidad.id">
              {{ comunidad.nombre }}
            </option>
          </select>
          <span class="help-text">
            Deja en "General" para que todos los usuarios vean el aviso
          </span>
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input type="checkbox" formControlName="activo" />
            <span>Aviso activo</span>
          </label>
          <span class="help-text">
            Los avisos inactivos no se muestran a los usuarios
          </span>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-secondary" (click)="cerrarFormulario()">
            Cancelar
          </button>
          <button type="submit" class="btn-primary" [disabled]="avisoForm.invalid">
            {{ editingAviso ? 'Guardar cambios' : 'Crear aviso' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</div>
```

### 6. Integración en el Dashboard

```typescript
// components/dashboard/dashboard.component.ts
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  showAvisosModal = false;

  ngOnInit(): void {
    // Mostrar modal de avisos al entrar (opcional)
    this.checkAvisosNuevos();
  }

  checkAvisosNuevos(): void {
    // Puedes mostrar el modal automáticamente si hay avisos no leídos
    // this.showAvisosModal = true;
  }

  onAvisosModalClose(): void {
    this.showAvisosModal = false;
  }
}
```

```html
<!-- components/dashboard/dashboard.component.html -->
<div class="dashboard">
  <!-- Avisos destacados en la parte superior -->
  <app-avisos-usuario></app-avisos-usuario>

  <!-- Resto del dashboard -->
  <div class="dashboard-content">
    <!-- ... -->
  </div>

  <!-- Modal de avisos (opcional) -->
  <app-avisos-modal
    [isOpen]="showAvisosModal"
    (close)="onAvisosModalClose()"
  ></app-avisos-modal>
</div>
```

### 7. Icono de Notificación en Header (Badge)

```typescript
// components/header/header.component.ts
@Component({
  selector: 'app-header',
  templateUrl: './header.component.html'
})
export class HeaderComponent implements OnInit {
  cantidadAvisosNoLeidos = 0;

  constructor(private avisoService: AvisoService) {}

  ngOnInit(): void {
    this.cargarCantidadAvisos();
  }

  cargarCantidadAvisos(): void {
    this.avisoService.getMisAvisos().subscribe({
      next: (avisos) => {
        const leidos = JSON.parse(localStorage.getItem('avisosLeidos') || '[]');
        this.cantidadAvisosNoLeidos = avisos.filter(a => !leidos.includes(a.id)).length;
      }
    });
  }
}
```

```html
<!-- En el header -->
<a routerLink="/avisos" class="notification-icon">
  <i class="fas fa-bell"></i>
  <span class="badge" *ngIf="cantidadAvisosNoLeidos > 0">
    {{ cantidadAvisosNoLeidos }}
  </span>
</a>
```

## Diagrama de Flujo

```
┌─────────────────────────────────────────────────────────────────────┐
│                    FLUJO PARA USUARIOS                              │
└─────────────────────────────────────────────────────────────────────┘

Usuario hace login
         │
         ▼
    ┌─────────────┐
    │  Dashboard  │
    └──────┬──────┘
           │
           ▼
   ┌───────────────────┐
   │ GET /mis-avisos   │
   │ (con token JWT)   │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │ Backend extrae    │
   │ userId del token  │
   │ y busca comunidad │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │ Devuelve avisos:  │
   │ - Generales       │
   │ - De su comunidad │
   │ (solo activos)    │
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │ Frontend muestra  │
   │ avisos con badge  │
   │ de "nuevo"        │
   └───────────────────┘


┌─────────────────────────────────────────────────────────────────────┐
│                    FLUJO PARA ADMINS                                │
└─────────────────────────────────────────────────────────────────────┘

Admin accede a /admin/avisos
         │
         ▼
   ┌───────────────────┐
   │ GET /api/avisos   │
   │ (todos los avisos)│
   └───────┬───────────┘
           │
           ▼
   ┌───────────────────┐
   │ Lista con filtros │
   │ y acciones CRUD   │
   └───────┬───────────┘
           │
    ┌──────┴──────┬──────────────┐
    │             │              │
    ▼             ▼              ▼
┌────────┐  ┌──────────┐  ┌───────────┐
│ Crear  │  │ Editar   │  │ Eliminar  │
│ POST   │  │ PUT      │  │ DELETE    │
└────────┘  └──────────┘  └───────────┘
```

## Configuración de Rutas

```typescript
// app-routing.module.ts
const routes: Routes = [
  // ... otras rutas

  // Ruta para ver avisos (usuarios)
  {
    path: 'avisos',
    component: AvisosPageComponent,
    canActivate: [AuthGuard, PasswordChangeGuard]
  },

  // Ruta admin para gestionar avisos
  {
    path: 'admin/avisos',
    component: AvisosAdminComponent,
    canActivate: [AuthGuard, PasswordChangeGuard, AdminGuard]
  }
];
```

## Checklist de Implementación

### Servicio y Modelos
- [ ] Crear interfaz `Aviso`
- [ ] Crear interfaz `CreateAvisoRequest`
- [ ] Crear `AvisoService` con todos los métodos

### Componentes de Usuario
- [ ] Crear `AvisosUsuarioComponent` (lista en dashboard)
- [ ] Crear `AvisosModalComponent` (opcional, modal al entrar)
- [ ] Implementar lógica de "avisos leídos" en localStorage
- [ ] Añadir badge de notificaciones en header

### Componentes de Admin
- [ ] Crear `AvisosAdminComponent` con tabla y CRUD
- [ ] Crear formulario de creación/edición
- [ ] Implementar toggle de activo/inactivo
- [ ] Implementar confirmación de eliminación

### Integración
- [ ] Añadir `AvisosUsuarioComponent` al dashboard
- [ ] Configurar rutas con guards apropiados
- [ ] Añadir enlace en menú de administración
- [ ] Probar flujo completo usuario y admin

### Estilos
- [ ] Estilos para lista de avisos
- [ ] Estilos para modal
- [ ] Estilos para panel admin
- [ ] Responsive design
