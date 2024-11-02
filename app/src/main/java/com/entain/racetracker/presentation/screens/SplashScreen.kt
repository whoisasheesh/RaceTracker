package com.entain.racetracker.presentation.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.entain.racetracker.R
import com.entain.racetracker.presentation.components.AnimatedProgressBar
import com.entain.racetracker.presentation.graph.RaceTrackerAppScreen
import com.entain.racetracker.ui.theme.ColorGray
import com.entain.racetracker.ui.theme.ColorWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val animationDuration = 2000
    val splashDuration = 2000L
    val scale = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.97f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(splashDuration)
        navController.navigate(route = RaceTrackerAppScreen.RACE_LIST.route) {
            popUpTo(route = RaceTrackerAppScreen.SPLASH.route) {
                inclusive = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(ColorWhite),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(R.drawable.splash_screen)
                .decoderFactory(GifDecoder.Factory())
                .size(Size.ORIGINAL)
                .build()
        )

        Text(
            text = stringResource(R.string.next_race_no_stress),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                fontFamily = FontFamily.Cursive,
                color = ColorGray
            ),

            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)

        )

        Image(
            painter = painter,
            contentDescription = stringResource(R.string.racetracker_app_logo),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
                .wrapContentSize(Alignment.Center)
        )
        AnimatedProgressBar(progress = scale.value, animationDuration = animationDuration)
        Text(
            text = stringResource(R.string.inspired_by_neds),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = FontFamily.Cursive,
                color = ColorGray,
                letterSpacing = 2.sp
            ),
            modifier = Modifier
                .padding(top = 8.dp)
        )
    }
}