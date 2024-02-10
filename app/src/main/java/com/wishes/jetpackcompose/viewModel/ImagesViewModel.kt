package com.wishes.jetpackcompose.viewModel


import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.data.entities.AppDetails
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Image
import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.repo.ImagesRepo
import com.wishes.jetpackcompose.screens.GridItem
import com.wishes.jetpackcompose.utlis.AdsUtil
import com.wishes.jetpackcompose.utlis.Const.Companion.hasConnection
import com.wishes.jetpackcompose.utlis.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val imageRepo: ImagesRepo,
    application: Application
) : AndroidViewModel(application) {
    val LOG_IMAGE: String = "response_image"

    enum class VIEW_PAGER {
        LATEST,
        FAVORITES,
        BY_CATEGORY
        // Add other types as necessary
    }

    private val _images = MutableStateFlow<Resource<Latest>>(Resource.Loading())
    val latest: StateFlow<Resource<Latest>> = _images.asStateFlow()

    var showProgressLoadMore by mutableStateOf(false)

    var currentViewPagerType by mutableStateOf<VIEW_PAGER>(VIEW_PAGER.LATEST)

    private val _imagesByCategory = MutableStateFlow<Resource<Latest>>(Resource.Loading())
    val imagesByCategory: StateFlow<Resource<Latest>> = _imagesByCategory.asStateFlow()


    private val _favorites = MutableStateFlow<Resource<Latest>>(Resource.Idle())
    val favorites: StateFlow<Resource<Latest>> = _favorites.asStateFlow()


    private val _categories = MutableStateFlow<Resource<List<Category>>>(Resource.Loading())
    val categories: StateFlow<Resource<List<Category>>> = _categories.asStateFlow()

    var offset by mutableStateOf(0)

    private val _appDetails = MutableStateFlow<Resource<AppDetails>>(Resource.Loading())
    val appDetails: StateFlow<Resource<AppDetails>> = _appDetails.asStateFlow()


    //viewpager
    var currentPage by mutableStateOf<Int>(0)
    var currentListImage = MutableStateFlow<Resource<Latest>>(Resource.Loading())

    private val _message = MutableStateFlow<String>("Good Morning")
    val message: StateFlow<String> get() = _message

    val isImageFavorited = MutableStateFlow<Boolean>(false)


    fun getLatestImages() {
        viewModelScope.launch {
            val params = HashMap<String, Any>()
            imageRepo.getImages(params).collect {
                _images.emit(it)
            }
        }
    }

    fun loadMore() {
        showProgressLoadMore = true
        viewModelScope.launch {
            val offset = _images.value.data?.images?.size ?: 0
            val params = HashMap<String, Any>().apply {
                put("offset", offset)
            }
            imageRepo.getImages(params).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val currentImages = _images.value.data?.images ?: emptyList()
                        val newImages = result.data?.images ?: emptyList()
                        val allImages = currentImages + newImages
                        _images.emit(
                            Resource.Success(
                                _images.value.data?.copy(images = allImages) ?: result.data
                            )
                        )
                        showProgressLoadMore = false
                    }

                    is Resource.Error -> {
                        // Handle error case
                        showProgressLoadMore = false
                    }

                    is Resource.Loading -> {
                        // Handle loading state if necessary
                    }

                    else -> {

                    }
                }
            }
        }
    }


    fun getImageByCategory(categoryId: Int) {
        viewModelScope.launch {
            val params = HashMap<String, Any>()
            imageRepo.getImageByCategory(params, categoryId).collect {
                _imagesByCategory.emit(it)
            }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            viewModelScope.launch {
                val params = HashMap<String, Any>()
                imageRepo.getCategories(params).collect {
                    _categories.emit(it)
                }
            }
        }
    }


    fun getFavoritesRoom() = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.getFavorites().collect {
            val latest: Latest = Latest(it)
            _favorites.emit(Resource.Success(latest))
        }
    }




    fun addToFav(image: Image) {
        viewModelScope.launch {
            isImageFavorited.emit(!isImageFavorited.value)
            imageRepo.addToFav(image)
        }
    }

    fun removeFromFav(image: Image) {
        viewModelScope.launch {
            imageRepo.removeFromFav(image)
        }
    }

    fun checkIfImageFavorited(image: Image) {
        viewModelScope.launch {
            isImageFavorited.value = imageRepo.isImageInFav(image)
        }
    }

    fun setImagesForViewPager(type: VIEW_PAGER) {
        currentViewPagerType = type
        when (type) {
            VIEW_PAGER.LATEST -> {
                viewModelScope.launch {
                    currentListImage.emit(_images.value)
                }
            }

            VIEW_PAGER.FAVORITES -> {
                viewModelScope.launch {
                    currentListImage.emit(_favorites.value)
                }
            }

            VIEW_PAGER.BY_CATEGORY -> {
                viewModelScope.launch {
                    currentListImage.emit(_imagesByCategory.value)
                }
            }

        }
    }

    //suspend fun isFav(): Boolean = imageRepo.isImageInFav(currentListImage!!.get(currentPage!!))

    fun getAppDetails() {
        viewModelScope.launch {
            imageRepo.getAppDetails().collect() {
                if (it is Resource.Success) {
                    if (!it.data?.advertisements.isNullOrEmpty()) {
                        it.data?.ads!!.forEach {
                            AdsUtil.makeAd(it)
                        }
                    }
                }
                _appDetails.emit(it)

            }
        }
    }

    fun setMessage(context: Context) {
        val c: Calendar = Calendar.getInstance()
        val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)
        when (timeOfDay) {
            in 0..11 -> {
                _message.value = context.getString(R.string.morning)
            }

            in 12..15 -> {
                _message.value = context.getString(R.string.afternoon)
            }

            in 16..20 -> {
                _message.value = context.getString(R.string.evening)
            }

            in 21..23 -> {
                _message.value = context.getString(R.string.night)
            }

        }


    }


    fun hasInternetConnection(): Boolean = hasConnection(getApplication())


}