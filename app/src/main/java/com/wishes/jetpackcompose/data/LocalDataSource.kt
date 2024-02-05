package com.wishes.jetpackcompose.data



import com.wishes.jetpackcompose.data.entities.Image
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val dao: IDao,
) {

    suspend fun getFavorites(): Flow<List<Image>> = dao.getFavoriteImages()

    suspend fun addToFav(image: Image) = dao.toggleFavoriteStatus(image)


    suspend fun removeFromFav(image: Image) = dao.removeFromFav(image.id)

    suspend fun isImageInFav(image: Image):Boolean = dao.isImageFavorited(image.id)


}