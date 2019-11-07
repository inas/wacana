package inas.anisha.wacana.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import inas.anisha.wacana.db.entity.ItemEntity

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: ItemEntity)

    @Delete
    fun deleteItems(vararg item: ItemEntity)

    @Query("DELETE FROM item WHERE item_id = :itemId")
    fun deleteItem(itemId: Long)

    @Query("SELECT * from item WHERE trip_id = :tripId")
    fun getAllItems(tripId: Long): LiveData<List<ItemEntity>>

    @Query("SELECT * from item")
    fun getAllItems(): LiveData<List<ItemEntity>>
}