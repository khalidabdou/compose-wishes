package com.wishes.jetpackcompose.data.interfaces


import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.utlis.Resource
import kotlinx.coroutines.flow.Flow

interface ImageInterface {
    suspend fun getImages(params: HashMap<String, Any>): Flow<Resource<Latest>>

    suspend fun getImagesByCategory(
        params: HashMap<String, Any>,
        categoryId: Int
    ): Flow<Resource<Latest>>

    suspend fun getCategories(params: HashMap<String, Any>): Flow<Resource<List<Category>>>

    suspend fun incShareImg(params: HashMap<String, Any>): Flow<Resource<Unit>>

    suspend fun incViewsImg(params: HashMap<String, Any>): Flow<Resource<Unit>>


}
