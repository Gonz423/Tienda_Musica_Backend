# Tienda Música - Backend API REST (Spring Boot)

Backend robusto para la tienda online de música ("Sonido Vivo"), desarrollado para la **Evaluación Parcial 3** de la asignatura **Desarrollo Fullstack II (DSY1104)**.

---

## 👥 Integrantes del Equipo
- **Gonzalo** (@Gonz423)
- **Itsa**
- **Damián**

---

## 🛠️ Stack Tecnológico
- **Java 17**
- **Spring Boot 4.x / 3.x**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** (Base de Datos Relacional)
- **Spring Security & JWT** (Autenticación basada en tokens y control de accesos)
- **Springdoc OpenAPI / Swagger** (Documentación interactiva de endpoints)
- **Maven** (Gestión de dependencias)

---

## 🏛️ Arquitectura del Sistema
El proyecto sigue una arquitectura en capas limpia y desacoplada:
1. **Controllers (`tienda_musica.example.backend.controller`)**: Endpoints REST con validaciones y Swagger.
2. **Services (`tienda_musica.example.backend.service`)**: Reglas y lógica de negocio.
3. **Repositories (`tienda_musica.example.backend.repository`)**: Acceso a datos con Spring Data JPA.
4. **Entities / Models (`tienda_musica.example.backend.model`)**: Modelos relacionales de base de datos.
5. **Security / JWT (`tienda_musica.example.backend.security`)**: Filtros, generación de tokens y roles.
6. **DTOs (`tienda_musica.example.backend.dto`)**: Objetos de transferencia de datos para solicitudes y respuestas.

---

## 🔐 Roles y Permisos del Sistema
- **ADMIN / JEFE DE TIENDA**: Acceso total al sistema (gestión de productos, auditoría de usuarios, reportes y KPIs).
- **VENDEDOR / TRABAJADOR**: Gestión operativa de pedidos (preparación y despacho) y control de stock en bodega.
- **CLIENTE**: Consulta del catálogo público de productos, compras y órdenes personales.

---

## 🌿 Estrategia de Ramas
- `main`: Rama principal de producción integrada.
- `gonzalo`: Rama de trabajo para autenticación JWT, seguridad y CRUD de Productos.
- `itsa`: Rama de trabajo para gestión de Usuarios, roles y control de inventario/stock crítico.
- `damian`: Rama de trabajo para gestión de Pedidos, cálculo de KPIs de ventas y documentación Swagger.
