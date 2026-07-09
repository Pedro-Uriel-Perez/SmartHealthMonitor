package mx.edu.utng.smarthealthmonitor.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LecturaFC::class], version = 1, exportSchema = false)
abstract class SmartHealthDB : RoomDatabase() {
    abstract fun lecturaFCDao(): LecturaFCDao
}

