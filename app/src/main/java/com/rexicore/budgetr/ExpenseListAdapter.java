package com.rexicore.budgetr;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.NumberFormat;

/**
 * Created by Eddie on 2/4/2017.
 */

public class ExpenseListAdapter extends SimpleCursorAdapter {
    private Context context;
    private int layout;
    private Cursor c;
    private String[] from;
    private int[] to;
    private final LayoutInflater inflater;

    public ExpenseListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        this.layout = layout;
        this.c = c;
        this.from = from;
        this.to = to;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView expenseTitle = (TextView) view.findViewById(to[0]);
        TextView expenseAmount = (TextView) view.findViewById(to[1]);

        String title = c.getString(c.getColumnIndex(from[0]));
        double amt = c.getDouble(c.getColumnIndex(from[1]));
        String amount = Expense.format(amt);

        expenseTitle.setText(title);
        expenseAmount.setText(amount);
    }
}
