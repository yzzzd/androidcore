package com.crocodic.core.base.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.crocodic.core.R
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.api.ModelResponse
import com.crocodic.core.data.model.LocationState
import com.crocodic.core.extension.checkEnabledLocation
import com.crocodic.core.extension.checkLocationPermission
import com.crocodic.core.extension.notify
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.StateViewHelper
import com.crocodic.core.helper.util.ClickPrevention
import com.crocodic.core.data.model.AppNotification
import com.crocodic.core.ui.dialog.ExpiredDialog
import com.crocodic.core.ui.dialog.InformationDialog
import com.crocodic.core.ui.dialog.LoadingDialog
import com.crocodic.core.widget.stateview.StateView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by @yzzzd on 4/22/18.
 */

abstract class NoViewModelActivity<VB : ViewDataBinding>(@LayoutRes private val layoutRes: Int) : AppCompatActivity(), ClickPrevention {

    protected lateinit var binding: VB

    protected val loadingDialog by lazy { LoadingDialog(this) }

    protected val informationDialog by lazy { InformationDialog(this) }

    protected var inAppNotification = true

    protected var stateViewHelper: StateViewHelper? = null

    protected val expiredDialog by lazy {
        ExpiredDialog(this) { positive, dialog ->
            dialog.setLoading()
            if (positive) {
                authRenewToken()
            } else {
                authLogoutRequest()
            }
        }
    }

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    /* To change startActivityForResult that has been deprecated for general, inline result */
    val activityLauncher = BetterActivityResult.registerActivityForResult(this)

