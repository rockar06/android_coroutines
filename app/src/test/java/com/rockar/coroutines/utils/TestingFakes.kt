package com.rockar.coroutines.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rockar.coroutines.data.api.MainNetwork
import com.rockar.coroutines.data.databases.TitleDao
import com.rockar.coroutines.data.model.Title
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import retrofit2.Call

class TitleDaoFake(initialTitle: String) : TitleDao {

    private val _titleLiveData = MutableLiveData(Title(initialTitle))
    private val insertedForNext = Channel<Title>(capacity = Channel.BUFFERED)

    override val titleLiveData: LiveData<Title?>
        get() = _titleLiveData

    override fun insertTitleSync(title: Title) {
        TODO("Not yet implemented")
    }

    override suspend fun insertTitle(title: Title) {
        insertedForNext.send(title)
        _titleLiveData.value = title
    }

    fun nextInsertedOrNull(timeout: Long = 2_000): String? {
        var result: String? = null
        runBlocking {
            try {
                withTimeout(timeout) {
                    result = insertedForNext.receive().title
                }
            } catch (ex: TimeoutCancellationException) {
                // ignore
            }
        }
        return result
    }
}

class MainNetworkFake(var result: String) : MainNetwork {
    override fun fetchNextTitleAsync(): Call<String> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchNextTitle() = result
}

class MainNetworkCompletableFake() : MainNetwork {
    private var completable = CompletableDeferred<String>()

    override fun fetchNextTitleAsync(): Call<String> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchNextTitle(): String = completable.await()

    fun sendCompletionToAllCurrentRequests(result: String) {
        completable.complete(result)
        completable = CompletableDeferred()
    }

    fun sendErrorToCurrentRequests(throwable: Throwable) {
        completable.completeExceptionally(throwable)
        completable = CompletableDeferred()
    }
}
