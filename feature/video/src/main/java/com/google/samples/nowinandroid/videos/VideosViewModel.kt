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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fetchYouTubePlayList
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VideosViewModel @Inject constructor(
    // You can inject dependencies here if needed
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideosUiState>(VideosUiState.Loading)
    val uiState: StateFlow<VideosUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {
            fetchYouTubePlayList()
            YouTubePlaylistStorage.playlists.collect { newPlaylists ->
                _uiState.value = if (newPlaylists.isEmpty()) {
                    VideosUiState.Empty
                } else {
                    VideosUiState.Success(newPlaylists)
                }
            }
        }
    }
    sealed interface VideosUiState {
        data object Loading : VideosUiState
        data class Success(val playlists: List<YouTubePlaylist>) : VideosUiState
        data object Empty : VideosUiState
    }
}
