# Virtual Karate Dojo API REST con Spring Boot y Kotlin

Swagger (Local) -> http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

## Variables de entorno para pruebas

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
SPRING_PROFILES_ACTIVE=[PERFIL ACTIVO local|test|dev|pre|pro]
MONGODB_URL=[URL PARA DB DE LOCAL/DEV/PRE/PRO]
MONGODB_TEST_URL=[URL PARA DB DE PRUEBAS]
```

Con esto ya puedes ejecutar la aplicación con el perfil asignado apuntando a la base de datos correspondiente.