package com.jayway.viewtutorial.part6;

import android.app.Activity;
import android.os.Bundle;

import com.jayway.viewtutorial.part6.Chooser.ChooserListener;

public class ViewTutorialActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final LineChartView lineChart = (LineChartView) findViewById(R.id.linechart);
		lineChart.setChartData(getWalkingData());

		final Chooser chooser = (Chooser) findViewById(R.id.chooser);
		chooser.setChooserListener(new ChooserListener() {
			@Override
			public void onItemChosen(int item) {
				switch (item) {
				case 0:
					lineChart.setChartData(getWalkingData());
					break;
				case 1:
					lineChart.setChartData(getRuningData());
					break;
				case 2:
					lineChart.setChartData(getCyclingData());
					break;

				default:
					break;
				}
			}

			@Override
			public void onColorChanged(int color) {
				lineChart.setColor(color);

			}
		});
	}

	private float[] getWalkingData() {
		return new float[] { 10, 12, 7, 14, 15, 19, 13, 2, 10, 13, 13, 10, 15, 14 };
	}

	private float[] getRuningData() {
		return new float[] { 22, 14, 20, 25, 32, 27, 26, 21, 19, 26, 24, 30, 29, 19 };
	}

	private float[] getCyclingData() {
		return new float[] { 0, 0, 0, 10, 14, 23, 40, 35, 32, 37, 41, 32, 18, 39 };
	}
}