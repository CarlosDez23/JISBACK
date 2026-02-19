# Penalización en Simulacros

## Descripción General

El backend ha incorporado el campo `tipoPenalizacion` al modelo `Simulacro`. Este campo controla la penalización que se aplica a la puntuación final cuando el usuario comete errores durante un simulacro. El cálculo de la puntuación penalizada es responsabilidad exclusiva del frontend: el backend almacena y devuelve el tipo de penalización, pero no aplica la fórmula en ningún endpoint.

Esta integración afecta a tres áreas del frontend:
1. El panel de administración donde se crean o generan simulacros.
2. El listado de simulacros que ve el usuario (cards).
3. La lógica de cálculo de puntuación al finalizar un simulacro.

---

## APIs Involucradas

### 1. GET /api/simulacros — Listar simulacros

- **URL**: `GET /api/simulacros`
- **Autenticación**: Sí — JWT Bearer Token
- **Headers requeridos**:
  ```
  Authorization: Bearer <token>
  Content-Type: application/json
  ```

#### Response (200 OK)

```typescript
interface SimulacroResponse {
  id: number;
  nombreSimulacro: string;
  comunidad: {
    id: number;
    nombre: string;
    grupoOposicion: string;
  };
  materia: {
    id: number;
    nombreMateria: string;
    sigla: string;
    comunidad?: {
      id: number;
      nombre: string;
      grupoOposicion: string;
    };
  };
  tiempoLimiteSegundos: number;
  tipoPenalizacion: 'TRES_A_UNO' | 'CUATRO_A_UNO' | null;
  totalPreguntas: number;
}
```

#### Errores Posibles

| Código | Descripción            | Acción recomendada                          |
|--------|------------------------|---------------------------------------------|
| 401    | Token no válido o ausente | Redirigir al login                       |
| 403    | Sin permisos           | Mostrar mensaje de acceso denegado          |
| 500    | Error interno          | Mostrar mensaje genérico de error           |

---

### 2. GET /api/simulacros/{id} — Obtener simulacro por ID

- **URL**: `GET /api/simulacros/{id}`
- **Autenticación**: Sí — JWT Bearer Token
- **Path params**: `id: number`
- **Headers requeridos**:
  ```
  Authorization: Bearer <token>
  Content-Type: application/json
  ```

#### Response (200 OK)

El backend devuelve directamente el modelo de dominio `Simulacro`. La forma es idéntica al objeto interno pero sin el campo `totalPreguntas`:

```typescript
interface Simulacro {
  id: number;
  nombreSimulacro: string;
  comunidad: {
    id: number;
    nombre: string;
    grupoOposicion: string;
  };
  materia: {
    id: number;
    nombreMateria: string;
    sigla: string;
    comunidad?: {
      id: number;
      nombre: string;
      grupoOposicion: string;
    };
  };
  tiempoLimiteSegundos: number;
  tipoPenalizacion: 'TRES_A_UNO' | 'CUATRO_A_UNO' | null;
}
```

#### Errores Posibles

| Código | Descripción            | Acción recomendada                          |
|--------|------------------------|---------------------------------------------|
| 401    | Token no válido o ausente | Redirigir al login                       |
| 404    | Simulacro no encontrado | Mostrar mensaje de no encontrado            |
| 500    | Error interno          | Mostrar mensaje genérico de error           |

---

### 3. POST /api/simulacros — Crear simulacro con preguntas

- **URL**: `POST /api/simulacros`
- **Autenticación**: Sí — JWT Bearer Token
- **Headers requeridos**:
  ```
  Authorization: Bearer <token>
  Content-Type: application/json
  ```

#### Request

```typescript
interface CreateSimulacroRequest {
  nombreSimulacro: string;            // Requerido. No puede estar en blanco.
  comunidadId: number | null;         // Opcional.
  materiaId: number | null;           // Opcional.
  tiempoLimiteSegundos: number | null; // Opcional.
  tipoPenalizacion: 'TRES_A_UNO' | 'CUATRO_A_UNO' | null; // Opcional.
  preguntaIds: number[];              // Requerido. Lista de IDs de preguntas. No puede estar vacía.
}
```

#### Response (200 OK)

Devuelve el objeto `Simulacro` creado (mismo shape que `GET /api/simulacros/{id}`).

#### Errores Posibles

| Código | Descripción                          | Acción recomendada                             |
|--------|--------------------------------------|------------------------------------------------|
| 400    | `nombreSimulacro` en blanco o `preguntaIds` vacío | Mostrar errores de validación en formulario |
| 401    | Token no válido o ausente            | Redirigir al login                             |
| 500    | Error interno                        | Mostrar mensaje genérico de error              |

---

