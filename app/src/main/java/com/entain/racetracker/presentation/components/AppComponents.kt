package com.entain.racetracker.presentation.components

import android.os.Process
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entain.racetracker.R
import com.entain.racetracker.data.enum.RaceCategory
import com.entain.racetracker.ui.theme.ColorGray
import com.entain.racetracker.ui.theme.ColorPrimary
import com.entain.racetracker.ui.theme.ColorRed
import com.entain.racetracker.ui.theme.ColorWhite


@Composable
fun AnimatedProgressBar(progress: Float, animationDuration: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = animationDuration),
        label = "animated_progress_bar"
    )
    LinearProgressIndicator(
        progress = { animatedProgress },
        color = ColorPrimary,
        trackColor = ColorGray,
        modifier = Modifier
            .height(12.dp)
            .padding(start = 32.dp, end = 32.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
    )
}

@Composable
fun ExitDialog(onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(R.string.exit_app)) },
        text = { Text(text = stringResource(R.string.are_you_sure_you_want_to_exit_the_app)) },
        confirmButton = {
            TextButton(onClick = {
                Process.killProcess(Process.myPid())
            }) {
                Text(text = stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.no))
            }
        })

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RaceTrackerAppTopBar(
    title: String,
) {
    TopAppBar(
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = ColorPrimary, titleContentColor = ColorWhite

        ),
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .padding(start = 12.dp, end = 4.dp)
                    .height(40.dp)
                    .width(40.dp),
                painter = painterResource(id = R.drawable.ic_next_races),
                tint = Color.White,
                contentDescription = stringResource(R.string.next_race_icon)
            )

        },
    )
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun FilterRaceCategoryCbMenu(
    checkedState: List<Boolean>,
    onCheckedChange: (Int, Boolean) -> Unit
) {
    val items = RaceCategory.entries
    Row(

        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .padding(end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.filter_races),
            modifier = Modifier.padding(start = 8.dp, end = 4.dp),
            style = TextStyle(
                color = ColorPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontFamily = FontFamily.SansSerif
            ),
        )
        items.forEachIndexed { index, item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    tint = Color.Red,
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp),
                    painter = painterResource(item.categoryIcon),
                    contentDescription = item.categoryName
                )

                val context = LocalContext.current
                Checkbox(
                    checked = checkedState.getOrNull(index) ?: false,
                    onCheckedChange = { checked ->
                        onCheckedChange(index, checked)
                    },
                    modifier = Modifier.semantics {
                        contentDescription = context.getString(R.string.filter_checkbox_for) + item.categoryName
                    }
                )
            }
        }
    }
}

@Composable
fun ErrorMessage(error: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = ColorRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontFamily = FontFamily.SansSerif
            ),
            text = error ?: stringResource(R.string.unknown_error),
        )
    }
}















