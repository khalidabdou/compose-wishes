package com.wishes.jetpackcompose.data.repositories


import android.util.Log
import com.wishes.jetpackcompose.BuildConfig.PACKAGE_NAME
import com.wishes.jetpackcompose.data.entities.Category
import com.wishes.jetpackcompose.data.entities.Latest
import com.wishes.jetpackcompose.data.interfaces.ImageInterface
import com.wishes.jetpackcompose.utlis.ClientApiManager
import com.wishes.jetpackcompose.utlis.Const.Companion.GAT_CATEGORIES
import com.wishes.jetpackcompose.utlis.Const.Companion.GET_IMAGES_BY_CATEGORY
import com.wishes.jetpackcompose.utlis.Const.Companion.GET_LATEST
import com.wishes.jetpackcompose.utlis.JSONConverter
import com.wishes.jetpackcompose.utlis.Resource
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiManager: ClientApiManager,
) : ImageInterface {
    val TAG= "Images"
    override suspend fun getImages(params: HashMap<String, Any>): Flow<Resource<Latest>> = flow {
        apiManager.makeRequest(
            url = GET_LATEST(appPackage = PACKAGE_NAME),
            bodyMap = null,
            useBearer = true,
            multiPartForm = null,
            reqMethod = HttpMethod.Get,
            successCallbackObject = { jsonResponse ->
                Log.d(TAG, "Success $jsonResponse")

                // Directly parse the JSON response to Latest object
                val homeObject: Latest? =
                    JSONConverter.jsonToObject<Latest>(jsonResponse.toString())

                // Check if homeObject and its list are not null before accessing
                if (homeObject != null && homeObject.images.isNotEmpty()) {
                    Log.d(TAG, "${homeObject}")
                    emit(Resource.Success(homeObject))
                } else {
                    Log.d(TAG, "Latest images list is empty or null")
                    emit(Resource.Error("No images found", null))
                }
            },
            failureCallback = { error ->
                Log.e(TAG, " " + error.errorBody?.toString() + error.message?.toString())
                emit(Resource.Error(error.message, errorBody = error.errorBody))
            }
        )
    }

    override suspend fun getImagesByCategory(
        params: HashMap<String, Any>,
        categoryId: Int
    ): Flow<Resource<Latest>> = flow {
        apiManager.makeRequest(
            url = GET_IMAGES_BY_CATEGORY(categoryId = categoryId),
            bodyMap = null,
            useBearer = true,
            multiPartForm = null,
            reqMethod = HttpMethod.Get,
            successCallbackObject = { jsonResponse ->
                Log.d(TAG, "Success $jsonResponse")

                // Directly parse the JSON response to Latest object
                val homeObject: Latest? =
                    JSONConverter.jsonToObject<Latest>(jsonResponse.toString())

                // Check if homeObject and its list are not null before accessing
                if (homeObject != null && homeObject.images.isNotEmpty()) {
                    Log.d(TAG, "${homeObject}")
                    emit(Resource.Success(homeObject))
                } else {
                    Log.d(TAG, "Latest images list is empty or null")
                    emit(Resource.Error("No images found", null))
                }
            },
            failureCallback = { error ->
                Log.e(TAG, " " + error.errorBody?.toString() + error.message?.toString())
                emit(Resource.Error(error.message, errorBody = error.errorBody))
            }
        )
    }

    override suspend fun getCategories(params: HashMap<String, Any>): Flow<Resource<List<Category>>> =
        flow {
            apiManager.makeRequest(
                url = GAT_CATEGORIES(packageName = PACKAGE_NAME, language = "English"),
                bodyMap = null,
                useBearer = true,
                multiPartForm = null,
                reqMethod = HttpMethod.Get,
                successCallbackArray = { jsonResponse ->
                    Log.d(TAG, "Success Cate$jsonResponse")

                    // Directly parse the JSON response to Latest object
                    val homeObject: List<Category>? =
                        JSONConverter.jsonToObject<List<Category>>(jsonResponse.toString())
                    // Check if homeObject and its list are not null before accessing
                    if (homeObject != null && homeObject.isNotEmpty()) {
                        Log.d(TAG, "Success list $homeObject")

                        emit(Resource.Success(homeObject))
                    } else {
                        Log.d(TAG, "Latest images list is empty or null")
                        emit(Resource.Error("No images found", null))
                    }
                },
                failureCallback = { error ->
                    Log.e(TAG, " " + error.errorBody?.toString() + error.message?.toString())
                    emit(Resource.Error(error.message, errorBody = error.errorBody))
                }
            )
        }

    override suspend fun incShareImg(params: HashMap<String, Any>): Flow<Resource<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun incViewsImg(params: HashMap<String, Any>): Flow<Resource<Unit>> {
        TODO("Not yet implemented")
    }


}