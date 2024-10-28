package com.example.playlistmaker.sharing.domain.repository

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(pair: Pair<Int, Int>)
    fun openLink(link: Int)
    fun openEmail(emailData: EmailData)
}