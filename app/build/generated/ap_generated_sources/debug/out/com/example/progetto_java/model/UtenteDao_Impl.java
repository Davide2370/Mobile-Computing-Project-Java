package com.example.progetto_java.model;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.guava.GuavaRoom;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.google.common.util.concurrent.ListenableFuture;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.lang.Void;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class UtenteDao_Impl implements UtenteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Utente> __insertionAdapterOfUtente;

  private final EntityDeletionOrUpdateAdapter<Utente> __updateAdapterOfUtente;

  private final SharedSQLiteStatement __preparedStmtOfAggiornaImmagineUtente;

  private final SharedSQLiteStatement __preparedStmtOfAggiornaNomeUtente;

  private final SharedSQLiteStatement __preparedStmtOfAggiornaCondivisionePosizioneUtente;

  private final SharedSQLiteStatement __preparedStmtOfSetUserCoordinates;

  public UtenteDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUtente = new EntityInsertionAdapter<Utente>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Utente` (`uid`,`name`,`picture`,`life`,`experience`,`weapon`,`armor`,`amulet`,`profile_version`,`position_share`,`lat`,`lon`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Utente value) {
        stmt.bindLong(1, value.uid);
        if (value.name == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.name);
        }
        if (value.picture == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.picture);
        }
        stmt.bindLong(4, value.life);
        stmt.bindLong(5, value.experience);
        stmt.bindLong(6, value.weapon);
        stmt.bindLong(7, value.armor);
        stmt.bindLong(8, value.amulet);
        stmt.bindLong(9, value.profileVersion);
        final int _tmp = value.positionShare ? 1 : 0;
        stmt.bindLong(10, _tmp);
        stmt.bindDouble(11, value.lat);
        stmt.bindDouble(12, value.lon);
      }
    };
    this.__updateAdapterOfUtente = new EntityDeletionOrUpdateAdapter<Utente>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `Utente` SET `uid` = ?,`name` = ?,`picture` = ?,`life` = ?,`experience` = ?,`weapon` = ?,`armor` = ?,`amulet` = ?,`profile_version` = ?,`position_share` = ?,`lat` = ?,`lon` = ? WHERE `uid` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Utente value) {
        stmt.bindLong(1, value.uid);
        if (value.name == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.name);
        }
        if (value.picture == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.picture);
        }
        stmt.bindLong(4, value.life);
        stmt.bindLong(5, value.experience);
        stmt.bindLong(6, value.weapon);
        stmt.bindLong(7, value.armor);
        stmt.bindLong(8, value.amulet);
        stmt.bindLong(9, value.profileVersion);
        final int _tmp = value.positionShare ? 1 : 0;
        stmt.bindLong(10, _tmp);
        stmt.bindDouble(11, value.lat);
        stmt.bindDouble(12, value.lon);
        stmt.bindLong(13, value.uid);
      }
    };
    this.__preparedStmtOfAggiornaImmagineUtente = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE Utente SET picture = ? WHERE uid = ?";
        return _query;
      }
    };
    this.__preparedStmtOfAggiornaNomeUtente = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE Utente SET name = ? WHERE uid = ?";
        return _query;
      }
    };
    this.__preparedStmtOfAggiornaCondivisionePosizioneUtente = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE Utente SET position_share = ? WHERE uid = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetUserCoordinates = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE Utente SET lon = ?, lat = ? WHERE uid = ?";
        return _query;
      }
    };
  }

  @Override
  public ListenableFuture<Void> InserisciUtente(final Utente utente) {
    return GuavaRoom.createListenableFuture(__db, true, new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUtente.insert(utente);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public ListenableFuture<Void> AggiornaUtente(final Utente utente) {
    return GuavaRoom.createListenableFuture(__db, true, new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUtente.handle(utente);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public ListenableFuture<Void> AggiornaImmagineUtente(final int userId, final String newImage) {
    return GuavaRoom.createListenableFuture(__db, true, new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAggiornaImmagineUtente.acquire();
        int _argIndex = 1;
        if (newImage == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, newImage);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, userId);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
          __preparedStmtOfAggiornaImmagineUtente.release(_stmt);
        }
      }
    });
  }

  @Override
  public ListenableFuture<Void> AggiornaNomeUtente(final int userId, final String newName) {
    return GuavaRoom.createListenableFuture(__db, true, new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAggiornaNomeUtente.acquire();
        int _argIndex = 1;
        if (newName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, newName);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, userId);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
          __preparedStmtOfAggiornaNomeUtente.release(_stmt);
        }
      }
    });
  }

  @Override
  public ListenableFuture<Void> AggiornaCondivisionePosizioneUtente(final int userId,
      final boolean positionShare) {
    return GuavaRoom.createListenableFuture(__db, true, new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAggiornaCondivisionePosizioneUtente.acquire();
        int _argIndex = 1;
        final int _tmp = positionShare ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, userId);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
          __preparedStmtOfAggiornaCondivisionePosizioneUtente.release(_stmt);
        }
      }
    });
  }

  @Override
  public ListenableFuture<Void> setUserCoordinates(final double lon, final double lat,
      final int uid) {
    return GuavaRoom.createListenableFuture(__db, true, new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetUserCoordinates.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, lon);
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, lat);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, uid);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
          __preparedStmtOfSetUserCoordinates.release(_stmt);
        }
      }
    });
  }

  @Override
  public ListenableFuture<Utente> getUserById(final int uid) {
    final String _sql = "SELECT * FROM Utente WHERE uid = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return GuavaRoom.createListenableFuture(__db, false, new Callable<Utente>() {
      @Override
      public Utente call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, _cancellationSignal);
        try {
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfLife = CursorUtil.getColumnIndexOrThrow(_cursor, "life");
          final int _cursorIndexOfExperience = CursorUtil.getColumnIndexOrThrow(_cursor, "experience");
          final int _cursorIndexOfWeapon = CursorUtil.getColumnIndexOrThrow(_cursor, "weapon");
          final int _cursorIndexOfArmor = CursorUtil.getColumnIndexOrThrow(_cursor, "armor");
          final int _cursorIndexOfAmulet = CursorUtil.getColumnIndexOrThrow(_cursor, "amulet");
          final int _cursorIndexOfProfileVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "profile_version");
          final int _cursorIndexOfPositionShare = CursorUtil.getColumnIndexOrThrow(_cursor, "position_share");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final Utente _result;
          if(_cursor.moveToFirst()) {
            final int _tmpUid;
            _tmpUid = _cursor.getInt(_cursorIndexOfUid);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpPicture;
            if (_cursor.isNull(_cursorIndexOfPicture)) {
              _tmpPicture = null;
            } else {
              _tmpPicture = _cursor.getString(_cursorIndexOfPicture);
            }
            final int _tmpLife;
            _tmpLife = _cursor.getInt(_cursorIndexOfLife);
            final int _tmpExperience;
            _tmpExperience = _cursor.getInt(_cursorIndexOfExperience);
            final int _tmpProfileVersion;
            _tmpProfileVersion = _cursor.getInt(_cursorIndexOfProfileVersion);
            final boolean _tmpPositionShare;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPositionShare);
            _tmpPositionShare = _tmp != 0;
            _result = new Utente(_tmpUid,_tmpName,_tmpPicture,_tmpLife,_tmpExperience,_tmpProfileVersion,_tmpPositionShare);
            _result.weapon = _cursor.getInt(_cursorIndexOfWeapon);
            _result.armor = _cursor.getInt(_cursorIndexOfArmor);
            _result.amulet = _cursor.getInt(_cursorIndexOfAmulet);
            _result.lat = _cursor.getDouble(_cursorIndexOfLat);
            _result.lon = _cursor.getDouble(_cursorIndexOfLon);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }
    }, _statement, true, _cancellationSignal);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
