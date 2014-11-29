package cz.vutbr.fit.mogger;

// trida slouzi pro praci s gesty (jejich ulozeni, nacteni, kontrolu)

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Gesture {


    public String name;
    public Sounds melody;

    private int threshold;

    // gesto akcelerometr
    private ArrayList<Integer> coord_x;
    private ArrayList<Integer> coord_y;
    private ArrayList<Integer> coord_z;


    // signalizace zapoceti nahravani noveho gesta a ukonceni nahravani
    Sounds sounds;
    // pro kontrolu zda zahajit recording noveho gesta
    private int prev_x;
    private int prev_y;
    private int prev_z;
    private MoveTriggerActivity mogger;
    private FileStorage storage;

    public Gesture(MoveTriggerActivity mogger, FileStorage storage) {
        this.storage = storage;

        // gesto akcelerometr
        coord_x = new ArrayList<Integer>();
        coord_y = new ArrayList<Integer>();
        coord_z = new ArrayList<Integer>();



        prev_x = 100;
        prev_y = 100;
        prev_z = 100;
        sounds = new Sounds();
        this.mogger = mogger;
    }

    public int size() {
        return coord_x.size();
    }

    // vklada nove vektory na konec listu, pokud je list delsi jak 80 vektoru, nejstarsi vektor odstrani
    public void addCoords(int x, int y, int z) {
        coord_x.add(x);
        coord_y.add(y);
        coord_z.add(z);

        if (coord_x.size() > 80) {
            coord_x.remove(0);
            coord_y.remove(0);
            coord_z.remove(0);
        }
    }

    public int[][] getCoordsArray() {
        int size = coord_x.size();
        int[][] coords = new int[3][size];

        for (int i = 0; i < size; i++) {
            coords[0][i] = coord_x.get(i);
            coords[1][i] = coord_y.get(i);
            coords[2][i] = coord_z.get(i);
        }

        return coords;
    }

    // promaze aktualne posbirana data
    public void cleanCoords() {
        coord_x.clear();
        coord_y.clear();
        coord_z.clear();
    }

    // spocita prah pro rozpoznani prislusneho gesta podle jeho slozitosti
    protected int calculateThreshold() {
        int size = coord_x.size();
        int sum = 0;

        int strange_const = 4;

        for (int i = 0; i < size; i++) {
            sum += abs(coord_x.get(i)) + abs(coord_y.get(i)) + abs(coord_z.get(i));
        }

        if (size > 20) {
            strange_const = 5;
        } else if (size > 30) {
            strange_const = 6;
        }

        return (sum / size) * strange_const;
    }

    public int getThreshold() {
        return threshold;
    }

    // ulozeni noveho gesta
    public void save(int x, int y, int z) {
//        if (ref_coord_x.size() == 0) {
//            // prvni inicializace
//            if (prev_x != 100) {
//                // uzivatel zacal vytvaret gesto
//                if ((abs(x - prev_x) + abs(y - prev_y) + abs(z - prev_z)) > 1) {
//                    ref_coord_x.add(x);
//                    ref_coord_y.add(y);
//                    ref_coord_z.add(z);
//                    mogger.textView.setText("Recording ...");
//                    sounds.PlayTone();
//                }
//            }
//        } else {
//            ref_coord_x.add(x);
//            ref_coord_y.add(y);
//            ref_coord_z.add(z);
//
//            // mame alespon 8 vektoru, zacneme overovat, zda uzivatel neukoncil gesto
//            if (ref_coord_x.size() >= 8) {
//                if ((abs(x - prev_x) + abs(y - prev_y) + abs(z - prev_z)) < 3) {
//                    mogger.fastestListener.stopRecording();
//                    sounds.PlayTone();
//                    mogger.button2.setText("Save");
//                    mogger.button2.setTag(1);
//                    mogger.textView.setText("New gesture saved.");
//                    mogger.button1.setEnabled(true);
//                    threshold = calculateThreshold();
//                    storage.storeGesture();
//                }
//            }
//        }
//        prev_x = x;
//        prev_y = y;
//        prev_z = z;
    }

}
