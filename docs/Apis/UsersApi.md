# UsersApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**banUser**](UsersApi.md#banUser) | **PATCH** /users/{userId}/ban | Ban/unban user (Admin or Moderator only) |
| [**createUser**](UsersApi.md#createUser) | **POST** /users | Register a new user (Admin only) |
| [**deleteUser**](UsersApi.md#deleteUser) | **DELETE** /users/{userId} | Delete user (Admin only) |
| [**getAllUsers**](UsersApi.md#getAllUsers) | **GET** /users | Get list of all visible users (Admin only) |
| [**getAllUsersByCourse**](UsersApi.md#getAllUsersByCourse) | **GET** /users/by-course/{courseId} | List users in a course (Admin, Coordinator or Enrolled user only) |
| [**getUser**](UsersApi.md#getUser) | **GET** /users/{userId} | Retrieve user by ID (Admin, Coordinator or course partner) |
| [**updateUser**](UsersApi.md#updateUser) | **PUT** /users/{userId} | Update user information (Admin or Coordinator only) |
| [**updateUserCourses**](UsersApi.md#updateUserCourses) | **PATCH** /users/{userId}/courses | Assign or update user&#39;s courses (Admin or Coordinator only) |
| [**updateUserRoles**](UsersApi.md#updateUserRoles) | **PATCH** /users/{userId}/roles | Assign or update user&#39;s roles (Admin only) |


<a name="banUser"></a>
# **banUser**
> banUser(userId, UserBanStatusUpdateRequestDTO)

Ban/unban user (Admin or Moderator only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **Long**|  | [default to null] |
| **UserBanStatusUpdateRequestDTO** | [**UserBanStatusUpdateRequestDTO**](../Models/UserBanStatusUpdateRequestDTO.md)|  | |

### Return type

null (empty response body)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="createUser"></a>
# **createUser**
> UserResponseDTO createUser(UserRegisterRequestDTO)

Register a new user (Admin only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **UserRegisterRequestDTO** | [**UserRegisterRequestDTO**](../Models/UserRegisterRequestDTO.md)|  | |

### Return type

[**UserResponseDTO**](../Models/UserResponseDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteUser"></a>
# **deleteUser**
> deleteUser(userId)

Delete user (Admin only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **Long**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getAllUsers"></a>
# **getAllUsers**
> List getAllUsers(includes)

Get list of all visible users (Admin only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **includes** | **String**|  | [optional] [default to ACTIVE] [enum: ALL, ACTIVE, DELETED] |

### Return type

[**List**](../Models/UserListItemDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getAllUsersByCourse"></a>
# **getAllUsersByCourse**
> List getAllUsersByCourse(courseId)

List users in a course (Admin, Coordinator or Enrolled user only)

    * Non-admin users will only see public roles of the requested users. * Banned users will be included in the response. 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |

### Return type

[**List**](../Models/UserListItemDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getUser"></a>
# **getUser**
> UserDetailDTO getUser(userId)

Retrieve user by ID (Admin, Coordinator or course partner)

    * Non-admin users will only see public roles of the requested user. * Access requires admin/coordinator privileges OR shared course enrollment. 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **Long**|  | [default to null] |

### Return type

[**UserDetailDTO**](../Models/UserDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateUser"></a>
# **updateUser**
> UserResponseDTO updateUser(userId, UserUpdateRequestDTO)

Update user information (Admin or Coordinator only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **Long**|  | [default to null] |
| **UserUpdateRequestDTO** | [**UserUpdateRequestDTO**](../Models/UserUpdateRequestDTO.md)|  | |

### Return type

[**UserResponseDTO**](../Models/UserResponseDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="updateUserCourses"></a>
# **updateUserCourses**
> List updateUserCourses(userId, UserCoursesUpdateRequestDTO)

Assign or update user&#39;s courses (Admin or Coordinator only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **Long**|  | [default to null] |
| **UserCoursesUpdateRequestDTO** | [**UserCoursesUpdateRequestDTO**](../Models/UserCoursesUpdateRequestDTO.md)|  | |

### Return type

[**List**](../Models/CourseDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="updateUserRoles"></a>
# **updateUserRoles**
> List updateUserRoles(userId, UserRolesUpdateRequestDTO)

Assign or update user&#39;s roles (Admin only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userId** | **Long**|  | [default to null] |
| **UserRolesUpdateRequestDTO** | [**UserRolesUpdateRequestDTO**](../Models/UserRolesUpdateRequestDTO.md)|  | |

### Return type

[**List**](../Models/RolePublicResponseDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

