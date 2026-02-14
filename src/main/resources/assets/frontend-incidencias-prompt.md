# Prompt: Implementación del Sistema de Incidencias

## Resumen

El sistema de incidencias permite a los usuarios reportar problemas con la aplicación y a los administradores gestionarlas. Incluye:

- **Usuarios**: Crear, ver y eliminar sus incidencias. Conversar con administradores mediante comentarios.
- **Administradores**: Ver todas las incidencias, cambiar estados, responder a usuarios mediante comentarios.
- **Notificaciones**: Badge en el dashboard cuando hay novedades (cambios de estado o nuevos comentarios).

---

## Sistema de Conversación

Las incidencias incluyen un **hilo de comentarios** que funciona como una conversación entre el usuario y los administradores. **NO es un chat en tiempo real**, sino un sistema de mensajes tipo ticket de soporte.

```
┌─────────────────────────────────────────────────────────────────┐
│                 FLUJO DE CONVERSACIÓN                           │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  INCIDENCIA: "Error al cargar estadísticas"                    │
│  Estado: EN_PROGRESO                                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 👤 Juan García (Usuario)              15/01/2024 10:30  │   │
│  │ ────────────────────────────────────────────────────────│   │
│  │ Las estadísticas no cargan, me sale un error 500.       │   │
│  │ Adjunto captura de pantalla.                            │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 🛡️ Admin María (Soporte)              15/01/2024 11:45  │   │
│  │ ────────────────────────────────────────────────────────│   │
│  │ Hola Juan, estamos revisando el problema. ¿Podrías      │   │
│  │ indicarme qué navegador usas?                           │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 👤 Juan García (Usuario)              15/01/2024 12:00  │   │
│  │ ────────────────────────────────────────────────────────│   │
│  │ Uso Chrome versión 120. También probé en Firefox y      │   │
│  │ el mismo error.                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 🛡️ Admin María (Soporte)              15/01/2024 14:30  │   │
│  │ ────────────────────────────────────────────────────────│   │
│  │ ¡Solucionado! Era un problema del servidor. Ya debería  │   │
│  │ funcionar correctamente. ¿Puedes confirmar?             │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Escribe tu respuesta...                          [Enviar]│  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Permisos de Comentarios
- **Usuario creador de la incidencia**: Puede añadir comentarios a SU incidencia
- **Administradores**: Pueden añadir comentarios a CUALQUIER incidencia
- **Otros usuarios**: NO pueden ver ni comentar incidencias ajenas

### Notificaciones de Comentarios
- Cuando un **admin comenta** → el usuario ve badge de novedad
- Cuando un **usuario comenta** → los admins lo ven al entrar a la incidencia (no hay badge para admins)

---

## API de Incidencias

### Modelos de Datos

```typescript
// models/incidencia.model.ts

export type EstadoIncidencia = 'CREADA' | 'LEIDA' | 'EN_PROGRESO' | 'CERRADA';

export interface Incidencia {
  id: number;
  titulo: string;
  descripcion: string;
  imagenUrl: string | null;
  usuarioId: number;
  usuarioNombre: string;
  usuarioEmail: string;
  fechaCreacion: string;
  estado: EstadoIncidencia;
  tieneNovedades: boolean;
  comentarios: Comentario[];
}

export interface Comentario {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  esAdmin: boolean;        // ← IMPORTANTE: distingue comentarios de admin vs usuario
  texto: string;
  fechaCreacion: string;
}

export interface CreateIncidenciaRequest {
  titulo: string;
  descripcion: string;
  imagenUrl?: string;
}

export interface AddComentarioRequest {
  texto: string;
}

export interface NovedadesResponse {
  novedades: number;
}
```

### Endpoints para Usuarios

```typescript
// GET /api/incidencias/mis-incidencias
// Obtener todas mis incidencias
// Response: Incidencia[]

// GET /api/incidencias/mis-novedades
// Contar incidencias con novedades pendientes (para el badge)
// Response: { novedades: number }

// GET /api/incidencias/{id}
// Obtener detalle de una incidencia (marca novedades como leídas automáticamente)
// Response: Incidencia

// POST /api/incidencias
// Crear nueva incidencia
// Body: CreateIncidenciaRequest
// Response: Incidencia

