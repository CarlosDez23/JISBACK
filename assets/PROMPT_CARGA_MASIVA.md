# Integración Frontend - Carga Masiva de Datos (Bulk Load)

## Descripción General

La funcionalidad de Carga Masiva permite a los administradores cargar datos de forma masiva mediante archivos Excel (.xlsx). Existen dos tipos de carga:

1. **Carga Masiva de Usuarios**: Permite crear múltiples usuarios simultáneamente
2. **Carga Masiva de Temas**: Permite crear un tema completo con sus preguntas y respuestas

El flujo es común para ambos: descargar plantilla → rellenarla → subirla → ver resultados del procesamiento.

---

## APIs Involucradas

### 1. Descargar Plantilla de Usuarios

- **URL**: `GET /api/bulk/usuarios/plantilla`
- **Autenticación**: Bearer Token requerido
- **Headers requeridos**:
  ```
  Authorization: Bearer <token>
  ```

#### Response (200 OK)
- **Content-Type**: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- **Content-Disposition**: `attachment; filename=plantilla_usuarios.xlsx`
- **Body**: Archivo binario (byte array) del Excel

**Estructura del Excel:**
- 1 hoja llamada "Usuarios"
- Columnas:
  - Nombre (texto)
  - Apellidos (texto)
  - Correo Electrónico (texto)
  - Es Administrador (dropdown: "Si" / "No")
  - Comunidad (dropdown con nombres de todas las comunidades disponibles)

#### Errores Posibles
| Código | Descripción | Acción recomendada |
|--------|-------------|--------------------|
| 401    | Token inválido o expirado | Redirigir a login |
| 500    | Error interno al generar plantilla | Mostrar mensaje genérico de error |

---

### 2. Subir Usuarios desde Excel

- **URL**: `POST /api/bulk/usuarios`
- **Autenticación**: Bearer Token requerido
- **Headers requeridos**:
  ```
  Authorization: Bearer <token>
  Content-Type: multipart/form-data
  ```

#### Request
- **Body**: FormData con la clave `file` conteniendo el archivo .xlsx
```typescript
const formData = new FormData();
formData.append('file', file); // file es de tipo File
```

**Validaciones del archivo:**
- No debe estar vacío
- Extensión debe ser `.xlsx`

#### Response (200 OK)
```typescript
interface BulkLoadResult {
  totalProcesados: number;   // Total de filas procesadas
  exitosos: number;           // Usuarios creados exitosamente
  fallidos: number;           // Usuarios que fallaron
  errores: string[];          // Array de mensajes de error descriptivos
}
```

**Ejemplo de respuesta:**
```json
{
  "totalProcesados": 10,
  "exitosos": 8,
  "fallidos": 2,
  "errores": [
    "Fila 3: El correo 'juan@email.com' ya está registrado",
    "Fila 7: Comunidad 'Inexistente' no encontrada"
  ]
}
```

#### Errores Posibles
| Código | Descripción | Acción recomendada |
|--------|-------------|--------------------|
| 400    | Archivo vacío o no es .xlsx | Mostrar mensaje al usuario validando extensión |
| 401    | Token inválido o expirado | Redirigir a login |
| 500    | Error al procesar el Excel | Mostrar mensaje genérico de error |

---

### 3. Descargar Plantilla de Temas

- **URL**: `GET /api/bulk/temas/plantilla`
- **Autenticación**: Bearer Token requerido (el usuario autenticado debe tener comunidad asignada)
- **Headers requeridos**:
  ```
  Authorization: Bearer <token>
  ```

#### Response (200 OK)
- **Content-Type**: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- **Content-Disposition**: `attachment; filename=plantilla_tema.xlsx`
- **Body**: Archivo binario (byte array) del Excel

**Estructura del Excel:**

**Hoja 1 - "Tema":**
- Columnas:
  - Nombre del Tema (texto)
  - Materia (dropdown con las materias de la comunidad del usuario autenticado)

**Hoja 2 - "Preguntas":**
- Columnas (se repite el patrón para 4 respuestas posibles):
  - Enunciado (texto)
  - Respuesta 1 (texto)
  - Correcta 1 (dropdown: "Si" / "No")
  - Explicación 1 (texto)
  - Respuesta 2 (texto)
  - Correcta 2 (dropdown: "Si" / "No")
  - Explicación 2 (texto)
  - Respuesta 3 (texto)
  - Correcta 3 (dropdown: "Si" / "No")
  - Explicación 3 (texto)
  - Respuesta 4 (texto)
  - Correcta 4 (dropdown: "Si" / "No")
  - Explicación 4 (texto)

