
@file:OptIn(androidx.media3.common.util.UnstableApi::class)
package com.example.treinoapp


import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.common.util.UnstableApi


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerExo(
    uri: Uri?,
    modifier: Modifier = Modifier,
    isLooping: Boolean = true,
    autoPlay: Boolean = true
) {
    if (uri == null) return

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Cria e lembra o ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            this.repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        }
    }

    // Prepara a mídia quando a URI mudar
    LaunchedEffect(uri) {
        Log.d("DEBUG", "VideoPlayerExo: preparando uri = $uri")
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        if (autoPlay) {
            exoPlayer.play()
        }
    }

    // Gerencia o ciclo de vida: pausa/release conforme a tela
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                }
                Lifecycle.Event.ON_STOP -> {
                    exoPlayer.stop()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    exoPlayer.release()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
        },
        modifier = modifier
    )
    }