### 4. POST /api/simulacros/generate — Generar simulacro con preguntas aleatorias

- **URL**: `POST /api/simulacros/generate`
- **Autenticación**: Sí — JWT Bearer Token
- **Headers requeridos**:
  ```
  Authorization: Bearer <token>
  Content-Type: application/json
  ```

#### Request

```typescript
interface GenerateSimulacroRequest {
  nombreSimulacro: string;            // Requerido. No puede estar en blanco.
  comunidadId: number | null;         // Opcional.
  materiaId: number | null;           // Opcional.
  tiempoLimiteSegundos: number | null; // Opcional.
  tipoPenalizacion: 'TRES_A_UNO' | 'CUATRO_A_UNO' | null; // Opcional.
  preguntasPorTema: Record<number, number>; // Requerido. Mapa topicId → cantidad de preguntas. No puede estar vacío.
}
```

Ejemplo de `preguntasPorTema`:
```json
{
  "1": 10,
  "2": 15,
  "3": 5
}
```

#### Response (200 OK)

Devuelve el objeto `Simulacro` generado (mismo shape que `GET /api/simulacros/{id}`).

#### Errores Posibles

| Código | Descripción                                    | Acción recomendada                             |
|--------|------------------------------------------------|------------------------------------------------|
| 400    | `nombreSimulacro` en blanco o `preguntasPorTema` vacío | Mostrar errores de validación en formulario |
| 401    | Token no válido o ausente                      | Redirigir al login                             |
| 500    | Error interno (p. ej., no hay suficientes preguntas en un tema) | Mostrar mensaje de error descriptivo |

---

## Modelo TypeScript a Actualizar

El tipo/interfaz `Simulacro` (y `SimulacroResponse` si existen como entidades separadas en el frontend) deben incorporar el nuevo campo. Añadir el enum `TipoPenalizacion` como tipo literal de unión:

```typescript
// src/app/core/models/tipo-penalizacion.model.ts  (archivo nuevo)
export type TipoPenalizacion = 'TRES_A_UNO' | 'CUATRO_A_UNO';
```

```typescript
// src/app/core/models/simulacro.model.ts  (actualizar)
import { TipoPenalizacion } from './tipo-penalizacion.model';

export interface Simulacro {
  id: number;
  nombreSimulacro: string;
  comunidad: {
    id: number;
    nombre: string;
    grupoOposicion: string;
  };
  materia: {
    id: number;
    nombreMateria: string;
    sigla: string;
    comunidad?: {
      id: number;
      nombre: string;
      grupoOposicion: string;
    };
  };
  tiempoLimiteSegundos: number;
  tipoPenalizacion: TipoPenalizacion | null;
}

// Usado en el listado (GET /api/simulacros)
export interface SimulacroResponse extends Simulacro {
  totalPreguntas: number;
}
```

---

## Services a Crear / Actualizar

### SimulacroService

