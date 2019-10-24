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

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.FragmentOverviewBinding
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import kotlinx.coroutines.*

/**
 * This fragment shows the the status of the Mars real-estate web services transaction.
 */
class OverviewFragment : Fragment() {

    private val COISA = "COISAFRAG"
    /**
     * Lazily initialize our [OverviewViewModel].
     */
    private val viewModel: OverviewViewModel by lazy {
        ViewModelProviders.of(this).get(OverviewViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // DONE (04) Switch to inflating FragmentOverviewBinding
        val binding = FragmentOverviewBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        Log.d(COISA, "onCreateView")
        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel
        qualquerCoisa()
        Log.d(COISA, "onCreateView2")

        // DONE (12) Set binding.photosGrid.adapter to a new PhotoGridAdapter()
        binding.photosGrid.adapter = PhotoGridAdapter()
        setHasOptionsMenu(true)
        return binding.root
    }

    fun qualquerCoisa() {
        Log.d(COISA, "qualquerCoisa")
        CoroutineScope(Dispatchers.IO).launch {
            async(Dispatchers.IO) {
                Log.d(COISA, "qualquerCoisa 33")
                OverviewViewModel.currentThreadName(COISA)
                delay(15000)
                OverviewViewModel.currentThreadName(COISA)
                Log.d(COISA, "qualquerCoisa 333")
                val seconds = 3
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "vai alimpar a lista em $seconds", Toast.LENGTH_SHORT).show()
                }
                viewModel.initCountDown(toDo = {
                    when {
                        it <= 0 -> {
                            viewModel.alimpaLista()
                        }
                        else -> {
                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }, seconds = seconds)
                false
            }.await().let { Log.d(COISA, "qualquerCoisa 33333") }

            launch {
                Log.d(COISA, "qualquerCoisa 11")
                OverviewViewModel.currentThreadName(COISA)
                delay(7000)
                OverviewViewModel.currentThreadName(COISA)
                Log.d(COISA, "qualquerCoisa 111")
            }

        }
        Log.d(COISA, "qualquerCoisa2")
    }

    /**
     * Inflates the overflow menu that contains filtering options.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
