package com.kaasbrot.boehlersyugiohapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static com.kaasbrot.boehlersyugiohapp.GameInformation.p1;
import static com.kaasbrot.boehlersyugiohapp.GameInformation.p2;

public class PointsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_points_3);

        p1.updateActivity(
                (TextView) findViewById(R.id.pointsPlayer2),
                (TextView) findViewById(R.id.tmpText2),
                this
        );
        p2.updateActivity(
                (TextView) findViewById(R.id.pointsPlayer2),
                (TextView) findViewById(R.id.tmpText2),
                this
        );
    }
}
