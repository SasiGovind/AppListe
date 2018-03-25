package upec.projetandroid2017_2018.DatabaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by T450 on 16/02/2018.
 */

public class DataBase extends SQLiteOpenHelper{

    // DATABASE ************************************************************************************
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //**********************************************************************************************

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_USER_TABLE);
        onCreate(sqLiteDatabase);
    }

    //TABLE utilisateurs ***************************************************************************
    public static final String TABLE_USER = "users";
    public static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER + ";";

    public static final String USER_ID = "_id";
    public static final String USER_NAME = "name";
    public static final String PASSWORD = "password";

    private static final String CREATE_USER_TABLE = "create table " + TABLE_USER + "("
            + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + USER_NAME + " TEXT NOT NULL, "
            + PASSWORD + " TEXT NOT NULL)";

    // Ajout utilisateur
    public boolean addUser (String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, username);
        values.put(PASSWORD, password);
        long result = db.insert(TABLE_USER, null, values);
        if (result==-1) return false;
        else return true;
    }

    public Cursor getAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor dbResult = db.rawQuery("select * from "+TABLE_USER, null);
        return dbResult;
    }

    public boolean deleteUser (String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USER, USER_NAME + " ='" + username + "' AND "+PASSWORD+" ='"+password+"'", null) > 0;
    }
}
