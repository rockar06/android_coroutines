package com.rockar.coroutines.domain.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rockar.coroutines.data.repositories.TitleRepository
import com.rockar.coroutines.data.utils.BACKGROUND
import com.rockar.coroutines.domain.callbacks.TitleRefreshCallback
import com.rockar.coroutines.domain.model.TitleRefreshError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TitleRepository
) : ViewModel() {

    private val _snackBar = MutableLiveData<String?>()

    val snackbar: LiveData<String?> = _snackBar

    val title = repository.title

    private val _spinner = MutableLiveData(false)

    val spinner: LiveData<Boolean> = _spinner

    private var tapCount = 0

    private val _taps = MutableLiveData("$tapCount taps")

    val taps: LiveData<String> = _taps

    fun onMainViewClicked() {
        /*refreshTitleWithCallbacks()
        updateTapsAsync()*/
        refreshTitle()
        updateTaps()
    }

    private fun updateTapsAsync() {
        BACKGROUND.submit {
            Thread.sleep(1_000)
            _taps.postValue("${++tapCount} taps")
        }
    }

    private fun updateTaps() {
        viewModelScope.launch {
            delay(1_000)
            _taps.postValue("${++tapCount} taps")
        }
    }

    fun onSnackBarShown() {
        _snackBar.value = null
    }

    private fun refreshTitle() {
        launchDataLoad { repository.refreshTitle() }
    }

    private fun launchDataLoad(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: TitleRefreshError) {
                _snackBar.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }

    private fun refreshTitleWithCallbacks() {
        _spinner.value = true
        repository.refreshTitleWithCallbacks(object : TitleRefreshCallback {
            override fun onCompleted() {
                _spinner.postValue(false)
            }

            override fun onError(cause: Throwable) {
                _snackBar.postValue(cause.message)
                _spinner.postValue(false)
            }
        })
    }
}
