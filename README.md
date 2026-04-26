# report-service

Microservicio encargado de reportes operativos. Consume eventos y permite
consultar movimientos por producto y los ultimos movimientos registrados.

## Tecnologias

- Java 17
- Spring Boot 3.2.4
- Spring WebFlux
- Spring Data MongoDB Reactive
- Spring Kafka
- Reactor Kafka
- RxJava 3
- Reactor Adapter
- Spring Cloud Config
- Eureka Client
- Springdoc OpenAPI
- Maven
- JUnit 5
- JaCoCo
- Spotless
- Checkstyle

## Puerto

```text
http://localhost:8086
```

## OpenAPI

```text
http://localhost:8086/swagger-ui.html
http://localhost:8086/v3/api-docs
```

## Levantar sin Docker

Requisitos locales:

- Java 17
- Maven
- MongoDB en `localhost:27017`
- Kafka en `localhost:9092`
- Config Server opcional en `localhost:8888`
- Eureka en `localhost:8761`

```powershell
cd .\report-service
mvn spring-boot:run
```

## Levantar con Docker

Primero levantar la infraestructura:

```powershell
cd .\infra
docker compose up -d
```

Este proyecto aun no incluye `Dockerfile`. Cuando se agregue, debe usar nombres
de servicio de Docker Compose para MongoDB, Kafka, Config Server y Eureka.

## Tests

```powershell
cd .\report-service
mvn test
```

Si luego se agregan tests de integracion:

```powershell
mvn verify
```

## Formato y Checkstyle

```powershell
mvn spotless:apply
mvn checkstyle:check
```

## JaCoCo

```powershell
mvn test jacoco:report
```

Reporte:

```text
target/site/jacoco/index.html
```

## MongoDB

Base de datos:

```text
ntt_report
```

Coleccion principal:

```text
report_events
```

Consulta:

```powershell
mongosh
use ntt_report
show collections
db.report_events.find().pretty()
```

Se usa database per service logico: cada microservicio mantiene su propia base de
datos MongoDB.

