package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.R

import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): Pair<Int,Int> {

        return Pair(R.string.share_message,  R.string.curse_email)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = R.string.mymail,
            subject = R.string.support_subject,
            body = R.string.support_body
        )
    }

    private fun getTermsLink(): Int {
        return  R.string.offer
    }
}