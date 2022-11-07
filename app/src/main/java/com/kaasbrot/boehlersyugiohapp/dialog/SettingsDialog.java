package com.kaasbrot.boehlersyugiohapp.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaasbrot.boehlersyugiohapp.GlobalOptions;
import com.kaasbrot.boehlersyugiohapp.MainActivity;
import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.Dice;
import com.kaasbrot.boehlersyugiohapp.history.History;

import java.util.Arrays;
import java.util.Random;

public class SettingsDialog extends AppCompatDialogFragment {

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

        EditText startlifetext = view.findViewById(R.id.StartLifeInput);
        startlifetext.setText(String.valueOf(GlobalOptions.getStartingLifePoints()), TextView.BufferType.EDITABLE);
        startlifetext.setSelectAllOnFocus(true);
        startlifetext.setOnEditorActionListener((v, actionId, event) -> {
            if(event != null) {
                System.out.println(event);
            }
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                startlifetext.clearFocus();

//                InputMethodManager imm = (InputMethodManager) getSystemService(
//                        Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(startlifetext.getApplicationWindowToken(), 0);
//                String startlifetemp = startlifetext.getText().toString();
//                if(!startlifetemp.isEmpty()){
//                    int startlifetempint = Integer.parseInt(startlifetemp);
//                    Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.TOP, 0, 10);
//                    if(startlifetempint>40000){
//                        toast.setText(R.string.set_lifepoint_max);
//                        toast.show();
//                        startlifetext.setText(String.valueOf(startinglifepoints));
//                        return true;
//                    } else if(startlifetempint<1) {
//                        toast.setText(R.string.set_lifepoint_min);
//                        toast.show();
//                        startlifetext.setText(String.valueOf(startinglifepoints));
//                        return true;
//                    }
//                    else{
//
//                        startinglifepoints = startlifetempint;
//                        edit.putInt("startinglifepoints",startinglifepoints);
//                        edit.apply();
//                    }} else{
//                    startlifetext.setText(String.valueOf(startinglifepoints));
//                }
            }

            return true;
        });

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

//        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode,
//                                 KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//                    startlifetext.clearFocus();
//                }
//                return true;
//            }
//        });

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
