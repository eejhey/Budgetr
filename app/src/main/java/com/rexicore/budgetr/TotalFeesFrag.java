package com.rexicore.budgetr;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.NumberFormat;

public class TotalFeesFrag extends Fragment {

    private TextView totalFeesAmountTextView;
    private ListView expenseListView;

    public TotalFeesFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static TotalFeesFrag newInstance() {
        return new TotalFeesFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_total_fees, container, false);

        totalFeesAmountTextView = (TextView) view.findViewById(R.id.total_expense_textview);

        expenseListView = (ListView) view.findViewById(R.id.expense_list_listview);
        expenseListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeExpense(id);
                                updateUI();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setTitle(R.string.remove_expense)
                        .setMessage(R.string.confirm_expense_remove)
                        .create();
                dialog.show();

                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        db = new DbHandler(getActivity());
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();

        db.close();
        expensesCursor.close();
    }

    private void removeExpense(long id) {
        db.removeExpense((int)id);
    }

    private Cursor expensesCursor;
    private DbHandler db;

    public void updateUI() {

        // Update ListView
        expensesCursor = db.getExpenses("gen");
        ExpenseListAdapter cursorAdapter = new ExpenseListAdapter(getActivity(),
                R.layout.expense_list_cont,
                expensesCursor,
                new String[] {DbHandler.FEES, DbHandler.FEE_DUE},
                new int[] {R.id.expense_list_title, R.id.expense_list_amount},
                0);
        expenseListView.setAdapter(cursorAdapter);

        // Update total expenses due
        double totalFeeAmount = 0;
        if (expensesCursor.moveToFirst()) {
            do {
                totalFeeAmount += expensesCursor.getDouble(expensesCursor.getColumnIndex(DbHandler.FEE_DUE));
            } while (expensesCursor.moveToNext());
        }
        String amount = "$";
        // Format number to #,###.##
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        amount += formatter.format(totalFeeAmount);
        // Show number
        totalFeesAmountTextView.setText(amount);
    }
}
