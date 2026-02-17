# JIS Training Backend - API Documentation

## Base Configuration

| Setting | Value |
|---------|-------|
| **Base URL (local)** | `http://localhost:8080` |
| **Base URL (Railway)** | `https://<tu-dominio>.railway.app` |
| **API Prefix** | `/api` |
| **Swagger UI** | `/swagger-ui.html` |
| **OpenAPI Docs** | `/v3/api-docs` |

## Authentication

- **Type:** Bearer Token (JWT)
- **Header:** `Authorization: Bearer <token>`
- **Expiration:** 1 hora (3600000 ms)
- **Obtain token:** `POST /api/auth/login`

### Postman Setup

1. Login con `POST /api/auth/login`
2. Copia el `token` de la respuesta
3. En Postman, ve a la pestana **Authorization** de la coleccion
4. Selecciona **Bearer Token** y pega el token
5. Todas las peticiones de la coleccion heredaran el token

---

## CORS

| Setting | Value |
|---------|-------|
| Origins | `http://localhost:4200`, `http://localhost:3000` |
| Methods | `GET, POST, PUT, DELETE, OPTIONS, PATCH` |
| Credentials | `true` |

---

## 1. Auth (Public)

### POST `/api/auth/login`

Login de usuario.

**Request Body:**
```json
{
  "correoElectronico": "usuario@email.com",
  "password": "123456"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "correoElectronico": "usuario@email.com",
  "nombre": "Juan",
  "apellidos": "Garcia",
  "isAdmin": false,
  "passwordChangeRequired": false
}
```

### POST `/api/auth/register`

Registro de nuevo usuario.

**Request Body:**
```json
{
  "nombre": "Juan",
  "apellidos": "Garcia",
  "correoElectronico": "juan@email.com",
  "password": "123456",
  "comunidadId": 1,
  "isAdmin": false
}
```

**Response 200:** Mismo formato que login.

### POST `/api/auth/change-password`

Cambio de contrasena. Requiere token en header Authorization.

**Request Body:**
```json
{
  "newPassword": "nuevaPassword123"
}
```

**Response 200:** Mismo formato que login (con nuevo token).

---

## 2. Usuarios

> Requiere autenticacion en todos los endpoints.

### GET `/api/usuarios`
Lista todos los usuarios.

### GET `/api/usuarios/{id}`
Obtiene usuario por ID.

### GET `/api/usuarios/email/{correoElectronico}`
Obtiene usuario por email.

### GET `/api/usuarios/comunidad/{comunidadId}`
Obtiene usuarios de una comunidad.

### POST `/api/usuarios`
Crea un usuario.

**Request Body:**
```json
{
  "nombre": "Ana",
  "apellidos": "Lopez",
  "correoElectronico": "ana@email.com",
  "userPassword": "123456",
  "isAdmin": false,
  "passwordChangeRequired": true,
  "comunidad": { "id": 1 }
}
```

### PUT `/api/usuarios/{id}`
Actualiza un usuario.

**Request Body:**
```json
{
  "nombre": "Ana",
  "apellidos": "Lopez Perez",
  "correoElectronico": "ana@email.com",
  "comunidadId": 1,
  "isAdmin": false
}
```

### DELETE `/api/usuarios/{id}`
Elimina un usuario.

---

## 3. Comunidades

> Requiere autenticacion en todos los endpoints.

### GET `/api/comunidades`
Lista todas las comunidades.

### GET `/api/comunidades/{id}`
Obtiene comunidad por ID.

### POST `/api/comunidades`
Crea una comunidad.

**Request Body:**
```json
{
  "nombreComunidad": "Madrid"
}
```

### PUT `/api/comunidades/{id}`
Actualiza una comunidad.

### DELETE `/api/comunidades/{id}`
Elimina una comunidad.

---

## 4. Materias

> Requiere autenticacion en todos los endpoints.

### GET `/api/materias/mis-materias`
Obtiene las materias de la comunidad del usuario autenticado.

### GET `/api/materias`
Lista todas las materias.

### GET `/api/materias/{id}`
Obtiene materia por ID.

### POST `/api/materias`
Crea una materia.

**Request Body:**
```json
{
  "materiaNombre": "Derecho Civil"
}
```

### PUT `/api/materias/{id}`
Actualiza una materia.

### DELETE `/api/materias/{id}`
Elimina una materia.

---

## 5. Topics

> Requiere autenticacion en todos los endpoints.

### GET `/api/topics`
Lista todos los topics.

### GET `/api/topics/{id}`
Obtiene topic por ID.

### GET `/api/topics/by-materia/{materiaId}`
Obtiene topics de una materia con conteo de preguntas.

