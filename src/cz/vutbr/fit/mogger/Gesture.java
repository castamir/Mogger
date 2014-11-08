package cz.vutbr.fit.mogger;

// trida slouzi pro praci s gesty (jejich ulozeni, nacteni, kontrolu)

import android.util.Log;

import java.util.ArrayList;

public class Gesture {

    // gesto akcelerometr
    private ArrayList<Integer> coord_x;
    private ArrayList<Integer> coord_y;
    private ArrayList<Integer> coord_z;

    // gesto ulozeni list
    private ArrayList<Integer> ref_coord_x;
    private ArrayList<Integer> ref_coord_y;
    private ArrayList<Integer> ref_coord_z;

    // gesto ulozeni 2D pole
    public int[][] gesture;

    DTW dtw;

    public Gesture() {

        // gesto akcelerometr
        coord_x = new ArrayList<Integer>();
        coord_y = new ArrayList<Integer>();
        coord_z = new ArrayList<Integer>();

        // gesto ulozeni list
        ref_coord_x = new ArrayList<Integer>();
        ref_coord_y = new ArrayList<Integer>();
        ref_coord_z = new ArrayList<Integer>();

        dtw = new DTW();

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
        ref_coord_x.add(x);
        ref_coord_y.add(y);
        ref_coord_z.add(z);

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
            if (result < 60) {
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

}
