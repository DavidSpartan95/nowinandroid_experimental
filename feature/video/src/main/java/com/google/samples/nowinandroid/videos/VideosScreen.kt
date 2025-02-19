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

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaLoadingWheel
import com.google.samples.apps.nowinandroid.feature.videos.R
import com.google.samples.nowinandroid.videos.VideosViewModel.VideosUiState.Empty
import com.google.samples.nowinandroid.videos.VideosViewModel.VideosUiState.Loading
import com.google.samples.nowinandroid.videos.VideosViewModel.VideosUiState.Success
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme

@Composable
fun VideosScreen(
    viewModel: VideosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when(uiState){
            Loading -> NiaLoadingWheel(
                modifier = Modifier,
                contentDesc = stringResource(id = R.string.feature_video_loading),
            )

            Empty -> Text(text = "No Videos Found")

            is Success -> LazyColumn {

                items((uiState as Success).playlists) { playList ->

                    ListItem(
                        leadingContent = {
                            AsyncImage(
                                model = playList.snippet.thumbnails.medium.url,
                                contentDescription = null,
                            )
                        },
                        headlineContent = { Text(playList.snippet.title) },
                        modifier = Modifier
                            .clickable {
                                // Open the URL when the image is clicked
                                val uri = "https://www.youtube.com/playlist?list=${playList.id}"

                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                context.startActivity(intent)
                            },
                        overlineContent = { Text("Play List") },
                        supportingContent = { Text(playList.snippet.description) },
                        colors = ListItemDefaults.colors(
                            containerColor = if (false) {
                                MaterialTheme.colorScheme.surfaceVariant
                            } else {
                                Color.Transparent
                            },
                        ),
                    )
                }
            }
        }
    }
}