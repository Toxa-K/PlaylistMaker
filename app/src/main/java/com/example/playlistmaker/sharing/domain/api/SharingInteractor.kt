package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.model.EmailData

interface SharingInteractor {
    fun shareApp(message: Int, email: Int)
    fun openTerms(link: Int)
    fun openSupport(emailData: EmailData)
}