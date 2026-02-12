# Prompt: Integración de Autenticación JWT en Frontend Angular

## Contexto

Debes integrar autenticación JWT en un frontend Angular que consume una API REST en Spring Boot. El backend ya está implementado y funcionando. Tu tarea es implementar toda la lógica de autenticación en el frontend de manera limpia y siguiendo buenas prácticas.

---

## Información del Backend

### Base URL
```
http://localhost:8080
```

### Endpoints de Autenticación (públicos, no requieren token)

#### 1. Login
```
POST /api/auth/login
Content-Type: application/json
```

**Request:**
```json
{
  "correoElectronico": "usuario@ejemplo.com",
  "password": "contraseña123"
}
```

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "correoElectronico": "usuario@ejemplo.com",
  "nombre": "Juan",
  "apellidos": "García López"
}
```

**Response 401 Unauthorized:**
```
"Credenciales inválidas"
```

#### 2. Registro
```
POST /api/auth/register
Content-Type: application/json
```

**Request:**
```json
{
  "nombre": "Juan",
  "apellidos": "García López",
  "correoElectronico": "usuario@ejemplo.com",
  "password": "contraseña123",
  "comunidadId": 1
}
```

**Response 201 Created:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "correoElectronico": "usuario@ejemplo.com",
  "nombre": "Juan",
  "apellidos": "García López"
}
```

**Response 409 Conflict:**
```
"El correo electrónico ya está registrado"
```

**Response 400 Bad Request:**
```
"Comunidad no encontrada"
```

### Endpoints Protegidos (requieren token)

Todos los demás endpoints bajo `/api/**` requieren autenticación.

**Header requerido:**
```
Authorization: Bearer <TOKEN_JWT>
```

**Response 401 Unauthorized** (token inválido/expirado):
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/questions"
}
```

---

## Configuración del Token JWT

| Parámetro | Valor |
|-----------|-------|
| Algoritmo | HS256 |
| Expiración | 24 horas (86400000 ms) |
| Header | Authorization |
| Prefijo | Bearer |

---

## Tareas a Implementar

### 1. Crear Interfaces/Modelos

Crea las interfaces TypeScript para tipar las respuestas:

```typescript
// models/auth.models.ts

export interface LoginRequest {
  correoElectronico: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  apellidos: string;
  correoElectronico: string;
  password: string;
  comunidadId: number;
}

export interface AuthResponse {
  token: string;
  correoElectronico: string;
  nombre: string;
  apellidos: string;
}

