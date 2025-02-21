import com.google.samples.apps.nowinandroid.core.network.BuildConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@Serializable
data class Thumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

@Serializable
data class Thumbnails(
    val default: Thumbnail,
    val medium: Thumbnail,
    val high: Thumbnail,
    val standard: Thumbnail? = null,
    val maxres: Thumbnail? = null
)

@Serializable
data class YouTubeSnippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails
)

@Serializable
data class YouTubePlaylist(
    val id: String,
    val snippet: YouTubeSnippet
)

@Serializable
data class YouTubeApiResponse(
    val items: List<YouTubePlaylist>
)

object YouTubePlaylistStorage {
    private val _playlists = MutableStateFlow<List<YouTubePlaylist>>(emptyList())
    val playlists: StateFlow<List<YouTubePlaylist>> get() = _playlists

    fun savePlaylists(items: List<YouTubePlaylist>) {
        _playlists.value = items
    }

    fun getPlaylists(): List<YouTubePlaylist> = _playlists.value
}

suspend fun fetchYouTubePlayList() {

    val apiKey = BuildConfig.API_KEY
    withContext(Dispatchers.IO) {
        val url = "https://youtube.googleapis.com/youtube/v3/playlists?part=snippet&channelId=UCKNTZMRHPLXfqlbdOI7mCkg&maxResults=25&key=$apiKey"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            response.body?.string()?.let { responseBody ->
                val json = Json { ignoreUnknownKeys = true }
                val parsedResponse = json.decodeFromString<YouTubeApiResponse>(responseBody)
                YouTubePlaylistStorage.savePlaylists(parsedResponse.items)
            }
        } else {
            println("API Error: ${response.message}")
        }
    }
}
