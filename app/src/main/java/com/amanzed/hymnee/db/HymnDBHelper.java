package com.amanzed.hymnee.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import com.amanzed.hymnee.HymnListActivity;
import com.amanzed.hymnee.hymn.*;
public class HymnDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String HYMN_TABLE_NAME = "hymn";
    public static final String HYMN_COLUMN_ID = "id";
    public static final String HYMN_COLUMN_TITLE = "title";
    public static final String HYMN_COLUMN_DENOM = "denom";
    public static final String HYMN_COLUMN_DENOM_NUMB = "numb";
    public static final String HYMN_COLUMN_INFO = "info";
    public static final String HYMN_COLUMN_VERSES = "verses";

    public static final String VERSE_TABLE_NAME = "verse";
    //	HYMN_COLUMN_ID
    public static final String VERSE_COLUMN_POSITION = "position";
    public static final String VERSE_COLUMN_BODY = "body";
    private static String DB_PATH = "/data/data/com.amanzed.hymnee/databases/";
    Context ctx;

    public HymnDBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table hymn (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HYMN_COLUMN_TITLE+", "+ HYMN_COLUMN_DENOM+","+HYMN_COLUMN_DENOM_NUMB+ " INTEGER,"
                + HYMN_COLUMN_INFO+","+ HYMN_COLUMN_VERSES +")");
        /*db.execSQL("create table verse (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "hymn_id INTEGER,"+ VERSE_COLUMN_POSITION+" INTEGER,"+VERSE_COLUMN_BODY+")");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS hymn");
        onCreate(db);
    }

    public boolean insertHymn  (String title, String denom, int numb, String info, String verses)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("denom", denom);
        contentValues.put("numb", numb);
        contentValues.put("info", info);
        contentValues.put("verses", verses);
        db.insert("hymn", null, contentValues);
        /*int tempid = getHymnId(title, denom);
        for (Verse verse : verses) {
            insertVerse(tempid, verse.getPosition(), verse.getBody());
        }*/
        return true;
    }

    public boolean insertHymnWithID  (int id, String title, String denom, int numb, String info, String verses)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("denom", denom);
        contentValues.put("numb", numb);
        contentValues.put("info", info);
        contentValues.put("verses", verses);
        db.insert("hymn", null, contentValues);
        return true;
    }

    public int getHymnId(String title, String denom){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from hymn where title='"+title.replace("'", "''")+"' and denom ='"+denom+"'", null );
        res.moveToFirst();
        int out = Integer.valueOf(res.getString(res.getColumnIndex(HYMN_COLUMN_ID)));
        res.close();
        return out;
    }

    /*public boolean insertVerse(int hymnId, int position, String body){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("hymn_id", hymnId);
        contentValues.put("position", position);
        contentValues.put("body", body);
        db.insert("verse", null, contentValues);
        return true;
    }*/

    public Cursor getHymnData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from hymn where id="+id+"", null );
        res.close();
        return res;
    }

    public int numberOfHymnRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, HYMN_TABLE_NAME);
        return numRows;
    }

    public boolean updateHymn (int id, String title, String denom, int numb, String info, String verses)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("denom", denom);
        contentValues.put("numb", numb);
        contentValues.put("info", info);
        contentValues.put("verses", verses);
        db.update("hymn", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public void deleteAll(String denom){
        //this.getWritableDatabase().execSQL("delete from verse");
        this.getWritableDatabase().execSQL("delete from hymn where "+HYMN_COLUMN_DENOM+" = '"+denom+"'");
    }

   /*public Integer deleteHymn (Integer id)
   {
      SQLiteDatabase db = this.getWritableDatabase();
      return db.delete("hymn", "id = ? ", new String[] { Integer.toString(id) });
   }*/

    public ArrayList<Hymn> getAllHymns(String denom)
    {
        ArrayList<Hymn> array_list = new ArrayList<Hymn>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from hymn where "+HYMN_COLUMN_DENOM+" = '"+denom+"'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Hymn hm = new Hymn();
            hm.setId(Integer.valueOf(res.getString(res.getColumnIndex(HYMN_COLUMN_ID))));
            hm.setDenom(res.getString(res.getColumnIndex(HYMN_COLUMN_DENOM)));
            hm.setNumb(Integer.valueOf(res.getString(res.getColumnIndex(HYMN_COLUMN_DENOM_NUMB))));
            hm.setTitle(res.getString(res.getColumnIndex(HYMN_COLUMN_TITLE)));
            hm.setInfo(res.getString(res.getColumnIndex(HYMN_COLUMN_INFO)));
            //hm.setInfo(res.getString(res.getColumnIndex(HYMN_COLUMN_VERSES)));
            array_list.add(hm);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public String getVersesById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from hymn where id="+id, null );
        res.moveToFirst();
        return res.getString(res.getColumnIndex(HYMN_COLUMN_VERSES));
    }
    /*public ArrayList<Verse> getAllHymnVerses(int id)
    {
        ArrayList<Verse> array_list = new ArrayList<Verse>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from verse where hymn_id="+id, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Verse vs = new Verse();
            vs.setId(Integer.valueOf(res.getString(res.getColumnIndex("id"))));
            vs.setHymnId(Integer.valueOf(res.getString(res.getColumnIndex("hymn_id"))));
            vs.setPosition(Integer.valueOf(res.getString(res.getColumnIndex(VERSE_COLUMN_POSITION))));
            vs.setBody(res.getString(res.getColumnIndex(VERSE_COLUMN_BODY)));
            array_list.add(vs);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }*/

    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if(dbExist){
            //do nothing - database already exist
        }else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database does't exist yet.
        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = ctx.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }
}