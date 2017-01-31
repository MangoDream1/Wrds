package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * ResultActivity shows the score of the exam and a pie chart showing the distribution of mistakes
 * and right answer.
 */

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseManager dbm;
    long listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        dbm = DatabaseManager.getInstance();

        Intent intent = getIntent();
        listId = intent.getLongExtra("id", 0L);

        setButtons();
        setScore();
        createPieChart();
    }

    /**
     * Sets buttons of view
     */
    private void setButtons() {
        findViewById(R.id.continue_button).setOnClickListener(this);
        findViewById(R.id.retry_all_button).setOnClickListener(this);
        findViewById(R.id.retry_mistakes_button).setOnClickListener(this);
    }

    /**
     * Sets score to TextView
     */
    private void setScore() {
        long sizeList = dbm.countListWords(listId);
        int nMistakes = dbm.getNumberOfMistakes(listId);

        String score = calculateScore(sizeList, nMistakes);
        ((TextView) findViewById(R.id.score)).setText(score);

        // If max score cannot retry mistakes
        if (score.equals("10")) {
            findViewById(R.id.retry_mistakes_button).setEnabled(false);
        }
    }

    /**
     * Calculates score
     * @param total     total number of words
     * @param nMistakes number of mistakes
     * @return          return String score
     */
    private String calculateScore(long total, int nMistakes) {
        if (nMistakes > total) {
            return "1";
        }

        return String.valueOf(Math.round(100 + 900 / ((double) total / (total - nMistakes)))/100);
    }

    /**
     * Creates Pie Chart
     */
    private void createPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(dbm.getNumberOfMistakes(listId), "mistakes"));
        entries.add(new PieEntry(dbm.countNumberTries(listId, 1), "correct"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(getResources().getColor(R.color.pieChartRed),
                getResources().getColor(R.color.pieChartGreen));

        createPieChart(dataSet);
    }

    /**
     * Adds data to Pie Chart and styles legend
     * @param dataSet   pieChart data
     */
    private void createPieChart(PieDataSet dataSet) {
        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDrawEntryLabels(false);
    }

    /**
     * onClick all buttons finish
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_button:
                finish();
                break;

            case R.id.retry_all_button:
                retryAllButton();
                finish();
                break;

            case R.id.retry_mistakes_button:
                retryMistakesButton();
                finish();
                break;
        }
    }

    /**
     * Retry all button starts ExamActivity of same list
     */
    private void retryAllButton() {
        Intent intent = new Intent(this, ExamActivity.class);
        intent.putExtra("id", listId);

        startActivity(intent);
    }

    /**
     * Retry mistakes button starts ExamActivity of same list retrying only mistakes
     */
    private void retryMistakesButton() {
        Intent intent = new Intent(this, ExamActivity.class);
        intent.putExtra("id", listId);
        intent.putExtra("isRetryMistakes", true);

        startActivity(intent);
    }
}

