/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.*

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */


class OverviewViewModel : ViewModel() {

    companion object {
        const val COISA = "Coisa"
        private fun logIsMainThread(tag: String? = ""): Boolean {
            val tagName = if (TextUtils.isEmpty(tag)) COISA else tag
            val isMainThread = (Looper.myLooper() == Looper.getMainLooper())
            Log.d(tagName, "MainThread: $isMainThread")
            return isMainThread
        }

        fun currentThreadName(tagName: String? = null) {
            val textTag = if (TextUtils.isEmpty(tagName)) COISA else tagName
            Log.d(textTag, "Estou na thread ${Thread.currentThread().name}")
            logIsMainThread()
        }
    }

    // The internal MutableLiveData String that stores the most recent response status
    private val _status = MutableLiveData<String>()

    // The external immutable LiveData for the status String
    val status: LiveData<String>
        get() = _status

    // DONE (02) Update the ViewModel to return a LiveData of List<MarsProperty>
    // Internally, we use a MutableLiveData, because we will be updating the MarsProperty with
    // new values
    private val _properties = MutableLiveData<List<MarsProperty>>()

    // The external LiveData interface to the properties is immutable, so only this class can modify
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        Log.d(COISA, "Init")
        currentThreadName()
        getMarsRealEstateProperties()
    }

    /**
     * Gets Mars real estate properties information from the Mars API Retrofit service and updates the
     * [MarsProperty] [LiveData]. The Retrofit service returns a coroutine Deferred, which we await
     * to get the result of the transaction.
     */
    private fun getMarsRealEstateProperties() {
        Log.d(COISA, "Inicio")
        currentThreadName()

        coroutineScope.launch(Dispatchers.IO) {
            currentThreadName()
            Log.d(COISA, "esperando")
            delay(3000)
            Log.d(COISA, "passou 1")
            // Get the Deferred object for our Retrofit request
            val getPropertiesDeferred = MarsApi.retrofitService.getProperties()
            Log.d(COISA, "passou 2")
            try {
                // Await the completion of our Retrofit request
                currentThreadName()
                val listResult = getPropertiesDeferred.await()
                currentThreadName()
                Log.d(COISA, "passou 3")
                _status.setValueThread("Success: ${listResult.size} Mars properties retrieved")
                //                if (listResult.size > 0) {
                _properties.setValueThread(listResult)
                return@launch
//                }
//                when (logIsMainThread()) {
//                    true -> {
//                        _status.value = "Success: ${listResult.size} Mars properties retrieved"
//                        //                if (listResult.size > 0) {
//                        _properties.value = listResult
//                        return@launch
////                }
//                    }
//                    false -> {
//                        _status.postValue("Success: ${listResult.size} Mars properties retrieved")
//                        //                if (listResult.size > 0) {
//                        _properties.postValue(listResult)
//                        return@launch
////                }
//                    }

            } catch (e: Exception) {
                e.printStackTrace()
                _status.setValueThread("Failure: ${e.message}")
                _properties.setValueThread(listOf())
//                when (logIsMainThread()) {
//                    true -> {
//                        _status.value = "Failure: ${e.message}"
//                        _properties.value = listOf()
//                    }
//                    false -> {
//                        _status.postValue("Failure: ${e.message}")
//                        _properties.postValue(listOf())
//                    }
//
//                }

            }
            currentThreadName()
        }
    }

    private fun <T : Any?> MutableLiveData<T>.setValueThread(value: T) {
        when (logIsMainThread()) {
            true -> {
                this.value = value
            }
            false -> {
                this.postValue(value)
            }
        }

    }

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun alimpaLista() {
//        _properties.value = listOf()
        _properties.setValueThread(listOf())
    }

    suspend fun initCountDown(seconds: Int, toDo: (Int) -> Unit) {
        for (step in seconds.downTo(0)) {
            delay(1000)
            toDo.invoke(step)
        }
    }
}
