# Prompt: Envío de Resultados de Test al Backend

## Resumen

Cuando el usuario finaliza un test (personalizado o simulacro), el frontend debe enviar todas las respuestas al backend para que se guarden y se actualicen las estadísticas.

**IMPORTANTE**: El backend procesa las respuestas de forma asíncrona. No esperes la respuesta para mostrar los resultados al usuario (eso ya lo tienes calculado en el frontend).

---

## Endpoint

```typescript
// POST /api/user-answers/submit-test
// Guarda todas las respuestas del test en segundo plano

// Request Body
{
  "usuarioId": 1,
  "respuestas": [
    { "questionId": 1, "answerId": 5, "isCorrect": true },
    { "questionId": 2, "answerId": 9, "isCorrect": false },
    { "questionId": 3, "answerId": 14, "isCorrect": true }
  ]
}

// Response 202 Accepted
{
  "status": "ACCEPTED",
  "message": "Se están procesando 3 respuestas en segundo plano",
  "totalRespuestas": 3
}
```

---

## Modelo TypeScript

```typescript
// models/submit-test.model.ts
export interface SubmitTestRequest {
  usuarioId: number;
  respuestas: AnswerItem[];
}

export interface AnswerItem {
  questionId: number;
  answerId: number;
  isCorrect: boolean;
}

export interface SubmitTestResponse {
  status: string;
  message: string;
  totalRespuestas: number;
}
```

---

## Servicio

```typescript
// services/user-answer.service.ts
@Injectable({ providedIn: 'root' })
export class UserAnswerService {
  private apiUrl = 'http://localhost:8080/api/user-answers';

  constructor(private http: HttpClient) {}

  submitTest(request: SubmitTestRequest): Observable<SubmitTestResponse> {
    return this.http.post<SubmitTestResponse>(`${this.apiUrl}/submit-test`, request);
  }
}
```

---

## Integración en el Componente de Test/Simulacro

```typescript
// Durante el test, acumula las respuestas en un array
respuestasDelTest: AnswerItem[] = [];

// Cuando el usuario responde una pregunta
onRespuestaSeleccionada(pregunta: Question, respuestaSeleccionada: Answer): void {
  this.respuestasDelTest.push({
    questionId: pregunta.id,
    answerId: respuestaSeleccionada.id,
    isCorrect: respuestaSeleccionada.correct  // o como se llame en tu modelo
  });
}

// AL FINALIZAR EL TEST
finalizarTest(): void {
  const userId = this.authService.getCurrentUserId();

  // 1. Mostrar resultados al usuario INMEDIATAMENTE (ya los tienes calculados)
  this.mostrarPantallaResultados();

  // 2. Enviar respuestas al backend EN SEGUNDO PLANO (fire and forget)
  this.userAnswerService.submitTest({
    usuarioId: userId,
    respuestas: this.respuestasDelTest
  }).subscribe({
    next: () => {
      console.log('Respuestas enviadas correctamente');
      // Opcional: mostrar notificación discreta de que se guardaron
    },
    error: (error) => {
      console.error('Error al enviar respuestas:', error);
      // Opcional: guardar en localStorage para reintentar después
      // o mostrar mensaje discreto al usuario
    }
  });
}
```

---

## Flujo Visual

```
┌─────────────────────────────────────────────────────────────────┐
│  Usuario responde preguntas                                     │
│  → Frontend acumula respuestas en array                         │
│  → Frontend calcula aciertos/fallos en tiempo real              │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│  Usuario pulsa "Finalizar Test"                                 │
└─────────────────────┬───────────────────────────────────────────┘
                      │
          ┌──────────┴──────────┐
          │                     │
          ▼                     ▼
┌─────────────────┐   ┌─────────────────────────────┐
│ INMEDIATAMENTE  │   │ EN SEGUNDO PLANO            │
│                 │   │                             │
│ Mostrar         │   │ POST /submit-test           │
│ pantalla de     │   │ → Backend guarda respuestas │
│ resultados      │   │ → Stats se actualizan       │
│ (ya calculados) │   │                             │
└─────────────────┘   └─────────────────────────────┘
```

---

## Manejo de Errores (Opcional)

Si quieres ser más robusto ante fallos de red:

```typescript
// Guardar en localStorage si falla el envío
private guardarParaReintentar(request: SubmitTestRequest): void {
  const pendientes = JSON.parse(localStorage.getItem('testsPendientes') || '[]');
  pendientes.push({
    ...request,
    timestamp: new Date().toISOString()
  });
  localStorage.setItem('testsPendientes', JSON.stringify(pendientes));
}

// Intentar reenviar tests pendientes al iniciar la app
reenviarTestsPendientes(): void {
  const pendientes = JSON.parse(localStorage.getItem('testsPendientes') || '[]');
  if (pendientes.length === 0) return;

  pendientes.forEach((test: SubmitTestRequest, index: number) => {
    this.userAnswerService.submitTest(test).subscribe({
      next: () => {
        // Eliminar de pendientes
        pendientes.splice(index, 1);
        localStorage.setItem('testsPendientes', JSON.stringify(pendientes));
      }
    });
  });
}
```

---

## Checklist

- [ ] Crear modelos `SubmitTestRequest`, `AnswerItem`, `SubmitTestResponse`
- [ ] Añadir método `submitTest()` al servicio
- [ ] Durante el test, acumular respuestas en array `respuestasDelTest`
- [ ] Al finalizar, mostrar resultados INMEDIATAMENTE
- [ ] Al finalizar, llamar a `submitTest()` sin bloquear la UI
- [ ] Manejar error de envío (opcional: guardar para reintentar)