**IMPORTANTE**: Las materias del dropdown están filtradas por la comunidad del usuario, por eso necesita el token de autenticación.

#### Errores Posibles
| Código | Descripción | Acción recomendada |
|--------|-------------|--------------------|
| 401    | Token inválido o expirado | Redirigir a login |
| 404    | Usuario no encontrado o sin comunidad asignada | Mostrar mensaje indicando que el usuario debe tener una comunidad asignada |
| 500    | Error interno al generar plantilla | Mostrar mensaje genérico de error |

---

### 4. Subir Tema desde Excel

- **URL**: `POST /api/bulk/temas`
- **Autenticación**: Bearer Token requerido (el usuario autenticado debe tener comunidad asignada)
- **Headers requeridos**:
  ```
  Authorization: Bearer <token>
  Content-Type: multipart/form-data
  ```

#### Request
- **Body**: FormData con la clave `file` conteniendo el archivo .xlsx
```typescript
const formData = new FormData();
formData.append('file', file); // file es de tipo File
```

**Validaciones del archivo:**
- No debe estar vacío
- Extensión debe ser `.xlsx`

#### Response (200 OK)
```typescript
interface BulkLoadResult {
  totalProcesados: number;   // Total de elementos procesados (tema + preguntas)
  exitosos: number;           // Elementos creados exitosamente
  fallidos: number;           // Elementos que fallaron
  errores: string[];          // Array de mensajes de error descriptivos
}
```

**Ejemplo de respuesta:**
```json
{
  "totalProcesados": 11,
  "exitosos": 11,
  "fallidos": 0,
  "errores": []
}
```

**Ejemplo con errores:**
```json
{
  "totalProcesados": 5,
  "exitosos": 3,
  "fallidos": 2,
  "errores": [
    "Hoja Tema - Fila 2: La materia 'Matemáticas Avanzadas' no existe en tu comunidad",
    "Hoja Preguntas - Fila 4: Debe haber al menos una respuesta correcta"
  ]
}
```

#### Errores Posibles
| Código | Descripción | Acción recomendada |
|--------|-------------|--------------------|
| 400    | Archivo vacío o no es .xlsx | Mostrar mensaje al usuario validando extensión |
| 401    | Token inválido o expirado | Redirigir a login |
| 404    | Usuario no encontrado o sin comunidad asignada | Mostrar mensaje indicando que el usuario debe tener una comunidad asignada |
| 500    | Error al procesar el Excel | Mostrar mensaje genérico de error |

---

## Services a Crear

### BulkLoadService

**Ubicación**: `src/app/core/services/bulk-load.service.ts`

**Responsabilidades:**
- Gestionar la descarga de plantillas Excel
- Gestionar la subida de archivos Excel
- Transformar respuestas del backend

**Métodos requeridos:**

