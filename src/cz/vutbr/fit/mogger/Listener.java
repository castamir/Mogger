package cz.vutbr.fit.mogger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

import static cz.vutbr.fit.mogger.Constants.CHECK;
import static cz.vutbr.fit.mogger.Constants.SAMPLING_RATE;

public class Listener implements SensorEventListener {

    private long startTime;
    private boolean isActive = false;

    private long lastUpdate = 0;
    private MoveTriggerActivity mogger;

    // vzorce pro kontrolu podobnosti sekvenci vektoru
    public DTW dtw;

    // zvuk
    Sounds sounds;

    private ArrayList<Integer> arl = new ArrayList<Integer>();

    public Listener(MoveTriggerActivity mogger) {

        this.mogger = mogger;
        dtw = new DTW();
        sounds = new Sounds();
    }

    public void startRecording() {
        startTime = System.currentTimeMillis();
        isActive = true;
        // prevede ulozene gesto na 2D pole
        mogger.gesture.recalculate_gestures();
    }

    public void stopRecording() {
        isActive = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isActive) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];

            long curTime = System.currentTimeMillis();

            // vzorkovani dat
            if ((curTime - lastUpdate) >= SAMPLING_RATE) {
                lastUpdate = curTime;

                // odeslani dat prislusne metode (ulozeni gesta/kontrola gesta)
                int state = mogger.mogger_action();

                // porovnani gest
                if (state == CHECK) {
                    mogger.gesture.add_coords(x, y, z);
                    boolean result = mogger.gesture.check();

                    if (result) {
                        sounds.PlayTone();
                    }
                }
                // ulozeni gesta
                else {
                    System.out.println("ulozeno");
                    mogger.gesture.save(x, y, z);
                }
            }
        }
    }
}
