package edu.nd.pmcburne.hello

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = PlacemarkRepository(db.placemarkDao())

    private val _allPlacemarks = MutableStateFlow<List<PlacemarkEntity>>(emptyList())
    val selectedTag = MutableStateFlow("core")

    val filtered = combine(_allPlacemarks, selectedTag) { places, tag ->
        places.filter { tag in it.tags.split(",") }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allTags = _allPlacemarks.map { places ->
        places.flatMap { it.tags.split(",") }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            _allPlacemarks.value = repo.syncAndGetAll()
        }
    }
}