package id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.ac.ui.cs.mobileprogramming.anisha_inas.wacana.db.entity.ItemEntity

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(vararg item: ItemEntity)

    @Query("DELETE FROM item WHERE trip_id = :tripId")
    fun deleteItems(tripId: Long)

    @Query("SELECT * from item WHERE trip_id = :tripId")
    fun getAllItems(tripId: Long): LiveData<List<ItemEntity>>

    @Query("SELECT * from item")
    fun getAllItems(): LiveData<List<ItemEntity>>
}