// DELETE /api/incidencias/{id}
// Eliminar incidencia (solo el dueño o admin)
// Response: 204 No Content

// POST /api/incidencias/{id}/comentarios
// Añadir comentario a incidencia (usuario dueño o admin)
// Body: AddComentarioRequest
// Response: Comentario
```

### Endpoints para Administradores

```typescript
// GET /api/incidencias/admin/todas
// Listar todas las incidencias (solo admin)
// Response: Incidencia[]

// GET /api/incidencias/admin/estado/{estado}
// Listar incidencias por estado (CREADA, LEIDA, EN_PROGRESO, CERRADA)
// Response: Incidencia[]

// PATCH /api/incidencias/admin/{id}/estado
// Cambiar estado de incidencia (notifica al usuario)
// Body: { estado: EstadoIncidencia }
// Response: 200 OK

// POST /api/incidencias/{id}/comentarios
// Añadir comentario (usa el mismo endpoint que usuarios)
// Body: AddComentarioRequest
// Response: Comentario
```

---

## Servicio de Incidencias

```typescript
// services/incidencia.service.ts
@Injectable({ providedIn: 'root' })
export class IncidenciaService {
  private apiUrl = 'http://localhost:8080/api/incidencias';

  constructor(private http: HttpClient) {}

  // ========== USUARIOS ==========

  getMisIncidencias(): Observable<Incidencia[]> {
    return this.http.get<Incidencia[]>(`${this.apiUrl}/mis-incidencias`);
  }

  getMisNovedades(): Observable<NovedadesResponse> {
    return this.http.get<NovedadesResponse>(`${this.apiUrl}/mis-novedades`);
  }

  getById(id: number): Observable<Incidencia> {
    return this.http.get<Incidencia>(`${this.apiUrl}/${id}`);
  }

