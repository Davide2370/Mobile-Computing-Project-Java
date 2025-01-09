package com.example.progetto_java.model;

import androidx.room.*;

import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface UtenteDao {
    @Insert
    ListenableFuture<Void> InserisciUtente(Utente utente);
    @Update
    ListenableFuture<Void> AggiornaUtente(Utente utente);
    @Query("UPDATE Utente SET picture = :newImage WHERE uid = :userId")
    ListenableFuture<Void> AggiornaImmagineUtente(int userId, String newImage);

    @Query("UPDATE Utente SET name = :newName WHERE uid = :userId")
    ListenableFuture<Void> AggiornaNomeUtente(int userId, String newName);
    @Query("UPDATE Utente SET position_share = :positionShare WHERE uid = :userId")
    ListenableFuture<Void> AggiornaCondivisionePosizioneUtente(int userId, boolean positionShare);

    @Query("SELECT * FROM Utente WHERE uid = :uid")
    ListenableFuture<Utente> getUserById(int uid);

    @Query("UPDATE Utente SET lon = :lon, lat = :lat WHERE uid = :uid")
    ListenableFuture<Void> setUserCoordinates(double lon,double lat, int uid);

}

