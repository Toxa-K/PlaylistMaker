package com.example.playlistmaker.di.dataModel

import androidx.room.Room
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.mediateca.data.db.AppDatabase
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.iTunesAPI
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module{

    single<iTunesAPI>{
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}