package com.example.vn008xw.myapplication.feature

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.example.permissionshero.PermissionsActivityHelper
import com.example.permissionshero.contracts.PermissionsHelperContract

class MainActivity : AppCompatActivity(), PermissionsHelperContract.Callback {

  private val TAG = MainActivity::class.java.simpleName
  private val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

  private lateinit var permissionsHelper: PermissionsActivityHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    permissionsHelper = PermissionsActivityHelper.newInstance(this)

    val button = findViewById<Button>(R.id.location_button)
    button.setOnClickListener {
      permissionsHelper.makeRequest(LOCATION_PERMISSION)
    }
  }

  override fun onPermissionGranted() {
    Log.d(TAG, " Location granted")
  }

  override fun onPermissionDeclined(permission: String) {

  }

  override fun onPermissionNeedsRationale(permission: String) {
    AlertDialog.Builder(this)
        .setTitle(R.string.location_rationale_title)
        .setMessage(R.string.location_rationale_message)
        .setPositiveButton(android.R.string.ok, { _, _ ->
          permissionsHelper.makeRequest(LOCATION_PERMISSION)
        })
        .setNegativeButton(R.string.cancel, null)
        .show()
  }

  override fun onPermissionDeclinedForever(permission: String) {
    // These shouldn't be hardcoded
    AlertDialog.Builder(this)
        .setTitle(R.string.location_settings_title)
        .setMessage(R.string.location_settings_message)
        .setPositiveButton(R.string.location_settings_update) { _, _ ->
          showSettingsScreen()
        }
        .setNegativeButton(R.string.cancel, null)
        .show()
  }

  fun showSettingsScreen() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.parse("package:" + packageName)
    intent.data = uri
    startActivity(intent)
  }
}
