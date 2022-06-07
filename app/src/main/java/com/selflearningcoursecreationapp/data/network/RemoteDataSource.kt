package com.selflearningcoursecreationapp.data.network

import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.utils.ApiEndPoints.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


fun getApiProvider(): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(getHttpClient().build())
        .build()
}

fun getApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}


private fun getHttpClient(): OkHttpClient.Builder {
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer ${SelfLearningApplication.token}")
                .header("platform", "3")
                .header("api_key", "1234")
                .header("language", SelfLearningApplication.languageCode)
                .header("offset", getOffset())
                .header("timezone", getTimeZone())
                .method(original.method, original.body)
            val request = requestBuilder.build()
            val response = chain.proceed(request)
            response
        }
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .readTimeout(30000, TimeUnit.MILLISECONDS)
        .writeTimeout(30000, TimeUnit.MILLISECONDS)
}

private fun getOffset(): String {
    val tz = TimeZone.getDefault()
    val cal = GregorianCalendar.getInstance(tz)
    return tz.getOffset(cal.timeInMillis).toString()
}

private fun getTimeZone(): String {
    return TimeZone.getDefault().id
}


