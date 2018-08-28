package at.stefanirndorfer.popularmovies.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import at.stefanirndorfer.popularmovies.R;

public class InternetDialog extends DialogFragment {

    private View rootView;
    private InternetDialogListener listener;

    public static InternetDialog newInstance(InternetDialogListener listener) {
        InternetDialog dialog = new InternetDialog();
        dialog.listener = listener;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(rootView)
                .setTitle(R.string.internet_dialog_title)
                .setMessage(R.string.internet_dialog_text)
                .setCancelable(false)
                .setPositiveButton(R.string.got_it, null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialog -> onDialogShow(alertDialog));
        alertDialog.setOnKeyListener((arg0, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onDoneClicked();
            }
            return true;
        });
        return alertDialog;
    }

    private void onDialogShow(AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> onDoneClicked());
    }

    private void onDoneClicked() {
        this.listener.onDialogDone();
        dismiss();
    }
}
