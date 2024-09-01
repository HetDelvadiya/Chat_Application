package com.awcindia.chatapplication.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.awcindia.chatapplication.model.CallHistory
import kotlin.concurrent.Volatile

@Database(entities = [CallHistory::class], version = 1)
abstract class CallHistoryDatabase() : RoomDatabase() {

    abstract fun callDoa(): CallHistoryDao

    companion object {

        @Volatile
        private var INSTANCE: CallHistoryDatabase? = null

        fun getDatabase(context: Context): CallHistoryDatabase {

            if (INSTANCE == null) {

                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        CallHistoryDatabase::class.java,
                        "call_history"
                    ).build()
                }
            }

            return INSTANCE!!
        }

    }
}