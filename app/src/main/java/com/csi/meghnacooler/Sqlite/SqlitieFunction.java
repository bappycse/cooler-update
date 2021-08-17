package com.csi.meghnacooler.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Jahid on 5/02/2019.
 */

public class SqlitieFunction extends SQLiteOpenHelper {
    public static final String DB_Name ="product";
    public static final String Table_Name ="productTable";
    public static final String id = "id";
    public static final String productName = "productName";
    public static final String quantity = "quantity";
    public static final String productId = "productId";
    public static final String Table_Name_Cost ="costTable";
    public static final String id1 = "id1";
    public static final String id2 = "id2";
    public static final String productNameCost = "productNameCost";
    public static final String productIdCost = "productIdCost";
    public static final String quantityCost = "quantityCost";
    public static final String amountCost = "amountCost";
    public static final String unitPriceCost = "unitPriceCost";
    public static final String note = "note";
    public static final String costTitle = "costTitle";
    public static final String problemName = "problemName";
    public static final String problemId = "problemId";
    public static final String Table_Name_Problem ="problemTable";
    public static final int version = 5;

    public SqlitieFunction(Context c)
    {
        super(c,DB_Name,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String string = "CREATE TABLE "+Table_Name+" ("+id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+productName+" TEXT, "+quantity+" TEXT, "+productId+" TEXT)";
        String stringProblem = "CREATE TABLE "+Table_Name_Problem+" ("+id2+" INTEGER PRIMARY KEY AUTOINCREMENT, "+problemName+" TEXT, "+problemId+" TEXT)";
        String stringCost = "CREATE TABLE "+Table_Name_Cost+" ("+id1+" INTEGER PRIMARY KEY AUTOINCREMENT, "+productNameCost+" TEXT, "+quantityCost+" TEXT, "+productIdCost+" TEXT, "+amountCost+" TEXT, "+unitPriceCost+" TEXT, "+note+" TEXT, "+costTitle+" TEXT)";
        db.execSQL(string);
        db.execSQL(stringCost);
        db.execSQL(stringProblem);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void datainsert(Datagetset datagetset) {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(productName,datagetset.getProductName());
            contentValues.put(quantity,datagetset.getQuantity());
            contentValues.put(productId,datagetset.getProductId());
            sqLiteDatabase.insert(Table_Name,null, contentValues);
            sqLiteDatabase.close();

    }
    public void datainsertProblem(DataProblem dataProblem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(problemName,dataProblem.getProblemName());
        contentValues.put(problemId,dataProblem.getProblemId());
        sqLiteDatabase.insert(Table_Name_Problem,null, contentValues);
        sqLiteDatabase.close();
    }
    public void datainsertCost(DataCost dataCost)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(productNameCost,dataCost.getProductName());
        contentValues.put(quantityCost,dataCost.getQuantity());
        contentValues.put(productIdCost,dataCost.getProductId());
        contentValues.put(amountCost,dataCost.getAmount());
        contentValues.put(unitPriceCost,dataCost.getUnitPrice());
        contentValues.put(note,dataCost.getNote());
        contentValues.put(costTitle,dataCost.getTitle());
        sqLiteDatabase.insert(Table_Name_Cost,null, contentValues);
        sqLiteDatabase.close();

    }

    public void dataDelete(String id)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        db.execSQL("delete from "+ Table_Name +" WHERE id='"+id+"'");
        db.close();
    }
    public void dataDeleteProblem(String id2)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        db.execSQL("delete from "+ Table_Name_Problem +" WHERE id2='"+id2+"'");
        db.close();
    }
    public void dataDeleteCost(String id1)
    {
        SQLiteDatabase db =this.getWritableDatabase();
        db.execSQL("delete from "+ Table_Name_Cost +" WHERE id1 ='"+id1+"'");
        db.close();
    }
    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ Table_Name);
        db.close();
    }
    public void deleteAllCost()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ Table_Name_Cost);
        db.close();
    }
    public void deleteAllProblem() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ Table_Name_Problem);
        db.close();
    }

    public boolean isExist(String productId) {
        SQLiteDatabase sq=this.getReadableDatabase();
        String get="SELECT id FROM "+Table_Name+" WHERE productId='"+productId+"' LIMIT 1";
        Cursor c = sq.rawQuery(get,null);
        if(c.getCount() == 1) {
            return true;
        }
        return false;
    }

    public boolean isExistProblem(String problemId) {
        SQLiteDatabase sq = this.getReadableDatabase();
        String get= "SELECT problemId FROM "+Table_Name_Problem+" WHERE problemId='"+problemId+"' LIMIT 1";
        Cursor c = sq.rawQuery(get,null);
        if(c.getCount() == 1) {
            return true;
        }
        return false;
    }

}
