package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigator (private val context: Context) {



    fun shareLink(pair: Pair<Int, Int>) {
        val message = context.getString(pair.first) + context.getString(pair.second)
        val intent = Intent().apply {
            Log.d("ExternalNavigator", "Creating intent for sharing")
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(intent, context.getString(R.string.share))
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)

    }


    fun openLink(link: Int) {
        Log.d("ExternalNavigator", "Opening link: ${context.getString(link)}")
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(context.getString(link))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openEmail(emailData: EmailData) {
        Log.d("ExternalNavigator", "Opening email to: ${emailData.email}")
        val intent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL,context.getString(emailData.email ))
            putExtra(Intent.EXTRA_TEXT, context.getString(emailData.body))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(emailData.subject))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

