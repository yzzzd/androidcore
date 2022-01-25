package com.crocodic.core.ui.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.crocodic.core.R
import com.crocodic.core.databinding.CrActivityPermissionLocationBinding
import com.crocodic.core.extension.allPermissionsGranted
import com.crocodic.core.helper.util.ClickPrevention
import com.crocodic.core.ui.dialog.PermissionSettingDialog

/**
 * Created by @yzzzd on 4/22/18.
 */

class LocationPermissionActivity : AppCompatActivity(), ClickPrevention {

    private var binding: CrActivityPermissionLocationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CrActivityPermissionLocationBinding.inflate(layoutInflater)
        binding?.let { setContentView(it.root) }

        setResult(RESULT)
        binding?.buttonPositive?.setOnClickListener(this)
        binding?.buttonNegative?.setOnClickListener(this)
    }

    private fun checkPermission() {
        if (allPermissionsGranted(REQUIRED_PERMISSIONS_LOCATION)) {
            finish()
        } else {
            requestMultiplePermissions.launch(REQUIRED_PERMISSIONS_LOCATION)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding?.buttonPositive -> checkPermission()
            binding?.buttonNegative -> onBackPressed()
        }
        super.onClick(v)
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions : Map<String, Boolean> ->
        var denied = false
        permissions.entries.forEach {
            if(!it.value) {
                denied = true
            }
        }
        if (denied) {
            PermissionSettingDialog(this)
                .setContent(R.drawable.img_grant_location, R.string.cr_info_permission_location)
                .onButtonClick {
                    it.dismiss()

                    val setting = Intent()
                    setting.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    setting.data = Uri.fromParts("package", packageName, null)

                    resultSetting.launch(setting)
                }
                .show()
        } else {
            finish()
        }
    }

    private var resultSetting = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        if (result.resultCode != Activity.RESULT_OK){
            checkPermission()
        }
    }

    companion object {
        const val REQUEST = 601
        const val RESULT = 701
        private val REQUIRED_PERMISSIONS_LOCATION = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    //openSettingActivity.launch(Cons.REQ.LOCATION)

    /*private val openSettingActivity = registerForActivityResult(SettingActivityContract()) {
        checkPermission()
    }*/

    /*class SettingActivityContract : ActivityResultContract<Int, String?>() {

        override fun createIntent(context: Context, input: Int): Intent {

            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", context.packageName, null)

            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String? {
            return null
        }
    }*/
}