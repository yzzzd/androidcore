# Change Log

Version 4.0.11 *(2023-07)*
----------------------------
* Update AGP and bump version library
* Lifecycle aware for Core Dialog
* Add function extension to browse file directly from activity launcher
  ```kotlin
  activityLauncher.openFile(context, "*/*") { file, exception ->
     // file is nullable (File?)
     // will contain file that user choose
     // if operation failed then you will get file == null and return the exception
  }
  ```

Version 4.0.10 *(2023-06)*
----------------------------
* Add permission helper and context extension
* Add parcelable extension
  ```kotlin
  val model: Model = intent.parcelable<Model>(KEY)
  val model: Model = bundle.parcelable<Model>(KEY)
  ```
* Add function to provide okhttp client and api service
  ```kotlin
  val okHttpClient = NetworkHelper.provideOkHttpClient()
  
  val apiService = NetworkHelper.provideApiService<ApiService>(
    baseUrl = BuildConfig.BASE_URL,
    okHttpClient = okHttpClient,
    converterFactory = listOf(GsonConverterFactory.create())
  )
  ```

* Add function extension to open camera and gallery directly from activity launcher
  ```kotlin
  activityLauncher.openCamera(context) { file, exception ->
     // file is nullable (File?)
     // will contain photo that take with camera
     // if operation failed then you will get file == null and return the exception
  }
  ```
  
  **NOTE!** that ```openCamera()``` function have a parameter ```authority``` with default value is ```packageName.fileprovider```
  so you need to add a FileProvider Content Provider to the manifest file. The ```@xml/file_paths``` also need to add separately in the resources directory.
  
  ```kotlin
  activityLauncher.openGallery(context) { file, exception ->
     // file is nullable (File?)
     // will contain photo that take with camera
     // if operation failed then you will get file == null and return the exception
  }
  ```
  
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
