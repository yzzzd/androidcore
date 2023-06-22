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
import androidx.core.app.ActivityCompat
import com.crocodic.core.R
import com.crocodic.core.databinding.CrActivityPermissionCameraBinding
import com.crocodic.core.extension.allPermissionsGranted
import com.crocodic.core.helper.util.ClickPrevention
import com.crocodic.core.ui.dialog.PermissionSettingDialog

/**
 * Created by @yzzzd on 4/22/18.
 */

class CameraPermissionActivity : AppCompatActivity(), ClickPrevention {

    private var binding: CrActivityPermissionCameraBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CrActivityPermissionCameraBinding.inflate(layoutInflater)
        binding?.let { setContentView(it.root) }

        setResult(RESULT)
        binding?.buttonPositive?.setOnClickListener(this)
        binding?.buttonNegative?.setOnClickListener(this)
    }

    private fun checkPermission() {
        val granted = allPermissionsGranted(REQUIRED_PERMISSIONS_CAMERA)

        if (granted) {
            finish()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS_CAMERA, REQUEST)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding?.buttonPositive -> checkPermission()
            binding?.buttonNegative -> onBackPressed()
        }
        super.onClick(v)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST) {
            if (allPermissionsGranted(REQUIRED_PERMISSIONS_CAMERA)) {
                finish()
            } else {
                PermissionSettingDialog(this, this)
                    .setContent(R.drawable.img_grant_camera, R.string.cr_info_permission_camera)
                    .onButtonClick {
                        it.dismiss()

                        val setting = Intent()
                        setting.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        setting.data = Uri.fromParts("package", packageName, null)

                        resultSetting.launch(setting)
                    }
                    .show()
            }
        }
    }

    private var resultSetting = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            checkPermission()
        }
    }

    companion object {
        const val CONTENT = "content"
        const val REQUEST = 602
        const val RESULT = 702
        private val REQUIRED_PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)
    }
}