package com.selflearningcoursecreationapp.utils

import kotlinx.coroutines.*

abstract class CoroutineAsyncTask<Params, Progress, Result>() {


    open fun onPreExecute() {}

    abstract fun doInBackground(vararg params: Params?): Result
    open fun onProgressUpdate(vararg values: Progress?) {

    }

    open fun onPostExecute(result: Result?) {}

    open fun onCancelled(result: Result?) {

    }

    protected var job: Job? = null
    protected var isCancelled = false


    //Code
    protected fun publishProgress(vararg progress: Progress?) {
        GlobalScope.launch(Dispatchers.Main) {
            onProgressUpdate(*progress)
        }
    }

    fun execute(vararg params: Params?) {
        onPreExecute()
        job = GlobalScope.launch(Dispatchers.Default) {
            val result = doInBackground(*params)
            withContext(Dispatchers.Main) {
                onPostExecute(result)
            }
        }
    }

    fun cancel(mayInterruptIfRunnable: Boolean) {
        isCancelled = true
        job?.cancel()
        onCancelled(null)
    }
}