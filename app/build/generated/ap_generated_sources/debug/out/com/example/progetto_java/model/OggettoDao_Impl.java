package com.example.progetto_java.model;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
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
public final class OggettoDao_Impl implements OggettoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Oggetto> __insertionAdapterOfOggetto;

  public OggettoDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOggetto = new EntityInsertionAdapter<Oggetto>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Oggetto` (`id`,`type`,`level`,`lat`,`lon`,`image`,`name`,`distanza`,`isNear`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Oggetto value) {
        stmt.bindLong(1, value.id);
        if (value.type == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.type);
        }
        stmt.bindLong(3, value.level);
        stmt.bindDouble(4, value.lat);
        stmt.bindDouble(5, value.lon);
        if (value.image == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.image);
        }
        if (value.name == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.name);
        }
        stmt.bindLong(8, value.distanza);
        final int _tmp = value.isNear ? 1 : 0;
        stmt.bindLong(9, _tmp);
      }
    };
  }

  @Override
  public ListenableFuture<Void> inserisciOggetto(final Oggetto oggetto) {
    return GuavaRoom.createListenableFuture(__db, true, new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOggetto.insert(oggetto);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public ListenableFuture<Oggetto> getObjectById(final int id) {
    final String _sql = "SELECT * FROM Oggetto WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return GuavaRoom.createListenableFuture(__db, false, new Callable<Oggetto>() {
      @Override
      public Oggetto call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, _cancellationSignal);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "level");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final int _cursorIndexOfImage = CursorUtil.getColumnIndexOrThrow(_cursor, "image");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDistanza = CursorUtil.getColumnIndexOrThrow(_cursor, "distanza");
          final int _cursorIndexOfIsNear = CursorUtil.getColumnIndexOrThrow(_cursor, "isNear");
          final Oggetto _result;
          if(_cursor.moveToFirst()) {
            _result = new Oggetto();
            _result.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfType)) {
              _result.type = null;
            } else {
              _result.type = _cursor.getString(_cursorIndexOfType);
            }
            _result.level = _cursor.getInt(_cursorIndexOfLevel);
            _result.lat = _cursor.getDouble(_cursorIndexOfLat);
            _result.lon = _cursor.getDouble(_cursorIndexOfLon);
            if (_cursor.isNull(_cursorIndexOfImage)) {
              _result.image = null;
            } else {
              _result.image = _cursor.getString(_cursorIndexOfImage);
            }
            if (_cursor.isNull(_cursorIndexOfName)) {
              _result.name = null;
            } else {
              _result.name = _cursor.getString(_cursorIndexOfName);
            }
            _result.distanza = _cursor.getInt(_cursorIndexOfDistanza);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsNear);
            _result.isNear = _tmp != 0;
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
