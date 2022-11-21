package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaasbrot.boehlersyugiohapp.GlobalOptions;
import com.kaasbrot.boehlersyugiohapp.R;

public class AboutDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setMessage(R.string.about_us);

        LayoutInflater inflater =  LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.about_us, null);
        builder.setView(view);

        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        builder.setCancelable(true);

//      ((TextView) view.findViewById(R.id.settingsPointsLabel)).setTextSize(GlobalOptions.settingstextsize);


        builder.setPositiveButton("ok", (dialogInterface, i) -> {});
        return builder.create();
    }

    @Override
    public void onStart() {
        // once the dialog is built and shown, we can adjust text size and font alignment
        super.onStart();
        TextView texts = this.getDialog().findViewById(android.R.id.message);
        texts.setTextSize(30);
        texts.setGravity(Gravity.CENTER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
