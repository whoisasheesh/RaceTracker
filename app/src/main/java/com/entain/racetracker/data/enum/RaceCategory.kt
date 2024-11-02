package com.entain.racetracker.data.enum

import androidx.annotation.DrawableRes
import com.entain.racetracker.R
import com.entain.racetracker.utils.Constants

enum class RaceCategory(
    val categoryId: String,
    @DrawableRes val categoryIcon: Int,
    val categoryName: String
) {
    CATEGORY_HARNESS(
        categoryId = Constants.CATEGORY_HARNESS,
        categoryIcon = R.drawable.ic_category_harness,
        categoryName = "Harness"
    ),
    CATEGORY_HORSE(
        categoryId = Constants.CATEGORY_HORSE,
        categoryIcon = R.drawable.ic_category_horse,
        categoryName = "Horse"
    ),
    CATEGORY_GREYHOUND(
        categoryId = Constants.CATEGORY_GREYHOUND,
        categoryIcon = R.drawable.ic_category_greyhound,
        categoryName = "GreyHound"
    );

    companion object {
        fun fromCategoryId(id: String): RaceCategory? = entries.find { it.categoryId == id }
    }
}