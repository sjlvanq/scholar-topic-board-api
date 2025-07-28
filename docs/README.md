# Documentation for Scholar Topic Board API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost:8080*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *AuthenticationApi* | [**login**](Apis/AuthenticationApi.md#login) | **POST** /login | User login |
| *CoursesApi* | [**createCourse**](Apis/CoursesApi.md#createcourse) | **POST** /courses | Create course (Admin only) |
*CoursesApi* | [**deleteCourse**](Apis/CoursesApi.md#deletecourse) | **DELETE** /courses/{courseId} | Delete course (Admin only) |
*CoursesApi* | [**getAllCourses**](Apis/CoursesApi.md#getallcourses) | **GET** /courses | List all courses |
*CoursesApi* | [**getCourse**](Apis/CoursesApi.md#getcourse) | **GET** /courses/{courseId} | Get course by ID |
*CoursesApi* | [**updateCourse**](Apis/CoursesApi.md#updatecourse) | **PUT** /courses/{courseId} | Update course (Admin or Coordinator only) |
| *MyDataApi* | [**getMyCourses**](Apis/MyDataApi.md#getmycourses) | **GET** /my/courses | List my courses |
*MyDataApi* | [**getMyTopics**](Apis/MyDataApi.md#getmytopics) | **GET** /my/topics | List my topics |
| *RepliesApi* | [**createChildReply**](Apis/RepliesApi.md#createchildreply) | **POST** /courses/{courseId}/topics/{topicId}/replies/{parentId} | Create a child reply |
*RepliesApi* | [**createRootReply**](Apis/RepliesApi.md#createrootreply) | **POST** /courses/{courseId}/topics/{topicId}/replies | Create a first-level reply |
*RepliesApi* | [**deleteReply**](Apis/RepliesApi.md#deletereply) | **DELETE** /courses/{courseId}/topics/{topicId}/replies/{replyId} | Delete reply |
*RepliesApi* | [**getAllReplies**](Apis/RepliesApi.md#getallreplies) | **GET** /courses/{courseId}/topics/{topicId}/replies | List replies at root level with their children in a topic |
*RepliesApi* | [**getReply**](Apis/RepliesApi.md#getreply) | **GET** /courses/{courseId}/topics/{topicId}/replies/{replyId} | Get reply by ID |
*RepliesApi* | [**updateReply**](Apis/RepliesApi.md#updatereply) | **PUT** /courses/{courseId}/topics/{topicId}/replies/{replyId} | Update reply |
| *RolesApi* | [**getAllRoles**](Apis/RolesApi.md#getallroles) | **GET** /users/roles | List all roles (Admin only) |
| *TopicsApi* | [**createTopic**](Apis/TopicsApi.md#createtopic) | **POST** /courses/{courseId}/topics | Create topic |
*TopicsApi* | [**deleteTopic**](Apis/TopicsApi.md#deletetopic) | **DELETE** /courses/{courseId}/topics/{topicId} | Delete topic (Admin, Moderator or Author only) |
*TopicsApi* | [**getAllTopicsFromCourse**](Apis/TopicsApi.md#getalltopicsfromcourse) | **GET** /courses/{courseId}/topics | List topics in course |
*TopicsApi* | [**getTopicFromCourse**](Apis/TopicsApi.md#gettopicfromcourse) | **GET** /courses/{courseId}/topics/{topicId} | Get topic by ID |
*TopicsApi* | [**updateTopic**](Apis/TopicsApi.md#updatetopic) | **PUT** /courses/{courseId}/topics/{topicId} | Update topic |
| *UsersApi* | [**banUser**](Apis/UsersApi.md#banuser) | **PATCH** /users/{userId}/ban | Ban/unban user (Admin or Moderator only) |
*UsersApi* | [**createUser**](Apis/UsersApi.md#createuser) | **POST** /users | Register a new user (Admin only) |
*UsersApi* | [**deleteUser**](Apis/UsersApi.md#deleteuser) | **DELETE** /users/{userId} | Delete user (Admin only) |
*UsersApi* | [**getAllUsers**](Apis/UsersApi.md#getallusers) | **GET** /users | Get list of all visible users (Admin only) |
*UsersApi* | [**getAllUsersByCourse**](Apis/UsersApi.md#getallusersbycourse) | **GET** /users/by-course/{courseId} | List users in a course (Admin, Coordinator or Enrolled user only) |
*UsersApi* | [**getUser**](Apis/UsersApi.md#getuser) | **GET** /users/{userId} | Retrieve user by ID (Admin, Coordinator or course partner) |
*UsersApi* | [**updateUser**](Apis/UsersApi.md#updateuser) | **PUT** /users/{userId} | Update user information (Admin or Coordinator only) |
*UsersApi* | [**updateUserCourses**](Apis/UsersApi.md#updateusercourses) | **PATCH** /users/{userId}/courses | Assign or update user's courses (Admin or Coordinator only) |
*UsersApi* | [**updateUserRoles**](Apis/UsersApi.md#updateuserroles) | **PATCH** /users/{userId}/roles | Assign or update user's roles (Admin only) |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [CourseDetailDTO](./Models/CourseDetailDTO.md)
 - [CourseRegisterRequestDTO](./Models/CourseRegisterRequestDTO.md)
 - [CourseSummaryDTO](./Models/CourseSummaryDTO.md)
 - [CourseUpdateRequestDTO](./Models/CourseUpdateRequestDTO.md)
 - [EntityAlreadyExistsExceptionDTO](./Models/EntityAlreadyExistsExceptionDTO.md)
 - [ErrorStatus400FieldDTO](./Models/ErrorStatus400FieldDTO.md)
 - [ErrorStatus400ResponseDTO](./Models/ErrorStatus400ResponseDTO.md)
 - [LoginRequestDTO](./Models/LoginRequestDTO.md)
 - [ReplyDetailDTO](./Models/ReplyDetailDTO.md)
 - [ReplyRegisterRequestDTO](./Models/ReplyRegisterRequestDTO.md)
 - [ReplyUpdateRequestDTO](./Models/ReplyUpdateRequestDTO.md)
 - [RoleAdminResponseDTO](./Models/RoleAdminResponseDTO.md)
 - [RolePublicResponseDTO](./Models/RolePublicResponseDTO.md)
 - [TokenDTO](./Models/TokenDTO.md)
 - [TopicDetailDTO](./Models/TopicDetailDTO.md)
 - [TopicRegisterRequestDTO](./Models/TopicRegisterRequestDTO.md)
 - [TopicUpdateRequestDTO](./Models/TopicUpdateRequestDTO.md)
 - [TopicWithAuthorDTO](./Models/TopicWithAuthorDTO.md)
 - [UserAuthorDTO](./Models/UserAuthorDTO.md)
 - [UserBanStatusUpdateRequestDTO](./Models/UserBanStatusUpdateRequestDTO.md)
 - [UserCoursesUpdateRequestDTO](./Models/UserCoursesUpdateRequestDTO.md)
 - [UserDetailDTO](./Models/UserDetailDTO.md)
 - [UserListItemDTO](./Models/UserListItemDTO.md)
 - [UserRegisterRequestDTO](./Models/UserRegisterRequestDTO.md)
 - [UserResponseDTO](./Models/UserResponseDTO.md)
 - [UserRolesUpdateRequestDTO](./Models/UserRolesUpdateRequestDTO.md)
 - [UserUpdateRequestDTO](./Models/UserUpdateRequestDTO.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

<a name="bearer-key"></a>
### bearer-key

- **Type**: HTTP Bearer Token authentication (JWT)

