package com.wishes.jetpackcompose.utlis

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.RedirectResponseException
import io.ktor.client.features.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import javax.inject.Inject


private fun String.toValidJson(): JSONObject {
    return try {
        JSONObject(this)
    } catch (ex: Exception) {
        JSONObject("{}")
    }
}


class ClientApiManager @Inject constructor(
    private val client: HttpClient,
    private val myDataStore: DataStore
) {
    private fun List<*>.toJsonElement(): JsonElement {
        val list: MutableList<JsonElement> = mutableListOf()
        this.forEach { value ->
            when (value) {
                null -> list.add(JsonNull)
                is Map<*, *> -> list.add(value.toJsonElement())
                is List<*> -> list.add(value.toJsonElement())
                is Boolean -> list.add(JsonPrimitive(value))
                is Number -> list.add(JsonPrimitive(value))
                is String -> list.add(JsonPrimitive(value))
                is Enum<*> -> list.add(JsonPrimitive(value.toString()))
                else -> throw IllegalStateException("Can't serialize unknown collection type: $value")
            }
        }
        return JsonArray(list)
    }

    private fun Map<*, *>.toJsonElement(): JsonElement {
        val map: MutableMap<String, JsonElement> = mutableMapOf()
        this.forEach { (key, value) ->
            key as String
            when (value) {
                null -> map[key] = JsonNull
                is Map<*, *> -> map[key] = value.toJsonElement()
                is List<*> -> map[key] = value.toJsonElement()
                is Boolean -> map[key] = JsonPrimitive(value)
                is Number -> map[key] = JsonPrimitive(value)
                is String -> map[key] = JsonPrimitive(value)
                is Enum<*> -> map[key] = JsonPrimitive(value.toString())
                else -> throw IllegalStateException("Can't serialize unknown type: $value")
            }
        }
        return JsonObject(map)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getLiveTables() {

    }

    suspend fun makeRequest(
        url: String,
        reqMethod: HttpMethod,
        useBearer: Boolean = true,
        bodyMap: HashMap<String, Any>? = null,
        requestBody: ArrayList<HashMap<String, String>>? = null,
        multiPartForm: MultiPartFormDataContent? = null,
        parameterFormData: ArrayList<Pair<String, Any>>? = null,
        successCallbackObject: suspend (response: JSONObject) -> Unit = {},
        successCallbackArray: suspend (response: JSONArray) -> Unit = {},
        failureCallback: suspend (error: Resource.Error<*>) -> Unit
    ) {

        //val language = "en "
        val token = myDataStore.getToken.first()
        Log.d("token",token.toString())


        try {
            val httpResponse: HttpResponse =
                when (reqMethod) {
                    HttpMethod.Post -> {
                        Log.e("response_api", "$url $bodyMap")
                        client.post(url) {
                            if (bodyMap != null) {
                                contentType(ContentType.Application.Json)
                                body = bodyMap.toJsonElement()
                                Log.e("response_api", "$url $bodyMap")
                            }
                            if (multiPartForm != null) {
                                body = multiPartForm
                                Log.e("multiPartForm post", "$url $bodyMap")
                            }
                            if (parameterFormData != null) {
                                contentType(ContentType.Application.Json)
                                Log.e("parameterFormData post", "$url $parameterFormData")
                                formData {
                                    for (param in parameterFormData) {
                                        parameter(param.first, param.second)
                                    }
                                }
                            }
                            formData {
                                //parameter("lang", language)
                            }

                            if (useBearer)
                                headers {
                                    append(
                                        HttpHeaders.Authorization,
                                        "Bearer " + token
                                    )
                                }
                        }
                    }

                    HttpMethod.Put -> {
                        Log.e("URL POST", url)

                        client.put(url) {
                            contentType(ContentType.Application.Json)
                            if (bodyMap != null) {
                                body = bodyMap.toJsonElement()
                                Log.e("url with params put", "$url $bodyMap")
                            }
                            if (parameterFormData != null) {
                                Log.e("url with params put", "$url $parameterFormData")
                                formData {
                                    for (param in parameterFormData) {
                                        parameter(param.first, param.second)
                                    }
                                }
                            }
                            if (requestBody != null) {
                                body = requestBody
                                Log.e("requestBody url with params put", "$url $requestBody")
                            }
                            if (useBearer)
                                headers {
                                    append(
                                        HttpHeaders.Authorization,
                                        "Bearer " + token
                                    )
                                }
                            formData {
                                //parameter("lang", language)
                            }
                        }
                    }

                    else -> {
                        client.request(url) {
                            contentType(ContentType.Application.Json)
                            if (parameterFormData != null) {
                                Log.e("url with params", "$url $parameterFormData")
                                formData {
                                    for (param in parameterFormData) {
                                        parameter(param.first, param.second)
                                    }
                                }
                            }
                            formData {
                            }
                            if (useBearer)
                                headers {
                                    append(
                                        HttpHeaders.Authorization,
                                        "Bearer " + token
                                    )
                                    Log.e("request", "url : $url response : ")

                                }

                        }
                    }
                }
            val response: String = httpResponse.receive()
            Log.e("request", "url : $url response : $response")
            val jsonObject = JSONTokener(response).nextValue()
            if (jsonObject is JSONObject) {
                println("Successful response Obj!")
                successCallbackObject(jsonObject)
            } else if (jsonObject is JSONArray) {
                println("Successful response Array!")
                successCallbackArray(jsonObject)
            }
        } catch (e: RedirectResponseException) {
            // 3xx - Response
            failureCallback(
                Resource.Error<Any>(
                    message = "Error ${e.response.status.description}",
                    errorBody = ErrorHandler(
                        e.response.status.value,
                        e.response.content.toString(),
                    )
                )
            )
        } catch (e: ClientRequestException) {
            // 4xx - Response
            println("ClientRequestException! ${e.response.status}")
            Log.e("response_api", e.response.receive())

            val json = e.response.receive<String>().toValidJson()
            val message = if (json.has("message")) json.getString("message") else ""
            Log.e("ClientRequestException", message)
            failureCallback(
                Resource.Error<Any>(
                    message = "Error ${e.message}",
                    errorBody = ErrorHandler(e.response.status.value, message, null)
                )
            )
        } catch (e: ServerResponseException) {
            // 5xx - Response
            failureCallback(
                Resource.Error<Any>(
                    message = "Error ${e.response.status.description}",
                    errorBody = ErrorHandler(
                        e.response.status.value,
                        e.response.content.toString(),
                    )
                )
            )
        } catch (e: Exception) {
            failureCallback(
                Resource.Error<Any>(
                    message = "Error ${e.message}",
                    errorBody = ErrorHandler(500, e.message.toString())
                )
            )
        }
    }
}