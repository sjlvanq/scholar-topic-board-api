[![Alura + Oracle ONE](https://app.aluracursos.com/form-one/assets/images/logo-aluralatam-oracle.svg)](https://aluracursos.com)

<sub>Java y Spring Framework (II) G8 - ONE </sub><br> 
Challenge: <ins>Foro Hub</ins>

# Scholar Topic Board API
<sub>A Spring Boot 3 forum API REST</sub><br>
API REST de foro con Spring Boot 3 [[EN](README.md)]

> ⚠️ **Este es un proyecto educativo.**  
> No está pensado para su uso en entornos de producción.

Próximos pasos:
* Revisar y ampliar cobertura de tests
* Optimizar consultas a la base de datos
* Revisar y consolidar el sistema de excepciones
* ...

## Stack técnico

- **Java**: 21
- **Spring Boot**: 3.5.4
- **Base de datos**: MariaDB con JPA/Hibernate
- **Migraciones**: Flyway
- **Seguridad**: Spring Security con autenticación JWT
- **Documentación**: OpenAPI 3 (Swagger UI)
- **Build**: Maven
- **Testing**: Spring Boot Test + Spring Security Test

## Referencia completa:
* [docs/README.md](docs/README.md) (OpenApi Generator)

## <sub>Business rules</sub><br>Reglas de negocio

#### Tipos de usuarios y su función principal
* **Administrador**: `Registra usuarios y asigna roles`
* **Coordinador**: `Registra cursos y matricula usuarios`
* **Moderador**: `Suspende usuarios y edita/elimina mensajes`
* **Profesores y estudiantes**: `Crean hilos y respuestas`

`Usuarios no autenticados puede consultar la lista de cursos y cursos por id.`

Usuarios enrolables:
_(Usuarios susceptibles de ser matriculados a un curso con efecto sobre el alcance de sus permisos para determinadas operaciones)_

* Moderador
* Profesor
* Estudiante


#### Login

* Usuarios cuyo rol más elevado sea de moderador, profesor 
o estudiante que no estén matriculados a ningún curso no 
podrán completar el proceso de autenticación.
* Usuarios suspendidos no podrán completar el proceso de
autenticación.


#### Acceso a un curso
```
| Tipo de Usuario          | Acceso a Curso |
| :----------------------- | :------------- |
| Administrador            | Sí             |
| Coordinador              | Sí             |
| Matriculado en el curso  | Sí             |
| No matriculado*          | No             |

(*): Excepto Administrador o Coordinador
```
#### Operaciones sobre hilos y respuestas

##### Sin acceso al curso
```
| Operación   | Admin. | Coord. | Otros |
| :---------- | :----- | :----- | :---- |
| Lectura     | Sí     | Sí     | No    |
| Creación    | Sí     | Sí     | No    |
| Edición     | Sí     | No     | No    |
| Eliminación | Sí     | No     | No    |

```

##### Con acceso al curso
```
| Operación   | Moder. | Prof./Est. |
| :---------- | :----- | ---------- |
| Lectura     | Sí     | Sí         |
| Creación    | Sí     | Sí         |
| Edición     | Sí     | Sí (Autor) |
| Eliminación | Sí     | Sí (Autor) |

```

### Códigos de error
```
BAD_CREDENTIALS_401
BAD_PATHVARIABLE_400
BAD_REQUEST_400
CONFLICT_409
DEPTH_EXCEEDED_400
FORBIDDEN_403
LOCKED_423
MALFORMED_400
NOT_BELONG_400
NOT_ENROLLED_403
NOT_FOUND_404
UNAUTHORIZED_401
USER_BANNED_403
```

## Instalación
### Variables de entorno
```
| Variable            |                          | Default       |
| :------------------ | :----------------------- | :------------ |
| STBOARD_DB_HOST     | STBOARD_TEST_DB_HOST     | localhost     |
| STBOARD_DB_PORT     | STBOARD_TEST_DB_PORT     | 3306          |
| STBOARD_DB_DATABSE  | STBOARD_TEST_DB_DATABSE  | stboard       |
| STBOARD_DB_USERNAME | STBOARD_TEST_DB_USERNAME | alura         |
| STBOARD_DB_PASSWORD | STBOARD_TEST_DB_PASSWORD | alura1234     |
| STBOARD_JWT_SECRET  | STBOARD_TEST_JWT_SECRET  | secret        |

```
