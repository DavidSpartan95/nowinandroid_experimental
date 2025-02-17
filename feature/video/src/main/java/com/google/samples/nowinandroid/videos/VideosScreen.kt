/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.nowinandroid.videos

import YouTubePlaylist
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.runBlocking
import printYouTubeApiResponse
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import com.google.samples.apps.nowinandroid.core.designsystem.component.DynamicAsyncImage
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink

@Composable
fun VideosScreen() {
    var youtubeList: List<YouTubePlaylist> by remember { mutableStateOf(emptyList()) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row{
            Button(onClick = {
                printYouTubeApiResponse()
            }){
                Text("Load")
            }
            Button(onClick = {
                youtubeList = YouTubePlaylistStorage.getPlaylists()
            }){
                Text("fetch")
            }
        }

        LazyColumn {

            items(youtubeList) {video ->
                Text(
                    buildAnnotatedString {
                        withLink(
                            LinkAnnotation.Url(
                                "https://www.youtube.com/playlist?list=${video.id}",
                                TextLinkStyles(style = SpanStyle(color = Color.Blue))
                            )
                        ) {
                            append(video.snippet.title)
                        }
                    }
                )

                AsyncImage(
                    model = video.snippet.thumbnails.high.url,
                    contentDescription = null
                )
            }
        }
    }
}