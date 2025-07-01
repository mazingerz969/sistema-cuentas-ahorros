# Backend - API de Gestión de Cuentas de Ahorros

## Descripción
API REST desarrollada con Spring Boot para la gestión de cuentas de ahorros. Esta aplicación proporciona endpoints para crear, consultar, actualizar y eliminar cuentas, así como para realizar depósitos y retiros.

## Tecnologías Utilizadas

### Core Framework
- **Spring Boot 3.2.0**: Framework principal para desarrollo de aplicaciones Java
- **Java 17**: Versión de Java utilizada
- **Maven**: Gestión de dependencias y build

### Persistencia de Datos
- **Spring Data JPA**: Abstracción para acceso a datos
- **Hibernate**: ORM (Object-Relational Mapping)
- **H2 Database**: Base de datos en memoria para desarrollo

### Validación y Documentación
- **Spring Validation**: Validación de datos de entrada
- **SpringDoc OpenAPI**: Documentación automática de la API
- **Swagger UI**: Interfaz para probar la API

### Utilidades
- **Lombok**: Reducción de código boilerplate
- **SLF4J**: Logging

## Estructura del Proyecto

```
src/main/java/com/ahorros/
├── CuentaAhorrosApplication.java    # Clase principal de Spring Boot
├── controllers/                     # Controladores REST
│   ├── CuentaController.java       # Endpoints para gestión de cuentas
│   └── TransaccionController.java  # Endpoints para transacciones
├── services/                       # Lógica de negocio
│   ├── CuentaService.java         # Servicios para cuentas
│   └── TransaccionService.java    # Servicios para transacciones
├── repositories/                   # Acceso a datos
│   ├── CuentaRepository.java      # Repositorio de cuentas
│   └── TransaccionRepository.java # Repositorio de transacciones
├── models/                        # Entidades JPA
│   ├── Cuenta.java               # Entidad Cuenta
│   └── Transaccion.java          # Entidad Transaccion
└── dto/                          # Objetos de transferencia de datos
    ├── CuentaDTO.java            # DTO para Cuenta
    └── TransaccionDTO.java       # DTO para Transaccion
```

## Configuración

### application.properties
```properties
# Servidor
server.port=8080
server.servlet.context-path=/api

# Base de datos H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# CORS
spring.web.cors.allowed-origins=http://localhost:4200
```

## Endpoints de la API

### Gestión de Cuentas

#### Crear Cuenta
- **POST** `/api/cuentas`
- **Body**: 
```json
{
  "numeroCuenta": "123456789",
  "titular": "Juan Pérez",
  "saldo": 1000.00
}
```

#### Obtener Todas las Cuentas
- **GET** `/api/cuentas`

#### Obtener Cuenta por ID
- **GET** `/api/cuentas/{id}`

#### Obtener Cuenta por Número
- **GET** `/api/cuentas/numero/{numeroCuenta}`

#### Buscar Cuentas por Titular
- **GET** `/api/cuentas/buscar?titular={nombre}`

#### Obtener Cuentas Activas
- **GET** `/api/cuentas/activas`

#### Actualizar Cuenta
- **PUT** `/api/cuentas/{id}`
- **Body**: 
```json
{
  "titular": "Juan Pérez Actualizado",
  "activa": true
}
```

#### Eliminar Cuenta
- **DELETE** `/api/cuentas/{id}`

#### Estadísticas de Cuentas
- **GET** `/api/cuentas/estadisticas`

### Gestión de Transacciones

#### Realizar Depósito
- **POST** `/api/transacciones/deposito`
- **Body**:
```json
{
  "cuentaId": 1,
  "monto": 500.00,
  "descripcion": "Depósito inicial"
}
```

#### Realizar Retiro
- **POST** `/api/transacciones/retiro`
- **Body**:
```json
{
  "cuentaId": 1,
  "monto": 200.00,
  "descripcion": "Retiro para gastos"
}
```

#### Obtener Todas las Transacciones
- **GET** `/api/transacciones`

#### Obtener Transacciones por Cuenta
- **GET** `/api/transacciones/cuenta/{cuentaId}`

#### Obtener Transacciones por Tipo
- **GET** `/api/transacciones/tipo/{tipo}` (DEPOSITO o RETIRO)

#### Estadísticas de Transacciones
- **GET** `/api/transacciones/estadisticas/cuenta/{cuentaId}`

## Instalación y Ejecución

### Prerrequisitos
- Java 17 o superior
- Maven 3.6+

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd backend
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

4. **Verificar que esté funcionando**
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

### Configuración de H2 Console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (dejar vacío)

## Características del Código

### Arquitectura en Capas
1. **Controllers**: Manejan las peticiones HTTP y validan datos de entrada
2. **Services**: Contienen la lógica de negocio y validaciones
3. **Repositories**: Acceso a datos y consultas a la base de datos
4. **Models**: Entidades JPA que representan las tablas
5. **DTOs**: Objetos para transferir datos entre capas

### Validaciones Implementadas
- Números de cuenta únicos
- Saldos positivos o cero
- Montos de transacciones positivos
- Saldo suficiente para retiros
- Cuentas activas para transacciones

### Manejo de Errores
- Respuestas HTTP apropiadas (200, 201, 400, 404, 409)
- Mensajes de error descriptivos
- Logging detallado de operaciones

### Documentación
- Anotaciones Swagger/OpenAPI en todos los endpoints
- Comentarios JavaDoc en todas las clases y métodos
- README detallado con ejemplos

## Desarrollo

### Comandos Útiles

```bash
# Compilar sin ejecutar
mvn compile

# Ejecutar tests
mvn test

# Ejecutar con perfil de desarrollo
mvn spring-boot:run -Dspring.profiles.active=dev

# Generar JAR ejecutable
mvn package

# Ejecutar JAR
java -jar target/cuenta-ahorros-api-1.0.0.jar
```

### Estructura de Logs
La aplicación utiliza SLF4J con los siguientes niveles:
- **INFO**: Operaciones normales
- **ERROR**: Errores y excepciones
- **DEBUG**: Información detallada (solo en desarrollo)

### Base de Datos
- **Desarrollo**: H2 en memoria (se reinicia cada vez)
- **Producción**: Configurar PostgreSQL o MySQL

## Próximos Pasos

1. **Implementar autenticación JWT**
2. **Agregar tests unitarios y de integración**
3. **Configurar base de datos PostgreSQL**
4. **Implementar paginación en consultas**
5. **Agregar filtros avanzados**
6. **Implementar auditoría de cambios**
7. **Configurar Docker para despliegue**

## Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles. 