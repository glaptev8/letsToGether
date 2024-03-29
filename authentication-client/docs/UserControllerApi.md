# OpenApiDefinition.UserControllerApi

All URIs are relative to *http://localhost:8081*

Method | HTTP request | Description
------------- | ------------- | -------------
[**auth**](UserControllerApi.md#auth) | **POST** /auth/v1 | 
[**login**](UserControllerApi.md#login) | **POST** /auth/v1/login | 
[**saveUser**](UserControllerApi.md#saveUser) | **POST** /auth/v1/register | 
[**test**](UserControllerApi.md#test) | **POST** /auth/v1/test | 
[**updateUser**](UserControllerApi.md#updateUser) | **POST** /auth/v1/update | 

<a name="auth"></a>
# **auth**
> &#x27;Number&#x27; auth()



### Example
```javascript
import {OpenApiDefinition} from 'open_api_definition';

let apiInstance = new OpenApiDefinition.UserControllerApi();
apiInstance.auth((error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters
This endpoint does not need any parameter.

### Return type

**&#x27;Number&#x27;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

<a name="login"></a>
# **login**
> TokenDetails login(body)



### Example
```javascript
import {OpenApiDefinition} from 'open_api_definition';

let apiInstance = new OpenApiDefinition.UserControllerApi();
let body = new OpenApiDefinition.User(); // User | 

apiInstance.login(body, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**User**](User.md)|  | 

### Return type

[**TokenDetails**](TokenDetails.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="saveUser"></a>
# **saveUser**
> UserDto saveUser(user, opts)



### Example
```javascript
import {OpenApiDefinition} from 'open_api_definition';

let apiInstance = new OpenApiDefinition.UserControllerApi();
let user = new OpenApiDefinition.User(); // User | 
let opts = { 
  'avatar': "avatar_example" // Blob | 
};
apiInstance.saveUser(user, opts, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user** | [**User**](.md)|  | 
 **avatar** | **Blob**|  | [optional] 

### Return type

[**UserDto**](UserDto.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: */*

<a name="test"></a>
# **test**
> &#x27;Number&#x27; test(X_USER_ID)



### Example
```javascript
import {OpenApiDefinition} from 'open_api_definition';

let apiInstance = new OpenApiDefinition.UserControllerApi();
let X_USER_ID = 789; // Number | 

apiInstance.test(X_USER_ID, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **X_USER_ID** | **Number**|  | 

### Return type

**&#x27;Number&#x27;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

<a name="updateUser"></a>
# **updateUser**
> UserDto updateUser(body)



### Example
```javascript
import {OpenApiDefinition} from 'open_api_definition';

let apiInstance = new OpenApiDefinition.UserControllerApi();
let body = new OpenApiDefinition.User(); // User | 

apiInstance.updateUser(body, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**User**](User.md)|  | 

### Return type

[**UserDto**](UserDto.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

