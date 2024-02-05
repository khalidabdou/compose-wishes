package com.wishes.jetpackcompose.repo


import com.wishes.jetpackcompose.data.LocalDataSource
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Image
import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.data.repositories.RemoteDataSource
import com.wishes.jetpackcompose.utlis.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ImagesRepo @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {
    // images
    suspend fun getImages(params: HashMap<String, Any>): Flow<Resource<Latest>> {
        return remoteDataSource.getImages(params)
    }

    suspend fun getImageByCategory(params: HashMap<String, Any>,categoryID: Int): Flow<Resource<Latest>> {
        return remoteDataSource.getImagesByCategory(params,categoryID)
    }

    suspend fun getCategories(params: HashMap<String, Any>): Flow<Resource<List<Category>>> {
        return remoteDataSource.getCategories(params)
    }

    suspend fun getFavorites(): Flow<List<Image>> {
        return localDataSource.getFavorites()
    }


    suspend fun addToFav(image: Image) {
        return localDataSource.addToFav(image)
    }

    suspend fun removeFromFav(image: Image) {
        return localDataSource.removeFromFav(image)
    }

    suspend fun isImageInFav(image: Image):Boolean = localDataSource.isImageInFav(image)


}
