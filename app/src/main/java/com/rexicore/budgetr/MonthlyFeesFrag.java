package com.rexicore.budgetr;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Eddie on 2/5/2017.
 */

public class MonthlyFeesFrag extends Fragment {

    private long group;
    private ListView monthlyExpensesListView;
    private TextView monthlyExpensesHeader;
    private TextView yearlyExpensesHeader;

    public MonthlyFeesFrag() {

    }

    public static MonthlyFeesFrag newInstance(long group) {
        MonthlyFeesFrag frag = new MonthlyFeesFrag();
        frag.group = group;

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_month_plan, null);

        monthlyExpensesHeader = (TextView) view.findViewById(R.id.monthly_expense_textview);
        yearlyExpensesHeader = (TextView) view.findViewById(R.id.yearly_expense_textview);

        monthlyExpensesListView = (ListView) view.findViewById(R.id.monthly_expenses_listview);
        monthlyExpensesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.remove_expense)
                        .setMessage(R.string.confirm_expense_remove)
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
                        }).create();
                dialog.show();

                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    private void removeExpense(long id) {
        DbHandler db = new DbHandler(getActivity());
        db.removeExpense((int)id);
        db.close();
    }

    public void updateUI() {
        // Update listview
        DbHandler db = new DbHandler(getActivity());
        Cursor monthlyFeesCursor = db.getExpenses(group, "monthly");
        MonthlyExpenseListAdapter mAdapter = new MonthlyExpenseListAdapter(
                getActivity(), R.layout.monthly_expenses_list_cont, monthlyFeesCursor,
                new String[] {DbHandler.FEES, DbHandler.FEE_DUE},
                new int[] {R.id.monthly_expense_title, R.id.monthly_fee_textview, R.id.yearly_fee_textview}, 0
        );
        monthlyExpensesListView.setAdapter(mAdapter);

        // Update header
        double totalMonthlyFee = 0;
        if (monthlyFeesCursor.moveToFirst()) {
            do {
                totalMonthlyFee += monthlyFeesCursor.getDouble(monthlyFeesCursor.getColumnIndex(DbHandler.FEE_DUE));
            } while (monthlyFeesCursor.moveToNext());

        }
        double totalYearlyFee = totalMonthlyFee * 12.0f;
        monthlyExpensesHeader.setText(Expense.format(totalMonthlyFee));
        yearlyExpensesHeader.setText(Expense.format(totalYearlyFee));
    }

}
