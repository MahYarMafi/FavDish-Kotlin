package ca.android.jac.favdish.application

import android.app.Application
import ca.android.jac.favdish.model.database.FavDishRepository
import ca.android.jac.favdish.model.database.FavDishRoomDatabase

class FavDishApplication : Application() {

    private val database by lazy { FavDishRoomDatabase.getDatabase(this@FavDishApplication) }
    val repository by lazy { FavDishRepository(database.favDishDao()) }
}