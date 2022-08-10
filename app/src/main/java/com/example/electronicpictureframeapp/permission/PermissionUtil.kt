package com.example.electronicpictureframeapp.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

object PermissionUtil {
    private const val REQUEST_PERMISSION_RESULT_CODE = 1000

    private var mPermissionConstant : PermissionConstant? = null // 체크 및 요청하고자 하는 권한
    private var mPermissionListener: PermissionListener? = null // 권한 획득 또는 거절 시 발생하는 이벤트 리스너

    /**
     * 권한 체크 및 요청
     */
    fun easyPermission(activity: Activity, permissionConstant: PermissionConstant, permissionListener: PermissionListener?) {
        mPermissionListener = permissionListener
        mPermissionConstant = permissionConstant

        val permissions = permissionConstant.getPermissions()

        when {
            checkPermissions(activity, permissions) -> {
                requestPermissions(activity, permissions, REQUEST_PERMISSION_RESULT_CODE)
            }
            // 한번이라도 거부했으면, 먼저 다이얼로그로 띄우고 묻는다.
            shouldShowRequestPermissionRationale(activity, permissions) -> {
                showPermissionContextPopup(activity, permissions, mPermissionConstant!!)
            }

            else -> {
                requestPermissions(activity, permissions, REQUEST_PERMISSION_RESULT_CODE)
            }
        }
    }

    private fun onPermissionGranted() {
        mPermissionListener?.onPermissionGranted()
        mPermissionListener = null
    }

    private fun onPermissionDenied() {
        mPermissionListener?.onPermissionDenied()
        mPermissionListener = null
    }

    /** 이전 거부한 내역이 있나 확인 */
    private fun shouldShowRequestPermissionRationale(activity: Activity, permissions: Array<String>) : Boolean {
        var hasPermission = false

        permissions.forEach { permission ->
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                hasPermission = true
                return@forEach
            }
        }

        return hasPermission
    }

    private fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
        var hasPermission = true
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false
                return@forEach
            }
        }
        return hasPermission
    }

    fun onPermissionsResult(activity: Activity, requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_RESULT_CODE) {
            var checkResult = true

            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    return@forEach
                }
            }

            if (checkResult) {
                onPermissionGranted()
            } else {
                val permissionsMsg = mPermissionConstant!!.getPermissionsMsgMap()
                val isEssential = permissionsMsg[KEY_DENIED_TITLE] != null

                // 필수일 경우에만 띄움
                if (isEssential) {
                    val permissionTitle = permissionsMsg[KEY_DENIED_TITLE]!!
                    val permissionMsg = permissionsMsg[KEY_DENIED_MSG]!!
                    showPermissionDialog(activity, permissionTitle, permissionMsg) {
                        // 설정으로이동
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:" + activity.packageName))
                            .apply { activity.startActivity(this) }
                    }
                }
                onPermissionDenied()
            }
        } else {
            Toast.makeText(activity, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPermissionContextPopup(activity: Activity, permissions: Array<String>, mPermissionConstant: PermissionConstant) {
        val permissionsMsg = mPermissionConstant.getPermissionsMsgMap()
        val permissionTitle = permissionsMsg[KEY_TITLE]
        val permissionMsg = permissionsMsg[KEY_MSG]

        AlertDialog.Builder(activity)
            .setTitle(permissionTitle)
            .setMessage(permissionMsg)
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(activity, permissions, REQUEST_PERMISSION_RESULT_CODE)
            }
            .setNegativeButton("취소하기") { _, _ ->
                onPermissionDenied()
            }
            .create()
            .show()
    }

    private fun showPermissionDialog(activity: Activity, titleRes: String, msgRes: String, onClick: () -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle(titleRes)
            .setMessage(msgRes)
            .setPositiveButton("설정") { _, _ ->
                onClick()
            }
            .setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }
}


