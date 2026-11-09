# Tienda Música - Backend API REST (Spring Boot)

Backend para la tienda online de música ("Sonido Vivo"), desarrollado para la **Evaluación Parcial 3** de la asignatura **Desarrollo Fullstack II (DSY1104)**.

---

## 👥 Integrantes del Equipo
- **Gonzalo** (@Gonz423)
- **Itsa**
- **Damián**

---

## 🛠️ Stack Tecnológico
- **Java 17 / 21** & **Spring Boot 4 / Spring 6**
- **Spring Security 6** con autenticación stateless y tokens **JWT (JJWT 0.12.6)**
- **Spring Data JPA & Hibernate** con base de datos **MySQL local** (`tienda_musica_db`)
- **Swagger / OpenAPI 3** (`springdoc-openapi-starter-webmvc-ui`)
- **Frontend conectado:** React + Vite (`http://localhost:5173`)

---

## 🗺️ Hoja de Ruta de Implementación (20 Pasos Atómicos)

El proyecto se encuentra dividido en **20 tareas específicas y atómicas**, asegurando que cada miembro implemente tanto endpoints de backend como la integración de pantallas en el frontend:

| Paso | Responsable | Rama | Issue | Tarea / Endpoint / Conexión |
|---|---|---|---|---|
| **01** | **Gonzalo** | `gonzalo` | [#15](https://github.com/Gonz423/Tienda_Musica_Backend/issues/15) | Configuración de Spring Security, CORS y `JwtService` |
| **02** | **Gonzalo** | `gonzalo` | [#16](https://github.com/Gonz423/Tienda_Musica_Backend/issues/16) | Endpoints de Autenticación: `POST /api/auth/login` y `POST /api/auth/register` |
| **03** | **Gonzalo** | `gonzalo` | [#34](https://github.com/Gonz423/Tienda_Musica_Backend/issues/34) | Conexión Frontend: Login de Usuario, Almacenamiento JWT y Persistencia F5 |
| **04** | **Gonzalo** | `gonzalo` | [#17](https://github.com/Gonz423/Tienda_Musica_Backend/issues/17) | Entidad `Producto` JPA y Endpoint `GET /api/productos` con Filtros de Búsqueda |
| **05** | **Gonzalo** | `gonzalo` | [#18](https://github.com/Gonz423/Tienda_Musica_Backend/issues/18) | Conexión Frontend: Catálogo (`Catalogo.jsx`) y Detalle consumiendo API REST |
| **06** | **Gonzalo** | `gonzalo` | [#19](https://github.com/Gonz423/Tienda_Musica_Backend/issues/19) | Endpoints Administrativos CRUD: `POST`, `PUT`, `DELETE /api/productos` (Rol ADMIN) |
| **07** | **Gonzalo** | `gonzalo` | [#20](https://github.com/Gonz423/Tienda_Musica_Backend/issues/20) | Conexión Frontend: Gestión de Productos de Jefe (`ProductosJefe.jsx`) |
| **08** | **Itsa** | `itsa` | [#21](https://github.com/Gonz423/Tienda_Musica_Backend/issues/21) | Entidad `Usuario` JPA y Endpoints CRUD `/api/usuarios` con Hash BCrypt (Rol ADMIN) |
| **09** | **Itsa** | `itsa` | [#22](https://github.com/Gonz423/Tienda_Musica_Backend/issues/22) | Conexión Frontend: Administración de Usuarios y Roles (`UsuariosJefe.jsx`) |
| **10** | **Itsa** | `itsa` | [#23](https://github.com/Gonz423/Tienda_Musica_Backend/issues/23) | Endpoint `PATCH /api/productos/{id}/stock` y Lógica de Stock Crítico |
| **11** | **Itsa** | `itsa` | [#24](https://github.com/Gonz423/Tienda_Musica_Backend/issues/24) | Conexión Frontend: Control de Stock en Bodega (`StockEmpleado.jsx`) |
| **12** | **Itsa** | `itsa` | [#25](https://github.com/Gonz423/Tienda_Musica_Backend/issues/25) | Módulo de Auditoría de Acciones y Notas Administrativas de Usuario |
| **13** | **Damián** | `damian` | [#26](https://github.com/Gonz423/Tienda_Musica_Backend/issues/26) | Entidades `Pedido`/`DetallePedido` y Endpoint Transaccional `POST /api/pedidos` |
| **14** | **Damián** | `damian` | [#27](https://github.com/Gonz423/Tienda_Musica_Backend/issues/27) | Conexión Frontend: Checkout del Carrito de Compras hacia la API de Pedidos |
| **15** | **Damián** | `damian` | [#28](https://github.com/Gonz423/Tienda_Musica_Backend/issues/28) | Endpoints `GET /api/pedidos` y `PATCH /api/pedidos/{id}/estado` (Operación Empleado) |
| **16** | **Damián** | `damian` | [#29](https://github.com/Gonz423/Tienda_Musica_Backend/issues/29) | Conexión Frontend: Panel de Pedidos de Empleado (`PedidosEmpleado.jsx`) |
| **17** | **Damián** | `damian` | [#30](https://github.com/Gonz423/Tienda_Musica_Backend/issues/30) | Endpoint `GET /api/metricas/kpis` y Conexión Frontend en `MetricasJefe.jsx` |
| **18** | **Equipo** | `main` | [#31](https://github.com/Gonz423/Tienda_Musica_Backend/issues/31) | Configuración Global de Swagger OpenAPI con Soporte Bearer JWT (`OpenApiConfig`) |
| **19** | **Equipo** | `main` | [#32](https://github.com/Gonz423/Tienda_Musica_Backend/issues/32) | Suite de Pruebas Unitarias e Integración Backend con MockMvc |
| **20** | **Equipo** | `main` | [#33](https://github.com/Gonz423/Tienda_Musica_Backend/issues/33) | Documentación Oficial: Manual de Usuario (Anexo 4 con bordes 1px), ERS y APIs |

---

## 🚀 Puesta en Marcha Local

1. Asegúrate de tener **MySQL** corriendo en el puerto `3306`.
2. Clona el repositorio:
   ```bash
   git clone https://github.com/Gonz423/Tienda_Musica_Backend.git
   cd Tienda_Musica_Backend
   ```
3. Ejecuta la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Accede a la documentación interactiva Swagger:
   ```
   http://localhost:8080/swagger-ui.html
   ```
