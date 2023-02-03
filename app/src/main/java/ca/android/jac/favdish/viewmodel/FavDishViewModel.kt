package ca.android.jac.favdish.viewmodel

import androidx.lifecycle.*
import ca.android.jac.favdish.model.database.FavDishRepository
import ca.android.jac.favdish.model.entities.FavDish
import kotlinx.coroutines.launch

class FavDishViewModel(private val repository: FavDishRepository) : ViewModel() {

    fun insert(dish: FavDish) = viewModelScope.launch {
        // Call the repository function and pass the details.
        repository.insertFavDishData(dish)
    }


    val allDishesList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    val favoriteDishes: LiveData<List<FavDish>> = repository.favoriteDishes.asLiveData()

    /**
     * Launching a new coroutine to delete the data in a non-blocking way.
     */
    fun delete(dish: FavDish) = viewModelScope.launch {
        repository.deleteFavDishData(dish)
    }

    fun getFilteredList(value: String): LiveData<List<FavDish>> = repository.filteredListDishes(value).asLiveData()
}


class FavDishViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}