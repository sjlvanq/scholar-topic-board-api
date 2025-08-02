[![Alura + Oracle ONE](https://app.aluracursos.com/form-one/assets/images/logo-aluralatam-oracle.svg)](https://aluracursos.com)

<sub>Java y Spring Framework (II) G8 - ONE </sub><br> 
Challenge: <ins>Foro Hub</ins>

# Scholar Topic Board API
<sub>API REST de foro con Spring Boot 3</sub><br>
A Spring Boot 3 forum API REST [[ES](README_es.md)]

> ⚠️ **This is an educational project.**  
> It is not intended for use in production environments.

Next steps:
* Separate logic into dedicated services
* Review and expand test coverage
* Optimize database queries and avoid N+1 problems
* Review and consolidate the exception handling system  
* ...

## Stack

- **Java**: 21
- **Spring Boot**: 3.5.4
- **Database**: MariaDB with JPA/Hibernate
- **Migration**: Flyway
- **Security**: Spring Security with JWT authentication
- **Documentation**: OpenAPI 3 (Swagger UI)
- **Build Tool**: Maven
- **Testing**: Spring Boot Test + Spring Security Test

## <sub>Business rules</sub><br>Business Rules

#### User types and their main function
* **Administrator**: `Registers users and assigns roles`
* **Coordinator**: `Registers courses and enrolls users`
* **Moderator***: `Suspends users and edits/deletes messages`
* **Teachers and students***: `Create threads and replies`

`Unauthenticated users can query the course list and courses by id.`

(*): _Users susceptible to being enrolled in a course with effect on the scope of their permissions for certain operations_

#### Login

* Users whose highest role is moderator, teacher 
or student who are not enrolled in any course will not 
be able to complete the authentication process.
* Suspended users will not be able to complete the
authentication process.


#### Course access
```
| User Type                | Course Access |
| :----------------------- | :------------ |
| Administrator            | Yes           |
| Coordinator              | Yes           |
| Enrolled in the course   | Yes           |
| Not enrolled*            | No            |

(*): Except Administrator or Coordinator
```
#### Operations on threads and replies

##### Without course access
```
| Operation | Admin. | Coord. | Others |
| :-------- | :----- | :----- | :----- |
| Read      | Yes    | Yes    | No     |
| Create    | Yes    | Yes    | No     |
| Edit      | Yes    | No     | No     |
| Delete    | Yes    | No     | No     |

```

##### With course access
```
| Operation | Moder. | Teacher/Student |
| :-------- | :----- | :-------------- |
| Read      | Yes    | Yes             |
| Create    | Yes    | Yes             |
| Edit      | Yes    | Yes (Author)    |
| Delete    | Yes    | Yes (Author)    |

```

## Notes on reply depth structure

Replies can be organized hierarchically up to a maximum of three levels, starting with those that respond directly to the topic:

- Level 1: Reply to the topic (no parent reply)
- Level 2: Reply to another reply
- Level 3: Nested reply (second-level reply)

Attempts to exceed this limit will result in an error with code: `DEPTH_EXCEEDED_400`.

## Special endpoint permission notes

`GET /users/{userId}`
- Non-admin users will only see public roles.
- Access is granted to:
  - Administrators or coordinators
  - Users enrolled in the same course as the requested user
- If the requested user exists but access is not authorized, the API will simulate a 404 response to prevent information disclosure.

`GET /users/by-course/{courseId}`
- Non-admin users will only see public roles.
- Banned users are included in the response.

`PATCH /users/{userId}/ban`
- Admins cannot ban other admins
- Moderators can only ban regular users from shared courses
- Other users cannot ban

### Error codes
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

## Setup
### Environment variables
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

## Technical reference:
* [docs/README.md](docs/README.md) (OpenApi Generator)

