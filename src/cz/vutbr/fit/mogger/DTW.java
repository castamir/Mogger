package cz.vutbr.fit.mogger;

import android.util.Log;

import static java.lang.Math.sqrt;

public class DTW {

    public DTW() {

    }

    // pocita hodnotu DTW
    public int dtw_check (int[][] acc_gesture, int[][] gesture) {

        int dtw = 0;

        int size = acc_gesture[0].length;

        // LOKALNI DIFERENCE
        int n = size;
        int m = size;

        int[][] local_distance = new int[n][m];
        int cost = 0;

        for (int i = 0; i < n; i++) {
            local_distance[i][0] = 10000000;
        }

        for (int i = 0; i < n; i++) {
            local_distance[0][i] = 10000000;
        }

        local_distance[0][0] = 0;

        for(int i=0; i<n; i++) {

            for(int k=0; k<m; k++) {
                cost = euclidean_distance(gesture[0][i],acc_gesture[0][k],gesture[1][i],acc_gesture[1][k],gesture[2][i],acc_gesture[2][k]);

                local_distance[i][k] = cost;
            }

        }

        // GLOBALNI DIFERENCE
        int[][] global_distance = new int[n][m];

        global_distance[0][0] = local_distance[0][0];

        // prvni radek
        for(int i=1; i<n; i++) {
            global_distance[i][0] = local_distance[i][0] + global_distance[i - 1][0];
        }

        // prvni sloupec
        for(int k=1; k<m; k++) {
            global_distance[0][k] = local_distance[0][k] + global_distance[0][k-1];
        }


        for(int i=1; i<n; i++) {

            for(int k=1; k<m; k++) {
                global_distance[i][k] = local_distance[i][k] + min(global_distance[i-1][k],global_distance[i-1][k-1],global_distance[i][k-1]);
            }

        }

        dtw = global_distance[n-1][m-1];

        return dtw;
    }

    // spocita diferenci 2 vektoru
    public int euclidean_distance (double x1, double x2, double y1, double y2, double z1, double z2) {
        int ed = 0;

        ed = (int) sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2) + (z1 - z2)*(z1 - z2));

        return ed;
    }

    // return minimum among integer x, y and z
    public int min(int x, int y, int z)
    {
        if(( x <= y ) && ( x <= z )) {
            return x;
        }
        if(( y <= x ) && ( y <= z )) {
            return y;
        }
        return z;
    }

}
