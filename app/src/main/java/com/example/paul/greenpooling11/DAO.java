package com.example.paul.greenpooling11;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

public class DAO {

    //user table
    public static final String USERID = "userId";
    public static final String USERNAME = "userName";
    public static final String USERAGE = "userAge";
    public static final String USERGENDER = "userGender";
    public static final String USERLOCATION = "userLocation";
    public static final String USEREMAIL = "userEmail";
    public static final String USERBIO = "userBio";
    public static final String USERPREFCHATTY = "userPrefChatty";
    public static final String USERPREFSMOKING = "userPrefSmoking";

    //user car table
    public static final String USERCARMAKE = "userCarMake";
    public static final String USERCARMODEL = "userCarModel";
    public static final String USERCARSEATS = "userCarSeats";
    public static final String USERCARCOLOUR = "userCarColour";

    public static final int DATABASE_VERSION = 1;

    private static String USER_TABLE="Users";
    private static String USER_CAR_TABLE="UserCar";
    private final Context context;
    private MyDatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private static String DBName ="Greenpooling";

    private static final String USER_TABLE_CREATE =
            "create table "+ USER_TABLE+"("+
                    USERID+" integer primary key" +
                    ", " +
                    USERNAME+"text not null, " +
                    USERAGE+ " number, " +
                    USERGENDER+" text, " +
                    USERLOCATION+" text, " +
                    USEREMAIL+" text, " +
                    USERBIO+"text, " +
                    USERPREFCHATTY+"text, " +
                    USERPREFSMOKING+"text)";

    private static final String USER_CAR_TABLE_CREATE =
            "create table "+ USER_CAR_TABLE+"("+
                    USERID + "integer, "+
                    USERCARMAKE+"text, " +
                    USERCARMODEL+"text, " +
                    USERCARSEATS+"number, " +
                    USERCARCOLOUR +"text)";


    public DAO(Context ctx) {
        this.context = ctx;
        DBHelper = new MyDatabaseHelper(context);
    }

    public static class MyDatabaseHelper extends SQLiteOpenHelper {

        MyDatabaseHelper(Context context) {

            super(context, DBName, null, DATABASE_VERSION);
        }


        public void onCreate(SQLiteDatabase db) {

            db.execSQL(USER_TABLE_CREATE);
            db.execSQL(USER_CAR_TABLE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + USER_CAR_TABLE);

            onCreate(db);
        }
    }

    public DAO open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {

        DBHelper.close();
    }

    /*public long insertSomething(String TaskName, String TaskDesc)
    {
        ContentValues initialValues = new ContentValues();

        initialValues.put(TASKNAME, TaskName);
        initialValues.put(TASKDESCRIPTION, TaskDesc);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public Cursor selectSomething(long rowId) throws SQLException
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{
                KEY_ROWID,
                TASKNAME,
                TASKDESCRIPTION
        }, KEY_ROWID + "="+ rowId, null, null, null, null, null);

        return mCursor;
    }

    public Cursor getSomeThing(long rowId) throws SQLException
    {
        Cursor mCursor= db.query(true, DATABASE_TABLE, new String[]{
                KEY_ROWID,
                TASKNAME,
                TASKDESCRIPTION
        }, KEY_ROWID + "="+ rowId, null, null, null, null, null);

        if(mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }*/

}