**Response 200:**
```json
[
  {
    "id": 1,
    "topicName": "Obligaciones",
    "questionCount": 25
  }
]
```

### GET `/api/topics/{id}/questions/count`
Obtiene el numero de preguntas de un topic.

### POST `/api/topics`
Crea un topic.

**Request Body:**
```json
{
  "topicName": "Obligaciones",
  "materia": { "id": 1 }
}
```

### PUT `/api/topics/{id}`
Actualiza un topic.

### DELETE `/api/topics/{id}`
Elimina un topic.

---

## 6. Questions

> Requiere autenticacion en todos los endpoints.

### GET `/api/questions`
Lista todas las preguntas.

### GET `/api/questions/{id}`
Obtiene pregunta con respuestas.

**Response 200:**
```json
{
  "id": 1,
  "questionText": "Cual es el plazo general de prescripcion?",
  "topicId": 1,
  "topicName": "Obligaciones",
  "answers": [
    {
      "id": 1,
      "answerText": "5 anos",
      "isCorrect": true,
      "explanation": "Art. 1964 CC"
    },
    {
      "id": 2,
      "answerText": "10 anos",
      "isCorrect": false,
      "explanation": "Ese era el plazo anterior"
    }
  ]
}
```

### GET `/api/questions/by-topic/{topicId}`
Preguntas de un topic.

### GET `/api/questions/by-materia/{materiaId}`
Preguntas de una materia (todos los topics).

### POST `/api/questions`
Crea pregunta con respuestas.

**Request Body:**
```json
{
  "questionText": "Cual es el plazo general de prescripcion?",
  "topic": { "id": 1 },
  "answers": [
    {
      "answerText": "5 anos",
      "isCorrect": true,
      "explanation": "Art. 1964 CC"
    },
    {
      "answerText": "10 anos",
      "isCorrect": false,
      "explanation": "Plazo anterior a la reforma"
    },
    {
      "answerText": "15 anos",
      "isCorrect": false,
      "explanation": "No existe este plazo"
    },
    {
      "answerText": "3 anos",
      "isCorrect": false,
      "explanation": "Es plazo de acciones personales"
    }
  ]
}
```

### PUT `/api/questions/{id}`
Actualiza pregunta con respuestas.

**Request Body:**
```json
{
  "questionText": "Texto actualizado",
  "answers": [
    {
      "id": 1,
      "answerText": "Respuesta actualizada",
      "isCorrect": true,
      "explanation": "Explicacion"
    }
  ]
}
```

### POST `/api/questions/generate-quiz`
Genera un quiz con PDF.

**Request Body:**
```json
{
  "topicIds": [1, 2, 3],
  "numberOfQuestions": 20
}
```

**Response 200:**
```json
{
  "totalQuestions": 20,
  "topicsCount": 3,
  "questions": [ ... ],
  "pdfBase64": "JVBERi0xLjQ...",
  "pdfBase64TestSolution": "JVBERi0xLjQ..."
}
```

### DELETE `/api/questions/{id}`
Elimina una pregunta.

---

## 7. Answers

> Requiere autenticacion en todos los endpoints.

### GET `/api/answers`
Lista todas las respuestas.

### GET `/api/answers/{id}`
Obtiene respuesta por ID.

### GET `/api/answers/by-question/{questionId}`
Respuestas de una pregunta.

### POST `/api/answers`
Crea una respuesta.

**Request Body:**
```json
{
  "answerText": "Respuesta de ejemplo",
  "isCorrect": true,
  "explanation": "Explicacion",
  "question": { "id": 1 }
}
```

### PUT `/api/answers/{id}`
Actualiza una respuesta.

### DELETE `/api/answers/{id}`
Elimina una respuesta.

---

## 8. Simulacros

> Requiere autenticacion en todos los endpoints.

### GET `/api/simulacros`
Lista simulacros con conteo de preguntas.

**Response 200:**
```json
[
  {
    "id": 1,
    "nombreSimulacro": "Simulacro Civil 1",
    "comunidad": { "id": 1, "nombreComunidad": "Madrid" },
    "materia": { "id": 1, "materiaNombre": "Derecho Civil" },
    "tiempoLimiteSegundos": 3600,
    "totalPreguntas": 50
  }
]
```

### GET `/api/simulacros/{id}`
Obtiene simulacro por ID.

### POST `/api/simulacros`
Crea simulacro con preguntas existentes.

**Request Body:**
```json
{
  "nombreSimulacro": "Simulacro Civil 1",
  "comunidadId": 1,
  "materiaId": 1,
  "tiempoLimiteSegundos": 3600,
  "preguntaIds": [1, 2, 3, 4, 5]
}
```

