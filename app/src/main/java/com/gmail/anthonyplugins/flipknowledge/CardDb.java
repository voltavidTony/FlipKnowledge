package com.gmail.anthonyplugins.flipknowledge;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Update;

import java.util.List;

@Database(entities = {CardObject.class}, version = 1)
abstract class CardDb extends RoomDatabase {
    abstract CardDao cardDao();

    @Dao
    interface CardDao {
        @Query("SELECT * FROM cardobject")
        List<CardObject> getAll();

        @Insert
        void insertAll(CardObject... flashcards);

        @Delete
        void delete(CardObject flashcard);

        @Update(onConflict = OnConflictStrategy.REPLACE)
        void update(CardObject flashcard);
    }
}