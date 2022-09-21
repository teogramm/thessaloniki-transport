package xyz.teogramm.thessalonikitransit.database.alerts

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * User alert database module for Hilt dependency injection. Code based on:
 * https://stackoverflow.com/questions/63146318/how-to-create-and-use-a-room-database-in-kotlin-dagger-hilt
 * https://developer.android.com/codelabs/android-hilt
 */
@Module
@InstallIn(SingletonComponent::class)
object AlertsDatabaseModule {
    @Provides
    @Singleton
    fun provideAlertsDatabase(@ApplicationContext app: Context): AlertsDatabase =
        Room.databaseBuilder(app, AlertsDatabase::class.java, "alerts.db").build()

    @Provides
    fun provideAlertsDao(db: AlertsDatabase): AlertsDao = db.alertsDao()
}
