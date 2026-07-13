# Ecosistema de Microservicios — Tienda de Computadores

## Descripción del proyecto

Sistema distribuido basado en microservicios para una tienda de productos de cómputo (notebooks, componentes y accesorios). Cubre el flujo completo de un e-commerce: catálogo de productos, control de inventario, generación de pedidos, checkout con validación de stock y precio real, autenticación de usuarios con JWT, reseñas de productos, reportes, despacho (delivery) y un catálogo especializado de equipos de cómputo.

La arquitectura sigue el patrón **Controller–Service–Repository (CSR)** en cada microservicio, con **Eureka** como servidor de descubrimiento y un **API Gateway** como punto de entrada único hacia todos los servicios.

## Estudiantes

- Benja A.
- Julio N.
- Cristobal C.

## Microservicios implementados

| Servicio | Nombre registrado en Eureka | Puerto | Responsabilidad |
|---|---|---|---|
| `productos` | `productos` | 8082 | Catálogo de productos, precio real, descuento de stock |
| `ms-inventario` | `ms-inventario` | 8084 | Control de stock, alertas de stock bajo mínimo |
| `ms-pedidos` | `ms-pedidos` | 8083 | Creación y consulta de pedidos, detalle de items |
| `ms-checkout-fix` | `ms-checkout` | 8085 | Orquesta la compra: valida stock/precio real, crea el pedido y compensa el stock si algo falla |
| `ms-user-fixed` | `ms-user` | 9090 | Registro/login, emisión y validación de JWT, roles USER/ADMIN |
| `ms-review` | `ms-review` | 8087 | Reseñas de productos |
| `ms-report` | `ms-report` | 8086 | Reportes |
| `ms-delivery` | `ms-delivery` | 8089 | Gestión de despacho |
| `ms-computer` | `ms-computer` | 8090 | Catálogo específico de computadores |
| `eureka-server` | `eureka-server` | 8761 | Servidor de descubrimiento de servicios |
| `api-gateway` | `api-gateway` | 6767 | Punto de entrada único, enrutamiento por descubrimiento dinámico |

> **Nota:** `ms-notification` está referenciado en `docker-compose.yml` pero su implementación aún no está incluida en este repositorio.

## Rutas principales del Gateway

El Gateway usa **discovery locator** de Spring Cloud Gateway (`spring.cloud.gateway.server.webflux.discovery.locator.enabled=true`), por lo que enruta automáticamente hacia cada servicio registrado en Eureka usando su nombre en minúsculas como prefijo:

```
http://localhost:6767/productos/**
http://localhost:6767/ms-inventario/**
http://localhost:6767/ms-pedidos/**
http://localhost:6767/ms-checkout/**
http://localhost:6767/ms-user/**
http://localhost:6767/ms-review/**
http://localhost:6767/ms-report/**
http://localhost:6767/ms-delivery/**
http://localhost:6767/ms-computer/**
```

Ejemplo real: `GET http://localhost:6767/productos/api/v1/productos` enruta internamente a `productos` → `GET /api/v1/productos`.

## Documentación Swagger / OpenAPI

Cada microservicio expone su propia documentación en:

```
http://localhost:<puerto>/swagger-ui.html
```

Por ejemplo:
- Productos: http://localhost:8082/swagger-ui.html
- Pedidos: http://localhost:8083/swagger-ui.html
- Checkout: http://localhost:8085/swagger-ui.html
- Usuarios: http://localhost:9090/swagger-ui.html

(reemplaza el puerto según la tabla de microservicios de arriba)

## Ejecución local (sin Docker)

Requisitos: JDK 17+, Maven, MySQL corriendo localmente.

1. Levantar primero **eureka-server**:
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```
2. Levantar el resto de los microservicios (cada uno en su propia terminal, cualquier orden):
   ```bash
   cd productos && mvn spring-boot:run
   cd ms-inventario && mvn spring-boot:run
   cd ms-pedidos && mvn spring-boot:run
   cd ms-checkout-fix && mvn spring-boot:run
   cd ms-user-fixed && mvn spring-boot:run
   cd ms-review && mvn spring-boot:run
   cd ms-report && mvn spring-boot:run
   cd ms-delivery && mvn spring-boot:run
   cd ms-computer && mvn spring-boot:run
   ```
3. Levantar **api-gateway** al final, una vez que los demás estén registrados en Eureka:
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```
4. Verificar registro de servicios en el dashboard de Eureka: http://localhost:8761

## Ejecución con Docker

Requisitos: Docker y Docker Compose.

```bash
docker-compose up --build
```

Esto levanta el contenedor de MySQL, `eureka-server`, `api-gateway` y todos los microservicios en la red interna de Docker. Los servicios se resuelven entre sí por nombre de contenedor (no `localhost`).

Para bajar el entorno:
```bash
docker-compose down
```

Para bajarlo eliminando también los datos de MySQL:
```bash
docker-compose down -v
```

## Pruebas unitarias y cobertura

Cada microservicio incluye pruebas unitarias con JUnit 5 y Mockito, estructuradas en formato Given-When-Then. Para ejecutarlas y generar el reporte de cobertura (JaCoCo) de un servicio:

```bash
cd <nombre-del-servicio>
mvn test
```

El reporte HTML de cobertura queda disponible en:
```
target/site/jacoco/index.html
```

> Ejecuta este comando en cada servicio antes de la defensa para confirmar el porcentaje real de cobertura alcanzado.
