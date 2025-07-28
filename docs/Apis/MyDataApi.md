# MyDataApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getMyCourses**](MyDataApi.md#getMyCourses) | **GET** /my/courses | List my courses |
| [**getMyTopics**](MyDataApi.md#getMyTopics) | **GET** /my/topics | List my topics |


<a name="getMyCourses"></a>
# **getMyCourses**
> List getMyCourses()

List my courses

### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/CourseDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getMyTopics"></a>
# **getMyTopics**
> List getMyTopics()

List my topics

### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/TopicDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

