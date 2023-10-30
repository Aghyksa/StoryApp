package com.aghyksa.storyapp.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.aghyksa.storyapp.adapter.StoryAdapter
import com.aghyksa.storyapp.api.StoryRepository
import com.aghyksa.storyapp.model.Story
import com.aghyksa.storyapp.utils.DataDummy
import com.aghyksa.storyapp.utils.MainDispatcherRule
import com.aghyksa.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner



@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryPagerViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock private lateinit var storyRepository: StoryRepository

    private val dummyStoriesResponse = DataDummy.generateDummyStories()
    private val dummyNoStoryResponse = DataDummy.generateDummyNoStory()

    @Test
    fun `when success get story data`() = runTest {
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStoriesResponse.listStory)
        val expectedStories = MutableLiveData<PagingData<Story>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)

        val listStoryViewModel = StoryPagerViewModel(storyRepository)
        val actualStories: PagingData<Story> = listStoryViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStoriesResponse.listStory, differ.snapshot())
        assertEquals(dummyStoriesResponse.listStory.size, differ.snapshot().size)
        assertEquals(dummyStoriesResponse.listStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when there is no story data`() = runTest {
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyNoStoryResponse.listStory)
        val expectedStories = MutableLiveData<PagingData<Story>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)

        val listStoryViewModel = StoryPagerViewModel(storyRepository)
        val actualStories: PagingData<Story> = listStoryViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}