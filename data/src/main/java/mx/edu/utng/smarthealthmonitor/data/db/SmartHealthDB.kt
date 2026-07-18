package mx.edu.utng.smarthealthmonitor.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE lecturas_fc ADD COLUMN dispositivo TEXT NOT NULL DEFAULT 'app'")
        db.execSQL("ALTER TABLE lecturas_fc ADD COLUMN sincronizado INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(entities = [LecturaFC::class], version = 2, exportSchema = false)
abstract class SmartHealthDB : RoomDatabase() {
    abstract fun lecturaFCDao(): LecturaFCDao
}

