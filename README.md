# 🏢 Franchise Manager API

API reactiva para gestionar franquicias, sucursales y productos con arquitectura hexagonal implementada en Spring Boot WebFlux.
Proyecto Base Implementando Clean Architecture

---

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Scaffolding of Clean Architecture](https://bancolombia.github.io/scaffold-clean-architecture/docs/tasks/generate-model/)

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

# 🐳 Docker 

## 🏗️ Multi-stage Build
El uso de *multi-stage builds* permite optimizar el tamaño de la imagen final y mejorar la seguridad al no incluir herramientas de compilación en el entorno de ejecución.

### 🔨 Stage 1 – Build
* **Imagen base:** `eclipse-temurin:25-jdk-alpine`
* **Comando de compilación:**
    ```bash
    ./gradlew bootJar -x test -x validateStructure --no-daemon
    ```
    *Genera el archivo `.jar` necesario para la ejecución.*

### 🚀 Stage 2 – Runtime
* **Imagen base:** `eclipse-temurin:25-jre-alpine`
* **Contenido:** Solo contiene el archivo JAR final extraído del Stage 1.

### ✨ Beneficios
* **Imagen más ligera:** Menor consumo de almacenamiento y transferencia rápida.
* **Menor superficie de ataque:** Se eliminan compiladores y dependencias innecesarias.
* **Separación clara:** Distinción total entre el proceso de construcción y el de ejecución.


---

## 💻 Tecnologías

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 4.0.2 |
| **Reactivo** | Project Reactor | Latest |
| **Base de Datos** | MongoDB | 8+ |
| **Build** | Gradle | 9.3.0 |
| **Java** | Eclipse Temurin | 25 |
| **Testing** | JUnit 5, Mockito | Latest |
| **Logging** | SLF4J + Log4j2 | Latest |
| **Container** | Docker | Latest |
| **Code Coverage** | JaCoCo | 0.8.14 |

---

## 🚀 Instalación y Testing



### Testing Manual

**Con Postman:**
1. Importar [Franchise_Manager_API.postman_collection.json](https://.postman.co/workspace/My-Workspace~d954e3ec-6e2b-49a2-80c8-d3613b1d26b0/collection/27253828-ee0d61fd-3fa4-4d88-8bce-38821a6b2ce0?action=share&creator=27253828)
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
https://seti-franchise-manager.onrender.com/


### 🚀 Postman Collection

Para probar la API, puedes copiar el siguiente JSON e importarlo directamente en Postman (**Import > Raw text**). Todos los endpoints han sido configurados para apuntar al entorno de producción en Render.
**⚠️ Importante:**  
La primera petición a la API puede tardar un poco más de lo normal debido al servicio en la nube. Después de eso, las respuestas se procesan a velocidad normal.

<details>
<summary><b>Click para expandir la Colección de Postman</b></summary>

```json
{
  "info": {
    "_postman_id": "9f5aceea-3cf9-4af6-b79b-f7c8bcbd578d",
    "name": "Prueba_SETI_Ropa_Production",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Franquicia",
      "item": [
        {
          "name": "Listar todas la informacion",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises"
              ]
            }
          }
        },
        {
          "name": "Buscar franquicia por ID",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises/f001",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises",
                "f001"
              ]
            }
          }
        },
        {
          "name": "Exponer endpoint para agregar una nueva franquicia.",
          "request": {
            "auth": {
              "type": "noauth"
            },
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\r\n  \"id\": \"f001\",\r\n  \"name\": \"Franquicia de Ropa Central\",\r\n  \"branches\": [\r\n    {\r\n      \"name\": \"Sucursal Norte\",\r\n      \"products\": [\r\n        {\r\n          \"name\": \"Camiseta\",\r\n          \"stock\": 150\r\n        },\r\n        {\r\n          \"name\": \"Pantalon\",\r\n          \"stock\": 103\r\n        }\r\n      ]\r\n    },\r\n    {\r\n      \"name\": \"Sucursal Sur\",\r\n      \"products\": [\r\n        {\r\n          \"name\": \"Chaqueta\",\r\n          \"stock\": 80\r\n        },\r\n        {\r\n          \"name\": \"Gorra\",\r\n          \"stock\": 210\r\n        }\r\n      ]\r\n    }\r\n  ]\r\n}\r\n",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises"
              ]
            }
          }
        },
        {
          "name": "Exponer endpoint para agregar una nueva sucursal a una franquicia.",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\r\n      \"name\": \"Sucursal Este\",\r\n      \"products\": [\r\n        {\r\n          \"name\": \"Camiseta Pro\",\r\n          \"stock\": 155\r\n        },\r\n        {\r\n          \"name\": \"Pantalon Casual\",\r\n          \"stock\": 111\r\n        }\r\n      ]\r\n    }",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises/f001/branches",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises",
                "f001",
                "branches"
              ]
            }
          }
        },
        {
          "name": "Exponer endpoint para agregar un nuevo producto a una sucursal.",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\r\n    \"name\": \"Medias\",\r\n    \"stock\": 20\r\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises/f001/branches/Sucursal Norte/products",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises",
                "f001",
                "branches",
                "Sucursal Norte",
                "products"
              ]
            }
          }
        },
        {
          "name": "Exponer endpoint para eliminar un nuevo producto a una sucursal.",
          "request": {
            "method": "DELETE",
            "header": [],
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises/f001/branches/Sucursal Norte/products/Medias",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises",
                "f001",
                "branches",
                "Sucursal Norte",
                "products",
                "Medias"
              ]
            }
          }
        },
        {
          "name": "Exponer endpoint para modificar el stock de un producto.",
          "request": {
            "method": "PATCH",
            "header": [],
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises/f001/branches/Sucursal Norte/products/Medias/stock?newStock=22",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises",
                "f001",
                "branches",
                "Sucursal Norte",
                "products",
                "Medias",
                "stock"
              ],
              "query": [
                {
                  "key": "newStock",
                  "value": "22"
                }
              ]
            }
          }
        },
        {
          "name": "Exponer endpoint que permita mostrar cual es el producto que más stock tiene",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises/f001/products/top",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises",
                "f001",
                "products",
                "top"
              ]
            }
          }
        },
        {
          "name": "endpoint que permita actualizar el nombre de una franquicia.",
          "request": {
            "method": "PUT",
            "header": [],
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises/f001/name/Franquicia Ropa 1",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises",
                "f001",
                "name",
                "Franquicia Ropa 1"
              ]
            }
          }
        },
        {
          "name": "actualizar el nombre de una sucursal.",
          "request": {
            "method": "PUT",
            "header": [],
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises/f001/branch/Sucursal Norte 2/name/Sucursal Norte",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises",
                "f001",
                "branch",
                "Sucursal Norte 2",
                "name",
                "Sucursal Norte"
              ]
            }
          }
        },
        {
          "name": "actualizar el nombre de un producto.",
          "request": {
            "method": "PUT",
            "header": [],
            "url": {
              "raw": "https://seti-franchise-manager.onrender.com/api-v1/franchises/f001/branch/Sucursal Norte/product/Camiseta/name/Camiseta Deportiva",
              "protocol": "https",
              "host": [
                "seti-franchise-manager",
                "onrender",
                "com"
              ],
              "path": [
                "api-v1",
                "franchises",
                "f001",
                "branch",
                "Sucursal Norte",
                "product",
                "Camiseta",
                "name",
                "Camiseta Deportiva"
              ]
            }
          }
        }
      ]
    }
  ]
}
```

</details>

---

## 📡 Endpoints API
 Testing Manual con Postman

Incluye todos los endpoints pre-configurados para testing local.
### Base URL
```
http://localhost:8080/api-v1
```

**Colección Postman:** [Franchise_Manager_API.postman_collection.json](https://github.com/miguellara5/SETI_Franchise_Manager/blob/main/Prueba_SETI_pruebas__local.postman_collection.json)

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

**Cobertura alcanzada:** >80%
<img width="1148" height="479" alt="image" src="https://github.com/user-attachments/assets/1869ed7c-11d1-4af1-a161-7bfec78db5e9" />



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

- Java 25 (Eclipse Temurin recomendado)
- Gradle 9.3.0+
- Docker (opcional, para contenerización)
- Acceso a MongoDB (local o Atlas)
- Git

## Herramientas recomendadas

- IDE: IntelliJ IDEA, VS Code, Eclipse
- Cliente REST: Postman, Insomnia
- MongoDB Compass
- JUnit 5, Mockito


---

## 🚀 Cómo usar en local

### 1️⃣ Clonar el repositorio

Copia el repositorio en tu máquina local y accede a la carpeta del proyecto:

```bash
git clone https://github.com/miguellara5/SETI_Franchise_Manager.git
````
````
cd SETI_Franchise_Manager
````
##⚙️ Configuración del Proyecto

El proyecto utiliza perfiles de Spring Boot para gestionar los entornos.

📌 Perfil activo por defecto

En el archivo application.yml, el perfil activo configurado es:
````bash
spring:
  profiles:
    active: "dev"
````
##🗄️ Configuración de Base de Datos (MongoDB)
📄 Archivo: application.yml

Configuración general de la aplicación y endpoints de monitoreo:
````bash
spring:
  application:
    name: "franchise-api"
  data:
    mongodb:
      uri: "mongodb://localhost:27017/test"

management:
  endpoints:
    web:
      exposure:
        include: "health,prometheus"
  endpoint:
    health:
      probes:
        enabled: true

cors:
  allowed-origins: "http://localhost:4200,http://localhost:8080"
````
##📄 Archivo: application-dev.yml

Configuración específica para el entorno local (perfil dev):
````bash
server:
  port: 8080

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/first_db

logging:
  level:
    org.springframework.web.reactive.function.server: DEBUG
````
