package com.example.accelerometerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity {

    TextView txt_currentAccel, txt_prevAccel, txt_acceleration;
    ProgressBar prog_shakeMeter;
    //mendefinisi variabel sensor
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;

    //mendefinisi konten grafik
    private int pointsplotted = 5;
    private int graphIntervalCounter = 0;
    private Viewport viewport;
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
              new DataPoint(0, 1),
              new DataPoint(1, 5),
              new DataPoint(2, 3),
              new DataPoint(3, 2),
              new DataPoint(4, 6)
    });
    
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accelerationCurrentValue = Math.sqrt( (x * x + y * y + z * z) );
            double changeInAccelleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;
            //update text views
            txt_currentAccel.setText("Current = " + (int) accelerationCurrentValue);
            txt_prevAccel.setText("Prev = " + (int) accelerationPreviousValue);
            txt_acceleration.setText("Acceleration Change" + (int) changeInAccelleration );
            prog_shakeMeter.setProgress((int)changeInAccelleration);

            //ubah warna berdasar nilai guncangan atau shake
            if (changeInAccelleration > 14 ){
                txt_acceleration.setBackgroundColor(Color.RED);
            }
            else if (changeInAccelleration > 5){
                txt_acceleration.setBackgroundColor(Color.parseColor("#fcad03"));
            }
            else if (changeInAccelleration>2){
                txt_acceleration.setBackgroundColor(Color.YELLOW);
            }
            else {
                txt_acceleration.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_background));
            }
             // update grafik kode
            pointsplotted++;
            series.appendData(new DataPoint(pointsplotted, changeInAccelleration),true, pointsplotted);
            viewport.setMaxX(pointsplotted);
            viewport.setMinX(pointsplotted - 200);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_acceleration = findViewById(R.id.txt_accel);
        txt_currentAccel = findViewById(R.id.txt_currentAccel);
        txt_prevAccel = findViewById(R.id.txt_prevAccel);

        prog_shakeMeter = findViewById(R.id.prog_shakeMeter);

        //inisiasi objek sensor
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        viewport= graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);

        graph.addSeries(series);
        

    }
    //kode grafik
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

}