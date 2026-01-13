package com.example.am01;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private SoundPool soundPool;
    private int pianoSoundId;
    private boolean soundLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        applyInsets();

        setupTopButtons();
        setupSoundPool();
        setupPianoKeys();
    }

    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupTopButtons() {
        Button mohidButton = findViewById(R.id.mohid);
        Button soundButton = findViewById(R.id.sound);

        mohidButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Hello Mohid !", Toast.LENGTH_SHORT).show()
        );

        // "Play a Sound!" plays the original sample (base note)
        mediaPlayer = MediaPlayer.create(this, R.raw.pianoa);
        soundButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                // If already playing, restart for quick repeated taps
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(0);
                }
                mediaPlayer.start();
            }
        });
    }

    private void setupSoundPool() {
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(8) // allow more overlap when tapping multiple keys fast
                .setAudioAttributes(attrs)
                .build();

        pianoSoundId = soundPool.load(this, R.raw.pianoa, 1);

        soundPool.setOnLoadCompleteListener((sp, sampleId, status) -> {
            if (status == 0) {
                soundLoaded = true;
            }
        });
    }

    private void setupPianoKeys() {
        Button btnC  = findViewById(R.id.btnC);
        Button btnCs = findViewById(R.id.btnCs);
        Button btnD  = findViewById(R.id.btnD);
        Button btnDs = findViewById(R.id.btnDs);
        Button btnE  = findViewById(R.id.btnE);
        Button btnF  = findViewById(R.id.btnF);
        Button btnFs = findViewById(R.id.btnFs);
        Button btnG  = findViewById(R.id.btnG);
        Button btnGs = findViewById(R.id.btnGs);
        Button btnA  = findViewById(R.id.btnA);
        Button btnAs = findViewById(R.id.btnAs);
        Button btnB  = findViewById(R.id.btnB);

        btnC.setOnClickListener(v  -> playNote(0));
        btnCs.setOnClickListener(v -> playNote(1));
        btnD.setOnClickListener(v  -> playNote(2));
        btnDs.setOnClickListener(v -> playNote(3));
        btnE.setOnClickListener(v  -> playNote(4));
        btnF.setOnClickListener(v  -> playNote(5));
        btnFs.setOnClickListener(v -> playNote(6));
        btnG.setOnClickListener(v  -> playNote(7));
        btnGs.setOnClickListener(v -> playNote(8));
        btnA.setOnClickListener(v  -> playNote(9));
        btnAs.setOnClickListener(v -> playNote(10));
        btnB.setOnClickListener(v  -> playNote(11));
    }

    private void playNote(int semitoneOffset) {
        if (!soundLoaded || soundPool == null) return;

        // Equal temperament: rate = 2^(n/12)
        float rate = (float) Math.pow(2.0, semitoneOffset / 12.0);

        float leftVol = 1.0f, rightVol = 1.0f;
        int priority = 1;
        int loop = 0;

        soundPool.play(pianoSoundId, leftVol, rightVol, priority, loop, rate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
