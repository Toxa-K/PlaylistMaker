package com.example.playlistmaker.sharing.domain.model

import androidx.annotation.StringRes

data class EmailData(
    @StringRes val email: Int,
    @StringRes val subject: Int,
    @StringRes val body: Int
)
