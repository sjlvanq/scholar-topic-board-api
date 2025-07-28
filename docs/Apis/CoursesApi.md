# CoursesApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createCourse**](CoursesApi.md#createCourse) | **POST** /courses | Create course (Admin only) |
| [**deleteCourse**](CoursesApi.md#deleteCourse) | **DELETE** /courses/{courseId} | Delete course (Admin only) |
| [**getAllCourses**](CoursesApi.md#getAllCourses) | **GET** /courses | List all courses |
| [**getCourse**](CoursesApi.md#getCourse) | **GET** /courses/{courseId} | Get course by ID |
| [**updateCourse**](CoursesApi.md#updateCourse) | **PUT** /courses/{courseId} | Update course (Admin or Coordinator only) |


<a name="createCourse"></a>
# **createCourse**
> CourseDetailDTO createCourse(CourseRegisterRequestDTO)

Create course (Admin only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **CourseRegisterRequestDTO** | [**CourseRegisterRequestDTO**](../Models/CourseRegisterRequestDTO.md)|  | |

### Return type

[**CourseDetailDTO**](../Models/CourseDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteCourse"></a>
# **deleteCourse**
> CourseDetailDTO deleteCourse(courseId)

Delete course (Admin only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |

### Return type

[**CourseDetailDTO**](../Models/CourseDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getAllCourses"></a>
# **getAllCourses**
> List getAllCourses()

List all courses

### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/CourseDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getCourse"></a>
# **getCourse**
> CourseDetailDTO getCourse(courseId)

Get course by ID

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |

### Return type

[**CourseDetailDTO**](../Models/CourseDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateCourse"></a>
# **updateCourse**
> CourseDetailDTO updateCourse(courseId, CourseUpdateRequestDTO)

Update course (Admin or Coordinator only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **CourseUpdateRequestDTO** | [**CourseUpdateRequestDTO**](../Models/CourseUpdateRequestDTO.md)|  | |

### Return type

[**CourseDetailDTO**](../Models/CourseDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

