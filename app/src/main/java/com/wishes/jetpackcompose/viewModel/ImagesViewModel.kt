package com.wishes.jetpackcompose.viewModel


import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wishes.jetpackcompose.R
import com.wishes.jetpackcompose.data.entities.Ads
import com.wishes.jetpackcompose.data.entities.App
import com.wishes.jetpackcompose.data.entities.Categories
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Image
import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.repo.ImagesRepo
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




    private val _images = MutableStateFlow<Resource<Latest>>(Resource.Loading())
    val latest: StateFlow<Resource<Latest>> = _images.asStateFlow()

    private val _categories = MutableStateFlow<Resource<List<Category>>>(Resource.Loading())
    val categories: StateFlow<Resource<List<Category>>> = _categories.asStateFlow()

    var offset by mutableStateOf(30)

    val infos = mutableStateOf<Resource<Ads>>(Resource.Loading())
    val apps = mutableStateOf<List<App>?>(null)

    private val _message = MutableStateFlow<String>("Good Morning")
    val message: StateFlow<String> get() = _message

    /**ROOM DATABASE**/
    var catId: Int = 0
    var byLatest by mutableStateOf(emptyList<Image>())
    var categoriesList by mutableStateOf(emptyList<Category>())
    var favoritesList by mutableStateOf(emptyList<Image>())
    var imagesByCategory by mutableStateOf(emptyList<Image>())


    fun getLatestImages() {
        viewModelScope.launch {
            val params = HashMap<String,Any>()
            imageRepo.getImages(params).collect {
                _images.emit(it)
            }
        }
    }

    fun getCategories() = viewModelScope.launch {
        viewModelScope.launch {
            val params = HashMap<String,Any>()
            imageRepo.getCategories(params).collect {
                _categories.emit(it)
            }
        }
    }

    fun getFavoritesRoom() = viewModelScope.launch(Dispatchers.IO) {
//        imageRepo.local.getFavoriteImages().collect {
//            favoritesList = it
//        }
    }

    fun getByCatRoom(id: Int) = viewModelScope.launch(Dispatchers.IO) {
//        imageRepo.local.getImagesByCat(id, LANGUAGE_ID).collect {
//            imagesByCategory = it
//        }
    }


    private suspend fun getCategoriesSafeCall() {

        //categories.value=NetworkResults.Loading()
    }


    fun addToFav(id: Int, fav: Int) {

    }

    fun getImages() {
        viewModelScope.launch {
            getImagesSafeCall()
        }
    }

    private suspend fun getImagesSafeCall() {
    }

    private fun offlineCacheImages(images: List<Image>) {

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
//        if (timeOfDay in 0..11) {
//            _message.value = context.getString(R.string.morning)
//            //message = "Good Morning"
//        } else if (timeOfDay in 12..15) {
//            _message.value = context.getString(R.string.afternoon)
//            //message = "Good Afternoon"
//        } else if (timeOfDay in 16..20) {
//            _message.value = context.getString(R.string.evening)
//            //message = "Good Evening"
//        } else if (timeOfDay in 21..23) {
//            _message.value = context.getString(R.string.night)
//            //message = "Good Night"
//        }
    }


    fun hasInternetConnection(): Boolean = hasConnection(getApplication())

}