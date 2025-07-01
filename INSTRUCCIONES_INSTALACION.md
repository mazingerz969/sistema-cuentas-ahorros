# Instrucciones de Instalación y Ejecución

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java 17 o superior**
- **Node.js 18 o superior**
- **npm o yarn**
- **Git**

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd Proyecto-Angular-Spring
```

### 2. Configurar el Backend (Spring Boot)

```bash
# Navegar al directorio del backend
cd backend

# Verificar que Java esté instalado
java -version

# Compilar y ejecutar el proyecto
./mvnw spring-boot:run
```

El backend estará disponible en: `http://localhost:8080`

**Endpoints principales:**
- API REST: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`

### 3. Configurar el Frontend (Angular)

```bash
# Abrir una nueva terminal y navegar al directorio del frontend
cd frontend

# Instalar dependencias
npm install

# Ejecutar el servidor de desarrollo
ng serve
```

El frontend estará disponible en: `http://localhost:4200`

## 🛠️ Comandos Útiles

### Backend (Spring Boot)

```bash
# Compilar el proyecto
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Crear JAR ejecutable
./mvnw clean package

# Ejecutar JAR
java -jar target/cuenta-ahorros-0.0.1-SNAPSHOT.jar
```

### Frontend (Angular)

```bash
# Instalar dependencias
npm install

# Servidor de desarrollo
ng serve

# Construir para producción
ng build --prod

# Ejecutar tests
ng test

# Linting
ng lint
```

## 📊 Base de Datos

El proyecto utiliza **H2** como base de datos en memoria:

- **URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: (vacía)
- **Console**: `http://localhost:8080/h2-console`

## 🔧 Configuración Adicional

### Variables de Entorno (Opcional)

Crear archivo `.env` en el directorio raíz:

```env
# Backend
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# Frontend
API_BASE_URL=http://localhost:8080/api
```

### Configuración de CORS

El backend ya está configurado para permitir peticiones desde `http://localhost:4200`.

## 🧪 Datos de Prueba

El proyecto incluye datos de ejemplo que se cargan automáticamente:

- **Cuenta de ejemplo**: Número `1234567890`, Saldo inicial `$1000.00`
- **Transacciones de ejemplo**: Varios depósitos y retiros

## 📱 Uso de la Aplicación

### 1. Dashboard
- Vista principal con resumen de la cuenta
- Estadísticas de ingresos y gastos
- Transacciones recientes

### 2. Gestión de Cuentas
- Crear nuevas cuentas
- Editar información de cuentas
- Activar/desactivar cuentas
- Ver detalles de cuentas

### 3. Gestión de Transacciones
- Realizar depósitos
- Realizar retiros
- Ver historial de transacciones
- Filtrar y buscar transacciones

## 🔍 Solución de Problemas

### Error: Puerto 8080 ocupado
```bash
# Cambiar puerto del backend
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Error: Puerto 4200 ocupado
```bash
# Cambiar puerto del frontend
ng serve --port 4201
```

### Error: Dependencias no encontradas
```bash
# Limpiar cache de npm
npm cache clean --force

# Reinstalar dependencias
rm -rf node_modules package-lock.json
npm install
```

### Error: Java no encontrado
```bash
# Verificar instalación de Java
java -version

# Configurar JAVA_HOME si es necesario
export JAVA_HOME=/path/to/java
```

## 📚 Documentación Adicional

- **Spring Boot**: https://spring.io/projects/spring-boot
- **Angular**: https://angular.io/docs
- **Angular Material**: https://material.angular.io/
- **H2 Database**: http://www.h2database.com/

## 🤝 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Soporte

Si tienes problemas o preguntas:

1. Revisa la documentación
2. Busca en los issues existentes
3. Crea un nuevo issue con detalles del problema

---

¡Disfruta desarrollando con Spring Boot y Angular! 🚀 