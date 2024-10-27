package com.example.playlistmaker.settings.presenter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.use_case.SwitchThemeUseCase
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.util.Creator.getTheme

class SettingsViewModel(
    private val switchTheme:SwitchThemeUseCase,
    private val sharingInter: SharingInteractor
) :ViewModel() {

    private val settingsLiveData = MutableLiveData<SettingsModel>()
    fun getSettingsModelLiveData(): LiveData<SettingsModel> = settingsLiveData

    init {

        val isThemeEnabled = getTheme().getThemeSetting()
        settingsLiveData.value = SettingsModel(isThemeEnabled)
    }

    fun toggleTheme(isEnabled: Boolean) {
        settingsLiveData.value?.isThemeEnabled = isEnabled
        switchTheme.execute(isEnabled)
    }


    fun shareApp() {
        sharingInter.shareApp()
    }

    fun support() {
        sharingInter.openSupport()
    }

    fun agree() {
        sharingInter.openTerms()
    }

    companion object{
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val switchTheme = Creator.provideSwitchThemeUseCase()
                val sharingInter = Creator.provideSharingInteractor()
                SettingsViewModel(
                    switchTheme,
                    sharingInter

                )
            }
        }
    }
}
