package com.rockar.coroutines.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.rockar.coroutines.data.api.MainNetwork
import com.rockar.coroutines.data.databases.TitleDao
import com.rockar.coroutines.data.model.Title
import com.rockar.coroutines.data.utils.BACKGROUND
import com.rockar.coroutines.domain.callbacks.TitleRefreshCallback
import com.rockar.coroutines.domain.model.TitleRefreshError
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class TitleRepositoryImpl @Inject constructor(
    private val mainNetwork: MainNetwork,
    private val titleDao: TitleDao
) : TitleRepository {

    override val title: LiveData<String?> = titleDao.titleLiveData.map { it?.title }

    override suspend fun refreshTitle() {
        // 8 lines of code
        try {
            val result = withTimeout(5_000) {
                mainNetwork.fetchNextTitle()
            }
            titleDao.insertTitle(Title(result))
        } catch (error: Throwable) {
            throw TitleRefreshError("Unable to refresh title", error)
        }
    }

    override fun refreshTitleWithCallbacks(callback: TitleRefreshCallback) {
        // 15 lines of code
        BACKGROUND.submit {
            try {
                val result = mainNetwork.fetchNextTitleAsync().execute()
                if (result.isSuccessful) {
                    titleDao.insertTitleSync(
                        Title(result.body().orEmpty())
                    )
                    callback.onCompleted()
                } else {
                    callback.onError(TitleRefreshError("Unable to refresh title", null))
                }
            } catch (cause: Throwable) {
                callback.onError(TitleRefreshError("Unable to refresh title", cause))
            }
        }
    }
}
