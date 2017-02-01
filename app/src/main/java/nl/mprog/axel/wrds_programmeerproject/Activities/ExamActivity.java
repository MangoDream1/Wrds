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

/**
 * ExamActivity that asks a random word out of data list. Feedback is given with the contents
 * depending on if the answer is correct or not.
 */

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

        setButtonListeners();
        restoreInstanceState(isRetryMistakes, savedInstanceState);
    }

    /**
     * Set button listeners
     */
    private void setButtonListeners() {
        (findViewById(R.id.check_button)).setOnClickListener(this);
        (findViewById(R.id.quit_button)).setOnClickListener(this);
        (findViewById(R.id.continue_button)).setOnClickListener(this);
    }

    /**
     * Create dataList using createDataList(Cursor cursor) getting the right cursor depending on
     * isRetryMistakes
     * @param isRetryMistakes if current exam is retry from mistakes or new
     */
    private void createDataList(boolean isRetryMistakes) {
        Cursor cursor;

        if (isRetryMistakes) {
            cursor = dbm.getRetryWords(listId);
        } else {
            cursor = dbm.getListWords(listId);
        }

        // Reset mistakes to play new
        dbm.resetWordTries(listId, isRetryMistakes);

        createDataList(cursor);
    }

    /**
     * Create dataList from the given cursor
     * @param cursor data cursor
     */
    private void createDataList(Cursor cursor) {
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            dataList.add(new String[]{
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.PK_WORD_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_A)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_B))
            });
        }

        dataListSize = dataList.size();
    }

    /**
     * Create progressBar
     */
    private void createProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(dataListSize);
    }

    /**
     * Set the word to be translated
     */
    private void setWord() {
        createRandomInt();

        // If empty then exam is finished
        if (dataList.isEmpty()) {
            startResultActivity();
            return;
        }

        wordId = Integer.parseInt(dataList.get(randomInt)[0]);
        String translate = dataList.get(randomInt)[1];
        wordA = dataList.get(randomInt)[2];

        ((TextView) findViewById(R.id.translate)).setText(translate);
    }

    /**
     * Start the ResultActivity
     */
    private void startResultActivity() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("id", listId);

        startActivity(intent);

        finish();
    }

    /**
     * Create random int that will select the word
     */
    private void createRandomInt() {
        if (dataList.isEmpty()) {
            return;
        }
        randomInt = new Random().nextInt(dataList.size());
    }

    /**
     * Update progressBar
     */
    private void updateProgressBar() {
        progressBar.setProgress(progressBar.getProgress()+1);
    }

    /**
     * Show correct feedback
     * @param isCorrect answer correct or not
     */
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
        disableEditText();
    }

    /**
     * Hide feedback, continue button and show check button
     */
    private void hideFeedback() {
        findViewById(R.id.continue_button).setVisibility(View.GONE);
        findViewById(R.id.check_button).setVisibility(View.VISIBLE);

        findViewById(R.id.feedback_incorrect).setVisibility(View.GONE);
        findViewById(R.id.feedback_correct).setVisibility(View.GONE);

        enableEditText();
    }

    /**
     * Enable editText and reset content
     */
    private void enableEditText() {
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setEnabled(true);
        editText.setText("");
    }

    /**
     * Disable editText, show continue button and hide check button
     */
    private void disableEditText() {
        findViewById(R.id.editText).setEnabled(false);
        findViewById(R.id.continue_button).setVisibility(View.VISIBLE);
        findViewById(R.id.check_button).setVisibility(View.GONE);
    }

    /**
     * onClick for the buttons
     * @param view view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_button:
                checkButton();
                break;

            case R.id.quit_button:
                quitButton();
                break;

            case R.id.continue_button:
                continueButton();
                break;
        }
    }

    /**
     * Check if the answer is correct. Show the correct feedback
     */
    private void checkButton() {
        wordB = ((EditText) findViewById(R.id.editText)).getText().toString().trim();
        dbm.incrementWordTry(wordId);

        if (!wordB.isEmpty() || AnswerComparison.checkCorrect(wordA, wordB)) {
            updateProgressBar();

            // Correct thus don't ask again
            dataList.remove(randomInt);
            showFeedback(true);

        } else {
            showFeedback(false);
        }
    }

    /**
     * Call DefaultDialog with origin "quit", if confirmation is correct finish activity.
     */
    private void quitButton() {
        DefaultDialog defaultDialog = new DefaultDialog();

        Bundle bundle = new Bundle();

        bundle.putInt("title", R.string.dialog_cancel_title);
        bundle.putInt("message", R.string.dialog_cancel_message);
        bundle.putInt("positive", R.string.button_quit);
        bundle.putInt("negative", R.string.button_cancel);
        bundle.putString("origin", "quit");

        defaultDialog.setArguments(bundle);
        defaultDialog.show(getFragmentManager(), "DefaultDialog");
    }

    /**
     * Continue with new word thus set new random word
     */
    private void continueButton() {
        setWord();
        hideFeedback();
    }

    /**
     * Interface callback if DefaultDialog is positive, used by quit
     * @param origin origin of Dialog to be called back
     */
    @Override
    public void dialogPositive(String origin) {
        switch (origin) {
            case "quit":
                finish();
        }
    }

    /**
     * Save the dataList, original size, wordA, wordB and the current selected  word
     * @param outState outState bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("dataList", dataList);
        outState.putInt("dataListSize", dataListSize);

        outState.putString("wordA", wordA);
        outState.putString("wordB", wordB);
        outState.putInt("randomInt", randomInt);
    }


    /**
     * Checks if instanceState needs to be restored if yes restore else start anew
     * @param isRetryMistakes if current exam is retry from mistakes or new
     * @param savedInstanceState bundle with saved values
     */
    private void restoreInstanceState(boolean isRetryMistakes, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            createDataList(isRetryMistakes);
            createProgressBar();
            setWord();
        } else {
            restoreInstanceState(savedInstanceState);
        }
    }

    /**
     * Restore instance state from savedInstanceState
     * @param savedInstanceState bundle with saved values
     */
    private void restoreInstanceState(Bundle savedInstanceState) {
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