```typescript
@Injectable({
  providedIn: 'root'
})
export class BulkLoadService {
  private readonly apiUrl = environment.apiUrl + '/api/bulk';

  constructor(private http: HttpClient) {}

  /**
   * Descarga la plantilla Excel para carga de usuarios
   * @returns Observable<Blob> - Archivo Excel binario
   */
  downloadUsuariosTemplate(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/usuarios/plantilla`, {
      responseType: 'blob',
      headers: this.getAuthHeaders()
    });
  }

  /**
   * Sube un archivo Excel con usuarios para procesamiento masivo
   * @param file - Archivo .xlsx con los usuarios
   * @returns Observable<BulkLoadResult> - Resultado del procesamiento
   */
  uploadUsuarios(file: File): Observable<BulkLoadResult> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<BulkLoadResult>(
      `${this.apiUrl}/usuarios`,
      formData,
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Descarga la plantilla Excel para carga de temas
   * Las materias del dropdown están filtradas por la comunidad del usuario
   * @returns Observable<Blob> - Archivo Excel binario
   */
  downloadTemasTemplate(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/temas/plantilla`, {
      responseType: 'blob',
      headers: this.getAuthHeaders()
    });
  }

  /**
   * Sube un archivo Excel con un tema y sus preguntas
   * @param file - Archivo .xlsx con el tema y preguntas
   * @returns Observable<BulkLoadResult> - Resultado del procesamiento
   */
  uploadTemas(file: File): Observable<BulkLoadResult> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<BulkLoadResult>(
      `${this.apiUrl}/temas`,
      formData,
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Obtiene los headers de autenticación
   * NOTA: Ajustar según la implementación de autenticación del proyecto
   */
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken'); // o desde un servicio de auth
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  /**
   * Valida que el archivo sea .xlsx
   * @param file - Archivo a validar
   * @returns boolean - true si es válido
   */
  validateExcelFile(file: File): boolean {
    return file && file.name.endsWith('.xlsx') && file.size > 0;
  }
}
```

**Interfaces TypeScript:**

```typescript
export interface BulkLoadResult {
  totalProcesados: number;
  exitosos: number;
  fallidos: number;
  errores: string[];
}
```

---

## Flujo Lógico

### Flujo de Carga Masiva de Usuarios

1. **Usuario selecciona "Carga Masiva" → "Usuarios"**
   - Mostrar pantalla con dos secciones: Descarga y Subida

2. **Descarga de Plantilla**
   - Usuario hace clic en "Descargar Plantilla"
   - Llamar a `bulkLoadService.downloadUsuariosTemplate()`
   - Al recibir el Blob, crear URL y descargar automáticamente
   ```typescript
   this.bulkLoadService.downloadUsuariosTemplate().subscribe({
     next: (blob) => {
       const url = window.URL.createObjectURL(blob);
       const a = document.createElement('a');
       a.href = url;
       a.download = 'plantilla_usuarios.xlsx';
       a.click();
       window.URL.revokeObjectURL(url);
     },
     error: (err) => {
       // Manejar error
     }
   });
   ```

3. **Subida de Plantilla Rellenada**
   - Usuario selecciona archivo mediante input file o drag & drop
   - Validar que sea .xlsx antes de enviar
   - Mostrar estado de loading
   - Llamar a `bulkLoadService.uploadUsuarios(file)`
   - Mostrar resultados al completar

4. **Visualización de Resultados**
   - Mostrar resumen: "X de Y usuarios creados exitosamente"
   - Si hay errores, mostrarlos en una lista con scroll
   - Opción para descargar nuevamente la plantilla y reintentar

### Flujo de Carga Masiva de Temas

1. **Usuario selecciona "Carga Masiva" → "Temas"**
   - Mostrar pantalla con dos secciones: Descarga y Subida

2. **Descarga de Plantilla**
   - Usuario hace clic en "Descargar Plantilla"
   - Llamar a `bulkLoadService.downloadTemasTemplate()`
   - Mismo manejo de descarga que usuarios

3. **Subida de Plantilla Rellenada**
   - Usuario selecciona archivo mediante input file o drag & drop
   - Validar que sea .xlsx antes de enviar
   - Mostrar estado de loading
   - Llamar a `bulkLoadService.uploadTemas(file)`
   - Mostrar resultados al completar

4. **Visualización de Resultados**
   - Mostrar resumen: "Tema y X preguntas creados exitosamente"
   - Si hay errores, mostrarlos en una lista con scroll
   - Opción para descargar nuevamente la plantilla y reintentar

---

## Manejo de Estados

### Loading
- **Cuándo**: Durante la descarga de plantilla y durante el procesamiento del archivo subido
- **Cómo**:
  - Mostrar spinner o skeleton
  - Deshabilitar botones durante el proceso
  - Mensaje: "Descargando plantilla..." o "Procesando archivo..."

### Success
- **Cuándo**: Cuando la petición se completa exitosamente
- **Qué mostrar**:
  - Para descarga: Confirmación visual breve (toast/snackbar)
  - Para subida: Card o modal con el resumen del `BulkLoadResult`
    ```
    ✓ Procesamiento completado

    Total procesados: 10
    Exitosos: 8
    Fallidos: 2

    [Lista de errores si existen]
    ```

### Error
- **Tipos de error**:

1. **Error 400 (Archivo inválido)**
   - Mensaje: "El archivo debe ser un Excel (.xlsx) válido y no estar vacío"
   - Acción: Permitir seleccionar otro archivo

2. **Error 401 (No autenticado)**
   - Mensaje: "Tu sesión ha expirado"
   - Acción: Redirigir a login

3. **Error 404 (Sin comunidad - solo para temas)**
   - Mensaje: "Tu usuario no tiene una comunidad asignada. Contacta al administrador."
   - Acción: No permitir continuar

4. **Error 500 (Error servidor)**
   - Mensaje: "Ocurrió un error al procesar tu solicitud. Intenta nuevamente."
   - Acción: Permitir reintentar

---

## Consideraciones Adicionales

### Guards

**AdminGuard Requerido**:
- Esta funcionalidad es exclusiva para administradores
- Proteger la ruta con un guard que verifique `isAdmin === true`
- Redirigir a home o dashboard si no es admin

```typescript
// Ejemplo de configuración de ruta
{
  path: 'carga-masiva',
  component: CargaMasivaComponent,
  canActivate: [AuthGuard, AdminGuard]
}
```

### Interceptors

**AuthInterceptor**:
- Si existe un interceptor global que añade el token automáticamente, NO es necesario añadirlo manualmente en el service
- Si NO existe, implementar `getAuthHeaders()` como se muestra en el service

**ErrorInterceptor**:
- Manejar globalmente errores 401 para redirigir a login
- Transformar errores del servidor a mensajes user-friendly

### Validaciones en Cliente

**Validación de archivo antes de subir:**
```typescript
validateFile(file: File): { valid: boolean, error?: string } {
  if (!file) {
    return { valid: false, error: 'Selecciona un archivo' };
  }

  if (!file.name.endsWith('.xlsx')) {
    return { valid: false, error: 'El archivo debe tener extensión .xlsx' };
  }

  if (file.size === 0) {
    return { valid: false, error: 'El archivo está vacío' };
  }

  // Límite de tamaño (ejemplo: 10MB)
  const maxSize = 10 * 1024 * 1024; // 10MB
  if (file.size > maxSize) {
    return { valid: false, error: 'El archivo no debe superar los 10MB' };
  }

  return { valid: true };
}
```

### Caché

**NO implementar caché** para estas operaciones:
- Las plantillas pueden cambiar (nuevas comunidades, nuevas materias)
- Cada subida es única y debe procesarse en tiempo real

### Seguridad

- **NUNCA** almacenar archivos Excel en el navegador
- Limpiar el FormData después de enviar
- No loggear contenido de archivos (pueden tener datos sensibles)
- Validar siempre extensión y tamaño antes de enviar

### Accesibilidad

- Input file debe tener label descriptivo
- Botones deben tener estados disabled claros
- Mensajes de error deben ser descriptivos y accionables
- Soporte para navegación por teclado

### Performance

- Mostrar progress bar si el backend lo soporta (actualmente no)
- Para archivos grandes, considerar timeout más amplio en el HttpClient
- Deshabilitar botón de subir mientras se procesa para evitar duplicados

---

## Estructura de Archivos Sugerida

```
src/app/
├── core/
│   └── services/
│       └── bulk-load.service.ts
│
├── shared/
│   └── models/
│       └── bulk-load.model.ts  // Interfaces BulkLoadResult, etc.
│
└── features/
    └── admin/
        └── bulk-load/
            ├── bulk-load.component.ts
            ├── bulk-load.component.html
            ├── bulk-load.component.scss
            └── components/
                ├── usuarios-upload/
                │   ├── usuarios-upload.component.ts
                │   └── usuarios-upload.component.html
                └── temas-upload/
                    ├── temas-upload.component.ts
                    └── temas-upload.component.html
```

---

## Ejemplos de Uso

### Ejemplo 1: Descargar Plantilla en Componente

```typescript
export class UsuariosUploadComponent {
  isDownloading = false;

  constructor(private bulkLoadService: BulkLoadService) {}

  downloadTemplate(): void {
    this.isDownloading = true;

    this.bulkLoadService.downloadUsuariosTemplate().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'plantilla_usuarios.xlsx';
        link.click();
        window.URL.revokeObjectURL(url);

        this.isDownloading = false;
        // Mostrar toast de éxito
      },
      error: (error) => {
        this.isDownloading = false;
        // Manejar error según el código
        if (error.status === 401) {
          // Redirigir a login
        } else {
          // Mostrar mensaje de error
        }
      }
    });
  }
}
```

### Ejemplo 2: Subir Archivo con Validación

```typescript
export class UsuariosUploadComponent {
  isUploading = false;
  result: BulkLoadResult | null = null;
  uploadError: string | null = null;

  constructor(
    private bulkLoadService: BulkLoadService
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    // Validar archivo
    if (!this.bulkLoadService.validateExcelFile(file)) {
      this.uploadError = 'El archivo debe ser un Excel (.xlsx) válido';
      return;
    }

    // Subir archivo
    this.isUploading = true;
    this.uploadError = null;
    this.result = null;

    this.bulkLoadService.uploadUsuarios(file).subscribe({
      next: (result) => {
        this.isUploading = false;
        this.result = result;
        // Limpiar input
        input.value = '';
      },
      error: (error) => {
        this.isUploading = false;

        switch (error.status) {
          case 400:
            this.uploadError = 'El archivo no es válido o está vacío';
            break;
          case 401:
            this.uploadError = 'Tu sesión ha expirado';
            // Redirigir a login
            break;
          case 500:
            this.uploadError = 'Error al procesar el archivo. Intenta nuevamente.';
            break;
          default:
            this.uploadError = 'Error desconocido. Contacta al administrador.';
        }

        // Limpiar input
        input.value = '';
      }
    });
  }
}
```

### Ejemplo 3: Mostrar Resultados

```typescript
// En el template
<div *ngIf="result" class="result-card">
  <h3>Resultado del Procesamiento</h3>

  <div class="summary">
    <p>Total procesados: <strong>{{ result.totalProcesados }}</strong></p>
    <p class="success">Exitosos: <strong>{{ result.exitosos }}</strong></p>
    <p class="error" *ngIf="result.fallidos > 0">
      Fallidos: <strong>{{ result.fallidos }}</strong>
    </p>
  </div>

  <div *ngIf="result.errores.length > 0" class="errors">
    <h4>Errores encontrados:</h4>
    <ul>
      <li *ngFor="let error of result.errores">{{ error }}</li>
    </ul>
  </div>

  <button (click)="result = null">Cerrar</button>
</div>
```

---

## Testing

### Unit Tests para BulkLoadService

```typescript
describe('BulkLoadService', () => {
  let service: BulkLoadService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BulkLoadService]
    });

    service = TestBed.inject(BulkLoadService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should download usuarios template', () => {
    const mockBlob = new Blob(['test'], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

    service.downloadUsuariosTemplate().subscribe(blob => {
      expect(blob).toEqual(mockBlob);
    });

    const req = httpMock.expectOne('/api/bulk/usuarios/plantilla');
    expect(req.request.method).toBe('GET');
    expect(req.request.responseType).toBe('blob');
    req.flush(mockBlob);
  });

  it('should upload usuarios file', () => {
    const mockFile = new File(['test'], 'usuarios.xlsx');
    const mockResult: BulkLoadResult = {
      totalProcesados: 5,
      exitosos: 5,
      fallidos: 0,
      errores: []
    };

    service.uploadUsuarios(mockFile).subscribe(result => {
      expect(result).toEqual(mockResult);
    });

    const req = httpMock.expectOne('/api/bulk/usuarios');
    expect(req.request.method).toBe('POST');
    expect(req.request.body instanceof FormData).toBe(true);
    req.flush(mockResult);
  });
});
```

---

## Checklist de Implementación

- [ ] Crear `BulkLoadService` con los 4 métodos principales
- [ ] Crear interface `BulkLoadResult`
- [ ] Implementar componente de selección (Usuarios / Temas)
- [ ] Implementar componente de carga de usuarios
- [ ] Implementar componente de carga de temas
- [ ] Implementar función de descarga de Blob
- [ ] Implementar validación de archivos .xlsx
- [ ] Implementar manejo de estados (loading, success, error)
- [ ] Implementar visualización de resultados
- [ ] Añadir `AdminGuard` a la ruta
- [ ] Verificar que el `AuthInterceptor` añade el token
- [ ] Implementar manejo de error 401 (sesión expirada)
- [ ] Implementar manejo de error 404 (sin comunidad para temas)
- [ ] Añadir tests unitarios para el service
- [ ] Añadir tests de integración para los componentes
- [ ] Documentar en el README del frontend

---

## Notas Finales

- Esta funcionalidad es crítica y maneja datos sensibles, asegúrate de validar exhaustivamente
- Los mensajes de error del backend son descriptivos, muéstralos al usuario tal cual
- La plantilla de temas está personalizada por usuario, no cachearla
- Considera añadir analytics para trackear uso de esta funcionalidad
- En producción, considera añadir límite de tamaño de archivo en el backend también
