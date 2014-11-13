package cz.vutbr.fit.mogger;

// trida slouzi pro praci s gesty (jejich ulozeni, nacteni, kontrolu)

import android.util.Log;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Gesture {

    // gesto akcelerometr
    private ArrayList<Integer> coord_x;
    private ArrayList<Integer> coord_y;
    private ArrayList<Integer> coord_z;

    // gesto ulozeni list
    private ArrayList<Integer> ref_coord_x;
    private ArrayList<Integer> ref_coord_y;
    private ArrayList<Integer> ref_coord_z;

    // pro kontrolu zda zahajit recording noveho gesta
    private int prev_x;
    private int prev_y;
    private int prev_z;

    // signalizace zapoceti nahravani noveho gesta a ukonceni nahravani
    Sounds sounds;

    // gesto ulozeni 2D pole
    public int[][] gesture;

    private int treshold;

    DTW dtw;

    private MoveTriggerActivity mogger;

    public Gesture(MoveTriggerActivity mogger) {

        // gesto akcelerometr
        coord_x = new ArrayList<Integer>();
        coord_y = new ArrayList<Integer>();
        coord_z = new ArrayList<Integer>();

        // gesto ulozeni list
        ref_coord_x = new ArrayList<Integer>();
        ref_coord_y = new ArrayList<Integer>();
        ref_coord_z = new ArrayList<Integer>();

        dtw = new DTW();

        prev_x = 100;
        prev_y = 100;
        prev_z = 100;

        sounds = new Sounds();

        this.mogger = mogger;

        treshold = 1;

    }

    // vklada nove vektory na konec listu, pokud je list delsi jak 80 vektoru, nejstarsi vektor odstrani
    public void add_coords(int x, int y, int z) {
        coord_x.add(x);
        coord_y.add(y);
        coord_z.add(z);

        if (coord_x.size() > 80) {
            coord_x.remove(0);
            coord_y.remove(0);
            coord_z.remove(0);
        }

    }

    // ulozeni noveho gesta
    public void save(int x, int y, int z) {
        if (ref_coord_x.size() == 0) {
            // prvni inicializace
            if (prev_x == 100) {
                prev_x = x;
                prev_y = y;
                prev_z = z;
            }
            else {
                // uzivatel zacal vytvaret gesto
                if ((abs(x - prev_x) + abs(y - prev_y) + abs(z - prev_z)) > 1) {
                    ref_coord_x.add(x);
                    ref_coord_y.add(y);
                    ref_coord_z.add(z);
                    mogger.textView.setText("Recording ...");
                    sounds.PlayTone();
                }
                prev_x = x;
                prev_y = y;
                prev_z = z;
            }
        }
        else {
            ref_coord_x.add(x);
            ref_coord_y.add(y);
            ref_coord_z.add(z);

            // mame alespon 8 vektoru, zacneme overovat, zda uzivatel neukoncil gesto
            if (ref_coord_x.size() >= 8) {
                if ((abs(x - prev_x) + abs(y - prev_y) + abs(z - prev_z)) < 3) {
                    mogger.fastestListener.stopRecording();
                    sounds.PlayTone();
                    mogger.button2.setText("Save");
                    mogger.button2.setTag(1);
                    mogger.textView.setText("New gesture saved.");
                    mogger.button1.setEnabled(true);
                    treshold = calculate_treshold();
                }
            }
            prev_x = x;
            prev_y = y;
            prev_z = z;
        }
    }

    // porovnani gest
    public boolean check() {
        // mame v bufferu vice nebo stejne vektoru jako ma gesto? (= muzeme zkontrolovat celou sekvenci?)
        if (ref_coord_x.size() <= coord_x.size()) {

            // vytvorime pole o delce referencniho gesta
            int size = ref_coord_x.size();
            int[][] acc_gesture = new int[3][size];

            int size_acc = coord_x.size() - 1;

            for (int i = (size - 1); i >= 0; i--) {
                acc_gesture[0][i] = coord_x.get(size_acc);
                acc_gesture[1][i] = coord_y.get(size_acc);
                acc_gesture[2][i] = coord_z.get(size_acc);
                size_acc--;
            }

            // vypocet DTW
            int result = dtw.dtw_check(acc_gesture, gesture);

            // gesto zachyceno, promazeme zachycene gesto (kvuli mnohonasobnemu zachyceni s dalsimi vzorky)
            if (result < treshold) {
                coord_x.clear();
                coord_y.clear();
                coord_z.clear();

                return true;
            }

            return false;
        }

        return false;

    }

    // prevede array list zachyceneho gesta na 2D pole
    public void recalculate_gestures() {
        int size = ref_coord_x.size();

        if (size != 0) {
            gesture = new int[3][size];

            for (int i = 0; i < size; i++) {
                gesture[0][i] = ref_coord_x.get(i);
                gesture[1][i] = ref_coord_y.get(i);
                gesture[2][i] = ref_coord_z.get(i);
            }
        }

    }

    // promaze stare gesto
    public void clear() {
        ref_coord_x.clear();
        ref_coord_y.clear();
        ref_coord_z.clear();
    }

    // promaze aktualne posbirana data
    public void clear_saved_data() {
        coord_x.clear();
        coord_y.clear();
        coord_z.clear();
    }

    // spocita prah pro rozpoznani prislusneho gesta podle jeho slozitosti
    public int calculate_treshold() {
        int size = ref_coord_x.size();
        int sum = 0;

        int strange_const = 4;

        for (int i = 0; i < size; i++) {
            sum += abs(ref_coord_x.get(i)) + abs(ref_coord_y.get(i)) + abs(ref_coord_z.get(i));
        }

        if (size > 20) {
            strange_const = 5;
        }
        else if (size > 30) {
            strange_const = 6;
        }

        //System.out.println("delka: " + size + ", suma: " + sum + ", tresh: " + (sum/size) * strange_const);

        return (sum/size) * strange_const;
    }

}
