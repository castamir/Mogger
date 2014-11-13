package cz.vutbr.fit.mogger;

// konstanty

public final class Constants {

    private Constants() {

    }

    public static final int SAVE = 1;
    public static final int CHECK = 2;
    public static final int START_SAVING = 3;
    public static final int CHECK_GESTURE_START = 4;
    public static final int CHECK_GESTURE_STOP = 5;

    // konstanta pro vzorkovani (vhodne editovat v nastaveni), pocet_vzorku/sec = 1000/round(samplingRate) ... v nasem pripade je to 20
    public static final double SAMPLING_RATE = 45;

}
