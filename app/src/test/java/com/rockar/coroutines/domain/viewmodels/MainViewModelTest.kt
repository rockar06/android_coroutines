package com.rockar.coroutines.domain.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.rockar.coroutines.data.repositories.TitleRepositoryImpl
import com.rockar.coroutines.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    private var subject: MainViewModel = MainViewModel(
        TitleRepositoryImpl(
            MainNetworkFake("OK"),
            TitleDaoFake("initial")
        )
    )

    @Test
    fun `test that when MainClicked should be updated the taps`() {
        subject.onMainViewClicked()
        assertThat(subject.taps.getValueForTest()).isEqualTo("0 taps")
        coroutineScope.advanceTimeBy(1_000)
        assertThat(subject.taps.getValueForTest()).isEqualTo("1 taps")
    }

    @Test
    fun `test that loads a title by default`() {
        assertThat(subject.title.getValueForTest()).isEqualTo("initial")
    }

    @Test
    fun `when successful title loaded show and hides spinner`() = coroutineScope.runBlockingTest {
        val network = MainNetworkCompletableFake()

        subject = MainViewModel(
            TitleRepositoryImpl(
                network,
                TitleDaoFake("title")
            )
        )

        subject.spinner.captureValues {
            subject.onMainViewClicked()
            assertThat(values).isEqualTo(listOf(false, true))
            network.sendCompletionToAllCurrentRequests("OK")
            //coroutineScope.advanceTimeBy(5_000)
            assertThat(values).isEqualTo(listOf(false, true, false))
        }
    }

    @Test
    fun `when error loading title shows error and hide spinner`() = coroutineScope.runBlockingTest {
        val network = MainNetworkCompletableFake()
        subject = MainViewModel(
            TitleRepositoryImpl(
                network,
                TitleDaoFake("title")
            )
        )

        subject.spinner.captureValues {
            assertThat(values).isEqualTo(listOf(false))
            subject.onMainViewClicked()
            assertThat(values).isEqualTo(listOf(false, true))
            network.sendErrorToCurrentRequests(makeErrorResult("An error"))
            assertThat(values).isEqualTo(listOf(false, true, false))
        }
    }

    @Test
    fun `when error loading title shows error text`() = coroutineScope.runBlockingTest {
        val network = MainNetworkCompletableFake()
        subject = MainViewModel(
            TitleRepositoryImpl(
                network,
                TitleDaoFake("title")
            )
        )

        subject.onMainViewClicked()
        network.sendErrorToCurrentRequests(makeErrorResult("An error"))
        assertThat(subject.snackbar.getValueForTest()).isEqualTo("Unable to refresh title")
        subject.onSnackBarShown()
        assertThat(subject.snackbar.getValueForTest()).isEqualTo(null)
    }

    @Test
    fun `when MainViewClicked title should be refreshed`() = coroutineScope.runBlockingTest {
        val titleDao = TitleDaoFake("title")
        subject = MainViewModel(
            TitleRepositoryImpl(
                MainNetworkFake("OK"),
                titleDao
            )
        )
        subject.onMainViewClicked()
        assertThat(titleDao.nextInsertedOrNull()).isEqualTo("OK")
    }

    private fun makeErrorResult(result: String): HttpException {
        return HttpException(
            Response.error<String>(
                500,
                ResponseBody.create(
                    MediaType.get("application/json"),
                    "\"$result\""
                )
            )
        )
    }
}
