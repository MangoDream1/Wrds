package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import nl.mprog.axel.wrds_programmeerproject.Algorithms.AnswerComparison;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseHelper;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.Dialogs.DefaultDialog;
import nl.mprog.axel.wrds_programmeerproject.Interfaces.DefaultDialogInterface;
import nl.mprog.axel.wrds_programmeerproject.R;

public class ExamActivity extends AppCompatActivity implements View.OnClickListener,
        DefaultDialogInterface{

    private String wordA;
    private String wordB;

    private int randomInt;

    private long wordId;
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
        boolean isRetryMistakes = intent.getBooleanExtra("isRetryMistakes", false);

        (findViewById(R.id.check_button)).setOnClickListener(this);
        (findViewById(R.id.cancel_button)).setOnClickListener(this);
        (findViewById(R.id.continue_button)).setOnClickListener(this);

        if (savedInstanceState == null) {
            Cursor cursor;

            if (isRetryMistakes) {
                cursor = dbm.getRetryWords(listId);
            } else {
                cursor = dbm.getListWords(listId);
            }

            // Reset mistakes to play new
            dbm.resetWordTries(listId, isRetryMistakes);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                dataList.add(new String[]{
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.PK_WORD_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_A)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_B))
                });
            }

            dataListSize = dataList.size();

            createProgressBar();
            createRandomInt();
            setWord();

        } else {
            dataList = (ArrayList<String[]>) savedInstanceState.getSerializable("dataList");
            dataListSize = savedInstanceState.getInt("dataListSize");

            wordA = savedInstanceState.getString("wordA");
            wordB = savedInstanceState.getString("wordB");
            randomInt = savedInstanceState.getInt("randomInt");

            setWord();
            createProgressBar();
            updateProgressBar();
        }
    }

    private void startResultActivity() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("id", listId);

        startActivity(intent);

        finish();
    }

    private void createRandomInt() {
        if (dataList.isEmpty()) {
            return;
        }
        randomInt = new Random().nextInt(dataList.size());
    }

    private void setWord() {
        // If empty then exam is finished
        if (dataList.isEmpty()) {
            startResultActivity();
            return;
        }

        wordId = Integer.parseInt(dataList.get(randomInt)[0]);
        String translate = dataList.get(randomInt)[1];
        wordA = dataList.get(randomInt)[2];

        ((TextView) this.findViewById(R.id.translate)).setText(translate);
    }

    private void createProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(dataListSize);
    }

    private void updateProgressBar() {
        progressBar.setProgress(progressBar.getProgress()+1);
    }

    private void showFeedback(boolean isCorrect) {
        View feedback;

        findViewById(R.id.feedbacks_linearLayout).bringToFront();

        if (isCorrect) {
            feedback = findViewById(R.id.feedback_correct);
        } else {
            feedback = findViewById(R.id.feedback_incorrect);
            TextView correction = (TextView) findViewById(R.id.correction_textView);

            correction.setText(AnswerComparison.underlineWrongPart(wordA, wordB));
        }

        feedback.setVisibility(View.VISIBLE);

        // Disable editText. User should not be able to edit now
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setEnabled(false);

        findViewById(R.id.continue_button).setVisibility(View.VISIBLE);
        findViewById(R.id.check_button).setVisibility(View.GONE);

    }

    private void hideFeedback() {
        findViewById(R.id.continue_button).setVisibility(View.GONE);
        findViewById(R.id.check_button).setVisibility(View.VISIBLE);

        findViewById(R.id.feedback_incorrect).setVisibility(View.GONE);
        findViewById(R.id.feedback_correct).setVisibility(View.GONE);

        // Enable editText and reset content
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setEnabled(true);
        editText.setText("");

    }

    @Override
    public void dialogPositive() {
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("dataList", dataList);
        outState.putInt("dataListSize", dataListSize);

        outState.putString("wordA", wordA);
        outState.putString("wordB", wordB);
        outState.putInt("randomInt", randomInt);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_button:
                EditText editText = (EditText) findViewById(R.id.editText);

                wordB = editText.getText().toString().trim();
                dbm.incrementWordTry(wordId);

                if (wordB.isEmpty() || !AnswerComparison.checkCorrect(wordA, wordB)) {
                    // Made a mistake
                    showFeedback(false);

                } else {
                    updateProgressBar();

                    // Correct thus don't ask again
                    dataList.remove(randomInt);

                    showFeedback(true);
                }
                break;

            case R.id.cancel_button:
                DefaultDialog defaultDialog = new DefaultDialog();

                Bundle bundle = new Bundle();

                bundle.putString("title", "Are you sure you want to quit?");
                bundle.putString("message", "You can continue where you left of by pressing " +
                        "results and then retry mistakes");
                bundle.putString("positive", "Quit");
                bundle.putString("negative", "Cancel");

                defaultDialog.setArguments(bundle);
                defaultDialog.show(getFragmentManager(), "DefaultDialog");

                break;

            case R.id.continue_button:
                // set new random word
                createRandomInt();
                setWord();
                hideFeedback();
        }
    }
}