    /* To change startActivityForResult that has been deprecated for baseActivity */
    protected var activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onResult(result)
    }

    protected var locationLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startLocationManager()
        } else {
            tos(R.string.cr_error_enable_location)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
    }

    //region function can override

    /* data binding tidak aktif dengan fungsi ini */
    @Deprecated("Enable data binding and change to add layout resource directly into activity constructor")
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    fun withStateView(stateView: StateView, @LayoutRes emptyRes: Int = R.layout.state_empty, @LayoutRes errorRes: Int = R.layout.state_error, @LayoutRes loadingRes: Int = R.layout.state_loading) {
        stateViewHelper = StateViewHelper(stateView = stateView, emptyRes = emptyRes, errorRes = errorRes, loadingRes = loadingRes)
    }

    /* Override this function to perform renew token from activity that have viewModel */
    open fun authRenewToken() {}

    /* Override this function to perform logout from activity that have viewModel */
    open fun authLogoutRequest() {}

    /* Override this function to perform logout */
    open fun authLogoutSuccess() {}

    /* Override this function to get result from activityResultLauncher */
    open fun onResult(result: ActivityResult) {}

    /* Override this function to do something when user click the in app notification */
    open fun onNotificationClick() {}

    /* Override this function to do retry when state error */
    open fun onErrorRetryClick() {}

    //endregion

    /* to check location permission */
    protected fun listenLocationChange() {
        checkLocationPermission() {
            checkEnabledLocation(locationLauncher) {
                startLocationManager()
            }
        }
    }

    /* Override this function to retrieve location update */
    open fun retrieveLocationChange(location: Location) { }

    /* Override this function if want to start location manager at activity */
    @SuppressLint("MissingPermission")
    private fun startLocationManager() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {

            override fun onLocationChanged(location: Location) {
                retrieveLocationChange(location)
                val isMock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    location.isMock
                } else {
                    location.isFromMockProvider
                }
                EventBus.getDefault().post(LocationState.LocationFake(isMock))
            }

            override fun onProviderEnabled(provider: String) {
                //Logg.d(log = "status location enabled $provider")
                EventBus.getDefault().post(LocationState.LocationState(true))
            }

            override fun onProviderDisabled(provider: String) {
                //Logg.d(log = "status location disable $provider")
                EventBus.getDefault().post(LocationState.LocationState(false))
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // Register the listener with the Location Manager to receive location updates
        locationListener?.let { locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, it) }
    }

    //region lifecycle
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        locationListener?.let { locationManager?.removeUpdates(it) }
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    open override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationListener?.let { locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, it) }
    }

    override fun onPause() {
        locationListener?.let { locationManager?.removeUpdates(it) }
        super.onPause()
    }
    //endregion

    //region eventbus
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onNotify(appNotification: AppNotification) {
        if (inAppNotification) {
            notify(appNotification) { onNotificationClick() }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LocationState.LocationState) {
        if (event.enabled) {
            informationDialog.dismiss()
        } else {
            informationDialog.setMessage(R.string.cr_error_ups, R.string.cr_error_please_enable_location, R.drawable.img_gps_off).showButton(false).show()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LocationState.LocationFake) {
        if (event.enabled) {
            informationDialog.setMessage(R.string.cr_error_ups, R.string.cr_error_please_disable_mock, R.drawable.img_gps_fake).showButton(true).show()
        } else {
            informationDialog.dismiss()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(apiResponse: ApiResponse) {
        if (apiResponse.isTokenExpired) {
            if (expiredDialog.isShowing()) {
                authLogoutSuccess()
            } else {
                expiredDialog.show()
            }
        } else {
            expiredDialog.dismiss()
            disconnect(apiResponse)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(apiResponse: ModelResponse) {
        if (apiResponse.status == ApiStatus.EXPIRED) {
            if (expiredDialog.isShowing()) {
                authLogoutSuccess()
            } else {
                expiredDialog.show()
            }
        } else {
            expiredDialog.dismiss()
            disconnect(apiResponse)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String) {
        when (event) {
            CoreActivity.Companion.EVENT.RENEW_TOKEN -> expiredDialog.dismiss()
            CoreActivity.Companion.EVENT.LOGGED_OUT -> authLogoutSuccess()
        }
    }

    //endregion

    //region UI feedback

    /* when fall from calling api request*/
    open fun disconnect(apiResponse: ApiResponse) {
        apiResponse.message?.let { msg ->
            if (apiResponse.flagView == 1) {
                if (stateViewHelper != null) {
                    stateViewHelper?.showError(msg, false) {
                        onErrorRetryClick()
                    }
                } else {
                    tos(msg)
                }
            } else {
                if (apiResponse.isToast){
                    tos(msg)
                } else {
                    loadingDialog.setResponse(msg)
                }
            }
        }
    }

    open fun disconnect(apiResponse: ModelResponse) {
        apiResponse.message?.let { msg ->
            if (apiResponse.flagView == 1) {
                if (stateViewHelper != null) {
                    stateViewHelper?.showError(msg, false) {
                        onErrorRetryClick()
                    }
                } else {
                    tos(msg)
                }
            } else {
                if (apiResponse.isToast){
                    tos(msg)
                } else {
                    loadingDialog.setResponse(msg)
                }
            }
        }
    }

    //endregion

    class BetterActivityResult<Input, Result> private constructor(caller: ActivityResultCaller, contract: ActivityResultContract<Input, Result>, var onActivityResult: ((Result) -> Unit)?) {

        private val launcher: ActivityResultLauncher<Input> = caller.registerForActivityResult(contract) { onActivityResult?.invoke(it) }

        /**
         * Launch activity, same as [ActivityResultLauncher.launch] except that it
         * allows a callback
         * executed after receiving a result from the target activity.
         */
        /**
         * Same as [.launch] with last parameter set to `null`.
         */
        @JvmOverloads
        fun launch(input: Input, onActivityResult: ((Result) -> Unit)? = this.onActivityResult) {
            this.onActivityResult = onActivityResult
            launcher.launch(input)
        }

        @JvmOverloads
        fun launch(input: Input, option: ActivityOptionsCompat, onActivityResult: ((Result) -> Unit)? = this.onActivityResult) {
            this.onActivityResult = onActivityResult
            launcher.launch(input, option)
        }

        companion object {
            /**
             * Register activity result using a [ActivityResultContract] and an in-place
             * activity result callback like
             * the default approach. You can still customise callback using [.launch].
             */
            fun <Input, Result> registerForActivityResult(caller: ActivityResultCaller, contract: ActivityResultContract<Input, Result>, onActivityResult: ((Result) -> Unit)?): BetterActivityResult<Input, Result> {
                return BetterActivityResult(caller, contract, onActivityResult)
            }

            /**
             * Same as [.registerForActivityResult] except
             * the last argument is set to `null`.
             */
            fun <Input, Result> registerForActivityResult(caller: ActivityResultCaller, contract: ActivityResultContract<Input, Result>): BetterActivityResult<Input, Result> {
                return registerForActivityResult(caller, contract, null)
            }

            /**
             * Specialised method for launching new activities.
             */
            fun registerActivityForResult(caller: ActivityResultCaller): BetterActivityResult<Intent, ActivityResult> {
                return registerForActivityResult(caller, ActivityResultContracts.StartActivityForResult())
            }
        }
    }
}