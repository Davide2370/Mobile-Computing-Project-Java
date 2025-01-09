package com.example.progetto_java.model;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class UtenteModel_Impl extends UtenteModel {
  private volatile UtenteDao _utenteDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Utente` (`uid` INTEGER NOT NULL, `name` TEXT, `picture` TEXT, `life` INTEGER NOT NULL, `experience` INTEGER NOT NULL, `weapon` INTEGER NOT NULL, `armor` INTEGER NOT NULL, `amulet` INTEGER NOT NULL, `profile_version` INTEGER NOT NULL, `position_share` INTEGER NOT NULL, `lat` REAL NOT NULL, `lon` REAL NOT NULL, PRIMARY KEY(`uid`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '846e96162df48c6a25804835674ec0f0')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `Utente`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsUtente = new HashMap<String, TableInfo.Column>(12);
        _columnsUtente.put("uid", new TableInfo.Column("uid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("picture", new TableInfo.Column("picture", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("life", new TableInfo.Column("life", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("experience", new TableInfo.Column("experience", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("weapon", new TableInfo.Column("weapon", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("armor", new TableInfo.Column("armor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("amulet", new TableInfo.Column("amulet", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("profile_version", new TableInfo.Column("profile_version", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("position_share", new TableInfo.Column("position_share", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("lat", new TableInfo.Column("lat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUtente.put("lon", new TableInfo.Column("lon", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUtente = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUtente = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUtente = new TableInfo("Utente", _columnsUtente, _foreignKeysUtente, _indicesUtente);
        final TableInfo _existingUtente = TableInfo.read(_db, "Utente");
        if (! _infoUtente.equals(_existingUtente)) {
          return new RoomOpenHelper.ValidationResult(false, "Utente(com.example.progetto_java.model.Utente).\n"
                  + " Expected:\n" + _infoUtente + "\n"
                  + " Found:\n" + _existingUtente);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "846e96162df48c6a25804835674ec0f0", "a11f4e64d661647595ef9e88cccd1e54");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "Utente");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `Utente`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UtenteDao.class, UtenteDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public UtenteDao utenteDao() {
    if (_utenteDao != null) {
      return _utenteDao;
    } else {
      synchronized(this) {
        if(_utenteDao == null) {
          _utenteDao = new UtenteDao_Impl(this);
        }
        return _utenteDao;
      }
    }
  }
}
