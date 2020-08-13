package com.toggl.database.dao

import androidx.annotation.RestrictTo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.toggl.database.models.DatabaseUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun get(): DatabaseUser?

    @Query("SELECT * FROM users LIMIT 1")
    fun getFlow(): Flow<DatabaseUser?>

    @Insert
    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    fun insert(user: DatabaseUser)

    @Transaction
    fun set(databaseUser: DatabaseUser) {
        clear()
        insert(databaseUser)
    }

    @Query("DELETE FROM users")
    fun clear()
}
