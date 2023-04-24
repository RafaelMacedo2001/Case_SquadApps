import com.example.motivation.VideoDevices
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface VideoApiService {

    data class VideoDeviceResponse(val data: List<VideoDevices>)

    data class VideoDevice(val id: String, val name: String, val serial: String, val username: String, val password: String)

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjlhMmU0YTczLWNiODktNGUzZS1hMGE5LTYwODYxZDM3NWYwMSIsImlhdCI6MTY4MjAwODgzMSwiZXhwIjoxNjg0NjAwODMxfQ._WZkRusg8qj-kWeqVoFD3yVRdRneWmx7voHo2Jk7XU0")
    @GET("http://squadapps.ddns-intelbras.com.br:3000/video-devices/")
    fun getvideoDevices(): Call<VideoDeviceResponse>
}