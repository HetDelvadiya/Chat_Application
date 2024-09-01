package com.awcindia.chatapplication.model.database

import android.telecom.Call
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.awcindia.chatapplication.model.CallHistory
import com.google.api.ResourceDescriptor.History
@Dao
interface CallHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCallHistory(callHistory: CallHistory)

    @Query("SELECT * FROM call_history ORDER BY id DESC")
     fun getAllCallHistory() : LiveData<List<CallHistory>>
}