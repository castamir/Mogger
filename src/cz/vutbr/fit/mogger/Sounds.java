package cz.vutbr.fit.mogger;


import android.media.AudioManager;
import android.media.ToneGenerator;

public class Sounds{

    public void PlayTone(){

        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, ToneGenerator.MAX_VOLUME);
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 100);

    }

}
