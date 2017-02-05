package com.rexicore.budgetr;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Created by Eddie on 2/5/2017.
 */

public class NewGroupWindow extends DialogFragment {

    public interface NewGroupWindowListener {
        void onNewGroupDialogPositiveClick(DialogFragment dialog);
        void onNewGroupDialogNegativeClick(DialogFragment dialog);
    }

    NewGroupWindowListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NewGroupWindowListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.new_group_dialog, null);

        builder.setView(view)
                .setTitle(R.string.new_group)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNewGroupDialogPositiveClick(NewGroupWindow.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNewGroupDialogNegativeClick(NewGroupWindow.this);
                    }
                });

        return builder.create();
    }
}