  crear(request: CreateIncidenciaRequest): Observable<Incidencia> {
    return this.http.post<Incidencia>(this.apiUrl, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Usado tanto por usuarios como por admins
  addComentario(incidenciaId: number, texto: string): Observable<Comentario> {
    return this.http.post<Comentario>(
      `${this.apiUrl}/${incidenciaId}/comentarios`,
      { texto }
    );
  }

  // ========== ADMIN ==========

  getAllAdmin(): Observable<Incidencia[]> {
    return this.http.get<Incidencia[]>(`${this.apiUrl}/admin/todas`);
  }

  getByEstadoAdmin(estado: EstadoIncidencia): Observable<Incidencia[]> {
    return this.http.get<Incidencia[]>(`${this.apiUrl}/admin/estado/${estado}`);
  }

  cambiarEstado(id: number, estado: EstadoIncidencia): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/admin/${id}/estado`, { estado });
  }
}
```

---

## Componentes a Implementar

### 1. Badge de Novedades en Dashboard

El dashboard debe mostrar un badge con el número de incidencias con novedades.

```typescript
// En el dashboard o en el componente de navegación
novedadesCount = 0;

ngOnInit(): void {
  this.cargarNovedades();
}

cargarNovedades(): void {
  this.incidenciaService.getMisNovedades().subscribe({
    next: (response) => {
      this.novedadesCount = response.novedades;
    }
  });
}
```

```html
<!-- Badge en el menú/dashboard -->
<a routerLink="/incidencias" class="incidencias-link">
  <i class="fas fa-exclamation-triangle"></i>
  Incidencias
  <span class="badge" *ngIf="novedadesCount > 0">{{ novedadesCount }}</span>
</a>
```

### 2. Panel de Usuario: Lista de Incidencias

```typescript
// components/incidencias/incidencias-lista.component.ts
export class IncidenciasListaComponent implements OnInit {
  incidencias: Incidencia[] = [];
  isLoading = true;
  showCrearModal = false;

  constructor(
    private incidenciaService: IncidenciaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarIncidencias();
  }

  cargarIncidencias(): void {
    this.isLoading = true;
    this.incidenciaService.getMisIncidencias().subscribe({
      next: (incidencias) => {
        this.incidencias = incidencias;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  verDetalle(incidencia: Incidencia): void {
    this.router.navigate(['/incidencias', incidencia.id]);
  }

  eliminar(incidencia: Incidencia): void {
    if (confirm('¿Estás seguro de eliminar esta incidencia?')) {
      this.incidenciaService.eliminar(incidencia.id).subscribe({
        next: () => {
          this.incidencias = this.incidencias.filter(i => i.id !== incidencia.id);
        }
      });
    }
  }

  getEstadoClass(estado: EstadoIncidencia): string {
    switch (estado) {
      case 'CREADA': return 'estado-creada';
      case 'LEIDA': return 'estado-leida';
      case 'EN_PROGRESO': return 'estado-progreso';
      case 'CERRADA': return 'estado-cerrada';
    }
  }

  getEstadoLabel(estado: EstadoIncidencia): string {
    switch (estado) {
      case 'CREADA': return 'Pendiente';
      case 'LEIDA': return 'Leída';
      case 'EN_PROGRESO': return 'En Progreso';
      case 'CERRADA': return 'Cerrada';
    }
  }
}
```

### 3. Formulario Crear Incidencia

```typescript
// components/incidencias/crear-incidencia.component.ts
export class CrearIncidenciaComponent {
  @Output() created = new EventEmitter<Incidencia>();
  @Output() cancel = new EventEmitter<void>();

  form: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private incidenciaService: IncidenciaService
  ) {
    this.form = this.fb.group({
      titulo: ['', [Validators.required, Validators.maxLength(255)]],
      descripcion: ['', Validators.required],
      imagenUrl: ['']
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    this.incidenciaService.crear(this.form.value).subscribe({
      next: (incidencia) => {
        this.isSubmitting = false;
        this.created.emit(incidencia);
      },
      error: () => {
        this.isSubmitting = false;
      }
    });
  }
}
```

### 4. Detalle de Incidencia con Hilo de Comentarios

Este componente es **compartido** entre usuarios y admins. La diferencia es solo visual (los comentarios de admin se muestran con estilo diferente).

```typescript
// components/incidencias/incidencia-detalle.component.ts
export class IncidenciaDetalleComponent implements OnInit {
  incidencia: Incidencia | null = null;
  isLoading = true;
  nuevoComentario = '';
  enviandoComentario = false;
  currentUserId: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private incidenciaService: IncidenciaService,
    private authService: AuthService
  ) {
    this.currentUserId = this.authService.getCurrentUserId();
  }

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.cargarIncidencia(id);
  }

  cargarIncidencia(id: number): void {
    this.isLoading = true;
    this.incidenciaService.getById(id).subscribe({
      next: (incidencia) => {
        this.incidencia = incidencia;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.router.navigate(['/incidencias']);
      }
    });
  }

  enviarComentario(): void {
    if (!this.nuevoComentario.trim() || !this.incidencia) return;

    this.enviandoComentario = true;

    this.incidenciaService.addComentario(this.incidencia.id, this.nuevoComentario).subscribe({
      next: (comentario) => {
        this.incidencia!.comentarios.push(comentario);
        this.nuevoComentario = '';
        this.enviandoComentario = false;
      },
      error: () => {
        this.enviandoComentario = false;
      }
    });
  }

  // Determina si el comentario es del usuario actual
  esMiComentario(comentario: Comentario): boolean {
    return comentario.usuarioId === this.currentUserId;
  }

  // Para mostrar fecha relativa
  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleString('es-ES', {
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
<!-- components/incidencias/incidencia-detalle.component.html -->
<div class="incidencia-detalle" *ngIf="incidencia">
  <!-- Cabecera -->
  <div class="incidencia-header">
    <button class="btn-back" (click)="router.navigate(['/incidencias'])">
      <i class="fas fa-arrow-left"></i> Volver
    </button>
    <span class="estado-badge" [class]="'estado-' + incidencia.estado.toLowerCase()">
      {{ incidencia.estado }}
    </span>
  </div>

  <!-- Info de la incidencia -->
  <div class="incidencia-info">
    <h2>{{ incidencia.titulo }}</h2>
    <p class="meta">
      Creada el {{ formatearFecha(incidencia.fechaCreacion) }}
    </p>
    <div class="descripcion">
      {{ incidencia.descripcion }}
    </div>
    <img *ngIf="incidencia.imagenUrl" [src]="incidencia.imagenUrl" class="imagen-adjunta" />
  </div>

  <!-- Hilo de comentarios -->
  <div class="comentarios-section">
    <h3>Conversación</h3>

    <div class="comentarios-lista">
      <div
        *ngFor="let comentario of incidencia.comentarios"
        class="comentario"
        [class.comentario-admin]="comentario.esAdmin"
        [class.comentario-usuario]="!comentario.esAdmin"
        [class.comentario-mio]="esMiComentario(comentario)">

        <div class="comentario-header">
          <span class="autor">
            <i [class]="comentario.esAdmin ? 'fas fa-shield-alt' : 'fas fa-user'"></i>
            {{ comentario.usuarioNombre }}
            <span class="rol" *ngIf="comentario.esAdmin">(Soporte)</span>
          </span>
          <span class="fecha">{{ formatearFecha(comentario.fechaCreacion) }}</span>
        </div>

        <div class="comentario-texto">
          {{ comentario.texto }}
        </div>
      </div>

      <div class="sin-comentarios" *ngIf="incidencia.comentarios.length === 0">
        <i class="fas fa-comments"></i>
        <p>No hay comentarios todavía</p>
      </div>
    </div>

    <!-- Formulario para nuevo comentario -->
    <div class="nuevo-comentario" *ngIf="incidencia.estado !== 'CERRADA'">
      <textarea
        [(ngModel)]="nuevoComentario"
        placeholder="Escribe tu respuesta..."
        rows="3"
        [disabled]="enviandoComentario">
      </textarea>
      <button
        class="btn-enviar"
        (click)="enviarComentario()"
        [disabled]="!nuevoComentario.trim() || enviandoComentario">
        <span *ngIf="!enviandoComentario">
          <i class="fas fa-paper-plane"></i> Enviar
        </span>
        <span *ngIf="enviandoComentario">
          <i class="fas fa-spinner fa-spin"></i> Enviando...
        </span>
      </button>
    </div>

    <div class="incidencia-cerrada" *ngIf="incidencia.estado === 'CERRADA'">
      <i class="fas fa-lock"></i>
      Esta incidencia está cerrada. No se pueden añadir más comentarios.
    </div>
  </div>
</div>
```

### 5. Panel Admin: Detalle con Comentarios y Cambio de Estado

Los admins usan el mismo componente de detalle, pero además pueden cambiar el estado.

```typescript
// components/admin/incidencia-admin-detalle.component.ts
export class IncidenciaAdminDetalleComponent implements OnInit {
  incidencia: Incidencia | null = null;
  isLoading = true;
  nuevoComentario = '';
  enviandoComentario = false;
  cambiandoEstado = false;

  estados: EstadoIncidencia[] = ['CREADA', 'LEIDA', 'EN_PROGRESO', 'CERRADA'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private incidenciaService: IncidenciaService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.cargarIncidencia(id);
  }

  cargarIncidencia(id: number): void {
    this.isLoading = true;
    this.incidenciaService.getById(id).subscribe({
      next: (incidencia) => {
        this.incidencia = incidencia;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.router.navigate(['/admin/incidencias']);
      }
    });
  }

  cambiarEstado(nuevoEstado: EstadoIncidencia): void {
    if (!this.incidencia || this.cambiandoEstado) return;

    this.cambiandoEstado = true;
    this.incidenciaService.cambiarEstado(this.incidencia.id, nuevoEstado).subscribe({
      next: () => {
        this.incidencia!.estado = nuevoEstado;
        this.cambiandoEstado = false;
      },
      error: () => {
        this.cambiandoEstado = false;
      }
    });
  }

  enviarComentario(): void {
    if (!this.nuevoComentario.trim() || !this.incidencia) return;

    this.enviandoComentario = true;

    this.incidenciaService.addComentario(this.incidencia.id, this.nuevoComentario).subscribe({
      next: (comentario) => {
        this.incidencia!.comentarios.push(comentario);
        this.nuevoComentario = '';
        this.enviandoComentario = false;
      },
      error: () => {
        this.enviandoComentario = false;
      }
    });
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleString('es-ES', {
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
<!-- Parte específica del admin: selector de estado -->
<div class="admin-controls">
  <label>Cambiar estado:</label>
  <div class="estado-buttons">
    <button
      *ngFor="let estado of estados"
      [class.active]="incidencia.estado === estado"
      [class]="'btn-estado estado-' + estado.toLowerCase()"
      (click)="cambiarEstado(estado)"
      [disabled]="cambiandoEstado || incidencia.estado === estado">
      {{ estado }}
    </button>
  </div>
</div>

<!-- El resto es igual que el detalle de usuario (comentarios) -->
```

### 6. Panel Admin: Lista de Incidencias

```typescript
// components/admin/incidencias-admin.component.ts
export class IncidenciasAdminComponent implements OnInit {
  incidencias: Incidencia[] = [];
  isLoading = true;
  filtroEstado: EstadoIncidencia | 'TODAS' = 'TODAS';

  estados: EstadoIncidencia[] = ['CREADA', 'LEIDA', 'EN_PROGRESO', 'CERRADA'];

  constructor(
    private incidenciaService: IncidenciaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarIncidencias();
  }

  cargarIncidencias(): void {
    this.isLoading = true;

    const request$ = this.filtroEstado === 'TODAS'
      ? this.incidenciaService.getAllAdmin()
      : this.incidenciaService.getByEstadoAdmin(this.filtroEstado);

    request$.subscribe({
      next: (incidencias) => {
        this.incidencias = incidencias;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  verDetalle(incidencia: Incidencia): void {
    this.router.navigate(['/admin/incidencias', incidencia.id]);
  }

  onFiltroChange(): void {
    this.cargarIncidencias();
  }

  // Contar comentarios pendientes (desde última respuesta admin)
  tieneComentariosPendientes(incidencia: Incidencia): boolean {
    if (!incidencia.comentarios || incidencia.comentarios.length === 0) return false;
    const ultimoComentario = incidencia.comentarios[incidencia.comentarios.length - 1];
    return !ultimoComentario.esAdmin; // El último comentario es del usuario
  }
}
```

---

## Estilos para Comentarios

```scss
// Estilos para distinguir comentarios de admin vs usuario

.comentarios-lista {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem 0;
}

.comentario {
  padding: 1rem;
  border-radius: 8px;
  max-width: 85%;
}

// Comentarios de usuario (alineados a la izquierda)
.comentario-usuario {
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  align-self: flex-start;

  .autor i {
    color: #6b7280;
  }
}

// Comentarios de admin (alineados a la derecha, color diferente)
.comentario-admin {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  align-self: flex-end;

  .autor {
    color: #1d4ed8;

    i {
      color: #2563eb;
    }
  }

  .rol {
    font-size: 0.75rem;
    background: #dbeafe;
    padding: 0.125rem 0.5rem;
    border-radius: 4px;
    margin-left: 0.25rem;
  }
}

// Si es MI comentario (borde resaltado)
.comentario-mio {
  border-width: 2px;
}

.comentario-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
}

.autor {
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.fecha {
  color: #9ca3af;
  font-size: 0.75rem;
}

.comentario-texto {
  color: #374151;
  line-height: 1.5;
  white-space: pre-wrap;
}

// Formulario de nuevo comentario
.nuevo-comentario {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;

  textarea {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    resize: vertical;
    font-family: inherit;

    &:focus {
      outline: none;
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }
  }

  .btn-enviar {
    margin-top: 0.5rem;
    padding: 0.5rem 1rem;
    background: #3b82f6;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;

    &:hover:not(:disabled) {
      background: #2563eb;
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
}

.incidencia-cerrada {
  text-align: center;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
  color: #6b7280;

  i {
    margin-right: 0.5rem;
  }
}
```

---

## Flujo de Estados

```
┌─────────────────────────────────────────────────────────────────┐
│                    CICLO DE VIDA DE INCIDENCIA                  │
└─────────────────────────────────────────────────────────────────┘

   CREADA ──────► LEIDA ──────► EN_PROGRESO ──────► CERRADA
     │              │                │                  │
     │              │                │                  │
     └──────────────┴────────────────┴──────────────────┘
                         │
                         ▼
              (Cualquier transición es válida)


Estados:
┌──────────────┬────────────────────────────────────────────────┐
│ CREADA       │ Incidencia recién creada, pendiente de revisar │
├──────────────┼────────────────────────────────────────────────┤
│ LEIDA        │ Admin ha visto la incidencia                   │
├──────────────┼────────────────────────────────────────────────┤
│ EN_PROGRESO  │ Se está trabajando en resolver la incidencia   │
├──────────────┼────────────────────────────────────────────────┤
│ CERRADA      │ Incidencia resuelta (no permite comentarios)   │
└──────────────┴────────────────────────────────────────────────┘
```

---

## Sistema de Notificaciones (Badge)

El badge de novedades se actualiza cuando:
1. Un admin cambia el estado de la incidencia
2. Un admin añade un comentario

```
┌─────────────────────────────────────────────────────────────────┐
│                    FLUJO DE NOVEDADES                           │
└─────────────────────────────────────────────────────────────────┘

Admin cambia estado o añade comentario
              │
              ▼
┌─────────────────────────────┐
│ tieneNovedades = true       │
│ (en la incidencia)          │
└─────────────────────┬───────┘
                      │
                      ▼
┌─────────────────────────────┐
│ Usuario ve badge en         │
│ el dashboard                │
└─────────────────────┬───────┘
                      │
                      ▼
┌─────────────────────────────┐
│ Usuario abre la incidencia  │
│ GET /incidencias/{id}       │
└─────────────────────┬───────┘
                      │
                      ▼
┌─────────────────────────────┐
│ tieneNovedades = false      │
│ (automáticamente)           │
└─────────────────────────────┘
```

---

## Rutas

```typescript
// app-routing.module.ts
const routes: Routes = [
  // Rutas de usuario
  {
    path: 'incidencias',
    component: IncidenciasListaComponent,
    canActivate: [AuthGuard, PasswordChangeGuard]
  },
  {
    path: 'incidencias/:id',
    component: IncidenciaDetalleComponent,
    canActivate: [AuthGuard, PasswordChangeGuard]
  },

  // Rutas de admin
  {
    path: 'admin/incidencias',
    component: IncidenciasAdminComponent,
    canActivate: [AuthGuard, PasswordChangeGuard, AdminGuard]
  },
  {
    path: 'admin/incidencias/:id',
    component: IncidenciaAdminDetalleComponent,
    canActivate: [AuthGuard, PasswordChangeGuard, AdminGuard]
  }
];
```

---

## Colores de Estados (CSS)

```scss
.estado-creada {
  background: #fee2e2;
  color: #dc2626;
}

.estado-leida {
  background: #fef3c7;
  color: #d97706;
}

.estado-en_progreso, .estado-progreso {
  background: #dbeafe;
  color: #2563eb;
}

.estado-cerrada {
  background: #d1fae5;
  color: #059669;
}
```

---

## Checklist de Implementación

### Modelos y Servicios
- [ ] Crear modelos TypeScript (Incidencia, Comentario, etc.)
- [ ] Crear IncidenciaService con todos los métodos

### Panel Usuario
- [ ] Crear componente lista de incidencias
- [ ] Crear componente/modal crear incidencia
- [ ] Crear componente detalle incidencia con hilo de comentarios
- [ ] Implementar envío de comentarios
- [ ] Implementar eliminación de incidencias

### Panel Admin
- [ ] Crear componente admin lista de incidencias
- [ ] Crear componente admin detalle con cambio de estado
- [ ] Implementar filtro por estado
- [ ] Implementar respuesta a usuarios (comentarios)
- [ ] Indicador visual de incidencias con comentarios pendientes

### Notificaciones
- [ ] Añadir badge de novedades en dashboard/navegación
- [ ] Llamar a /mis-novedades al cargar el dashboard
- [ ] Actualizar badge después de ver una incidencia

### Estilos
- [ ] Estilos diferenciados para comentarios de admin vs usuario
- [ ] Alineación tipo chat (usuario izquierda, admin derecha)
- [ ] Badge "Soporte" en comentarios de admin

### Rutas y Guards
- [ ] Configurar rutas de usuario
- [ ] Configurar rutas de admin con AdminGuard
- [ ] Ruta separada para detalle admin con cambio de estado
