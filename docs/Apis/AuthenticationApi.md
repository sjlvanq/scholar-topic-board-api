# AuthenticationApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**login**](AuthenticationApi.md#login) | **POST** /login | User login |


<a name="login"></a>
# **login**
> TokenDTO login(LoginRequestDTO)

User login

    Authenticates a user with email and password. Returns a JWT token upon success.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **LoginRequestDTO** | [**LoginRequestDTO**](../Models/LoginRequestDTO.md)|  | |

### Return type

[**TokenDTO**](../Models/TokenDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

