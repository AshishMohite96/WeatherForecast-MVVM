package com.example.forecastify.ui.weather.future.list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.forecastify.R
import com.example.forecastify.data.db.entity.FutureWeatherEntry
import com.example.forecastify.data.db.unitlocalised.future.UnitSpecificFutureEntry
import com.example.forecastify.ui.base.ScopedFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.future_list_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.closestKodein
import org.kodein.di.generic.instance

class FutureListWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModelFactory: FutureListWeatherViewModelFactory by instance()
    private lateinit var viewModel: FutureListWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.future_list_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FutureListWeatherViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main){
        val futureWeatherEntries = viewModel.weatherEntries.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(this@FutureListWeatherFragment, Observer {
            if (it == null) return@Observer

            updateLocation(it.name)
        })

        futureWeatherEntries.observe(this@FutureListWeatherFragment, Observer {
            if (it == null) return@Observer

            group_loading.visibility = View.GONE

            updateDateToNextWeek()
            initRecyclerview(it.toFutureItems())
        })
    }

    private fun List<UnitSpecificFutureEntry>.toFutureItems(): List<FutureWeatherItem>{
        return this.map {
            FutureWeatherItem(it)
        }
    }

    private fun updateDateToNextWeek() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Next Week"
    }

    private fun updateLocation(name: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = name
    }

    private fun initRecyclerview(items: List<FutureWeatherItem>){
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FutureListWeatherFragment.context)
            adapter = groupAdapter
        }

        groupAdapter.setOnItemClickListener { item, view ->
            Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show()
        }
    }

}
