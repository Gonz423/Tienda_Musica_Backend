# Tienda Música — Backend Monorepo (Microservicios)

Backend para **Sonido Vivo**, desarrollado para la Evaluación Parcial 3 de **DSY1104 Desarrollo Fullstack II**.

---

## 👥 Integrantes

| Integrante | Rama | Microservicios asignados |
|---|---|---|
| **Gonzalo** (@Gonz423) | `gonzalo` | `auth-service` · `producto-service` |
| **Itsa** | `itsa` | `usuario-service` |
| **Damián** | `damian` | `pedido-service` · `metrica-service` |

---

## 🗂️ Estructura del Monorepo

```
Tienda_Musica_Backend/
├── auth-service/        ← Puerto 8081 · DB: tienda_musica_auth       (Gonzalo ✅)
├── usuario-service/     ← Puerto 8082 · DB: tienda_musica_usuarios    (Itsa 🔧)
├── producto-service/    ← Puerto 8083 · DB: tienda_musica_productos   (Gonzalo ✅)
├── pedido-service/      ← Puerto 8084 · DB: tienda_musica_pedidos     (Damián ✅)
└── metrica-service/     ← Puerto 8085 · DB: tienda_musica_metricas    (Damián ✅)
```

---

## 🚀 Ejecución de Microservicios

Para iniciar todos los microservicios y el Frontend simultáneamente en ventanas independientes:
- **Doble clic en:** `iniciar_servicios.bat`
- **O por PowerShell:** `.\iniciar_servicios.ps1`

---

## 🌿 Estrategia de Ramas

- `main` — Integración final de producción
- `gonzalo` — Rama de Gonzalo (auth-service + producto-service)
- `itsa` — Rama de Itsa (usuario-service)
- `damian` — Rama de Damián (pedido-service + metrica-service)

---

## 🗺️ Hoja de Ruta (Issues en GitHub)

```
[Fase 1: Fundaciones]
  ├── Paso 01: Spring Security & JWT (Gonzalo) ✅
  ├── Paso 02: Modelo Usuarios y Roles (Itsa) 🔧
  └── Paso 03: Catálogo Productos CRUD (Gonzalo) ✅

[Fase 2: Lógica de Negocio]
  ├── Paso 04: Inventario y Stock Crítico (Itsa) 🔧
  ├── Paso 05: Pedidos, Checkout transaccional (Damián) 🔧
  ├── Paso 06: Máquina de estados y KPIs (Damián) 🔧
  └── Paso 07: Auditoría e Historial de Usuarios (Itsa) 🔧

[Fase 3: Pruebas y Swagger]
  ├── Paso 08: Tests Auth & Productos + Swagger (Gonzalo) ✅
  └── Paso 09: Tests Pedidos + Swagger (Damián) 🔧

[Fase 4: Integración Frontend ↔ Backend]
  ├── Paso 10: React Auth, Sesión F5, Catálogo (Gonzalo) ✅
  ├── Paso 11: React Usuarios, Auditoría, Bodega (Itsa) 🔧
  └── Paso 12: React Checkout, Pedidos, KPIs (Damián) 🔧

[Fase 5: Documentación]
  ├── Paso 13: Manual de Usuario + Doc Integración (Equipo)
  └── Paso 14: Presentación y Defensa (Equipo)
```

---

## 🔐 Roles y Permisos

| Rol | Acceso |
|---|---|
| `ADMIN` | Todo el sistema |
| `VENDEDOR` | Pedidos, stock, catálogo |
| `CLIENTE` | Catálogo público, sus pedidos |

---

## 🛠️ Stack Tecnológico

- **Java 17** · Spring Boot 4.1.2
- **Spring Data JPA** (Hibernate) · MySQL
- **Spring Security** + JWT (JJWT 0.12.6)
- **Springdoc OpenAPI / Swagger** (2.8.13)
- **Maven**

---

## ⚙️ Variables de Entorno (opcional)

| Variable | Default |
|---|---|
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(vacío)* |
| `JWT_SECRET` | `iuwyeriuwyriweiw123456789012345678901234567890!` |
