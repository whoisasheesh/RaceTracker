package com.entain.racetracker.screens.race_list

import com.entain.racetracker.data.model.AdvertisedStart
import com.entain.racetracker.data.model.RaceState
import com.entain.racetracker.data.model.RaceSummary
import com.entain.racetracker.data.network.ConnectionState
import com.entain.racetracker.data.network.NetworkConnectivityService
import com.entain.racetracker.domain.usecases.DeleteExpiredRacesUseCase
import com.entain.racetracker.domain.usecases.FetchNextToGoRacesUseCase
import com.entain.racetracker.presentation.viewmodel.RaceListViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class RaceListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: RaceListViewModel
    private val networkConnectivityService: NetworkConnectivityService =
        mock(NetworkConnectivityService::class.java)
    private val fetchNextToGoRacesUseCase: FetchNextToGoRacesUseCase =
        mock(FetchNextToGoRacesUseCase::class.java)
    private val deleteExpiredRacesUseCase: DeleteExpiredRacesUseCase =
        mock(DeleteExpiredRacesUseCase::class.java)

    private var raceSummaries = emptyList<RaceSummary>()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(networkConnectivityService.connectionState).thenReturn(
            MutableStateFlow(
                ConnectionState.Available
            )
        )
        `when`(fetchNextToGoRacesUseCase.invoke(anyLong())).thenReturn(MutableStateFlow(RaceState.Loading()))
        `when`(deleteExpiredRacesUseCase.invoke(anyLong(), anyList())).thenReturn(
            MutableStateFlow(
                RaceState.Loading()
            )
        )

        viewModel = RaceListViewModel(
            fetchNextToGoRacesUseCase = fetchNextToGoRacesUseCase,
            deleteExpiredRacesUseCase = deleteExpiredRacesUseCase,
            networkConnectivityService = networkConnectivityService
        )

        raceSummaries =
            listOf(
                RaceSummary(
                    race_id = "1ee60e62-a256-4c46-8813-eafc46300c5b",
                    race_number = 1,
                    meeting_name = "Geelong",
                    category_id = "161d9be2-e909-4326-8c2c-35ed71fb460b",
                    advertised_start = AdvertisedStart(System.currentTimeMillis() / 1000)
                )
            )
    }

    @Test
    fun testObserveConnectivityState(): Unit = runBlocking {
        val expectedState = ConnectionState.Available
        viewModel.observeConnectivity(networkConnectivityService)
        verify(
            networkConnectivityService,
            times(2)
        ).connectionState //the wanted number of invocation is two because, at first, the app will assume the connection is lost
        assertEquals(expectedState, viewModel.connectionState.value)
    }

    @Test
    fun fetchNextToGoRaces_success_state_test_case(): Unit = runBlocking {
        `when`(fetchNextToGoRacesUseCase.invoke(anyLong())).thenReturn(
            flowOf(
                RaceState.Success(
                    raceSummaries
                )
            )
        )
        viewModel.fetchNextToGoRaces()

        assertEquals(RaceState.Success(raceSummaries).data, viewModel.raceState.value.data)
        assertEquals(raceSummaries, viewModel.nextToGoRaces.value)
    }

    @Test
    fun fetchNextToGoRaces_error_state_test_case(): Unit = runBlocking {
        val errorMessage = "Unknown error occurred."
        `when`(fetchNextToGoRacesUseCase.invoke(anyLong())).thenReturn(
            flowOf(
                RaceState.Error(errorMessage)
            )
        )
        viewModel.fetchNextToGoRaces()

        val actualState = viewModel.raceState.value
        if (actualState is RaceState.Error) {
            assertEquals(errorMessage, actualState.message)
        }
        assertTrue(viewModel.nextToGoRaces.value.isEmpty())
    }

    @Test
    fun `deleteExpiredRacesAndFilterByCategory_success state_testcase`(): Unit = runBlocking {
        `when`(deleteExpiredRacesUseCase.invoke(anyLong(), anyList())).thenReturn(
            flowOf(
                RaceState.Success(raceSummaries)
            )
        )
        viewModel.deleteExpiredRacesAndFilterByCategory(
            System.currentTimeMillis() / 1000,
            listOf("161d9be2-e909-4326-8c2c-35ed71fb460b")
        )

        assertEquals(RaceState.Success(raceSummaries).data, viewModel.raceState.value.data)
        assertEquals(raceSummaries, viewModel.nextToGoRaces.value)
    }

    @Test
    fun `deleteExpiredRacesAndFilterByCategory_error state_testcase`(): Unit = runBlocking {
        val errorMessage = "Unknown error occurred."
        `when`(deleteExpiredRacesUseCase.invoke(anyLong(), anyList())).thenReturn(
            flowOf(
                RaceState.Error(errorMessage)
            )
        )

        viewModel.deleteExpiredRacesAndFilterByCategory(
            System.currentTimeMillis() / 1000,
            listOf(
                "161d9be2-e909-4326-8c2c-35ed71fb460b,",
                "161d9be2-e909-4326-8c2c-35ed71fb460b"
            )
        )

        val actualState = viewModel.raceState.value
        if (actualState is RaceState.Error) {
            assertEquals(errorMessage, actualState.message)
        }
        assertTrue(viewModel.nextToGoRaces.value.isEmpty())
    }
}


class MainDispatcherRule @OptIn(ExperimentalCoroutinesApi::class) constructor(private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
    TestWatcher() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}