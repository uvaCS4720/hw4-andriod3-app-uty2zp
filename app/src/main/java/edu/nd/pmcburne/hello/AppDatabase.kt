package edu.nd.pmcburne.hello

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlacemarkEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placemarkDao(): PlacemarkDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "placemarks.db")
                    .build().also { INSTANCE = it }
            }
        }
    }
}