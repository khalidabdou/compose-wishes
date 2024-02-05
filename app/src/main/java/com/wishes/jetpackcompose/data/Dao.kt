package com.wishes.jetpackcompose.data

import android.util.Log
import androidx.room.*
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Image

import kotlinx.coroutines.flow.Flow


@Dao
interface IDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToFavorites(image: Image)


    @Query("DELETE FROM tbl_images WHERE id =:id")
    suspend fun removeFromFav(id: Int)



    @Query("SELECT EXISTS(SELECT 1 FROM tbl_images WHERE id = :id)")
    suspend fun isImageFavorited(id: Int): Boolean


    suspend fun toggleFavoriteStatus(image: Image) {
        if (isImageFavorited(image.id)) {
            // If the image is already favorited, remove it from favorites
            removeFromFav(image.id)
        } else {
            // If the image is not in favorites, add it
            insertToFavorites(image)
        }
    }

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertImages(images: List<Image>): List<Long>
//

//    @Query("UPDATE tbl_images SET viewCount = :views, shareCount = :shares WHERE id = :id")
//    suspend fun upadteImages(id: Int, shares: Int, views: Int): Int
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertImage(image: Image)
//
//
//    @Transaction
//    suspend fun insertOrUpdate(images: List<Image>) {
//        val insertResult = insertImages(images)
//        for (i in insertResult.indices) {
//            if (insertResult[i] == -1L) {
//                Log.d("DATABASE --", insertResult[i].toString())
//                upadteImages(images[i].id, images[i].shareCount, images[i].viewCount)
//            }
//        }
//    }

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertCategories(categories: List<Category>)
//
//
//    @Query("SELECT * FROM tbl_images where language_app=:languageID")
//    fun getImages(languageID: Int): Flow<List<Image>>
//
//    @Query("SELECT * FROM tbl_images WHERE categoryId=:catId AND language_app=:languageID")
//    fun getImagesByCat(catId: Int, languageID: Int): Flow<List<Image>>
//
//    @Query("Update tbl_images set isfav =:fav where id =:id")
//    suspend fun addToFav(id: Int, fav: Int)
//
//    @Query("SELECT * FROM tbl_category WHERE type=:type AND language_app=:languageID")
//    fun readCategories(type: String, languageID: Int): Flow<List<Category>>
//
//
//    @Query("SELECT * FROM tbl_images WHERE categoryId=:catID AND language_app=:languageID")
//    fun readImagesByCategory(catID: Int, languageID: Int): Flow<List<Image>>
//
    @Query("SELECT * FROM tbl_images")
    fun getFavoriteImages(): Flow<List<Image>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image)
//
//    @Query("Delete FROM tbl_category")
//    suspend fun deleteAllCatImage()
//
//    @Query("Delete FROM tbl_images")
//    suspend fun deleteAllImage()
//
//    @Query("DELETE FROM tbl_images WHERE id =:id")
//    suspend fun deleteImage(id: Int)
//
//    @Query("DELETE FROM tbl_category WHERE id =:id")
//    suspend fun deleteCategory(id: Int)

//    @Transaction
//    suspend fun deleteAll(images: List<Image>) {
//        val insertResult = insertImages(images)
//        //val updateList = mutableListOf<Image>()
//        for (i in insertResult.indices) {
//            if (insertResult[i] == -1L) {
//                upadteImages(images[i].id, images[i].shareCount, images[i].viewCount)
//            }
//
//        }
//    }
}