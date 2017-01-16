package nl.mprog.axel.wrds_programmeerproject;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        int nMistakes = intent.getIntExtra("nMistakes", 0);
        int sizeList = intent.getIntExtra("sizeList", 0);

        TextView score = (TextView) findViewById(R.id.score);
        score.setText(calculateScore(sizeList, nMistakes));

    }

    private String calculateScore(int total, int nMistakes) {
        if (nMistakes > total) {
            return "1";
        }

        return String.valueOf(Math.round(100 + 900 / ((double) total / nMistakes))/100);
    }

}

