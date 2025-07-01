# 🏦 Sistema de Gestión de Cuentas de Ahorros

Una aplicación web completa para gestionar cuentas de ahorros, desarrollada con **Angular** (frontend) y **Spring Boot** (backend).

## 🚀 Características Principales

### Frontend (Angular)
- ✅ **Dashboard interactivo** con estadísticas en tiempo real
- ✅ **Gestión completa de cuentas** (CRUD)
- ✅ **Sistema de transacciones** (depósitos y retiros)
- ✅ **Interfaz responsive** y moderna
- ✅ **Navegación intuitiva** con menú lateral
- ✅ **Validaciones en tiempo real**

### Backend (Spring Boot)
- ✅ **API RESTful** completa
- ✅ **Base de datos H2** en memoria
- ✅ **Validaciones de negocio**
- ✅ **Documentación Swagger**
- ✅ **Manejo de errores** robusto
- ✅ **CORS configurado** para Angular

## 🛠️ Tecnologías Utilizadas

### Frontend
- **Angular 17** - Framework de desarrollo
- **TypeScript** - Lenguaje de programación
- **SCSS** - Estilos avanzados
- **RxJS** - Programación reactiva

### Backend
- **Spring Boot 3** - Framework Java
- **Spring Data JPA** - Persistencia de datos
- **H2 Database** - Base de datos en memoria
- **Maven** - Gestión de dependencias
- **Swagger/OpenAPI** - Documentación de API

## 📁 Estructura del Proyecto

```
Proyecto Angular- Spring/
├── frontend/                 # Aplicación Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/   # Componentes de la UI
│   │   │   ├── services/     # Servicios para API
│   │   │   └── models/       # Modelos de datos
│   │   └── assets/           # Recursos estáticos
│   └── package.json
├── backend/                  # API Spring Boot
│   ├── src/main/java/
│   │   └── com/ahorros/
│   │       ├── controllers/  # Controladores REST
│   │       ├── services/     # Lógica de negocio
│   │       ├── models/       # Entidades JPA
│   │       └── repositories/ # Repositorios de datos
│   └── pom.xml
└── README.md
```

## 🚀 Instalación y Configuración Local

### Prerrequisitos
- **Node.js** (versión 18 o superior)
- **Java 17** o superior
- **Maven** (versión 3.6 o superior)
- **Git**

### 1. Clonar el Repositorio
```bash
git clone <URL_DEL_REPOSITORIO>
cd "Proyecto Angular- Spring"
```

### 2. Configurar el Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
El backend estará disponible en: `http://localhost:8080`
- **API Base**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console`

### 3. Configurar el Frontend
```bash
cd frontend
npm install
ng serve
```
El frontend estará disponible en: `http://localhost:4200`

## 🌐 Despliegue Online

### Opción 1: Railway (Recomendado)
Railway es una plataforma gratuita perfecta para aplicaciones full-stack.

#### Pasos para desplegar en Railway:

1. **Crear cuenta en Railway**
   - Ve a [railway.app](https://railway.app)
   - Regístrate con tu cuenta de GitHub

2. **Conectar el repositorio**
   - Haz push de tu código a GitHub
   - En Railway, selecciona "Deploy from GitHub repo"
   - Selecciona tu repositorio

3. **Configurar variables de entorno**
   ```bash
   # Para el backend
   SPRING_PROFILES_ACTIVE=production
   SERVER_PORT=8080
   
   # Para el frontend
   NODE_ENV=production
   ```

4. **Configurar el build**
   Railway detectará automáticamente que es un proyecto Maven y Node.js

### Opción 2: Render
Render también ofrece despliegue gratuito para aplicaciones full-stack.

#### Pasos para desplegar en Render:

1. **Crear cuenta en Render**
   - Ve a [render.com](https://render.com)
   - Regístrate con tu cuenta de GitHub

2. **Crear servicios**
   - **Backend Service**: Selecciona "Web Service" y conecta tu repositorio
   - **Frontend Service**: Selecciona "Static Site" para Angular

3. **Configurar build commands**
   ```bash
   # Backend
   Build Command: mvn clean install
   Start Command: java -jar target/cuenta-ahorros-0.0.1-SNAPSHOT.jar
   
   # Frontend
   Build Command: npm install && npm run build
   Publish Directory: dist/frontend
   ```

### Opción 3: Heroku
Heroku ofrece un tier gratuito limitado pero funcional.

## 📊 API Endpoints

### Cuentas
- `GET /api/cuentas` - Obtener todas las cuentas
- `POST /api/cuentas` - Crear nueva cuenta
- `GET /api/cuentas/{id}` - Obtener cuenta por ID
- `PUT /api/cuentas/{id}` - Actualizar cuenta
- `DELETE /api/cuentas/{id}` - Eliminar cuenta

### Transacciones
- `GET /api/transacciones` - Obtener todas las transacciones
- `POST /api/transacciones` - Crear nueva transacción
- `GET /api/transacciones/cuenta/{cuentaId}` - Transacciones por cuenta
- `POST /api/transacciones/deposito` - Realizar depósito
- `POST /api/transacciones/retiro` - Realizar retiro

### Estadísticas
- `GET /api/cuentas/estadisticas` - Estadísticas generales
- `GET /api/transacciones/estadisticas` - Estadísticas de transacciones

## 🧪 Testing

### Backend
```bash
cd backend
mvn test
```

### Frontend
```bash
cd frontend
ng test
```

## 🔧 Configuración Avanzada

### Variables de Entorno
```bash
# Backend
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb

# Frontend
NODE_ENV=production
API_BASE_URL=https://tu-backend.railway.app/api
```

### Base de Datos
El proyecto usa H2 en memoria por defecto. Para producción, puedes cambiar a:
- **PostgreSQL** (recomendado para Railway)
- **MySQL**
- **MongoDB**

## 📝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👨‍💻 Autor

**Tu Nombre**
- GitHub: [@tu-usuario](https://github.com/tu-usuario)
- LinkedIn: [Tu Perfil](https://linkedin.com/in/tu-perfil)

## 🙏 Agradecimientos

- **Angular Team** por el excelente framework
- **Spring Team** por Spring Boot
- **Railway** por la plataforma de despliegue gratuita
- **Comunidad open source** por las herramientas utilizadas

---

⭐ **¡No olvides dar una estrella al proyecto si te gustó!** 