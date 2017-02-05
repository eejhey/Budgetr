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

/**
 * Created by Eddie on 2/5/2017.
 */

public class MonthlyFeesFrag extends Fragment {

    private ListView groupsListView;

    public MonthlyFeesFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_monthly_fees, null);

        groupsListView = (ListView) view.findViewById(R.id.monthly_plans_listview);
        groupsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.remove_plan)
                        .setMessage(R.string.confirm_remove_plan)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeGroup(id);
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

    public void updateUI() {
        DbHandler db = new DbHandler(getActivity());
        Cursor groupsCursor = db.getGroups();

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                getActivity(), android.R.layout.simple_list_item_1, groupsCursor,
                new String[] {DbHandler.GROUP_NAME}, new int[] {android.R.id.text1}, 0
        );
        groupsListView.setAdapter(cursorAdapter);
        db.close();
    }

    private void removeGroup(long id) {
        DbHandler db = new DbHandler(getActivity());
        db.removeGroup((int)id);
        db.close();
    }
}
