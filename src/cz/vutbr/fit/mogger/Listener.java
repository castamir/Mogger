package cz.vutbr.fit.mogger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

import static cz.vutbr.fit.mogger.Constants.*;
import static java.lang.Math.abs;

public class Listener implements SensorEventListener {

    // vzorce pro kontrolu podobnosti sekvenci vektoru
    public DTW dtw;
    // zvuk
    Sounds sounds;
    private long startTime;
    private boolean isActive = false;
    private long lastUpdate = 0;
    private MoveTriggerActivity mogger;
    private int check_gesture;
    private int prev_x, prev_y, prev_z;
    private ArrayList<Integer> arl = new ArrayList<Integer>();

    public Listener(MoveTriggerActivity mogger) {

        this.mogger = mogger;
        dtw = new DTW();
        sounds = new Sounds();

        check_gesture = CHECK_GESTURE_STOP;
        prev_x = 100;
    }

    public void startRecording() {
        startTime = System.currentTimeMillis();
        isActive = true;
    }

    public void stopRecording() {
        isActive = false;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

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
                    // kontrola, zda se provadi gesto
                    // prvni inicializace
                    if (prev_x == 100) {
                        prev_x = x;
                        prev_y = y;
                        prev_z = z;
                    } else {
                        // stav kontrola neaktivni
                        if (check_gesture == CHECK_GESTURE_START) {
                            if ((abs(x - prev_x) + abs(y - prev_y) + abs(z - prev_z)) < 2) {
                                check_gesture = CHECK_GESTURE_STOP;
                            }
                        } else if (check_gesture == CHECK_GESTURE_STOP) {
                            if ((abs(x - prev_x) + abs(y - prev_y) + abs(z - prev_z)) > 2) {
                                // prepneme stav
                                check_gesture = CHECK_GESTURE_START;
                                // vyresetujeme zaznamenane udaje
                                mogger.gesture.cleanCoords();
                            }
                        }
                        prev_x = x;
                        prev_y = y;
                        prev_z = z;
                    }

                    // gesto se provadi
                    if (check_gesture == CHECK_GESTURE_START) {
                        mogger.gesture.addCoords(x, y, z);

//                        if (mogger.gesture.check()) {
//                            mogger.gesture.cleanCoords();
//                            sounds.PlayTone();
//                        }
                    }
                } else {
                    // ulozeni gesta
                    mogger.gesture.save(x, y, z);
                }
            }
        }
    }
}
