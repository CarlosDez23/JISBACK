# Prompt: Integración de Estadísticas con el Backend

## Resumen

El sistema de estadísticas funciona en dos partes:

1. **Registrar respuestas**: Cuando el usuario termina un test, se envían todas las respuestas con `POST /api/user-answers/submit-test` (ver prompt `frontend-submit-test-prompt.md`)
2. **Consultar estadísticas**: El componente de estadísticas existente debe consumir la API `/api/stats` para mostrar los datos.

Las estadísticas se calculan automáticamente en el backend mediante vistas SQL que agregan las respuestas del usuario.

> **Nota**: Para el envío de resultados del test, consulta el prompt `frontend-submit-test-prompt.md`

---

## Integrar Componente de Estadísticas con la API

### Endpoints de estadísticas

```typescript
// GET /api/stats/topic/usuario/{usuarioId}
// Estadísticas agrupadas por tema
[
  {
    "usuarioId": 1,
    "topicId": 1,
    "topicName": "Constitución Española - Título Preliminar",
    "totalRespuestas": 45,
    "aciertos": 38,
    "porcentajeAcierto": 84.44
  },
  ...
]

// GET /api/stats/materia/usuario/{usuarioId}
// Estadísticas agrupadas por materia
[
  {
    "usuarioId": 1,
    "materiaId": 1,
    "nombreMateria": "Derecho Constitucional",
    "totalRespuestas": 75,
    "aciertos": 60,
    "porcentajeAcierto": 80.00
  },
  ...
]
```

### Modelos TypeScript

```typescript
// models/stats.model.ts
export interface StatsPorTopic {
  usuarioId: number;
  topicId: number;
  topicName: string;
  totalRespuestas: number;
  aciertos: number;
  porcentajeAcierto: number;
}

export interface StatsPorMateria {
  usuarioId: number;
  materiaId: number;
  nombreMateria: string;
  totalRespuestas: number;
  aciertos: number;
  porcentajeAcierto: number;
}
```

### Servicio de estadísticas

```typescript
// services/stats.service.ts
@Injectable({ providedIn: 'root' })
export class StatsService {
  private apiUrl = 'http://localhost:8080/api/stats';

  constructor(private http: HttpClient) {}

  getStatsPorTopic(usuarioId: number): Observable<StatsPorTopic[]> {
    return this.http.get<StatsPorTopic[]>(`${this.apiUrl}/topic/usuario/${usuarioId}`);
  }

  getStatsPorMateria(usuarioId: number): Observable<StatsPorMateria[]> {
    return this.http.get<StatsPorMateria[]>(`${this.apiUrl}/materia/usuario/${usuarioId}`);
  }

  getTodasLasStats(usuarioId: number): Observable<{
    porTopic: StatsPorTopic[];
    porMateria: StatsPorMateria[];
  }> {
    return forkJoin({
      porTopic: this.getStatsPorTopic(usuarioId),
      porMateria: this.getStatsPorMateria(usuarioId)
    });
  }
}
```

### Integración en el componente de estadísticas existente

```typescript
// En tu componente de estadísticas existente

export class StatisticsComponent implements OnInit {
  statsPorTopic: StatsPorTopic[] = [];
  statsPorMateria: StatsPorMateria[] = [];
  isLoading = true;
  error = '';

  constructor(
    private statsService: StatsService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarEstadisticas();
  }

  cargarEstadisticas(): void {
    const userId = this.authService.getCurrentUserId();

    if (!userId) {
      this.error = 'Usuario no identificado';
      this.isLoading = false;
      return;
    }

    this.isLoading = true;

    this.statsService.getTodasLasStats(userId).subscribe({
      next: ({ porTopic, porMateria }) => {
        this.statsPorTopic = porTopic;
        this.statsPorMateria = porMateria;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar estadísticas:', error);
        this.error = 'Error al cargar las estadísticas';
        this.isLoading = false;
      }
    });
  }

  // Helpers para cálculos
  get totalRespuestas(): number {
    return this.statsPorMateria.reduce((sum, m) => sum + m.totalRespuestas, 0);
  }

  get totalAciertos(): number {
    return this.statsPorMateria.reduce((sum, m) => sum + m.aciertos, 0);
  }

  get porcentajeGlobal(): number {
    if (this.totalRespuestas === 0) return 0;
    return Math.round((this.totalAciertos / this.totalRespuestas) * 100 * 100) / 100;
  }

  get mejorMateria(): StatsPorMateria | null {
    const conDatos = this.statsPorMateria.filter(m => m.totalRespuestas > 0);
    if (conDatos.length === 0) return null;
    return conDatos.reduce((best, curr) =>
      curr.porcentajeAcierto > best.porcentajeAcierto ? curr : best
    );
  }

  get peorMateria(): StatsPorMateria | null {
    const conDatos = this.statsPorMateria.filter(m => m.totalRespuestas > 0);
    if (conDatos.length === 0) return null;
    return conDatos.reduce((worst, curr) =>
      curr.porcentajeAcierto < worst.porcentajeAcierto ? curr : worst
    );
  }
}
```

---

## Flujo Completo

```
┌─────────────────────────────────────────────────────────────────┐
│  1. Usuario realiza test/simulacro                              │
│     - Responde preguntas                                        │
│     - Frontend acumula respuestas y calcula resultados          │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│  2. Usuario finaliza el test                                    │
│     - Frontend muestra resultados INMEDIATAMENTE                │
│     - POST /api/user-answers/submit-test (en segundo plano)     │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│  3. Backend guarda respuestas                                   │
│     - Vistas SQL calculan estadísticas automáticamente          │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│  4. Usuario va al componente de estadísticas                    │
│     - GET /api/stats/topic/usuario/{id}                         │
│     - GET /api/stats/materia/usuario/{id}                       │
│     - Se muestran los datos en la UI existente                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Checklist

- [ ] Crear `StatsService` con métodos para obtener stats por topic y materia
- [ ] Crear modelos `StatsPorTopic` y `StatsPorMateria`
- [ ] En el componente de estadísticas, inyectar `StatsService`
- [ ] Llamar a `getTodasLasStats()` en `ngOnInit`
- [ ] Conectar `statsPorTopic` y `statsPorMateria` con los elementos visuales existentes
- [ ] Implementar getters para cálculos (totalRespuestas, porcentajeGlobal, etc.)
- [ ] Manejar estado de loading y errores
