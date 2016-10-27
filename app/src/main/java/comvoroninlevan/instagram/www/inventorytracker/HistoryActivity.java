package comvoroninlevan.instagram.www.inventorytracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Леван on 24.10.2016.
 */
public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_activities);

        TextView textView = (TextView)findViewById(R.id.text);
        textView.setText(R.string.historyActivity);
    }
}
