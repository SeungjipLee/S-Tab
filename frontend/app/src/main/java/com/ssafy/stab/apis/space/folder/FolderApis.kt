package com.ssafy.stab.apis.space.folder

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create


fun getFileList(folderId: String){
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val call = apiService.getFileList(authorizationHeader, folderId)

    call.enqueue(object : Callback<FileListResponse> {
        override fun onResponse(call: Call<FileListResponse>, response: Response<FileListResponse>) {
            if (response.isSuccessful) {
                // 성공적으로 데이터를 받아왔을 때
                val fileListResponse = response.body()
                fileListResponse?.let {
                    // 응답 데이터 처리, 예: 리스트 출력
                    Log.d("Folders :", it.folders.toString())
                    Log.d("Notes :", it.notes.toString())
                }
            } else {
                // 서버로부터 예상치 못한 응답을 받았을 때 (예: 404, 500 등)
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<FileListResponse>, t: Throwable) {
            // 네트워크 요청 실패 혹은 예외 발생 시
            println("Error fetching file list: ${t.message}")
        }
    })

}

fun createFolder(parentFolderId: String, title: String) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val createFolderRequest = CreateFolderRequest(parentFolderId, title)
    val call = apiService.createFolder(authorizationHeader, createFolderRequest)

    call.enqueue(object: retrofit2.Callback<Folder> {
        override fun onResponse(call: Call<Folder>, response: Response<Folder>) {
            Log.d("a", response.toString())
            if (response.isSuccessful && response.body() != null) {
                Log.i("APIResponse", "Successful response: ${response.body()}")
            } else {
                Log.e("APIResponse", "API Call failed!")
            }
        }

        override fun onFailure(call: Call<Folder>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun renameFolder(folderId: String, title: String) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val renameFolderRequest = RenameFolderRequest(folderId, title)
    val call = apiService.renameFolder(authorizationHeader, renameFolderRequest)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.d("APIResponse", response.toString())
        }

        override fun onFailure(call: Call<Void>, p1: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun relocateFolder(folderId: String, parentFolderId: String){
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val relocateFolderRequest = RelocateFolderRequest(folderId, parentFolderId)
    val call = apiService.relocateFolder(authorizationHeader, relocateFolderRequest)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.d("APIResponse", response.toString())
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun deleteFolder(folderId: String) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val call = apiService.deleteFolder(authorizationHeader, folderId)

    call.enqueue(object: Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.d("APIResponse", response.toString())
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}