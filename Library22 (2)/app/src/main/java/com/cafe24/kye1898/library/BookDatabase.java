package com.cafe24.kye1898.library;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by YeEun on 2017-06-25.
 */

public class BookDatabase {
    /* TAG for debugging */
    public static final String TAG = "BookDatabase";
    /* Singleton instance */
    private static BookDatabase database;
    /* database name */
   public static String DATABASE_NAME = "book.db";
    /* table name for BOOK_INFO*/
    public static String TABLE_BOOK_INFO = "BOOK_INFO";
    public static String TABLE_PHOTO = "PHOTO";
    public static String TABLE_TOREAD = "READ";
    public static String ACCOUNT_TABLE = "ACCOUNT";
    /* version */
    public static int DATABASE_VERSION = 1;
    /* Helper class defined */
    public DatabaseHelper dbHelper;
    /* Database object */
    public SQLiteDatabase db;
    private Context context;


    /**
     * Constructor
     */
    private BookDatabase(Context context) {
        this.context = context;
    }


    public static BookDatabase getInstance(Context context) {
        if (database == null) {
            database = new BookDatabase(context);
        }

        return database;
    }

    /* open database */
    public boolean open() {
        println("opening database [" + DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    /* close database */
    public void close() {
        println("closing database [" + DATABASE_NAME + "].");
        db.close();
        database = null;
    }

    /**
     * execute raw query using the input SQL
     * close the cursor after fetching any result
     *
     * @param SQL
     * @return
     */
    public Cursor rawQuery(String SQL) {
        println("\nexecuteQuery called.\n");

        Cursor c1 = null;

        try {
            c1 = db.rawQuery(SQL, null);
            println("cursor count : " + c1.getCount());
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }

        return c1;
    }

    public boolean execSQL(String SQL) {
        println("\nexecute called.\n");

        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }

        return true;
    }
    //"select count (*) from "+
    public boolean checkExists(String name){
        String query = TABLE_BOOK_INFO+ " where NAME = '"+name+"';";
        long count = DatabaseUtils.queryNumEntries(db,query,null);
        if(count>0) return true;
        else return false;
    }

    public boolean checkExistToRead(String name){
        String query = TABLE_TOREAD+ " where NAME = '"+name+"';";
        long count = DatabaseUtils.queryNumEntries(db,query,null);
        if(count>0) return true;
        else return false;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase _db) {
            // TABLE_BOOK_INFO
            println("creating table [" + TABLE_BOOK_INFO + "].");

            // drop existing table
            String DROP_SQL = "drop table if exists " + TABLE_BOOK_INFO;
            try {
                _db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }


            // create table
            String CREATE_SQL = "create table " + TABLE_BOOK_INFO + "("
                    + "  NAME VARCHAR(50) NOT NULL PRIMARY KEY, "
                    + "  AUTHOR VARCHAR(20), "
                    + "  PUBLISHER VARCHAR(20), "
                    + "  BOOKMARK INTEGER, "
                    + "  CONTENTS TEXT, "
                    + "  DATE DATE DEFAULT CURRENT_DATE, "
                    + "  STAR BOOLEAN DEFAULT 'false', "
                    + "  PHOTO INTEGER "
                    + ")";

            try {
                _db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            // create table
            CREATE_SQL = "create table " + TABLE_PHOTO + "("
                    + "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "  URI TEXT, "
                    + "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                    + ")";
            try {
                _db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            // create table
            CREATE_SQL = "create table " + TABLE_TOREAD + "("
                    + "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "  DONE BOOLEAN DEFAULT 'false',"
                    + "  NAME TEXT, "
                    + "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                    + ")";
            try {
                _db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            CREATE_SQL="create table " + ACCOUNT_TABLE + "("
                    + "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + " ALARM INTEGER, "
                    + " GOAL INTEGER,"
                    + " BOOK VARCHAR(50)"
                    + ")";

            try {
                _db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            try {
                _db.execSQL( "insert into " + ACCOUNT_TABLE + "(ALARM, GOAL,BOOK) values ('"+3+"', '"+ 80 + "', '없음');" );
            } catch(Exception ex) {
                Log.e(TAG, "Exception in executing insert SQL.", ex);
            }

        }

        public void insertDB(SQLiteDatabase _db, String name, String author, String publisher, int bookmark, String contents ){
            try {
                _db.execSQL( "insert into " + TABLE_BOOK_INFO +
                        "(NAME, AUTHOR, PUBLISHER, BOOKMARK, CONTENTS) values ('"
                        + name + "', '" + author + "', '" + publisher+ "', " +bookmark+", '"+contents + "');" );
            } catch(Exception ex) {
                Log.e(TAG, "Exception in executing insert SQL.", ex);
            }
        }

        private void insertRecord(SQLiteDatabase _db, String name, String author, String contents) {
            try {
                _db.execSQL( "insert into " + TABLE_BOOK_INFO +
                        "(NAME, AUTHOR, CONTENTS) values ('"
                        + name + "', '" + author + "', '" + contents + "');" );
            } catch(Exception ex) {
                Log.e(TAG, "Exception in executing insert SQL.", ex);
            }
        }

        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + DATABASE_NAME + "].");

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");

            if (oldVersion < 2) {   // version 1

            }

        }





    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }
}
