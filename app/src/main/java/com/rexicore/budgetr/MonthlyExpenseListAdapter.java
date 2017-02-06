package com.rexicore.budgetr;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;

/**
 * Created by Eddie on 2/5/2017.
 */

public class MonthlyExpenseListAdapter extends SimpleCursorAdapter {

    private String monthlyExpenseTitle;
    private String monthlyExpense;
    private String yearlyExpense;
    private int monthlyExpenseTitleTextView;
    private int monthlyExpenseTextView;
    private int yearlyExpenseTextView;
    private Cursor c;
    private int layout;
    private final LayoutInflater inflater;

    public MonthlyExpenseListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        this.monthlyExpenseTitle = from[0];
        this.monthlyExpense = from[1];
        this.c = c;
        this.layout = layout;
        this.monthlyExpenseTitleTextView = to[0];
        this.monthlyExpenseTextView = to[1];
        this.yearlyExpenseTextView = to[2];
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get views
        TextView monthlyExpenseTitleTV = (TextView) view.findViewById(monthlyExpenseTitleTextView);
        TextView monthlyExpenseTV = (TextView) view.findViewById(monthlyExpenseTextView);
        TextView yearlyExpenseTV = (TextView) view.findViewById(yearlyExpenseTextView);
        // Grab info from cursor
        String name = cursor.getString(c.getColumnIndex(monthlyExpenseTitle));
        double amt = cursor.getDouble(c.getColumnIndex(monthlyExpense));
        double yearAmt = 12.0f * amt;
        // Format amount
        String monthlyAmount = Expense.format(amt);
        String yearlyAmount = Expense.format(yearAmt);
        // Show info on views
        monthlyExpenseTitleTV.setText(name);
        monthlyExpenseTV.setText(monthlyAmount);
        yearlyExpenseTV.setText(yearlyAmount);
    }
}
