package nl.mprog.axel.wrds_programmeerproject;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class ExamActivity extends AppCompatActivity implements View.OnClickListener {

    private String wordA;
    private String wordB;

    private int randomInt;

    private int nMistakes;

    private long listId;
    private DatabaseManager dbm;
    private ArrayList<String[]> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        dbm = DatabaseManager.getInstance();

        Intent intent = getIntent();
        listId = intent.getLongExtra("id", 0L);

        Button checkButton = (Button) findViewById(R.id.check_button);
        checkButton.setOnClickListener(this);

        Cursor cursor = dbm.getListWords(listId);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            dataList.add(new String[]{
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordA)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordB))
            });
        }

        findRandomWord();
    }

    private void findRandomWord() {
        randomInt = new Random().nextInt(dataList.size());

        String translate = dataList.get(randomInt)[0];
        wordA = dataList.get(randomInt)[1];

        Log.d("test translate", translate);

        ((TextView) this.findViewById(R.id.translate)).setText(translate);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_button:
                // TODO check if wordB is empty then prevent AnswerComparison

                AnswerComparison answerComparison = new AnswerComparison();

                EditText editText = (EditText) findViewById(R.id.editText);

                wordB = editText.getText().toString();

                if (answerComparison.checkCorrect(wordA, wordB)) {
                    dataList.remove(randomInt);

                } else {
                    // Made a mistake
                    nMistakes++;

                    TextView test = (TextView) findViewById(R.id.textView2);

                    test.setText(answerComparison.underlineWrongPart(wordA, wordB));

                    // set new random word
                    findRandomWord();
                }

                // If empty all is done
                if (dataList.isEmpty()) {
                    Intent intent = new Intent(this, ResultActivity.class);
                    intent.putExtra("nMistakes", nMistakes);
                    intent.putExtra("sizeList", dataList.size());

                    startActivity(intent);


                }
        }
    }
}
