package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

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
        int nMistakes = dbm.getNumberOfMistakes(listId);

        findViewById(R.id.continue_button).setOnClickListener(this);
        findViewById(R.id.retry_all_button).setOnClickListener(this);
        findViewById(R.id.retry_mistakes_button).setOnClickListener(this);

        TextView score = (TextView) findViewById(R.id.score);
        score.setText(calculateScore(sizeList, nMistakes));

        createBarGraph();

    }

    private String calculateScore(long total, int nMistakes) {
        if (nMistakes > total) {
            return "1";
        }

        return String.valueOf(Math.round(100 + 900 / ((double) total / (total - nMistakes)))/100);
    }

    private void createBarGraph() {
        GraphView barGraphView = (GraphView) findViewById(R.id.barGraph);

        int max = dbm.getHighestTries(listId);
        int highest = 0;

        DataPoint[] dataPoints = new DataPoint[max];

        for (int i = 1; i <= max; i++) {
            Log.d("test i", String.valueOf(i));

            int count = dbm.countNumberTries(listId, i);

            dataPoints[i-1] = new DataPoint(i, count);

            if (count > highest) {
                highest = count;
            }
        }

        barGraphView.getViewport().setYAxisBoundsManual(true);
        barGraphView.getViewport().setMinY(0);
        barGraphView.getViewport().setMaxY(highest + highest * 0.1);

        barGraphView.getViewport().setXAxisBoundsManual(true);
        barGraphView.getViewport().setMinX(0);
        barGraphView.getViewport().setMaxX(max + 0.5);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
        barGraphView.addSeries(series);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });
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
                intent = new Intent(this, ExamActivity.class);
                intent.putExtra("id", listId);
                intent.putExtra("isRetryMistakes", true);

                startActivity(intent);
                finish();

                break;
        }
    }
}

