package com.example.playlistmaker.settings.presenter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.repository.ThemeRepository
import com.example.playlistmaker.settings.domain.use_case.SwitchThemeUseCase
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData


class SettingsViewModel(
    private val switchTheme:SwitchThemeUseCase,
    private val sharingInter: SharingInteractor,
    private val themeRepository: ThemeRepository
) :ViewModel() {

    private val settingsLiveData = MutableLiveData<SettingsModel>()
    fun getSettingsModelLiveData(): LiveData<SettingsModel> = settingsLiveData

    init {
        val isThemeEnabled = themeRepository.getThemeSetting()
//            getTheme().getThemeSetting()
        settingsLiveData.value = SettingsModel(isThemeEnabled)
    }

    fun toggleTheme(isEnabled: Boolean) {
        settingsLiveData.value?.isThemeEnabled = isEnabled
        switchTheme.execute(isEnabled)
    }



    fun shareApp() {
        val message =R.string.share_message
        val email= R.string.curse_email
        sharingInter.shareApp(message = message, email = email)
    }

    fun support() {
        val emailData = EmailData(
            email = R.string.mymail,
            subject = R.string.support_subject,
            body = R.string.support_body
        )
        sharingInter.openSupport(emailData)
    }

    fun agree() {
        val link = R.string.offer
        sharingInter.openTerms(link)
    }

    companion object{
    }
}
