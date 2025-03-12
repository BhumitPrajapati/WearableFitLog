package com.wearos.pbjWorkout.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import com.wearos.pbjWorkout.adapter.WorkoutAdapter;
import com.wearos.pbjWorkout.model.WorkoutSession;
import com.wearos.pbjWorkout.databinding.ActivityListBinding;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity {
    private ActivityListBinding binding;
    private List<WorkoutSession> sessionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load sessions from SharedPreferences
        loadSessionsFromPreferences();

        // Set up WearableRecyclerView
        WearableRecyclerView recyclerView = binding.recyclerView;
        recyclerView.setEdgeItemsCenteringEnabled(true); // center first and last items&#8203;:contentReference[oaicite:12]{index=12}
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        recyclerView.setAdapter(new WorkoutAdapter(sessionList));
    }

    /**
     * Reads the stored sessions from SharedPreferences and populates sessionList.
     */
    private void loadSessionsFromPreferences() {
        String data = getSharedPreferences("WorkoutSessions", MODE_PRIVATE)
                .getString("sessions", "");
        if (data == null || data.isEmpty()) {
            return; // No sessions saved
        }
        // Data format: "Type,HH:MM:SS,calories;Type,HH:MM:SS,calories;..."
        String[] entries = data.split(";");
        for (String entry : entries) {
            String[] parts = entry.split(",");
            if (parts.length == 3) {
                String type = parts[0];
                String duration = parts[1];
                double calories = 0;
                try {
                    calories = Double.parseDouble(parts[2]);
                } catch (NumberFormatException e) {
                    // Handle parse error if any (not expected if data format is correct)
                }
                sessionList.add(new WorkoutSession(type, duration, calories));
            }
        }
    }
}
