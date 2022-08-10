package com.example.electronicpictureframeapp.permission

import android.Manifest

const val KEY_TITLE = "title" // 권한 다이어로그 제목
const val KEY_MSG = "msg" // 권한 다이어로그 내용
const val KEY_DENIED_TITLE = "deniedTitle" // 권한 거부 시, 나오는 제목
const val KEY_DENIED_MSG = "deniedMsg" // 권한 거부 시, 나오는 내용

enum class PermissionConstant(_permissions: Array<String>, _permissionMap: HashMap<String, String>) {
    // 저장공간 쓰기
    // 저장공간
    EXTERNAL_STORAGE_PERMISSIONS(
    arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE
    ),
    HashMap<String, String>().apply {
        put(KEY_TITLE, "권한이 필요합니다.")
        put(KEY_MSG, "전자액자에 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
        put(KEY_DENIED_TITLE, "저장공간 권한이 필요합니다.")
        put(KEY_DENIED_MSG, "사진 및 미디어 파일 등록을 위해서는 \\'저장 공간\\' 권한이 필수입니다.")
    });

    private val permissions = _permissions
    private val permissionsMsgMap = _permissionMap

    fun getPermissions() = permissions
    fun getPermissionsMsgMap() = permissionsMsgMap
}