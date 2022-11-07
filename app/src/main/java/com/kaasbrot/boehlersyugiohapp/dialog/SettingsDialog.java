package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
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

public class SettingsDialog extends AppCompatDialogFragment {

    private boolean keyEvent(EditText startLifeText) {
        startLifeText.clearFocus();

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(startLifeText.getApplicationWindowToken(), 0);

        String startLifeTemp = startLifeText.getText().toString();
        Toast toast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);

        try {
            int startLifetempint = Integer.parseInt(startLifeTemp);
            toast.setGravity(Gravity.TOP, 0, 10);
            if (startLifetempint > 40000) {
                toast.setText(R.string.set_lifepoint_max);
                toast.show();
            } else if (startLifetempint < 1) {
                toast.setText(R.string.set_lifepoint_min);
                toast.show();
            } else {
                /*
                 * Only place where startLifePoints is updated and saved in local storage
                 */
                GlobalOptions.setStartingLifePoints(startLifetempint);
            }
        } catch(NumberFormatException e) {
            toast.setText(R.string.bad_number_format);
        }

        startLifeText.setText(String.valueOf(GlobalOptions.getStartingLifePoints()));

        return true;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setMessage(R.string.settings);

        LayoutInflater inflater =  LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.settings_dialogr, null);
        builder.setView(view);

        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        builder.setCancelable(true);

        ((TextView) view.findViewById(R.id.settingsok)).setTextSize(GlobalOptions.settingstextsize);
        ((TextView) view.findViewById(R.id.StartLifeText)).setTextSize(GlobalOptions.settingstextsize);
        ((TextView) view.findViewById(R.id.KeepScreenOnText)).setTextSize(GlobalOptions.settingstextsize);
        ((TextView) view.findViewById(R.id.KeepHistoryText)).setTextSize(GlobalOptions.settingstextsize);
        ((TextView) view.findViewById(R.id.BehindEdit)).setTextSize(GlobalOptions.settingstextsize);
        ((EditText) view.findViewById(R.id.StartLifeInput)).setTextSize(GlobalOptions.settingstextsize);

        EditText startLifeText = view.findViewById(R.id.StartLifeInput);
        startLifeText.setText(String.valueOf(GlobalOptions.getStartingLifePoints()), TextView.BufferType.EDITABLE);
        startLifeText.setSelectAllOnFocus(true);
        startLifeText.setOnEditorActionListener((v, actionId, event) -> keyEvent(startLifeText));

        ImageView buttonImage = view.findViewById(R.id.tickbutton1);
        if(GlobalOptions.isScreenAlwaysOn()){
            buttonImage.setImageResource(R.drawable.tick1);
        } else {
            buttonImage.setImageResource(R.drawable.tick0);
        }

        buttonImage = view.findViewById(R.id.tickbutton2);
        if(GlobalOptions.isDeleteAfter4()){
            buttonImage.setImageResource(R.drawable.tick1);
        } else {
            buttonImage.setImageResource(R.drawable.tick0);
        }
        builder.setPositiveButton("ok", (dialogInterface, i) -> {});

        view.setOnKeyListener((view1, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                startLifeText.clearFocus();
            }
            return true;
        });

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
}
