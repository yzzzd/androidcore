# Change Log

Version 4.0.9 *(2023-03)*
----------------------------
* Bump version & exclude firebase ads dependencies
* Add ModelResponse implementation on ApiObserver

  ```kotlin
  @FormUrlEncoded
  @POST("login")
  suspend fun login(
    @Field("phone") phone: String?,
    @Field("password") password: String?
  ): AuthResponse
  ```

  ```kotlin
  data class AuthResponse(
    @Expose
    @SerializedName("data")
    val user: User? = null
  ): ModelResponse() 
  ```

  **AuthResponse** extend of **ModelResponse** that have basic properties like: ```status, message, tokenExpired```. You can simply add the required properties following on your api response.

  ### Using ApiObserver new ```run()``` function

  ```kotlin
  ApiObserver.run({ apiServices.login(phone, password) }, object : ApiObserver.ResponseListener<AuthResponse> {
    override suspend fun onLoading(response: AuthResponse) {
      _responseLogin.emit(response)
    }            
    override suspend fun onSuccess(response: AuthResponse) {
      userDao.insert(response.user)
      _responseLogin.emit(response)
    }
    override suspend fun onError(response: AuthResponse) {
      _responseLogin.emit(response)
    }
    override suspend fun onExpired(response: AuthResponse) {
      _responseLogin.emit(response)
    }
  })
  ````
  
  If you want emit sharedFlow automatically, you can use ```ResponseListenerFlow```, so you need passing ```MutableSharedFlow``` object into the listener, and override callback only in what you need to make operation. Ex: ```onSuccess()```

  ```kotlin
  ApiObserver.run({ apiServices.login(phone, password) }, object : ApiObserver.ResponseListenerFlow<AuthResponse>(_responseLogin) {
    override suspend fun onSuccess(response: AuthResponse) {
      userDao.insert(response.user)
      super.onSuccess(response)
    }
  })
  ````
  
  ### Consuming data with ```collect()``` like before

  ```kotlin
  lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
      launch {
        viewModel.responseLogin.collect { apiResponse ->
          when (apiResponse.status) {
            ApiStatus.LOADING -> {
              loadingDialog.show(message = "Logging In...") 
            }
            ApiStatus.SUCCESS -> {
              loadingDialog.dismiss()
              openHome()
            }
            ApiStatus.ERROR -> {
              loadingDialog.show(apiResponse.message, false)
            }
          }
        }
      }
    }
  }
  ```

Version 4.0.8 *(2023-03)*
----------------------------

* Update: Migrate property *apiResponse* in **CoreViewModel** from using **Channel** into **SharedFlow**