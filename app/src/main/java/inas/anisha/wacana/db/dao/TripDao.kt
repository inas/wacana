package inas.anisha.wacana.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import inas.anisha.wacana.db.entity.TripEntity

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tripEntity: TripEntity): Long

    @Delete
    fun deleteTrip(vararg trip: TripEntity)

    @Query("DELETE FROM trip")
    fun deleteAll()

    @Query("SELECT * from trip")
    fun getAllTrip(): LiveData<List<TripEntity>>

    @Query("SELECT * from trip WHERE id = :id LIMIT 1")
    fun getTripById(id: Long): TripEntity
}