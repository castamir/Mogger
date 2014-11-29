package cz.vutbr.fit.mogger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import cz.vutbr.fit.mogger.gesture.GestureManager;
import static cz.vutbr.fit.mogger.Constants.*;
import static java.lang.Math.abs;

public class Listener implements SensorEventListener {

    // zvuk
    Sounds sounds;
    private boolean isActive = false;
    private long lastUpdate = 0;
    private MoveTriggerActivity mogger;
    private GestureManager gestureManager;
    private int check_gesture;
    private int prev_x, prev_y, prev_z;

    private Gesture currentGesture = new Gesture();

    public Listener(MoveTriggerActivity mogger, GestureManager gestureManager) {

        this.mogger = mogger;
        this.gestureManager = gestureManager;
        sounds = new Sounds();

        check_gesture = CHECK_GESTURE_STOP;
        prev_x = 100;
    }

    public void startRecording() {
        isActive = true;
        currentGesture = new Gesture(); // to avoid overriding existing gesture
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
                    if (prev_x != 100) {
                        // stav kontrola neaktivni
                        if (check_gesture == CHECK_GESTURE_START) {
                            if (calculateDiff(x, y, z) < 2) {
                                check_gesture = CHECK_GESTURE_STOP;
                            }
                        } else if (check_gesture == CHECK_GESTURE_STOP) {
                            if (calculateDiff(x, y, z) > 2) {
                                // prepneme stav
                                check_gesture = CHECK_GESTURE_START;
                                // vyresetujeme zaznamenane udaje
                                currentGesture.cleanCoords();
                            }
                        }
                    }
                    // gesto se provadi
                    if (check_gesture == CHECK_GESTURE_START) {
                        currentGesture.addCoords(x, y, z);

                        if (gestureManager.checkGesture(currentGesture)) {
                            onGestureDetected();
                        }
                    }
                } else {
                    // ulozeni gesta
                    save(x, y, z);
                }
                prev_x = x;
                prev_y = y;
                prev_z = z;
            }
        }
    }


    // ulozeni noveho gesta
    private void save(int x, int y, int z) {
        if (currentGesture.size() == 0) {
            // prvni inicializace
            if (prev_x != 100) {
                // uzivatel zacal vytvaret gesto
                if (calculateDiff(x, y, z) > 1) {
                    currentGesture.addCoords(x, y, z);
                    onGestureStart();
                }
            }
        } else {
            currentGesture.addCoords(x, y, z);

            // mame alespon 8 vektoru, zacneme overovat, zda uzivatel neukoncil gesto
            if (currentGesture.size() >= 8) {
                if (calculateDiff(x, y, z) < 3) {
                    onGestureEnd();
                }
            }
        }
    }

    protected int calculateDiff(int x, int y, int z) {
        return abs(x - prev_x) + abs(y - prev_y) + abs(z - prev_z);
    }

    private void onGestureStart() {
        mogger.textView.setText("Recording ...");
        sounds.PlayTone();
    }


    private void onGestureEnd() {
        mogger.fastestListener.stopRecording();
        mogger.button2.setText("Save");
        mogger.button2.setTag(1);
        mogger.textView.setText("New gesture saved.");
        mogger.button1.setEnabled(true);
        sounds.PlayTone();

        gestureManager.addGesture(currentGesture);
    }

    private void onGestureDetected() {
        currentGesture.cleanCoords();
        sounds.PlayTone();
    }
}
