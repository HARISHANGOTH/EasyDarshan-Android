package com.easydarshan.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.easydarshan.data.local.dao.BookingDao;
import com.easydarshan.data.local.dao.TempleDao;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.Temple;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Temple.class, Booking.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract TempleDao templeDao();
    public abstract BookingDao bookingDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "easydarshan_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
