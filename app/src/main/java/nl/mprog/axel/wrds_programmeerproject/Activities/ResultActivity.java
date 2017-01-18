package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.R;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseManager dbm;
    long listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        dbm = DatabaseManager.getInstance();

        Intent intent = getIntent();

        listId = intent.getLongExtra("listId", 0L);
        long sizeList = dbm.countListWords(listId);
        int nMistakes = dbm.getSumWordMistakesList(listId);

        findViewById(R.id.continue_button).setOnClickListener(this);
        findViewById(R.id.retry_all_button).setOnClickListener(this);
        findViewById(R.id.retry_mistakes_button).setOnClickListener(this);

        TextView score = (TextView) findViewById(R.id.score);
        score.setText(calculateScore(sizeList, nMistakes));

        ((TextView) findViewById(R.id.nMistakes)).setText(String.valueOf(nMistakes));

        ((TextView) findViewById(R.id.aRightFirstTime)).setText(
                String.valueOf(dbm.getCountNumberMistakesList(listId, 0)));

        ((TextView) findViewById(R.id.aRightSecondTime)).setText(
                String.valueOf(dbm.getCountNumberMistakesList(listId, 1)));

        ((TextView) findViewById(R.id.aRightThirdTime)).setText(
                String.valueOf(dbm.getCountNumberMistakesList(listId, 2)));


    }

    private String calculateScore(long total, int nMistakes) {
        if (nMistakes > total) {
            return "1";
        }

        return String.valueOf(Math.round(100 + 900 / ((double) total / (total - nMistakes)))/100);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_button:
                finish();
                break;

            case R.id.retry_all_button:
                Intent intent = new Intent(this, ExamActivity.class);
                intent.putExtra("id", listId);

                startActivity(intent);
                finish();

                break;

            case R.id.retry_mistakes_button:
                // TODO make it work retryMistakes

                intent = new Intent(this, ExamActivity.class);
                intent.putExtra("id", listId);
                intent.putExtra("isRetryMistakes", true);

                startActivity(intent);

                break;
        }
    }
}

