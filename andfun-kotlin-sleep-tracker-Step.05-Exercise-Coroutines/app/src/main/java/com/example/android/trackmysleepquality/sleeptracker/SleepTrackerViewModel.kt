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
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    private val TAG = SleepTrackerViewModel::javaClass.name

    //DONE(01) Declare Job() and cancel jobs in onCleared().
    private var viewModelJob = Job()

    //DONE (02) Define uiScope for coroutines.
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //DONE (03) Create a MutableLiveData variable tonight for one SleepNight.
    private var tonight = MutableLiveData<SleepNight?>()

    //DONE (04) Define a variable, nights. Then getAllNights() from the database
    //and assign to the nights variable.
    private val nights = database.getAllNights()

    //DONE (05) In an init block, initializeTonight(), and implement it to launch a coroutine
    //to getTonightFromDatabase().
    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    //DONE (06) Implement getTonightFromDatabase()as a suspend function.
    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "getTonightFromDatabase 1")
            Thread.sleep(5000)
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            Log.d(TAG, "getTonightFromDatabase 2")
            night
        }

    }

    //DONE (07) Implement the click handler for the Start button, onStartTracking(), using
    //coroutines. Define the suspend function insert(), to insert a new night into the database.
    fun onStartTracking() {
        uiScope.launch {
            Log.d(TAG, "onStartTracking 1: " + uiScope.coroutineContext)
            val newNight = SleepNight()
            Log.d(TAG, "onStartTracking 2")
            insert(newNight)
            Log.d(TAG, "onStartTracking 3")
            tonight.value = getTonightFromDatabase()
            Log.d(TAG, "onStartTracking 4")

        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "insert 1")
            database.insert(night)
            Thread.sleep(5000)
            Log.d(TAG, "insert 2")
        }
    }

    //DONE (08) Create onStopTracking() for the Stop button with an update() suspend function.
    fun onStopTracking() {
        uiScope.launch {
            Log.d(TAG, "onStopTracking 1")
            val oldNight = tonight.value ?: return@launch
            Log.d(TAG, "onStopTracking 2")
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            Log.d(TAG, "onStopTracking 3")
        }

    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "update 1")
            database.update(night)
            Thread.sleep(5000)
            Log.d(TAG, "update 2")
        }
    }

    //DONE (09) For the Clear button, created onClear() with a clear() suspend function.
    fun onClear() {
        uiScope.launch {
            Log.d(TAG, "onClear 1")
            clear()
            Log.d(TAG, "onClear 2")
            tonight.value = null
        }

    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "clear")
            database.clear()
            Thread.sleep(5000)
        }
    }

    //DONE (12) Transform nights into a nightsString using formatNights().
    val nightsString = Transformations.map(nights) { nightsList -> formatNights(nightsList, application.resources) }

}

