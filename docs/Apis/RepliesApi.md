# RepliesApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createChildReply**](RepliesApi.md#createChildReply) | **POST** /courses/{courseId}/topics/{topicId}/replies/{parentId} | Create a child reply |
| [**createRootReply**](RepliesApi.md#createRootReply) | **POST** /courses/{courseId}/topics/{topicId}/replies | Create a first-level reply |
| [**deleteReply**](RepliesApi.md#deleteReply) | **DELETE** /courses/{courseId}/topics/{topicId}/replies/{replyId} | Delete reply |
| [**getAllReplies**](RepliesApi.md#getAllReplies) | **GET** /courses/{courseId}/topics/{topicId}/replies | List replies at root level with their children in a topic |
| [**getReply**](RepliesApi.md#getReply) | **GET** /courses/{courseId}/topics/{topicId}/replies/{replyId} | Get reply by ID |
| [**updateReply**](RepliesApi.md#updateReply) | **PUT** /courses/{courseId}/topics/{topicId}/replies/{replyId} | Update reply |


<a name="createChildReply"></a>
# **createChildReply**
> ReplyDetailDTO createChildReply(courseId, topicId, parentId, ReplyRegisterRequestDTO)

Create a child reply

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **topicId** | **Long**|  | [default to null] |
| **parentId** | **Long**|  | [default to null] |
| **ReplyRegisterRequestDTO** | [**ReplyRegisterRequestDTO**](../Models/ReplyRegisterRequestDTO.md)|  | |

### Return type

[**ReplyDetailDTO**](../Models/ReplyDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="createRootReply"></a>
# **createRootReply**
> ReplyDetailDTO createRootReply(courseId, topicId, ReplyRegisterRequestDTO)

Create a first-level reply

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **topicId** | **Long**|  | [default to null] |
| **ReplyRegisterRequestDTO** | [**ReplyRegisterRequestDTO**](../Models/ReplyRegisterRequestDTO.md)|  | |

### Return type

[**ReplyDetailDTO**](../Models/ReplyDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteReply"></a>
# **deleteReply**
> deleteReply(courseId, topicId, replyId)

Delete reply

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **topicId** | **Long**|  | [default to null] |
| **replyId** | **Long**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getAllReplies"></a>
# **getAllReplies**
> List getAllReplies(courseId, topicId)

List replies at root level with their children in a topic

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **topicId** | **Long**|  | [default to null] |

### Return type

[**List**](../Models/ReplyDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getReply"></a>
# **getReply**
> ReplyDetailDTO getReply(courseId, topicId, replyId)

Get reply by ID

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **topicId** | **Long**|  | [default to null] |
| **replyId** | **Long**|  | [default to null] |

### Return type

[**ReplyDetailDTO**](../Models/ReplyDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateReply"></a>
# **updateReply**
> ReplyDetailDTO updateReply(courseId, topicId, replyId, ReplyUpdateRequestDTO)

Update reply

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courseId** | **Long**|  | [default to null] |
| **topicId** | **Long**|  | [default to null] |
| **replyId** | **Long**|  | [default to null] |
| **ReplyUpdateRequestDTO** | [**ReplyUpdateRequestDTO**](../Models/ReplyUpdateRequestDTO.md)|  | |

### Return type

[**ReplyDetailDTO**](../Models/ReplyDetailDTO.md)

### Authorization

[bearer-key](../README.md#bearer-key)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

