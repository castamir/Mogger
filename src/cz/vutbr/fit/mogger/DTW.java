package cz.vutbr.fit.mogger;

import static java.lang.Math.sqrt;

public class DTW {

    // pocita hodnotu DTW
    public int dtw_check(int[][] acc_gesture, int[][] gesture) {

        int size = acc_gesture[0].length;

        // LOKALNI DIFERENCE

        int[][] local_distance = new int[size][size];

        for (int i = 0; i < size; i++) {
            local_distance[i][0] = 10000000;
        }

        for (int i = 0; i < size; i++) {
            local_distance[0][i] = 10000000;
        }

        local_distance[0][0] = 0;

        for (int i = 0; i < size; i++) {
            for (int k = 0; k < size; k++) {
                local_distance[i][k] = euclidean_distance(gesture[0][i], acc_gesture[0][k], gesture[1][i], acc_gesture[1][k], gesture[2][i], acc_gesture[2][k]);
            }
        }

        // GLOBALNI DIFERENCE
        int[][] global_distance = new int[size][size];

        global_distance[0][0] = local_distance[0][0];

        // prvni radek
        for (int i = 1; i < size; i++) {
            global_distance[i][0] = local_distance[i][0] + global_distance[i - 1][0];
        }

        // prvni sloupec
        for (int k = 1; k < size; k++) {
            global_distance[0][k] = local_distance[0][k] + global_distance[0][k - 1];
        }

        for (int i = 1; i < size; i++) {

            for (int k = 1; k < size; k++) {
                global_distance[i][k] = local_distance[i][k] + min(global_distance[i - 1][k], global_distance[i - 1][k - 1], global_distance[i][k - 1]);
            }

        }

        return global_distance[size - 1][size - 1];
    }

    // spocita diferenci 2 vektoru
    public int euclidean_distance(double x1, double x2, double y1, double y2, double z1, double z2) {
        return (int) sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
    }

    // return minimum among integer x, y and z
    public int min(int x, int y, int z) {
        if ((x <= y) && (x <= z)) {
            return x;
        }
        if ((y <= x) && (y <= z)) {
            return y;
        }
        return z;
    }

}
