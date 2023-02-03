package ca.android.jac.favdish.model.database

import androidx.room.*
import ca.android.jac.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDish>>


    /**
     * A function to update favorite dish details to the local database using Room.
     *
     * @param favDish - Here we will pass the entity class that we have created with updated details along with "id".
     */
    @Update
    suspend fun updateFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favorite_dish = 1")
    fun getFavoriteDishesList(): Flow<List<FavDish>>

    /**
     * A function to delete favorite dish details from the local database using Room.
     *
     * @param favDish - Here we will pass the entity class with details that we want to delete.
     */
    @Delete
    suspend fun deleteFavDishDetails(favDish: FavDish)

    /**
     * A function to get the list of dishes based on the dish type from the database.
     *
     * @param filterType - DishType
     */
    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE type = :filterType")
    fun getFilteredDishesList(filterType: String): Flow<List<FavDish>>
    // END

}