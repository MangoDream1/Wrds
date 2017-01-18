package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.R;

public class ResultActivity extends AppCompatActivity {

    DatabaseManager dbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        dbm = DatabaseManager.getInstance();

        Intent intent = getIntent();

        long listId = intent.getLongExtra("listId", 0L);
        long sizeList = dbm.countListWords(listId);
        int nMistakes = dbm.getSumWordMistakesList(listId);

        TextView score = (TextView) findViewById(R.id.score);
        score.setText(calculateScore(sizeList, nMistakes));

    }

    private String calculateScore(long total, int nMistakes) {
        if (nMistakes > total) {
            return "1";
        }

        return String.valueOf(Math.round(100 + 900 / ((double) total / (total - nMistakes)))/100);
    }

}

