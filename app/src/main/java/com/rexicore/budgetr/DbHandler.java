package com.rexicore.budgetr;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Eddie on 2/4/2017.
 */

public class DbHandler {
    private Context context;
    private boolean DEBUG_MODE = true;

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Budgetr.db";
    private static final String EXPENSES_TBL = "Expenses";
    public static final String FEES = "Fees";
    public static final String FEE_DUE = "AmountDue";
    public static final String CATEGORY = "Category";

    /**
     * Constructor methods
     */
    public DbHandler(Context c) {
        this.context = c;
        dbHelper = new DbHelper(c);
    }

    public void close() {
        db.close();
    }

    public boolean newFee(Expense expense) {
        String title = expense.getTitle();
        String amount = Integer.toString(expense.getAmount());
        String category = expense.getCategory();

        try{
            _newFee(title, amount, category);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void _newFee(String title, String amount, String category) throws DbException{
        if (title.length() < 1 || amount.length() < 1 || category.length() < 1) {
            throw new DbException("Missing parameters");
        }
        if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "Adding new fee: " +
                                title + ", " + amount);

        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FEES, title);
        cv.put(FEE_DUE, amount);
        cv.put(CATEGORY, category);
        db.insert(EXPENSES_TBL, null, cv);
        db.close();
    }

    public Cursor getExpenses(String category) {
        Cursor cursor = null;

        try {
            cursor = _getExpenses(category);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return cursor;
    }

    private Cursor _getExpenses(String category) throws DbException {
        if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "Getting expenses: " + category);

        db = dbHelper.getWritableDatabase();
        Cursor c = db.query(
                EXPENSES_TBL,
                new String[] {"_id", FEES, FEE_DUE},
                CATEGORY + " = ?",
                new String[] {category},
                null, null, null
        );
        return c;
    }

    public boolean removeExpense(int id) {
        try {
            return _removeExpense(id);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean _removeExpense(int id) throws DbException {
        if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "Removing expense: " + id);

        db = dbHelper.getWritableDatabase();

        String cid = Integer.toString(id);
        // See if it even exists in database
        Cursor check = db.query(
                EXPENSES_TBL,
                new String[] {"_id"},
                "_id = ?",
                new String[] {cid},
                null, null, null
        );

        if (check.moveToFirst()) {
            // Expense exists, let's remove it
            db.delete(
                    EXPENSES_TBL,
                    "_id = ?",
                    new String[] {cid}
            );
            check.close();

            return true;
        } else {
            throw new DbException("BRDB-ajeoifj0w98: Expense " + id + " not found.");
        }
    }

    class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//            String CREATE_SELF_GROUPS_TBL = "CREATE TABLE IF NOT EXISTS " + GROUPS_TABLE + "(" +
//                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    G_GID + " INTEGER, " +
//                    G_USER + " VARCHAR(32))";
//            try{
//                Log.d(TAG, "SRCH83940SJ-SJF9238: Creating local tables.");
//                db.execSQL(SAVE_LOCAL_PHONENUM);
//                db.execSQL(CREATE_SELF_RIBBONS_TBL);
//                db.execSQL(CREATE_SELF_GROUPS_TBL);
//                Log.d(TAG, "SRCH30958203-SJF394802: Local tables created.");
//                //db.execSQL(CREATE_RIBBONS_TBL);
//            } catch( SQLException e ){
//                e.printStackTrace();
//            }
            String CREATE_EXPENSES_TBL = "CREATE TABLE IF NOT EXISTS " + EXPENSES_TBL + "(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FEES + " VARCHAR(255), " +
                    FEE_DUE + " INTEGER, " +
                    CATEGORY + " VARCHAR(32))";

            try {
                if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "BRDB-sfaih000: Creating local tables.");
                db.execSQL(CREATE_EXPENSES_TBL);
                if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "BRDB-aleif001: Local tables created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String DROP_EXPENSES_TBL = "DROP TABLE IF EXISTS " + EXPENSES_TBL;
            onCreate(db);
        }
    }

    class DbException extends Exception {
        public DbException(String m) {
            super(m);
        }
    }
}
