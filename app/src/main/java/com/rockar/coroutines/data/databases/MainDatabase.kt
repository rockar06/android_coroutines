package com.rockar.coroutines.data.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rockar.coroutines.data.model.Title

@Database(entities = [Title::class], version = 1, exportSchema = false)
abstract class MainDatabase: RoomDatabase() {
    abstract val titleDao: TitleDao
}
