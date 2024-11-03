package com.entain.racetracker.presentation.screens.race_lising_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entain.racetracker.data.enum.RaceCategory
import com.entain.racetracker.data.model.RaceSummary
import com.entain.racetracker.ui.theme.ColorBlack
import com.entain.racetracker.ui.theme.ColorDarkBrown
import com.entain.racetracker.ui.theme.ColorPrimary
import com.entain.racetracker.ui.theme.ColorRed
import com.entain.racetracker.utils.Constants
import kotlinx.coroutines.delay

@Composable
fun RaceItem(race: RaceSummary) {
    var remainingTime by remember {
        mutableLongStateOf(race.advertised_start.seconds - System.currentTimeMillis() / Constants.THOUSAND_MILLISECONDS)
    }

    LaunchedEffect(key1 = race.advertised_start.seconds) {
        remainingTime =
            race.advertised_start.seconds - System.currentTimeMillis() / Constants.THOUSAND_MILLISECONDS
        while (remainingTime > -Constants.FIFTY_NINE_SEC) {
            delay(1000L)
            remainingTime -= 1
        }
    }

    val raceAdvertisedTimerText =
        if (remainingTime < Constants.FIFTY_NINE_SEC && remainingTime > -Constants.FIFTY_NINE_SEC) {
            "$remainingTime s"
        } else {
            val minutesRemaining = remainingTime / 60
            val secondsRemaining = remainingTime % 60
            "${minutesRemaining}m ${secondsRemaining}s"
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 4.dp, top = 4.dp, end = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val categoryIconId = RaceCategory.fromCategoryId(race.category_id)?.categoryIcon

            categoryIconId?.let { painterResource(id = it) }?.let {
                Icon(
                    painter = it, contentDescription = RaceCategory.fromCategoryId(race.category_id)?.categoryName,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(40.dp),
                    tint = ColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(0.5f)
            ) {

                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = race.meeting_name,
                    style = TextStyle(
                        color = ColorBlack,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        fontFamily = FontFamily.SansSerif
                    ),
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = "Race ${race.race_number}",
                    style = TextStyle(
                        color = ColorDarkBrown,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.SansSerif
                    ),
                    textAlign = TextAlign.Start,
                )
            }

            Row(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(end = 8.dp, start = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = raceAdvertisedTimerText,
                    style = TextStyle(
                        color = ColorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                )
            }
        }
    }
}