package com.rockar.coroutines.data.di

import android.content.Context
import androidx.room.Room
import com.rockar.coroutines.data.api.MainNetwork
import com.rockar.coroutines.data.databases.MainDatabase
import com.rockar.coroutines.data.databases.TitleDao
import com.rockar.coroutines.data.repositories.TitleRepository
import com.rockar.coroutines.data.repositories.TitleRepositoryImpl
import com.rockar.coroutines.data.utils.SkipNetworkInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class CoroutinesBinds {

    @Binds
    abstract fun bindTitleRepository(
        titleRepositoryImpl: TitleRepositoryImpl
    ): TitleRepository
}

@Module
@InstallIn(SingletonComponent::class)
class CoroutinesModule {

    @Provides
    fun providesTitleDao(appDatabase: MainDatabase): TitleDao = appDatabase.titleDao

    @Provides
    @Singleton
    fun provideAppDataBase(
        @ApplicationContext context: Context
    ): MainDatabase {
        return Room.databaseBuilder(context, MainDatabase::class.java, "title_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMainNetwork(
        okHttpClient: OkHttpClient
    ): MainNetwork {
        return Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MainNetwork::class.java)
    }

    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(SkipNetworkInterceptor())
            .build()
    }
}
