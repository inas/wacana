package inas.anisha.wacana.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import inas.anisha.classify.db.converters.CalendarConverter
import inas.anisha.wacana.db.dao.DocumentDao
import inas.anisha.wacana.db.dao.TripDao
import inas.anisha.wacana.db.entity.DocumentEntity
import inas.anisha.wacana.db.entity.TripEntity

@Database(entities = [TripEntity::class, DocumentEntity::class], version = 1)
@TypeConverters(CalendarConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile
        private var mInstance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val i = mInstance
            i?.let { return i }

            return synchronized(AppDatabase) {
                val i2 = mInstance
                if (i2 != null) {
                    i2
                } else {
                    val created = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "app_database"
                    ).build()
                    mInstance = created
                    created
                }
            }
        }
    }

}