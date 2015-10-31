package com.fiuba.tdp.petadopt.fragments.dialog;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.fiuba.tdp.petadopt.R;

/**
 * Created by tomas on 10/31/15.
 */
public class ConfirmDialogFragment extends DialogFragment {
    String message;
    ConfirmDialogDelegate delegate;

    public ConfirmDialogFragment(String message, ConfirmDialogDelegate delegate) {
        this();
        this.message = message;
        this.delegate = delegate;
    }

    public ConfirmDialogFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(delegate.getConfirmMessage(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delegate.onConfirm(dialog, id);
                    }
                })
                .setNegativeButton(delegate.getRejectMessage(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delegate.onReject(dialog, id);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
