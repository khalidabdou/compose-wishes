package com.wishes.jetpackcompose.viewModel


import android.app.Application
import android.content.Context
import android.util.Log
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


    private val _ads = MutableStateFlow<List<NativeAd>>(emptyList())
    val ads = _ads.asStateFlow()

    private val _images = MutableStateFlow<Resource<Latest>>(Resource.Loading())
    //val latest: StateFlow<Resource<Latest>> = _images.asStateFlow()

    private val _imagesWithAd = MutableStateFlow<Resource<List<GridItem>>>(Resource.Loading())
    val imagesWithAd: StateFlow<Resource<List<GridItem>>> = _imagesWithAd.asStateFlow()

    var showProgressLoadMore by mutableStateOf(false)

    var currentViewPagerType by mutableStateOf<VIEW_PAGER>(VIEW_PAGER.LATEST)

    private val _imagesByCategory = MutableStateFlow<Resource<List<GridItem>>>(Resource.Loading())
    val imagesByCategory: StateFlow<Resource<List<GridItem>>> = _imagesByCategory.asStateFlow()


    private val _favorites = MutableStateFlow<Resource<List<GridItem>>>(Resource.Idle())
    val favorites: StateFlow<Resource<List<GridItem>>> = _favorites.asStateFlow()


    private val _categories = MutableStateFlow<Resource<List<Category>>>(Resource.Loading())
    val categories: StateFlow<Resource<List<Category>>> = _categories.asStateFlow()

    var offset by mutableStateOf(0)

    private val _appDetails = MutableStateFlow<Resource<AppDetails>>(Resource.Loading())
    val appDetails: StateFlow<Resource<AppDetails>> = _appDetails.asStateFlow()


    //viewpager
    var currentPage by mutableStateOf<Int>(0)
    var currentListImage = MutableStateFlow<Resource<List<GridItem>>>(Resource.Loading())

    private val _message = MutableStateFlow<String>("Good Morning")
    val message: StateFlow<String> get() = _message

    val isImageFavorited = MutableStateFlow<Boolean>(false)


    fun getLatestImages() {
        viewModelScope.launch {
            val params = HashMap<String, Any>()
            imageRepo.getImages(params).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val imagesList = result.data?.let { latest ->
                            latest.images.map {
                                GridItem.Content(it)
                            }
                        } ?: emptyList()

                        // Emit the transformed list wrapped in `Resource.Success`
                        _imagesWithAd.emit(Resource.Success(imagesList))
                        updateListWithNewAdsAndEmit()
                    }

                    is Resource.Loading -> {
                        _imagesWithAd.emit(Resource.Loading())
                    }

                    is Resource.Error -> {
                        _imagesWithAd.emit(Resource.Error(result.message, null))
                    }

                    else -> {}
                }
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

    fun addAd(nativeAd: NativeAd) {
        viewModelScope.launch {
            val updatedList = _ads.value.toMutableList().apply {
                add(nativeAd)
            }
            _ads.emit(updatedList)
            if (_ads.value.size >= 5) {
                updateListWithNewAdsAndEmit()
            }

        }
    }

    fun updateListWithNewAdsAndEmit() {
        viewModelScope.launch {
            // Assuming _ads.value gives you the new ads to add
            val newAdsList =
                _ads.value // You might need to prepare this list to match the desired ads to add
            // Process the current _imagesWithAd value
            val currentResource = _imagesWithAd.value
            val updatedList = when (currentResource) {
                is Resource.Success -> {
                    val currentList = currentResource.data ?: emptyList()
                    integrateAds(currentList, newAdsList)
                }

                else -> {
                    // Handle other states (Loading, Error) as appropriate, possibly just returning the current list
                    currentResource.data ?: emptyList()
                }
            }

            _imagesWithAd.emit(Resource.Success(updatedList))
        }
    }

    fun integrateAds(images: List<GridItem>, newAds: List<NativeAd>): List<GridItem> {
        val resultList = mutableListOf<GridItem>()
        var adIndex = 0

        // Collect existing ads' identifiers to avoid duplicates
        val existingAdIds = images.filterIsInstance<GridItem.Ad>().mapNotNull { it.nativeAd }

        images.forEachIndexed { index, item ->
            resultList.add(item)
            // After every 4 images, try to add a new ad if available and not already present
            if ((index + 1) % 4 == 0 && adIndex < newAds.size) {
                val newAd = newAds[adIndex]
                Log.d("adloader", "adloader ${adIndex}")
                // Assuming NativeAd has an 'id' property for this example
                if (!existingAdIds.contains(newAd)) {
                    resultList.add(GridItem.Ad(newAd))
                    Log.d("adloader", "adloader $adIndex")
                    adIndex++
                }
            }
        }
        return resultList
    }


    fun getImageByCategory(categoryId: Int) {
        viewModelScope.launch {
            val params = HashMap<String, Any>()
            imageRepo.getImageByCategory(params, categoryId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Assuming Latest contains a list of Images and you want to convert it to a list of GridItem.Content
                        val gridItems = resource.data?.images?.map { image ->
                            GridItem.Content(image)
                        } ?: emptyList()

                        _imagesByCategory.emit(Resource.Success(gridItems))
                    }

                    is Resource.Loading -> {
                        _imagesByCategory.emit(Resource.Loading())
                    }

                    is Resource.Error -> {
                        _imagesByCategory.emit(Resource.Error(resource.message, resource.errorBody))
                    }

                    else -> _imagesByCategory.emit(Resource.Error(resource.message, null))
                }
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
            val gridItems = latest?.images?.map { image ->
                GridItem.Content(image)
            } ?: emptyList()

            _favorites.emit(Resource.Success(gridItems))
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
        viewModelScope.launch {
            when (type) {
                VIEW_PAGER.LATEST -> {
                    currentListImage.emit(_imagesWithAd.value)
                }

                VIEW_PAGER.FAVORITES -> {
                    currentListImage.emit(_favorites.value)
                }

                VIEW_PAGER.BY_CATEGORY -> {
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