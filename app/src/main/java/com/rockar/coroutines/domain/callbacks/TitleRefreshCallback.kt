package com.rockar.coroutines.domain.callbacks

interface TitleRefreshCallback {
    fun onCompleted()
    fun onError(cause: Throwable)
}
