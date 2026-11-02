# Tienda Música - Backend API REST (Spring Boot)

Backend para la tienda online de música ("Sonido Vivo"), desarrollado para la **Evaluación Parcial 3** de la asignatura **Desarrollo Fullstack II (DSY1104)**.

---

## 👥 Integrantes del Equipo
- **Gonzalo** (@Gonz423)
- **Itsa**
- **Damián**

---

## 🗺️ Hoja de Ruta de Implementación (Orden Secuencial de Tareas)

El desarrollo está organizado en **5 Fases secuenciales** con 14 pasos numerados para garantizar que el proyecto se construya de forma ordenada y sin bloqueos de dependencias:

```
[Fase 1: Fundaciones y Modelos Base]
  ├── Paso 01: Seguridad Spring Security & JWT (Gonzalo)
  ├── Paso 02: Modelo de Usuarios y Roles (Itsa)
  └── Paso 03: Modelo de Productos y Catálogo (Gonzalo)
          │
[Fase 2: Lógica de Negocio y Transacciones]
  ├── Paso 04: Control de Inventario y Stock Crítico (Itsa)
  ├── Paso 05: Pedidos, Órdenes y Checkout Transaccional (Damián)
  ├── Paso 06: Flujo de Estados de Pedidos y KPIs (Damián)
  └── Paso 07: Auditoría e Historial de Usuarios (Itsa)
          │
[Fase 3: Pruebas Backend y Swagger Global]
  ├── Paso 08: Pruebas Unitarias/Integración Auth y Productos (Gonzalo)
  └── Paso 09: Pruebas Integración Pedidos y Swagger OpenAPI (Damián)
          │
[Fase 4: Integración Frontend ↔ Backend (React)]
  ├── Paso 10: Integración Frontend Auth, Sesión F5 y Catálogo (Gonzalo)
  ├── Paso 11: Integración Frontend Usuarios, Auditoría y Bodega (Itsa)
  └── Paso 12: Integración Frontend Checkout, Pedidos y Métricas (Damián)
          │
[Fase 5: Documentación y Defensa Individual]
  ├── Paso 13: Manual de Usuario (Anexo 4), ERS y Doc de Integración (Equipo)
  └── Paso 14: Diapositivas, Demo en Vivo y Defensa Técnica 60% (Equipo)
```

---

### 📋 Detalle de Tareas Paso a Paso (Issues en GitHub)

| Paso | Fase | Issue | Responsable | Rama | Tarea | Dependencia previa |
|---|---|---|---|---|---|---|
| **Paso 01** | Fase 1 | [#1](https://github.com/Gonz423/Tienda_Musica_Backend/issues/1) | **Gonzalo** | `gonzalo` | Configuración Spring Security, utilidades JWT y endpoints Auth | *Inicio* |
| **Paso 02** | Fase 1 | [#4](https://github.com/Gonz423/Tienda_Musica_Backend/issues/4) | **Itsa** | `itsa` | Entidad `Usuario`, roles (`ADMIN`, `VENDEDOR`, `CLIENTE`) y CRUD | Paso 01 |
| **Paso 03** | Fase 1 | [#2](https://github.com/Gonz423/Tienda_Musica_Backend/issues/2) | **Gonzalo** | `gonzalo` | Entidad `Producto`, catálogo de instrumentos y CRUD REST | Paso 01 |
| **Paso 04** | Fase 2 | [#5](https://github.com/Gonz423/Tienda_Musica_Backend/issues/5) | **Itsa** | `itsa` | Lógica de inventario, ajuste rápido y alerta stock crítico (<= 3) | Paso 03 |
| **Paso 05** | Fase 2 | [#7](https://github.com/Gonz423/Tienda_Musica_Backend/issues/7) | **Damián** | `damian` | Entidades `Pedido`/`DetallePedido`, checkout y descuento transaccional | Paso 02, 03 |
| **Paso 06** | Fase 2 | [#8](https://github.com/Gonz423/Tienda_Musica_Backend/issues/8) | **Damián** | `damian` | Máquina de estados (`Pendiente` ➔ `Preparación` ➔ `Despachado`) y KPIs | Paso 05 |
| **Paso 07** | Fase 2 | [#6](https://github.com/Gonz423/Tienda_Musica_Backend/issues/6) | **Itsa** | `itsa` | Entidad `HistorialUsuario`, bitácora de auditoría y notas | Paso 02 |
| **Paso 08** | Fase 3 | [#3](https://github.com/Gonz423/Tienda_Musica_Backend/issues/3) | **Gonzalo** | `gonzalo` | Pruebas unitarias Mockito y de integración MockMvc (Auth y Productos) | Paso 01, 03 |
| **Paso 09** | Fase 3 | [#9](https://github.com/Gonz423/Tienda_Musica_Backend/issues/9) | **Damián** | `damian` | Pruebas de integración de Pedidos y Swagger OpenAPI interactivo | Paso 05, 06 |
| **Paso 10** | Fase 4 | [#10](https://github.com/Gonz423/Tienda_Musica_Backend/issues/10) | **Gonzalo** | `gonzalo` | Integración React: Interceptor JWT, persistencia sesión F5 y Catálogo | Paso 01, 08 |
| **Paso 11** | Fase 4 | [#11](https://github.com/Gonz423/Tienda_Musica_Backend/issues/11) | **Itsa** | `itsa` | Integración React: Directorio usuarios, auditoría y botones bodega | Paso 02, 04, 07 |
| **Paso 12** | Fase 4 | [#12](https://github.com/Gonz423/Tienda_Musica_Backend/issues/12) | **Damián** | `damian` | Integración React: Checkout carrito, tabla pedidos y tarjetas KPIs | Paso 05, 06 |
| **Paso 13** | Fase 5 | [#13](https://github.com/Gonz423/Tienda_Musica_Backend/issues/13) | **Equipo** | `main` | Manual de Usuario (capturas 1px Anexo 4), ERS y Doc de Integración | Paso 10, 11, 12 |
| **Paso 14** | Fase 5 | [#14](https://github.com/Gonz423/Tienda_Musica_Backend/issues/14) | **Equipo** | `main` | Preparación de Diapositivas, Demo en Vivo y Defensa Individual (60%) | Todos |

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

## 🔐 Roles y Permisos del Sistema
- **ADMIN / JEFE DE TIENDA**: Acceso total al sistema (gestión de productos, auditoría de usuarios, reportes y KPIs).
- **VENDEDOR / TRABAJADOR**: Gestión operativa de pedidos (preparación y despacho) y control de stock en bodega.
- **CLIENTE**: Consulta del catálogo público de productos, compras y órdenes personales.

---

## 🌿 Estrategia de Ramas
- `main`: Rama principal de producción integrada.
- `gonzalo`: Rama de trabajo para Gonzalo.
- `itsa`: Rama de trabajo para Itsa.
- `damian`: Rama de trabajo para Damián.
