package com.empire.dome.hbview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private HBView hb;
    ArrayList<Point> mWhiteArray;
    private EditText edY;
    private EditText edX;
    private Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hb = findViewById(R.id.hb);
        edX = findViewById(R.id.ed_x);
        edY = findViewById(R.id.ed_y);
        okBtn = findViewById(R.id.btn_ok);
        mWhiteArray = new ArrayList<>();

        for (int i = 0;i < 10;i++){
            for(int j = 0; j < 10;j++){
                Point point = new Point();
                point.set(j,i);
                mWhiteArray.add(point);
            }
        }
        hb.setWhiteArray(mWhiteArray);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int x = Integer.valueOf(edX.getText().toString());
                int y = Integer.valueOf(edY.getText().toString());
                ArrayList mChangeArray = new ArrayList();
                Point point = new Point();
                point.set(x,y);
                mChangeArray.add(point);
                hb.setTypeArray(mChangeArray,0);
            }
        });
    }
}