### POST `/api/simulacros/generate`
Genera simulacro con preguntas aleatorias por tema.

**Request Body:**
```json
{
  "nombreSimulacro": "Simulacro Aleatorio",
  "comunidadId": 1,
  "materiaId": 1,
  "tiempoLimiteSegundos": 3600,
  "preguntasPorTema": {
    "1": 10,
    "2": 15,
    "3": 5
  }
}
```

### PUT `/api/simulacros/{id}`
Actualiza un simulacro.

### DELETE `/api/simulacros/{id}`
Elimina simulacro y sus preguntas asociadas.

---

## 9. Simulacro Preguntas

> Requiere autenticacion en todos los endpoints.

### GET `/api/simulacro-preguntas`
Lista todas las relaciones simulacro-pregunta.

### GET `/api/simulacro-preguntas/{id}`
Obtiene relacion por ID.

### GET `/api/simulacro-preguntas/by-simulacro/{simulacroId}`
Preguntas de un simulacro.

### POST `/api/simulacro-preguntas`
Asocia pregunta a simulacro.

### PUT `/api/simulacro-preguntas/{id}`
Actualiza relacion.

### DELETE `/api/simulacro-preguntas/simulacro/{simulacroId}/pregunta/{preguntaId}`
Elimina pregunta de un simulacro.

---

## 10. User Answers

> Requiere autenticacion en todos los endpoints.

### GET `/api/user-answers`
Lista todas las respuestas de usuarios.

### GET `/api/user-answers/{id}`
Obtiene respuesta por ID.

### GET `/api/user-answers/usuario/{usuarioId}`
Respuestas de un usuario.

### GET `/api/user-answers/question/{questionId}`
Respuestas a una pregunta.

### POST `/api/user-answers`
Registra una respuesta individual.

### POST `/api/user-answers/submit-test`
Envia un test completo. Retorna **202 ACCEPTED** (procesamiento asincrono).

**Request Body:**
```json
{
  "usuarioId": 1,
  "respuestas": [
    {
      "questionId": 1,
      "answerId": 3,
      "isCorrect": true
    },
    {
      "questionId": 2,
      "answerId": 7,
      "isCorrect": false
    }
  ]
}
```

**Response 202:**
```json
{
  "status": "ACCEPTED",
  "message": "Se estan procesando 2 respuestas en segundo plano",
  "totalRespuestas": 2
}
```

### DELETE `/api/user-answers/{id}`
Elimina una respuesta.

---

## 11. Stats

> Requiere autenticacion en todos los endpoints.

### GET `/api/stats/topic/usuario/{usuarioId}`
Estadisticas del usuario agrupadas por topic.

### GET `/api/stats/materia/usuario/{usuarioId}`
Estadisticas del usuario agrupadas por materia.

---

## 12. Avisos

> Requiere autenticacion en todos los endpoints.

### GET `/api/avisos`
Lista todos los avisos.

### GET `/api/avisos/{id}`
Obtiene aviso por ID.

### GET `/api/avisos/mis-avisos`
Avisos de la comunidad del usuario autenticado.

### GET `/api/avisos/comunidad/{comunidadId}`
Avisos de una comunidad.

### GET `/api/avisos/generales`
Avisos generales (sin comunidad).

### GET `/api/avisos/activos`
Avisos activos.

### POST `/api/avisos`
Crea un aviso.

**Request Body:**
```json
{
  "tituloAviso": "Mantenimiento programado",
  "cuerpoAviso": "El sistema estara en mantenimiento el sabado de 22:00 a 02:00",
  "comunidadId": 1,
  "activo": true
}
```

> `comunidadId` = `null` para avisos generales.

### PUT `/api/avisos/{id}`
Actualiza un aviso (mismo body que POST).

### PATCH `/api/avisos/{id}/toggle-activo`
Activa/desactiva un aviso.

### DELETE `/api/avisos/{id}`
Elimina un aviso.

---

## 13. Incidencias

> Requiere autenticacion en todos los endpoints.

### Endpoints de usuario

#### GET `/api/incidencias/mis-incidencias`
Incidencias del usuario autenticado.

#### GET `/api/incidencias/mis-novedades`
Conteo de incidencias con novedades.

**Response 200:**
```json
{
  "incidenciasConNovedades": 3
}
```

#### GET `/api/incidencias/{id}`
Obtiene incidencia (solo propietario o admin).