export interface User {
  correoElectronico: string;
  nombre: string;
  apellidos: string;
}
```

### 2. Crear AuthService

El servicio debe:
- Realizar login y registro contra la API
- Almacenar el token en localStorage con la clave `jwt_token`
- Almacenar los datos del usuario en localStorage con la clave `user`
- Exponer un BehaviorSubject con el usuario actual para que los componentes puedan suscribirse
- Recuperar la sesión al iniciar la aplicación (si hay token guardado)
- Proporcionar métodos: `login()`, `register()`, `logout()`, `getToken()`, `isLoggedIn()`, `getCurrentUser()`

### 3. Crear AuthInterceptor

El interceptor HTTP debe:
- Añadir automáticamente el header `Authorization: Bearer <token>` a TODAS las peticiones excepto las de `/api/auth/`
- Capturar errores 401 (Unauthorized)
- Cuando reciba un 401: hacer logout, limpiar el estado y redirigir a `/login`
- Registrarse como provider en la configuración de la aplicación

### 4. Crear AuthGuard

El guard debe:
- Verificar si el usuario está autenticado (tiene token válido)
- Si no está autenticado: redirigir a `/login` y retornar `false`
- Si está autenticado: retornar `true`
- Usar el patrón funcional de Angular 15+ (`CanActivateFn`)

### 5. Configurar Rutas

Proteger las rutas que requieran autenticación:
- Rutas públicas: `/login`
- Rutas protegidas: todas las demás (especialmente `/admin/**`)

### 6. Crear Componente de Login

**LoginComponent:**
- Formulario con campos: correoElectronico (email), password
- Validaciones: campos requeridos, formato email válido
- Mostrar errores de validación y de la API
- Redirigir a la página principal tras login exitoso

### 7. Crear Componente de Registro de Usuarios (Panel Admin)

**Nota:** El registro de usuarios lo realiza el administrador desde el panel de administración, no es un registro público.

**RegisterUserComponent (en zona admin):**
- Formulario con campos: nombre, apellidos, correoElectronico, password, comunidadId (select)
- Validaciones: campos requeridos, formato email, contraseña mínimo 6 caracteres
- Cargar lista de comunidades desde `GET /api/comunidades` (el admin ya está autenticado)
- Mostrar errores de validación y de la API
- Mostrar mensaje de éxito tras registro

### 7. Actualizar Layout/Navbar

- Mostrar nombre del usuario cuando está logueado
- Botón de logout que llame a `authService.logout()` y redirija a `/login`
- Ocultar opciones de navegación si no está autenticado

---

## Flujo de Autenticación

```
┌────────────────────────────────────────────────────────────────┐
│                    FLUJO DE AUTENTICACIÓN                       │
└────────────────────────────────────────────────────────────────┘

1. INICIO DE APLICACIÓN
   ├── ¿Existe token en localStorage?
   │   ├── SÍ → Cargar datos de usuario → Continuar navegación
   │   └── NO → Redirigir a /login

2. LOGIN
   ├── Usuario envía credenciales
   ├── POST /api/auth/login
   ├── ¿Respuesta exitosa?
   │   ├── SÍ → Guardar token + user en localStorage → Redirigir a /admin
   │   └── NO → Mostrar error "Credenciales inválidas"

3. PETICIÓN A API PROTEGIDA
   ├── Interceptor añade header: Authorization: Bearer <token>
   ├── ¿Respuesta exitosa?
   │   ├── SÍ → Procesar respuesta normalmente
   │   └── NO (401) → Logout → Redirigir a /login

4. LOGOUT
   ├── Borrar token de localStorage
   ├── Borrar user de localStorage
   ├── Limpiar BehaviorSubject
   └── Redirigir a /login
```

---

## Manejo de Expiración del Token

El token expira a las 24 horas. El backend responderá con 401 cuando el token expire.

**Estrategia a implementar:**

1. El `AuthInterceptor` detecta cualquier respuesta 401
2. Ejecuta `authService.logout()` para limpiar el estado
3. Redirige al usuario a `/login`
4. Opcionalmente, muestra un mensaje: "Tu sesión ha expirado, por favor inicia sesión de nuevo"

**Nota:** No hay endpoint de refresh token implementado. Si se requiere en el futuro, se añadirá `POST /api/auth/refresh`.

---

## Estructura de Archivos Sugerida

```
src/app/
├── core/
│   ├── auth/
│   │   ├── auth.service.ts
│   │   ├── auth.interceptor.ts
│   │   ├── auth.guard.ts
│   │   └── auth.models.ts
│   └── core.module.ts (si aplica)
├── pages/
│   ├── login/
│   │   ├── login.component.ts
│   │   ├── login.component.html
│   │   └── login.component.scss
│   └── admin/
│       └── usuarios/
│           ├── register-user.component.ts
│           ├── register-user.component.html
│           └── register-user.component.scss
└── app.config.ts (registrar interceptor)
```

---

## Ejemplo de Uso del Token en Peticiones

Una vez implementado el interceptor, todas las peticiones se autenticarán automáticamente:

```typescript
// Esto funcionará automáticamente con el token
this.http.get('/api/questions').subscribe(...)
this.http.post('/api/questions', data).subscribe(...)
```

El interceptor transformará la petición añadiendo:
```
GET /api/questions
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Testing Manual

Para probar la integración:

1. **Registrar usuario:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "nombre": "Test",
       "apellidos": "Usuario",
       "correoElectronico": "test@test.com",
       "password": "123456",
       "comunidadId": 1
     }'
   ```

2. **Login:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "correoElectronico": "test@test.com",
       "password": "123456"
     }'
   ```

3. **Petición autenticada:**
   ```bash
   curl http://localhost:8080/api/questions \
     -H "Authorization: Bearer <TOKEN_DEL_LOGIN>"
   ```

---

## Checklist de Implementación

- [ ] Crear interfaces/modelos de TypeScript
- [ ] Implementar AuthService con todos los métodos
- [ ] Implementar AuthInterceptor
- [ ] Registrar interceptor en app.config.ts
- [ ] Implementar AuthGuard
- [ ] Configurar rutas protegidas
- [ ] Crear LoginComponent con formulario
- [ ] Crear RegisterUserComponent en panel admin (para que el admin registre usuarios)
- [ ] Actualizar navbar con estado de autenticación
- [ ] Probar flujo completo de login
- [ ] Probar registro de usuarios desde panel admin
- [ ] Probar expiración de token (401)
- [ ] Probar logout

---

## Notas Adicionales

- El CORS está configurado para permitir peticiones desde `http://localhost:4200` y `http://localhost:3000`
- Los errores de validación del backend vienen en formato texto plano, no JSON
- El campo `comunidadId` en el registro debe ser un ID válido de la tabla `comunidad`
- El registro de usuarios lo realiza el administrador desde el panel de administración (ya autenticado), por lo que puede acceder a `GET /api/comunidades` para cargar el select de comunidades
