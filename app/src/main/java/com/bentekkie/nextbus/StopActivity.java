package com.bentekkie.nextbus;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bentekkie.nextbuslib.FeedTask;
import com.bentekkie.nextbuslib.Stop;
import com.bentekkie.nextbuslib.TripAdapter;

public class StopActivity extends AppCompatActivity {

    public static String EXTRA_STOP_DATA = StopActivity.class.getName()+".STOP_DATA";
    Stop stop;
    TextView stopNameTextView;
    TextView stopIDTextView;
    RecyclerView tripList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        try {
            stop = (Stop) getIntent().getSerializableExtra(EXTRA_STOP_DATA);
        } catch (ClassCastException ex) {
            ex.printStackTrace();
            finish();
        }

        if(stop == null){
            finish();
        }
        stopNameTextView = findViewById(R.id.stopNameSingle);
        stopIDTextView = findViewById(R.id.stopIDSingle);
        tripList = findViewById(R.id.tripList);
        stopNameTextView.setText(stop.getStopName());
        stopIDTextView.setText(stop.getStopId());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        tripList.setLayoutManager(mLayoutManager);
        TripAdapter adapter = new TripAdapter( R.id.route, R.id.time, R.layout.trip_list_item, (v, trip) -> {}, false);
        tripList.setAdapter(adapter);

        FeedTask.getTrips(stop.getStopId()).thenAccept(trips -> {
                runOnUiThread(() -> adapter.addAll(trips));
        });



    }
}
