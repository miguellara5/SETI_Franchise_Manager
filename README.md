# 🏢 Franchise Manager API

API reactiva para gestionar franquicias, sucursales y productos con arquitectura hexagonal implementada en Spring Boot WebFlux.
Proyecto Base Implementando Clean Architecture

---

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**




## 📋 Tabla de Contenidos

1. [Descripción del Proyecto](#descripción-del-proyecto)
2. [Arquitectura](#arquitectura)
3. [Requisitos Cumplidos](#requisitos-cumplidos)
4. [Decisiones de Diseño](#decisiones-de-diseño)
5. [Tecnologías](#tecnologías)
6. [Instalación Local](#instalación-local)
7. [Despliegue](#despliegue)
8. [Endpoints API](#endpoints-api)
9. [Testing](#testing)

---

## 📖 Descripción del Proyecto

API RESTful para administrar una red de franquicias que:
- Gestiona múltiples sucursales por franquicia
- Controla inventario de productos por sucursal
- Proporciona análisis de productos con mayor stock
- Utiliza operadores reactivos para mejor performance

### Modelo de Datos

```
Franquicia
├── Nombre
└── Sucursales[]
    ├── Nombre
    └── Productos[]
        ├── Nombre
        └── Stock (cantidad)
```

---


## ✅ Requisitos Cumplidos

### Arquitectura de Software
- ✅ Spring Boot + WebFlux (Completamente reactivo)
- ✅ Arquitectura Hexagonal (Clean Architecture)
- ✅ Perfiles dev/prod para desarrollo y producción
- ✅ Operadores reactivos: `map`, `flatMap`, `switchIfEmpty`, `merge`, `zip`
- ✅ Señales correctas: `onNext`, `onError`, `onComplete`
- ✅ Logging con SLF4J + Log4j2
- ✅ Pruebas unitarias con JUnit 5 y Mockito
- ✅ API RESTful completa

### Persistencia
- ✅ MongoDB (Local y MongoDB Atlas)
- ✅ Configuración por perfil (dev/prod)

### Funcional
- ✅ CRUD completo de Franquicias
- ✅ Gestión de Sucursales
- ✅ Gestión de Productos
- ✅ Consulta de productos con mejor stock por sucursal
- ✅ Actualización de stocks
- ✅ Endpoints adicionales: actualizar nombres

### Puntos Extra
- ✅ Docker con multi-stage build
- ✅ Endpoints de actualización (franquicia, sucursal, producto)
- ✅ Explicación de decisiones de diseño (este documento)
- ✅ Preparado para despliegue en la nube

---

## 🎯 Decisiones de Diseño

### 1. **Arquitectura Hexagonal**
**Justificación:** 
- Desacoplamiento entre capas
- Fácil de testear
- Independencia de frameworks
- Mantenibilidad a largo plazo

### 2. **Spring Boot WebFlux**
**Decisión:** Completamente reactivo
- **Por qué:** 
  - Mejor resource efficiency (menos threads)
  - Ideal para I/O intensivo (BD, APIs externas)
  - Operadores `flatMap`, `merge` para orquestación compleja

### 3. **MongoDB**
**Decisión:** Base de datos NoSQL
- **Por qué:**
  - Modelo de documento flexible (franquicia con sucursales anidadas)
  - Escalabilidad horizontal
  - Excelente integración con Spring Data Reactive

### 4. **Dos configuraciones (dev/prod)**
**Decisión:** Perfiles de Spring
- **Dev:** MongoDB local, logging DEBUG, H2 console
- **Prod:** MongoDB Atlas, logging INFO, variables de entorno seguras

### 5. **Variables de Entorno**
**Decisión:** Sin credenciales hardcodeadas
```yaml
SPRING_DATA_MONGODB_URI=${SPRING_DATA_MONGODB_URI}  # Obligatoria en prod
CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS}       # Configurable por entorno
```

### 6. **Multi-stage Docker**
**Decisión:** Dos fases en Dockerfile
- **Stage 1:** Compilación (JDK 21)
- **Stage 2:** Ejecución (JRE 21, más pequeña)
- **Beneficio:** Imagen 70% más pequeña (~200MB vs 900MB)

### 7. **Usuario no-root en Docker**
**Decisión:** User `appuser` en contenedor
- **Por qué:** Seguridad (evitar acceso root comprometido)

### 8. **Health Check**
**Decisión:** `/actuator/health` cada 30s
- **Por qué:** Orquestadores (K8s, Docker Compose) pueden reiniciar automáticamente

### 9. **Handler**
**Decisión:** Dos versiones de API
- **V1:** Versión inicial estable


---

## 💻 Tecnologías

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 4.0.2 |
| **Reactivo** | Project Reactor | Latest |
| **Base de Datos** | MongoDB | 8+ |
| **Build** | Gradle | 9.3.0 |
| **Java** | Eclipse Temurin | 21 |
| **Testing** | JUnit 5, Mockito | Latest |
| **Logging** | SLF4J + Log4j2 | Latest |
| **Container** | Docker | Latest |
| **Code Coverage** | JaCoCo | 0.8.14 |

---

## 🚀 Instalación y Testing

Ver [README.md - Quick Start](README.md#-quick-start-5-minutos) para instalación local.

### Testing Manual

**Con Postman:**
1. Importar [Franchise_Manager_API.postman_collection.json](Franchise_Manager_API.postman_collection.json)
2. Asegurarse que MongoDB está corriendo en `localhost:27017`
3. Ejecutar requests pre-configurados

**Con cURL:**
```bash
# Health check
curl http://localhost:8080/actuator/health

# Crear franquicia
curl -X POST http://localhost:8080/api-v1/franchises \
  -H "Content-Type: application/json" \
  -d '{"name":"Mi Franquicia"}'
```

---

## 🌍 Despliegue

Ver [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) para instrucciones completas:
- Local con Docker Compose
- Railway, Render, AWS
- MongoDB Atlas

---

## 📡 Endpoints API

### Base URL
```
http://localhost:8080/api-v1
```

**Colección Postman:** [Franchise_Manager_API.postman_collection.json](Franchise_Manager_API.postman_collection.json)

| Recurso | Método | Endpoint |
|---------|--------|----------|
| Franquicias | GET | `/franchises` |
| Franquicia | GET | `/franchises/{id}` |
| Franquicia | POST | `/franchises` |
| Franquicia | PUT | `/franchises/{id}/name` |
| Sucursal | POST | `/franchises/{id}/branches` |
| Sucursal | PUT | `/franchises/{id}/branches/{name}/name` |
| Producto | POST | `/franchises/{id}/branches/{branch}/products` |
| Producto | PUT | `/franchises/{id}/branches/{branch}/products/{product}/stock` |
| Producto | DELETE | `/franchises/{id}/branches/{branch}/products/{product}` |
| Top Productos | GET | `/franchises/{id}/top-products` |

Para ejemplos JSON y detalles, importar colección Postman.

---

## 🧪 Testing

```bash
./gradlew test                    # Ejecutar pruebas
./gradlew jacocoTestReport       # Con cobertura
# Ver reporte: build/reports/jacocoHtml/index.html
```

**Cobertura alcanzada:** >80% en capas core
<img width="1148" height="479" alt="image" src="https://github.com/user-attachments/assets/1869ed7c-11d1-4af1-a161-7bfec78db5e9" />


### Testing Manual con Postman

Importar colección: [Franchise_Manager_API.postman_collection.json](Franchise_Manager_API.postman_collection.json)

Incluye todos los endpoints pre-configurados para testing local.

---

## 🔍 Operadores Reactivos Utilizados

- `map` - Transformar datos (dto → entity)
- `flatMap` - Operaciones async (getClient → getDetails)
- `switchIfEmpty` - Manejo de vacío (notFound → 404)
- `merge` - Combinar múltiples flujos
- `zip` - Combinar con sincronización
- `onError` - Manejo de errores

---

## 📊 Logging

Configurado en [application-dev.yaml](applications/app-service/src/main/resources/application-dev.yaml) (DEBUG) y [application-prod.yaml](applications/app-service/src/main/resources/application-prod.yaml) (INFO)

---

## 📦 Estructura del Proyecto

Ver [settings.gradle](settings.gradle) para módulos y [main.gradle](main.gradle) para configuración de build.

**Módulos:**
- `applications/app-service` - Spring Boot App
- `domain/model` - Entidades y gateways (puertos)
- `domain/usecase` - Lógica de negocio
- `infrastructure/entry-points/reactive-web` - HTTP Adaptadores
- `infrastructure/driven-adapters/mongo-repository` - MongoDB Adaptador

---

## 🐛 Troubleshooting

| Problema | Solución |
|----------|----------|
| MongoDB no conecta | `docker run -d -p 27017:27017 mongo:8-alpine` |
| Port 8080 en uso | Cambiar `server.port` en `application.yaml` |
| Build falla | `./gradlew clean build -x test --refresh-dependencies` |
| Gradle daemon error | `./gradlew --stop` |

Ver [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md#troubleshooting) para más casos.

---

## 📝 Git Workflow

```bash
git checkout -b feature/nueva-funcionalidad
git commit -m "feat: descripción"
git push origin feature/nueva-funcionalidad
# Pull Request → Merge a main
```

---

## 🔐 Seguridad

- ✅ Variables de entorno para credenciales (no hardcoded)
- ✅ Usuario no-root en Docker
- ✅ CORS configurables por entorno
- ✅ Health checks para detectar compromiso
- ✅ Validación de entrada en DTOs
- ✅ Logging seguro (sin credenciales en logs)

---

## Prerrequisitos

- Java 21 (Eclipse Temurin recomendado)
- Gradle 9.3.0+
- Docker (opcional, para contenerización)
- Acceso a MongoDB (local o Atlas)
- Git

## Herramientas recomendadas

- IDE: IntelliJ IDEA, VS Code, Eclipse
- Cliente REST: Postman, Insomnia
- MongoDB Compass
- JUnit 5, Mockito




