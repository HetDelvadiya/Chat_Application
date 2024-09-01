package com.awcindia.chatapplication.repository

import androidx.lifecycle.LiveData
import com.awcindia.chatapplication.model.CallHistory
import com.awcindia.chatapplication.model.database.CallHistoryDao

class CallHistoryRepository(val callHistoryDao: CallHistoryDao) {

    fun getCallHistory(): LiveData<List<CallHistory>> {
        return callHistoryDao.getAllCallHistory()
    }

    suspend fun insertCallHistory(callHistory: CallHistory) {
        return callHistoryDao.insertCallHistory(callHistory)
    }
}