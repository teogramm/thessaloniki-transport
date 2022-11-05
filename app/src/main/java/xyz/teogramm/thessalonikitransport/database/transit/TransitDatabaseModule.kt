package xyz.teogramm.thessalonikitransport.database.transit

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.teogramm.thessalonikitransport.database.transit.alerts.AlertDao
import javax.inject.Singleton

/**
 * Transit database module for Hilt dependency injection. Code based on:
 * https://stackoverflow.com/questions/63146318/how-to-create-and-use-a-room-database-in-kotlin-dagger-hilt
 * https://developer.android.com/codelabs/android-hilt
 */
@Module
@InstallIn(SingletonComponent::class)
object TransitDatabaseModule {
    @Provides
    @Singleton
    fun provideTransitDatabase(@ApplicationContext app: Context): TransitDatabase =
        Room.databaseBuilder(app, TransitDatabase::class.java, "oasth.db").build()

    @Provides
    fun provideTransitDao(db: TransitDatabase): TransitDao = db.transitDao()
    @Provides
    fun provideAlertDao(db: TransitDatabase): AlertDao = db.alertDao()
}
