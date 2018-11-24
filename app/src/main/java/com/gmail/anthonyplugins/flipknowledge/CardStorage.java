package com.gmail.anthonyplugins.flipknowledge;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;

public class CardStorage {

    private final CardDb db;

    CardStorage(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), CardDb.class, "flashcard-database")
                .allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }

    public List<CardObject> getAllCards() {
        return db.cardDao().getAll();
    }

    public void insertCards(CardObject... flashcards) {
        db.cardDao().insertAll(flashcards);
    }

    public void deleteCard(CardObject flashcard) {
        db.cardDao().delete(flashcard);
    }

    public void updateCard(CardObject flashcard) {
        db.cardDao().update(flashcard);
    }
}
