package com.selflearningcoursecreationapp.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


object SignalR {
    var token = ""
    var hubConnection: HubConnection? = null

    var count: Int = 0
    const val HUB_ADD_LIKE = "AddLike"
    const val HUB_ADD_DISLIKE = "AddDisLike"

    var error = MutableLiveData<EventObserver<ToastData>>()


    fun turnOnTheSignalR() {


        GlobalScope.launch(Dispatchers.IO) {
            token = PreferenceDataStore.getString(Constants.USER_TOKEN).toString()

        }
        hubConnection =
            HubConnectionBuilder.create("https://appinventivlearningclientapiqa.azurewebsites.net/reviewlikedislike")
                .withAccessTokenProvider(Single.defer {
                    Single.just(
                        token

                    )
                }).build()
        hubConnection?.start()
        Log.e("SignalR", "turnOnTheSignalR")

    }

    fun listenDataForAddLike(wishClickListener: (response: ResponseFile) -> Unit) {
        hubConnection?.on(
            "demo", { message: ResponseFile ->

                wishClickListener.invoke(message)
                Log.e("SignalR", "" + message)

            },
            ResponseFile::class.java
        )
        Log.e("SignalR", "listenDataForAddLike")

    }

    fun addLikeDisLike(eventName: String, payload: Int, reviewId: Int) {
//        hubConnection?.start()?.doOnError {
//            Log.e("SignalR", "doOnError"+it.message)
//        }

        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {

            try {
                hubConnection?.invoke(ResponseFile::class.java, eventName, payload, reviewId)
                    .runCatching {

                        Log.e("SignalR", "addLikeDisLike$this")

                    }


            } catch (e: Exception) {
                Log.e("SignalR", "addLikeDisLike$e")
            }


//
        } else {
            count + 1
            if (count == 5) {
                error.value = EventObserver(ToastData(R.string.no_internet))
            } else {

//                hubConnection?.stop()?.andThen {
//                    hubConnection?.start()?.retry()
//                    Timer().schedule(2000) {
//
//                        addLikeDisLike(eventName, payload, reviewId)
//                    }
//                } ?: kotlin.run {
//
//                    addLikeDisLike(eventName, payload, reviewId)
//                }
            }
        }


    }

    fun stopConnection() {
        hubConnection?.stop()
    }


}


data class ResponseFile(
    var courseId: Int,
    var reviewId: Int,
    var userId: Int,
    var courseRating: Int,
    var description: String,
    var totalLikes: Int,
    var totalDislikes: Int,
    var userLiked: Int,
    var userDisLiked: Int,
    var name: String,
    var createdDate: String,
)