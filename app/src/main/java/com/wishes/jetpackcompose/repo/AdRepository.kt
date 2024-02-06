package com.wishes.jetpackcompose.repo

import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.data.repositories.RemoteDataSource
import com.wishes.jetpackcompose.utlis.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
) {


    suspend fun getAds(params: HashMap<String, Any>): Flow<Resource<Latest>> {
        return remoteDataSource.getImages(params)
    }

}