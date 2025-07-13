package com.example.gts_goattracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GoatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GoatRepository
    private val _goats = MutableLiveData<List<Goat>>()
    val goats: LiveData<List<Goat>> = _goats

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        val database = GoatDatabase.getDatabase(application)
        repository = GoatRepository(database.goatDao())
        loadGoats()
    }

    fun loadGoats() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _goats.value = repository.getAllGoats()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error loading goats: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun insertGoat(goat: Goat) {
        viewModelScope.launch {
            try {
                repository.insertGoat(goat)
                loadGoats() // Refresh list
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error saving goat: ${e.message}"
            }
        }
    }

    fun updateGoat(goat: Goat) {
        viewModelScope.launch {
            try {
                repository.updateGoat(goat)
                loadGoats()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error updating goat: ${e.message}"
            }
        }
    }

    fun deleteGoat(goat: Goat) {
        viewModelScope.launch {
            try {
                repository.deleteGoat(goat)
                loadGoats()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error deleting goat: ${e.message}"
            }
        }
    }
}