- **Ubicación**: `src/app/core/services/simulacro.service.ts` (o en `src/app/features/simulacros/services/` si ya existe allí)
- **Cambios necesarios**: Los métodos existentes ya devuelven el tipo correcto si se actualizan los modelos. Verificar que los métodos existentes estén tipados contra `SimulacroResponse` y `Simulacro`. Añadir los métodos que falten si no existen:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Simulacro, SimulacroResponse } from '../models/simulacro.model';
import { CreateSimulacroRequest } from '../models/create-simulacro-request.model';
import { GenerateSimulacroRequest } from '../models/generate-simulacro-request.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SimulacroService {
  private readonly baseUrl = `${environment.apiUrl}/api/simulacros`;

  constructor(private http: HttpClient) {}

  // Devuelve SimulacroResponse[] que ya incluye tipoPenalizacion y totalPreguntas
  getAll(): Observable<SimulacroResponse[]> {
    return this.http.get<SimulacroResponse[]>(this.baseUrl);
  }

  // Devuelve Simulacro (sin totalPreguntas) que ya incluye tipoPenalizacion
  getById(id: number): Observable<Simulacro> {
    return this.http.get<Simulacro>(`${this.baseUrl}/${id}`);
  }

  // Acepta tipoPenalizacion como campo opcional en el body
  create(request: CreateSimulacroRequest): Observable<Simulacro> {
    return this.http.post<Simulacro>(this.baseUrl, request);
  }

  // Acepta tipoPenalizacion como campo opcional en el body
  generate(request: GenerateSimulacroRequest): Observable<Simulacro> {
    return this.http.post<Simulacro>(`${this.baseUrl}/generate`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
```

### PenalizacionService (nuevo — utilidades de cálculo)

- **Ubicación**: `src/app/core/services/penalizacion.service.ts`
- **Propósito**: Centralizar la lógica de cálculo de puntuación penalizada para que sea reutilizable y testeable de forma aislada.

```typescript
import { Injectable } from '@angular/core';
import { TipoPenalizacion } from '../models/tipo-penalizacion.model';

export interface ResultadoSimulacro {
  correctas: number;
  incorrectas: number;
  sinContestar: number;
}

export interface PuntuacionCalculada {
  puntuacionBruta: number;   // solo correctas
  puntuacionFinal: number;   // correctas - penalización
  penalizacion: number;      // valor descontado (puede ser decimal)
  tipoPenalizacion: TipoPenalizacion | null;
}

@Injectable({ providedIn: 'root' })
export class PenalizacionService {

  // Devuelve la etiqueta legible para mostrar en UI
  getLabelLegible(tipo: TipoPenalizacion | null): string {
    if (tipo === 'TRES_A_UNO') return '3:1';
    if (tipo === 'CUATRO_A_UNO') return '4:1';
    return 'Sin penalización';
  }

  // Calcula la puntuación final aplicando la fórmula de penalización
  calcularPuntuacion(
    resultado: ResultadoSimulacro,
    tipo: TipoPenalizacion | null
  ): PuntuacionCalculada {
    const { correctas, incorrectas } = resultado;

    let penalizacion = 0;
    if (tipo === 'TRES_A_UNO') {
      penalizacion = incorrectas / 3;
    } else if (tipo === 'CUATRO_A_UNO') {
      penalizacion = incorrectas / 4;
    }

    const puntuacionFinal = correctas - penalizacion;

    return {
      puntuacionBruta: correctas,
      puntuacionFinal: Math.max(0, puntuacionFinal), // nunca por debajo de 0
      penalizacion,
      tipoPenalizacion: tipo
    };
  }
}
```

---

## Interfaces de Request a Crear / Actualizar

```typescript
// src/app/core/models/create-simulacro-request.model.ts
import { TipoPenalizacion } from './tipo-penalizacion.model';

export interface CreateSimulacroRequest {
  nombreSimulacro: string;
  comunidadId: number | null;
  materiaId: number | null;
  tiempoLimiteSegundos: number | null;
  tipoPenalizacion: TipoPenalizacion | null;
  preguntaIds: number[];
}
```

```typescript
// src/app/core/models/generate-simulacro-request.model.ts
import { TipoPenalizacion } from './tipo-penalizacion.model';

export interface GenerateSimulacroRequest {
  nombreSimulacro: string;
  comunidadId: number | null;
  materiaId: number | null;
  tiempoLimiteSegundos: number | null;
  tipoPenalizacion: TipoPenalizacion | null;
  preguntasPorTema: Record<number, number>;
}
```

---

## Flujo Lógico por Área

### Área 1: Panel de Administración — Crear / Generar Simulacro

1. El formulario de creación/generación ya existe. Localizar el componente correspondiente.
2. Añadir al `FormGroup` un nuevo control `tipoPenalizacion` de tipo `string | null`, con valor inicial `null` (campo opcional).
3. Enlazar el control con un elemento `<select>` que ofrezca las tres opciones: "Sin penalización" (valor `null`), "3 incorrectas = -1 correcta" (valor `'TRES_A_UNO'`), "4 incorrectas = -1 correcta" (valor `'CUATRO_A_UNO'`).
4. Al construir el objeto request antes del POST, incluir `tipoPenalizacion: form.value.tipoPenalizacion ?? null`.
5. Enviar la petición a `POST /api/simulacros` o `POST /api/simulacros/generate` según corresponda.
6. Manejar respuesta: en caso de éxito (200), notificar al usuario y refrescar el listado. En caso de error, mostrar el mensaje adecuado.

### Área 2: Listado de Simulacros del Usuario (Cards)

1. El servicio ya llama a `GET /api/simulacros`. Tras actualizar el modelo, `SimulacroResponse` tendrá `tipoPenalizacion` disponible.
2. En el componente de cada card, usar `PenalizacionService.getLabelLegible(simulacro.tipoPenalizacion)` para obtener la etiqueta legible (`'3:1'`, `'4:1'` o `'Sin penalización'`).
3. Mostrar la etiqueta en la card. Si `tipoPenalizacion` es `null`, mostrar `'Sin penalización'` o no mostrar el campo (decisión de producto; la lógica ya lo gestiona devolviendo `'Sin penalización'`).

### Área 3: Cálculo de Puntuación al Finalizar Simulacro

1. Cuando se carga el simulacro antes de iniciarlo (`GET /api/simulacros/{id}`), almacenar el objeto `Simulacro` completo en el estado del componente o en el store.
2. El campo `tipoPenalizacion` está disponible desde ese momento.
3. Al finalizar el simulacro (o tras enviar las respuestas a `POST /api/user-answers/submit-test`), contabilizar en el frontend:
   - `correctas`: número de respuestas donde `isCorrect === true`.
   - `incorrectas`: número de respuestas donde `isCorrect === false` y la pregunta fue contestada.
   - `sinContestar`: preguntas que el usuario no respondió (no penalizan).
4. Llamar a `PenalizacionService.calcularPuntuacion({ correctas, incorrectas, sinContestar }, simulacro.tipoPenalizacion)`.
5. Mostrar al usuario: `puntuacionFinal` como puntuación definitiva, `puntuacionBruta` como correctas brutas y `penalizacion` como descuento aplicado.

**Nota importante**: `POST /api/user-answers/submit-test` devuelve `202 ACCEPTED` con un objeto de confirmación, no con el resultado del test. El cómputo de correctas/incorrectas debe hacerse en el frontend antes de enviar, a partir de las respuestas que el usuario marcó.

---

## Manejo de Estados

### GET /api/simulacros y GET /api/simulacros/{id}

- **Loading**: Mostrar indicador de carga mientras la petición está en curso.
- **Success**: Renderizar los datos. `tipoPenalizacion` puede ser `null` si el simulacro se creó sin este campo; tratarlo como "Sin penalización".
- **Error 401**: Redirigir al login mediante el `AuthGuard` o el `AuthInterceptor`.
- **Error 404** (solo `/{id}`): Navegar a una página de error o al listado.
- **Error 500**: Mostrar un mensaje genérico de error al usuario.

### POST /api/simulacros y POST /api/simulacros/generate

- **Loading**: Deshabilitar el botón de envío y mostrar spinner mientras la petición está en curso.
- **Success (200)**: Mostrar notificación de éxito, resetear el formulario o navegar al listado.
- **Error 400**: Mapear los errores de validación del backend al formulario. Los mensajes posibles son:
  - `"nombreSimulacro cannot be blank"`
  - `"preguntaIds cannot be empty"` (para creación manual)
  - `"preguntasPorTema cannot be empty"` (para generación aleatoria)
- **Error 401**: Redirigir al login.
- **Error 500**: Mostrar mensaje genérico de error. Puede ocurrir si no hay suficientes preguntas disponibles en un tema al generar aleatoriamente.

### Cálculo de Puntuación

- **Sin penalización** (`tipoPenalizacion === null`): `puntuacionFinal === correctas`. No mostrar el desglose de penalización.
- **Con penalización**: Mostrar `puntuacionFinal`, `penalizacion` y la fórmula aplicada para que el usuario entienda el descuento.
- El resultado nunca debe mostrarse como negativo. Usar `Math.max(0, puntuacionFinal)`.

---

## Consideraciones Adicionales

### Autenticación

Todos los endpoints de simulacros requieren autenticación JWT. El token se obtiene en `POST /api/auth/login` y debe enviarse en el header `Authorization: Bearer <token>` en cada petición. Esto debe gestionarse mediante un `HttpInterceptor` que inyecte el header automáticamente a partir del token almacenado en sesión.

### Interceptor JWT

Verificar que el interceptor existente capture las respuestas `401` y ejecute el logout/redirección al login. No se requiere crear un interceptor nuevo; solo confirmar que el existente cubre los endpoints `/api/simulacros`.

### Guards

No se requieren guards nuevos específicos para esta funcionalidad. El guard de autenticación existente ya protege las rutas que consumen estos endpoints. Si existe un guard de administrador para el panel de creación de simulacros, no es necesario modificarlo.

### Validación en Cliente

Antes de enviar el formulario de creación o generación de simulacro, validar en el frontend:
- `nombreSimulacro`: campo requerido, no puede estar vacío.
- `preguntaIds` (creación manual): el array no puede estar vacío.
- `preguntasPorTema` (generación aleatoria): el objeto no puede tener cero entradas.
- `tipoPenalizacion`: campo opcional. Si el usuario no selecciona ningún valor, enviar `null`.

### Caché

No se requiere estrategia de caché específica para esta funcionalidad. Los simulacros se consultan bajo demanda. Si el proyecto ya usa un store (NgRx, Signals store, etc.), actualizar los selectors y reducers/effects para que propaguen el nuevo campo `tipoPenalizacion` sin cambios de estructura adicionales, solo añadiendo el campo al modelo de estado.

### Compatibilidad con Simulacros Existentes

Los simulacros creados antes de este cambio tendrán `tipoPenalizacion: null` en la respuesta de la API. El frontend debe tratar `null` como "sin penalización" en todos los puntos de visualización y cálculo. La función `getLabelLegible` y `calcularPuntuacion` del `PenalizacionService` ya contemplan este caso.
