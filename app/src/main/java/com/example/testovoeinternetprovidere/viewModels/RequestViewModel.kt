package com.example.testovoeinternetprovidere.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.House
import com.example.domain.models.Street
import com.example.domain.repository.Repository
import kotlinx.coroutines.launch

class RequestViewModel(private val repository: Repository): ViewModel() {
    var streets = MutableLiveData<List<Street>>()
    var houses = MutableLiveData<List<House>>()

    var streetId = "-1"

    fun getAllStreets(streetName: String){
        viewModelScope.launch {
            try{
                val request = repository.getAllStreets()
                request.onSuccess {
                    val neededStreets = mutableListOf<Street>()
                    for (i in it){
                        if (i.street.lowercase().contains(streetName.lowercase())){
                           neededStreets.add(i)
                        }
                    }
                    streets.value = neededStreets
                }
                request.onFailure {
                    Log.d("AllStreetsRequest", it.localizedMessage!!)
                    streetId = "-1"
                }
            }
            catch (exc: Exception){
                Log.d("AllStreetsRequestCatch", exc.localizedMessage!!)
                streetId = "-1"
            }
        }
    }

    fun getHouses(streetId: String){
        viewModelScope.launch {
            try{
                val request = repository.getHouseById(streetId)
                request.onSuccess {
                    val neededHouses = it
                    val elementComparator = compareByDescending <Element> { it.number }


                    val sorted = neededHouses
                        .map { parse(it.house) }
                        .sortedWith(elementComparator)
                        .map { it.originalValue }

                    val sortedWithTitle = mutableListOf<String>()

                    sortedWithTitle.addAll(sorted.reversed())
                    val newHouse = mutableListOf<House>()

                    for (i in it.indices){
                        for (j in it.indices){
                            if (neededHouses[j].house == sortedWithTitle[i]){
                                newHouse.add(neededHouses[j])
                            }
                        }
                    }
                    newHouse.add(0, House("-1","Выберите дом"))
                    houses.value = newHouse
                }
                request.onFailure {
                    Log.d("AllHousesRequest", it.localizedMessage!!)
                }
            }
            catch (exc: Exception){
                exc.printStackTrace()
            }
        }
    }


    data class Element(
        val originalValue: String,
        val number: Int?,
    )

    private fun parse(s: String): Element {
        val normalized = s.lowercase()
        val regex = Regex("[^0-9]")
        val numberList = normalized.split(regex)

        val number = numberList[0].toInt()
        return Element(s, number)
    }
}