package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import nl.mprog.axel.wrds_programmeerproject.Algorithms.AnswerComparison;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseHelper;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.R;

public class ExamActivity extends AppCompatActivity implements View.OnClickListener {

    private String wordA;
    private String wordB;

    private int randomInt;

    private int nMistakes;

    private long listId;
    private DatabaseManager dbm;

    private ArrayList<String[]> dataList = new ArrayList<>();
    private int dataListSize;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        dbm = DatabaseManager.getInstance();

        Intent intent = getIntent();
        listId = intent.getLongExtra("id", 0L);

        (findViewById(R.id.check_button)).setOnClickListener(this);
        (findViewById(R.id.cancel_button)).setOnClickListener(this);

        Cursor cursor = dbm.getListWords(listId);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            dataList.add(new String[]{
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordA)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordB))
            });
        }

        dataListSize = dataList.size();

        createProgressBar();
        findRandomWord();
    }

    private void startResultActivity() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("nMistakes", nMistakes);
        intent.putExtra("sizeList", dataListSize);

        startActivity(intent);

        finish();
    }

    private void findRandomWord() {
        // If empty then exam is finished
        if (dataList.isEmpty()) {
            startResultActivity();
            return;
        }

        randomInt = new Random().nextInt(dataList.size());

        String translate = dataList.get(randomInt)[0];
        wordA = dataList.get(randomInt)[1];

        ((TextView) this.findViewById(R.id.translate)).setText(translate);
    }

    private void createProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(dataListSize);
    }

    private void updateProgressBar() {
        progressBar.setProgress(progressBar.getProgress()+1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_button:
                AnswerComparison answerComparison = new AnswerComparison();

                EditText editText = (EditText) findViewById(R.id.editText);

                wordB = editText.getText().toString();

                if (wordB.isEmpty() || !answerComparison.checkCorrect(wordA, wordB)) {
                    // Made a mistake
                    nMistakes++;

                    TextView test = (TextView) findViewById(R.id.textView2);

                    test.setText(answerComparison.underlineWrongPart(wordA, wordB));

                    // set new random word
                    findRandomWord();
                } else {
                    updateProgressBar();

                    // Correct thus don't ask again
                    dataList.remove(randomInt);

                    findRandomWord();

                }
                break;

            case R.id.cancel_button:
                // TODO check if user is sure dialog
                finish();
                break;
        }
    }
}