**Response 200:**
```json
{
  "id": 1,
  "titulo": "Error en quiz",
  "descripcion": "Al generar quiz de 50 preguntas da error",
  "imagenUrl": "https://...",
  "usuarioId": 1,
  "usuarioNombre": "Juan Garcia",
  "usuarioEmail": "juan@email.com",
  "fechaCreacion": "2026-02-16T10:30:00",
  "estado": "CREADA",
  "tieneNovedades": false,
  "comentarios": [
    {
      "id": 1,
      "usuarioId": 2,
      "usuarioNombre": "Admin",
      "esAdmin": true,
      "texto": "Estamos revisando el error",
      "fechaCreacion": "2026-02-16T11:00:00"
    }
  ]
}
```

#### POST `/api/incidencias`
Crea una incidencia.

**Request Body:**
```json
{
  "titulo": "Error en quiz",
  "descripcion": "Al generar quiz de 50 preguntas da error 500",
  "imagenUrl": "https://ejemplo.com/screenshot.png"
}
```

#### POST `/api/incidencias/{id}/comentarios`
Anade comentario a una incidencia.

**Request Body:**
```json
{
  "texto": "He probado de nuevo y sigue fallando"
}
```

#### DELETE `/api/incidencias/{id}`
Elimina incidencia (propietario o admin).

### Endpoints de admin

#### GET `/api/incidencias/admin/todas`
Lista todas las incidencias.

#### GET `/api/incidencias/admin/estado/{estado}`
Filtra por estado: `CREADA`, `LEIDA`, `EN_PROGRESO`, `CERRADA`.

#### PATCH `/api/incidencias/admin/{id}/estado`
Cambia el estado de una incidencia.

**Request Body:**
```json
{
  "estado": "EN_PROGRESO"
}
```

---

## 14. Carga Masiva (Excel)

> Requiere autenticacion en todos los endpoints.

### GET `/api/bulk/usuarios/plantilla`
Descarga la plantilla Excel para carga masiva de usuarios.

**Response:** Archivo Excel (.xlsx) con columnas: Nombre, Apellidos, Correo Electronico, Es Administrador (desplegable Si/No), Comunidad (desplegable con comunidades disponibles).

### POST `/api/bulk/usuarios`
Procesa el Excel de usuarios relleno y da de alta los usuarios.

**Content-Type:** `multipart/form-data`
**Body:** campo `file` con el archivo .xlsx

**Response 200:**
```json
{
  "totalProcesados": 10,
  "exitosos": 8,
  "fallidos": 2,
  "errores": [
    "Fila 3: El correo 'juan@email.com' ya esta registrado",
    "Fila 7: Comunidad 'Inexistente' no encontrada"
  ]
}
```

> Los usuarios se crean con password `password` y `passwordChangeRequired: true`.

### GET `/api/bulk/temas/plantilla`
Descarga la plantilla Excel para carga de un tema con preguntas. Las materias del desplegable son las de la comunidad del usuario autenticado.

**Response:** Archivo Excel (.xlsx) con 2 hojas:
- **Hoja "Tema":** Nombre del Tema, Materia (desplegable con materias de la comunidad).
- **Hoja "Preguntas":** Enunciado, Respuesta 1, Correcta 1, Explicacion 1, Respuesta 2, Correcta 2, Explicacion 2, Respuesta 3, Correcta 3, Explicacion 3, Respuesta 4, Correcta 4, Explicacion 4.

### POST `/api/bulk/temas`
Procesa el Excel de tema relleno y da de alta el tema con sus preguntas y respuestas.

**Content-Type:** `multipart/form-data`
**Body:** campo `file` con el archivo .xlsx

**Response 200:** Mismo formato `BulkLoadResult` que la carga de usuarios.

---

## Codigos de respuesta comunes

| Codigo | Descripcion |
|--------|-------------|
| `200` | OK |
| `201` | Creado |
| `202` | Aceptado (procesamiento asincrono) |
| `400` | Request invalido |
| `401` | No autenticado |
| `403` | Sin permisos |
| `404` | No encontrado |
| `500` | Error interno |

---

## Flujo de uso tipico en Postman

1. **Login:** `POST /api/auth/login` → copiar token
2. **Configurar token:** Authorization > Bearer Token en la coleccion
3. **Crear comunidad:** `POST /api/comunidades`
4. **Crear materia:** `POST /api/materias`
5. **Crear topics:** `POST /api/topics`
6. **Crear preguntas:** `POST /api/questions`
7. **Generar quiz:** `POST /api/questions/generate-quiz`
8. **Crear simulacro:** `POST /api/simulacros/generate`
9. **Enviar test:** `POST /api/user-answers/submit-test`
10. **Ver estadisticas:** `GET /api/stats/topic/usuario/{id}`
