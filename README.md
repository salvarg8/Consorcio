# Sistema de Gestión de Consorcio - API REST

## Descripción del Proyecto
Este proyecto es un backend profesional (API REST) para la administración de consorcios (Multi-tenant). Permite gestionar consorcios, usuarios, unidades funcionales, amenities, reservas, infracciones y pagos pendientes. Fue diseñado con un enfoque fuerte en **buenas prácticas**, **Clean Architecture (Capas)**, **Multi-tenancy** y modelado de dominio real.

## Stack Tecnológico
- **Java 17**
- **Spring Boot 3.2.x**
- **Spring Web** (API REST)
- **Spring Data JPA** (Persistencia)
- **Spring Security + JWT** (Autenticación y Autorización basada en Roles)
- **MySQL** (Base de datos relacional)
- **Hibernate / Bean Validation** (Validaciones de datos)
- **Swagger / OpenAPI** (Documentación de API)
- **JUnit 5 & Mockito** (Pruebas unitarias)
- **Maven** (Gestión de dependencias)

## Arquitectura y Diseño
El proyecto sigue una arquitectura en capas clásica y limpia, con clara separación de responsabilidades para favorecer el mantenimiento y el testeo:

- **Controllers:** Manejan solicitudes HTTP, validan el payload y devuelven respuestas HTTP unificadas (`ApiResponseDTO`, `ErrorResponseDTO`). **No contienen lógica de negocio**.
- **Services:** Contienen el núcleo de la aplicación, aplican reglas de negocio y validaciones lógicas. Todo se hace de forma segura aislando datos por Consorcio.
- **Repositories:** Interfaces de Spring Data para abstracción sobre el motor de BD.
- **Entities:** Clases JPA que representan el modelo de dominio puro. **Nunca se exponen fuera del servicio**.
- **DTOs:** Objetos de transferencia de datos.
- **Mappers:** Convierten entre Entidades y DTOs.
- **Exceptions:** Clases de error de negocio centralizadas en un `@ControllerAdvice`.

## Modelo de Dominio Resumido
1. **Consorcio:** Entidad raíz del sistema multi-tenant.
2. **Usuario:** Administrador, Encargado, Propietario o Inquilino. Relacionado Muchos-a-Muchos con Consorcios.
3. **UnidadFuncional:** Departamentos/casas. Tienen un Propietario asignado y opcionalmente un Inquilino.
4. **Amenity:** Espacios comunes (SUM, Pileta).
5. **ReservaAmenity:** Relaciona Unidad, Amenity y el Usuario que reserva. Verifica superposiciones de horario.
6. **Infraccion:** Multas aplicadas a una Unidad.
7. **PagoPendiente:** Deudas (expensas u otros) de una Unidad. Gestiona estados de PENDIENTE, PAGADO y VENCIDO.

*(Nota: Se emplea Soft Delete lógico en las entidades de ciclo operativo y financiero: `Consorcio`, `Administracion`, `Usuario`, `UnidadFuncional`, `Amenity`, `Infraccion`, `ReservaAmenity`, `PagoPendiente`, `LiquidacionMensual` y `LiquidacionUnidad`. Estas entidades no se eliminan físicamente de la base de datos.)*

## Cómo Ejecutar el Proyecto

### 1. Configuración de Base de Datos y Variables de Entorno
Crea una base de datos en tu servidor MySQL local:
```sql
CREATE DATABASE consorcio_db;
```
La aplicación se configura a través de variables de entorno. Puedes configurarlas en tu IDE, crear un archivo `.env` o definirlas a nivel de sistema operativo. Las variables soportadas son:

- `DB_URL` (Por defecto: `jdbc:mysql://localhost:3306/consorcio_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`)
- `DB_USERNAME` (Por defecto: `root`)
- `DB_PASSWORD` (Por defecto: vacío)
- `JPA_DDL_AUTO` (Por defecto: `update`)
- `JWT_SECRET` (Por defecto: un secreto largo pre-generado para entornos locales)
- `JWT_EXPIRATION` (Por defecto: `86400000` -> 24h)

### 2. Levantar la Aplicación
Desde la raíz del proyecto, ejecuta:
```bash
mvn spring-boot:run
```
Hibernate generará automáticamente las tablas la primera vez (estrategia `update`).

### 3. Autenticación y Pruebas
1. Ve a la documentación de **Swagger**: `http://localhost:8080/swagger-ui.html`.
2. Como la BD estará vacía, debes crear un usuario administrador y un consorcio directamente en BD para iniciar, o bien, temporalmente deshabilitar la seguridad en el endpoint `POST /api/v1/users` y `POST /api/v1/consorcios` para crear el primero.
3. Haz `POST` a `/api/v1/auth/login` con tus credenciales.
4. Copia el token JWT de la respuesta.
5. En Swagger, haz clic en el botón **"Authorize"** y pega tu token.

## Endpoints Principales
Todos los endpoints inician con `/api/v1`. El ID del Consorcio se obtiene implícitamente del Usuario autenticado para garantizar el aislamiento multi-tenant.

- **Auth:** `POST /api/v1/auth/login`
- **Consorcios:** `GET, POST, PUT, DELETE /api/v1/consorcios`
- **Users:** `GET, POST, PUT, DELETE /api/v1/users`
- **Unidades Funcionales:** `GET, POST, PUT, DELETE /api/v1/units`, `PATCH /api/v1/units/{id}/assign-owner/{userId}`, `PATCH /api/v1/units/{id}/assign-tenant/{userId}`
- **Amenities:** `GET, POST, PUT, DELETE /api/v1/amenities`
- **Reservations:** `GET, POST /api/v1/reservations`, `PATCH /api/v1/reservations/{id}/confirm`, `PATCH /api/v1/reservations/{id}/cancel`
- **Infractions:** `GET, POST /api/v1/infractions`, `PATCH /api/v1/infractions/{id}/status`
- **Payments:** `GET, POST /api/v1/payments`, `PATCH /api/v1/payments/{id}/pay`


## Convenciones de Diseño

### Política de Servicios
La capa de servicios sigue una política uniforme de **interfaz + implementación**:
- Contrato: `*Service`
- Implementación concreta: `*ServiceImpl`

### Convención de Naming (Dominio/API)
Se adopta una convención explícita para evitar ambigüedad:
- **Dominio interno (entidades, DTOs, servicios):** español de negocio (`Consorcio`, `UnidadFuncional`, `PagoPendiente`, etc.).
- **API pública (paths REST):** inglés consistente y estable (`/users`, `/units`, `/payments`, etc.).

Esta combinación es intencional y forma parte del estándar del proyecto.

## Pruebas Unitarias
El proyecto cuenta con testing unitario de la capa de Servicios empleando Mockito para aislar las dependencias (Repositorios).

Para correr los tests:
```bash
mvn test
```
