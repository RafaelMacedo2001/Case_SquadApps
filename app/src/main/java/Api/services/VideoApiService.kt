package Api.services

import com.example.motivation.VideoDevices
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Path

interface VideoApiService {

    data class VideoDeviceResponse(val data: List<VideoDevices>)

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjlhMmU0YTczLWNiODktNGUzZS1hMGE5LTYwODYxZDM3NWYwMSIsImlhdCI6MTY4MjAwODgzMSwiZXhwIjoxNjg0NjAwODMxfQ._WZkRusg8qj-kWeqVoFD3yVRdRneWmx7voHo2Jk7XU0")
    @GET("http://squadapps.ddns-intelbras.com.br:3000/video-devices/")
    fun getvideoDevices(): Call<VideoDeviceResponse>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjlhMmU0YTczLWNiODktNGUzZS1hMGE5LTYwODYxZDM3NWYwMSIsImlhdCI6MTY4MjAwODgzMSwiZXhwIjoxNjg0NjAwODMxfQ._WZkRusg8qj-kWeqVoFD3yVRdRneWmx7voHo2Jk7XU0")
    @DELETE("{id}")
    suspend fun deleteVideoDevice(@Path("id") deviceId: String): Response<Unit>
}

