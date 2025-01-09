package com.example.progetto_java.model;

import androidx.room.*;

import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface OggettoDao {
    @Insert
    ListenableFuture<Void> inserisciOggetto(Oggetto oggetto);
    @Query("SELECT * FROM Oggetto WHERE id = :id")
    ListenableFuture<Oggetto> getObjectById(int id);

}

