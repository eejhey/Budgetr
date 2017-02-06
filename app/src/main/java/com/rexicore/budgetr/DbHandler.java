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

    private static final int DB_VERSION = 5;
    private static final String DB_NAME = "Budgetr.db";
    private static final String EXPENSES_TBL = "Expenses";
    private static final String GROUPS_TBL = "Groups";
    public static final String FEES = "Fees";
    public static final String FEE_DUE = "AmountDue";
    public static final String GROUP = "mGroup";
    public static final String CATEGORY = "Category";
    public static final String GROUP_NAME = "Name";

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
        String amount = Double.toString(expense.getAmount());
        long group = expense.getGroup();
        String category = expense.getCategory();

        try{
            _newFee(title, amount, group, category);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void _newFee(String title, String amount, long group, String category) throws DbException{
        if (title.length() < 1 || category.length() < 1) {
            throw new DbException("Missing parameters");
        }
        if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "Adding new fee: " +
                                title + ", " + amount + ", " + group);

        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FEES, title);
        cv.put(FEE_DUE, amount);
        cv.put(GROUP, group);
        cv.put(CATEGORY, category);
        db.insert(EXPENSES_TBL, null, cv);
        db.close();
    }

    public Cursor getExpenses(String category) {
        Cursor cursor = null;

        try {
            cursor = _getExpenses(-1, category);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public Cursor getExpenses(long group, String category) {
        Cursor cursor = null;

        try {
            cursor = _getExpenses(group, category);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return cursor;
    }

    private Cursor _getExpenses(long group, String category) throws DbException {
        if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "Getting expenses: " + group + ", " + category);

        db = dbHelper.getWritableDatabase();
        Cursor c = db.query(
                EXPENSES_TBL,
                new String[] {"_id", FEES, FEE_DUE},
                GROUP + " = ? AND " + CATEGORY + " = ?",
                new String[] {Long.toString(group), category},
                null, null, null
        );
        return c;
    }

    public boolean removeExpenseByGroup(long groupId) {
        Cursor cursor = getExpenses(groupId, "monthly");

        if (cursor.moveToFirst()) {
            do {
                try{
                    int expenseId = cursor.getInt(cursor.getColumnIndex("_id"));
                    _removeExpense(expenseId);
                } catch (DbException e) {
                    e.printStackTrace();
                    return false;
                }
            } while (cursor.moveToNext());
        }

        return true;
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

    public boolean newGroup(String name) {
        try {
            return _newGroup(name);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean _newGroup(String name) throws DbException {
        if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "Adding new plan.");

        if (name.length() < 1) {
            throw new DbException("BRDB-lfaiew99df0: Group name is missing.");
        }

        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(GROUP_NAME, name);
        db.insert(GROUPS_TBL, null, cv);

        return true;
    }

    public Cursor getGroups() {
        try {
            return _getGroups();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Cursor _getGroups() throws DbException {
        db = dbHelper.getWritableDatabase();

        Cursor c = db.query(
                GROUPS_TBL,
                new String[] {"_id", GROUP_NAME},
                null, null, null, null, null
        );

        return c;
    }

    public boolean removeGroup(int id) {
        try {
            return _removeGroup(id);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean _removeGroup(int id) throws DbException {
        if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "Removing group: " + id);

        db = dbHelper.getWritableDatabase();

        // Check if exists
        Cursor check = db.query(
                GROUPS_TBL,
                new String[] {"_id"},
                "_id = ?",
                new String[] {Integer.toString(id)},
                null, null, null
        );

        if (check.moveToFirst()) {
            // Group exists, let's remove it
            db.delete(GROUPS_TBL, "_id = ?", new String[] {Integer.toString(id)});
            check.close();

            return true;
        } else {
            throw new DbException("Plan does not exist.");
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
                    FEES + " VARCHAR(64), " +
                    FEE_DUE + " REAL, " +
                    GROUP + " INTEGER, " +
                    CATEGORY + " VARCHAR(10))";

            String CREATE_GROUPS_TBL = "CREATE TABLE IF NOT EXISTS " + GROUPS_TBL + "(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GROUP_NAME + " VARCHAR(64))";

            try {
                if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "BRDB-sfaih000: Creating local tables.");
                db.execSQL(CREATE_EXPENSES_TBL);
                db.execSQL(CREATE_GROUPS_TBL);
                if (DEBUG_MODE) Log.d(this.getClass().getSimpleName(), "BRDB-aleif001: Local tables created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String DROP_EXPENSES_TBL = "DROP TABLE IF EXISTS " + EXPENSES_TBL;
            String DROP_GROUPS_TBL = "DROP TABLE IF EXISTS " + GROUPS_TBL;

            // Remove existing tables and remake
            db.execSQL(DROP_EXPENSES_TBL);
            db.execSQL(DROP_GROUPS_TBL);
            onCreate(db);
        }
    }

    class DbException extends Exception {
        public DbException(String m) {
            super(m);
        }
    }
}
