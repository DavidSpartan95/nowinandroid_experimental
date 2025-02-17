import androidx.tracing.trace
import com.google.samples.apps.nowinandroid.core.network.model.*
import com.google.samples.apps.nowinandroid.core.network.retrofit.APIKEY
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.coroutines.*
import java.util.Properties
import java.io.FileInputStream

interface YouTubeApi {
    @GET("playlists")
    suspend fun getPlaylists(
        @Query("part") part: String = "snippet,contentDetails",
        @Query("channelId") channelId: String,
        @Query("maxResults") maxResults: Int = 2,
        @Query("key") apiKey: String
    ): String
}

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
    private val playlists = mutableListOf<YouTubePlaylist>()

    fun savePlaylists(items: List<YouTubePlaylist>) {
        playlists.clear()
        playlists.addAll(items)
    }

    fun getPlaylists(): List<YouTubePlaylist> = playlists
}

fun printYouTubeApiResponse() {
    val apiKey = APIKEY

    // Launch a coroutine to perform the network request off the main thread
    GlobalScope.launch(Dispatchers.Main) {
        try {
            // Switch to IO dispatcher for network operations
            withContext(Dispatchers.IO) {
                val url = "https://youtube.googleapis.com/youtube/v3/playlists?part=snippet&channelId=UCKNTZMRHPLXfqlbdOI7mCkg&maxResults=2&key=$apiKey"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                // Check if response is successful
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    if (responseBody != null) {
                        println("Raw Response: $responseBody")

                        try {
                            // Configure JSON to ignore unknown keys like "etag"
                            val json = Json { ignoreUnknownKeys = true }

                            // Attempt to parse the JSON response
                            val parsedResponse = json.decodeFromString<YouTubeApiResponse>(responseBody)
                            YouTubePlaylistStorage.savePlaylists(parsedResponse.items)
                            println("Saved Playlists: ${YouTubePlaylistStorage.getPlaylists()}")
                        } catch (e: Exception) {
                            println("Error parsing the API response: ${e.message}")
                        }
                    } else {
                        println("API Error: Empty response body")
                    }
                } else {
                    println("API Error: ${response.message}")
                }
            }
        } catch (e: Exception) {
            println("API Error: ${e.message}")
        }
    }
}