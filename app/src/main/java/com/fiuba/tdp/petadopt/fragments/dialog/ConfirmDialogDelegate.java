package com.fiuba.tdp.petadopt.fragments.dialog;

import android.content.DialogInterface;

/**
 * Created by tomas on 10/31/15.
 */
public interface ConfirmDialogDelegate {
    void onConfirm(DialogInterface dialog, int id);
    void onReject(DialogInterface dialog, int id);
    String getConfirmMessage();
    String getRejectMessage();
}
