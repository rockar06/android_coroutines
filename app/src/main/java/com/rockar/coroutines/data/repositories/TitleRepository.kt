package com.rockar.coroutines.data.repositories

import androidx.lifecycle.LiveData
import com.rockar.coroutines.domain.callbacks.TitleRefreshCallback

interface TitleRepository {

    val title: LiveData<String?>

    suspend fun refreshTitle()

    fun refreshTitleWithCallbacks(callback: TitleRefreshCallback)
}
