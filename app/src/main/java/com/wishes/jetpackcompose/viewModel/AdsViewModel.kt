package com.wishes.jetpackcompose.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishes_jetpackcompose.GridItemCategory
import com.google.android.gms.ads.nativead.NativeAd
import com.wishes.jetpackcompose.data.entities.App
import com.wishes.jetpackcompose.data.entities.Apps
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Image
import com.wishes.jetpackcompose.screens.GridItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {


}