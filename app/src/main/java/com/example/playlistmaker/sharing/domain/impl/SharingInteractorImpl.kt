package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.R

import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp(message: Int, email: Int) {
        externalNavigator.shareLink(Pair(message, email))
    }

    override fun openTerms(link:Int) {
        externalNavigator.openLink(link)
    }

    override fun openSupport(emailData: EmailData) {
        externalNavigator.openEmail(emailData)
    }

}