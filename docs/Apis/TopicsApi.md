# TopicsApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createTopic**](TopicsApi.md#createTopic) | **POST** /courses/{courseId}/topics | Create topic |
| [**deleteTopic**](TopicsApi.md#deleteTopic) | **DELETE** /courses/{courseId}/topics/{topicId} | Delete topic (Admin, Moderator or Author only) |
| [**getAllTopicsFromCourse**](TopicsApi.md#getAllTopicsFromCourse) | **GET** /courses/{courseId}/topics | List topics in course |
| [**getTopicFromCourse**](TopicsApi.md#getTopicFromCourse) | **GET** /courses/{courseId}/topics/{topicId} | Get topic by ID |
| [**updateTopic**](TopicsApi.md#updateTopic) | **PUT** /courses/{courseId}/topics/{topicId} | Update topic |


<a name="createTopic"></a>
# **createTopic**
> TopicDetailDTO createTopic(courseId, TopicRegisterRequestDTO)

Create topic

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **TopicRegisterRequestDTO** | [**TopicRegisterRequestDTO**](../Models/TopicRegisterRequestDTO.md)|  | |

### Return type

[**TopicDetailDTO**](../Models/TopicDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteTopic"></a>
# **deleteTopic**
> deleteTopic(courseId, topicId)

Delete topic (Admin, Moderator or Author only)

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **topicId** | **Long**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getAllTopicsFromCourse"></a>
# **getAllTopicsFromCourse**
> List getAllTopicsFromCourse(courseId)

List topics in course

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |

### Return type

[**List**](../Models/TopicWithAuthorDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getTopicFromCourse"></a>
# **getTopicFromCourse**
> TopicWithAuthorDTO getTopicFromCourse(courseId, topicId)

Get topic by ID

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **topicId** | **Long**|  | [default to null] |

### Return type

[**TopicWithAuthorDTO**](../Models/TopicWithAuthorDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateTopic"></a>
# **updateTopic**
> TopicUpdateRequestDTO updateTopic(courseId, topicId, TopicUpdateRequestDTO)

Update topic

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **topicId** | **Long**|  | [default to null] |
| **TopicUpdateRequestDTO** | [**TopicUpdateRequestDTO**](../Models/TopicUpdateRequestDTO.md)|  | |

### Return type

[**TopicUpdateRequestDTO**](../Models/TopicUpdateRequestDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

