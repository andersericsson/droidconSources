package com.jayway.dynamics.expander;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ExpanderView expanderView = (ExpanderView) findViewById(R.id.expander);

        findViewById(R.id.open_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expanderView.setOpen(true);
            }
        });

        findViewById(R.id.close_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expanderView.setOpen(false);
            }
        });
    }
}
