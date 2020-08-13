package com.toggl.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.toggl.database.models.DatabaseTag
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags")
    fun getAll(): Flow<List<DatabaseTag>>

    @Query("SELECT * FROM tags WHERE id = :id")
    fun getOne(id: Long): DatabaseTag

    @Insert
    fun insertAll(databaseTag: List<DatabaseTag>): List<Long>

    @Insert
    fun insert(databaseTag: DatabaseTag): Long

    @Update
    fun update(databaseTag: DatabaseTag)

    @Update
    fun updateAll(databaseTag: List<DatabaseTag>)

    @Delete
    fun delete(databaseTag: DatabaseTag)

    @Query("DELETE FROM tags")
    fun clear()
}
