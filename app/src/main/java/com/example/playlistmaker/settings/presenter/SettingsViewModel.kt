package com.example.playlistmaker.presentation.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.presenter.SettingsModel
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.util.Creator.getTheme

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    companion object{
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
    private val settingsLiveData = MutableLiveData<SettingsModel>()
    fun getSettingsModelLiveData(): LiveData<SettingsModel> = settingsLiveData


    init {
        val isThemeEnabled = getTheme(application).getThemeSetting()
        settingsLiveData.value = SettingsModel(isThemeEnabled)
    }

    fun toggleTheme(isEnabled: Boolean) {
        settingsLiveData.value?.isThemeEnabled = isEnabled
        Creator.provideSwitchThemeUseCase(getApplication()).execute(isEnabled)
    }

    fun shareApp(): Intent {
        return Intent().apply {
            val message = getApplication<Application>().getString(R.string.share_message) +
                    getApplication<Application>().getString(R.string.curse_email)
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
    }

    fun support(): Intent {
        return Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getApplication<Application>().getString(R.string.mymail)))
            putExtra(Intent.EXTRA_TEXT, getApplication<Application>().getString(R.string.support_body))
            putExtra(Intent.EXTRA_SUBJECT, getApplication<Application>().getString(R.string.support_subject))
        }
    }

    fun agree(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(getApplication<Application>().getString(R.string.offer))
        }
    }
}