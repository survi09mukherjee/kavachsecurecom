package com.kavachsecurecomm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, encryptionKey: ByteArray): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Critical: Load SQLCipher native libraries before any DB operations
                SQLiteDatabase.loadLibs(context.applicationContext)
                
                // Initialize SQLCipher encrypted database support factory
                val supportFactory = SupportFactory(encryptionKey)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kavach_secure_store.db"
                )
                .openHelperFactory(supportFactory) // Critical: Data rests encrypted on disk
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
