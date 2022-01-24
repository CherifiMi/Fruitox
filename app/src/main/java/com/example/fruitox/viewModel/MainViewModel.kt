package com.example.fruitox.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fruitox.data.Repository
import com.example.fruitox.model.Fruit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application ) : AndroidViewModel(application){

    val myResponse: MutableLiveData<Fruit> = MutableLiveData()

    fun getFruit(name : String) {
        viewModelScope.launch {
            myResponse.value = repository.remote.getFruit(name)
        }
    }

}