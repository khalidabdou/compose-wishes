package com.wishes.jetpackcompose.repo

import com.google.android.gms.common.api.Response
import com.wishes.jetpackcompose.data.LocalDataSource
import com.wishes.jetpackcompose.data.repositories.RemoteDataSource
import javax.inject.Inject


import com.wishes.jetpackcompose.data.entities.Categories
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.utlis.Resource
import kotlinx.coroutines.flow.Flow


class ImagesRepo @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {
    // images
    suspend fun getImages(params: HashMap<String, Any>): Flow<Resource<Latest>> {
        return remoteDataSource.getImages(params)
    }

    suspend fun getCategories(params: HashMap<String, Any>): Flow<Resource<List<Category>>> {
        return remoteDataSource.getCategories(params)
    }



}
