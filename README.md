# Building Management Backend

Backend para sistema de gestión de edificios y condominios desarrollado con Spring Boot.

## Características

- **Autenticación JWT**: Sistema seguro de login con tokens
- **Gestión de Usuarios**: Diferentes roles (Admin, Junta Directiva, Propietario, Inquilino)
- **Gestión Financiera**: Control de cuotas, pagos y gastos
- **Reportes**: Estados de cuenta y reportes financieros
- **Comunicación**: Tablón de anuncios y notificaciones

## Tecnologías

- Java 17
- Spring Boot 3.2.0
- Spring Security 6
- Spring Data JPA
- MySQL 8.0
- JWT (JSON Web Tokens)
- Flyway (migraciones de BD)
- Maven

## Configuración

### Requisitos previos

- Java 17 o superior
- MySQL 8.0
- Maven 3.6+

### Configuración de Base de Datos

```sql
CREATE DATABASE building_management 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### Variables de Entorno

Crear archivo `.env`:

```properties
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=myVerySecretKeyThatIsAtLeast32CharactersLong
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### Instalación

```bash
# Clonar repositorio
git clone https://github.com/your-repo/building-management-backend.git
cd building-management-backend

# Instalar dependencias
mvn clean install

# Ejecutar migraciones
mvn flyway:migrate

# Ejecutar aplicación
mvn spring-boot:run
```

### Usuarios de Prueba

```
Admin:
- Email: admin@test.com
- Password: 123456

Usuario Regular:
- Email: user@test.com
- Password: 123456
```

## Endpoints Principales

### Autenticación

```bash
# Login
POST /api/v1/auth/login
{
  "email": "admin@test.com",
  "password": "123456"
}

# Registro
POST /api/v1/auth/signup
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "OWNER"
}

# Recuperar contraseña
POST /api/v1/auth/forgot-password
{
  "email": "john@example.com"
}
```

### Pruebas

```bash
# Endpoint público
GET /api/v1/test/public

# Endpoint protegido (requiere token)
GET /api/v1/test/protected
Authorization: Bearer <token>

# Endpoint admin (requiere rol ADMIN)
GET /api/v1/test/admin
Authorization: Bearer <token>
```

## Estructura del Proyecto

```
src/main/java/com/buildingmanagement/
├── config/                 # Configuraciones
├── security/              # Seguridad JWT
├── common/                # Utilidades comunes
├── modules/               # Módulos del negocio
│   ├── auth/              # Autenticación
│   ├── user/              # Gestión de usuarios
│   ├── building/          # Gestión de edificios
│   └── ...
└── shared/                # DTOs compartidos
```

## Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=AuthServiceTest

# Ejecutar con coverage
mvn clean test jacoco:report
```

## Deployment

### Local con Docker

```bash
# Construir imagen
docker build -t building-management-backend .

# Ejecutar con docker-compose
docker-compose up -d
```

### Heroku

```bash
# Configurar Heroku
heroku create building-management-backend
heroku addons:create jawsdb:kitefin

# Configurar variables
heroku config:set JWT_SECRET=your-secret-key
heroku config:set SPRING_PROFILES_ACTIVE=prod

# Deploy
git push heroku main
```

## Próximas Funcionalidades

- [ ] Pagos online (Yape, Plin, tarjetas)
- [ ] Sistema de notificaciones
- [ ] Gestión de reservas
- [ ] Reportes avanzados
- [ ] API móvil optimizada

## Contribuir

1. Fork el proyecto
2. Crear branch para feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request
