package com.example.playlistmaker.settings.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.use_case.GetThemeUseCase
import com.example.playlistmaker.settings.domain.use_case.SwitchThemeUseCase
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData


class SettingsViewModel(
    private val switchTheme:SwitchThemeUseCase,
    private val sharingInter: SharingInteractor,
    private val getThemeUseCase: GetThemeUseCase
) :ViewModel() {

    private val _settingsLiveData = MutableLiveData<SettingsModel>()
    fun settingsModelLiveData(): LiveData<SettingsModel> = _settingsLiveData

    init {
        val isThemeEnabled = getThemeUseCase.execute()
        _settingsLiveData.value = SettingsModel(isThemeEnabled)
    }

    fun toggleTheme(isEnabled: Boolean) {
            switchTheme.execute(isEnabled)
            _settingsLiveData.value = SettingsModel(isEnabled)
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
