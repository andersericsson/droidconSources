package com.jayway.dynamics.barchart;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BarChartView barChartView = (BarChartView) findViewById(R.id.barchart);
        initBarData(barChartView);

        findViewById(R.id.stats1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Float> data = new ArrayList<Float>();
                data.add(37f);
                data.add(38f);
                data.add(13f);
                data.add(28f);
                data.add(22f);
                barChartView.setData(data);

            }
        });

        findViewById(R.id.stats2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Float> data = new ArrayList<Float>();
                data.add(15f);
                data.add(21f);
                data.add(31f);
                data.add(23f);
                data.add(38f);
                barChartView.setData(data);
            }
        });

        findViewById(R.id.stats3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Float> data = new ArrayList<Float>();
                data.add(16f);
                data.add(19f);
                data.add(17f);
                data.add(30f);
                data.add(10f);
                barChartView.setData(data);
            }
        });
    }

    private void initBarData(final BarChartView barChartView) {
        ArrayList<Float> data = new ArrayList<Float>();
        data.add(37f);
        data.add(38f);
        data.add(13f);
        data.add(28f);
        data.add(22f);
        barChartView.setData(data);
    }
}
