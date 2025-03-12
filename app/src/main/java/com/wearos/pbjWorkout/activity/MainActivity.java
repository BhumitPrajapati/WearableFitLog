package com.wearos.pbjWorkout.activity;

import android.app.Activity;
import com.wearos.pbjWorkout.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import com.wearos.pbjWorkout.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
    private ActivityMainBinding binding;
    private boolean isWorkoutRunning = false;
    private long startTime = 0;
    private Handler timerHandler = new Handler();

    // Runnable to update the timer display each second
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedMillis = System.currentTimeMillis() - startTime;
            int seconds = (int) (elapsedMillis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;
            // Update timer TextView in HH:MM:SS format
            binding.textTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            // Post again after 1 second for continuous update
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewBinding and set the content view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the click listener for the Start/Stop button
        binding.buttonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStartStop();
            }
        });
    }

    /**
     * Manages the Start/Stop button functionality.
     * Starts the timer if it's not running, or stops it and logs the workout if it is.
     */
    private void handleStartStop() {
        String workoutType = binding.inputWorkout.getText().toString().trim();

        if (!isWorkoutRunning) {
            // Begin a new workout session
            if (workoutType.isEmpty() ||
                    (!workoutType.equalsIgnoreCase("Walking") &&
                            !workoutType.equalsIgnoreCase("Running") &&
                            !workoutType.equalsIgnoreCase("Cycling"))) {
                // Show error if the input is invalid
                binding.inputWorkout.setError("Please enter a valid workout type (Walking, Running, or Cycling).");
                return;
            }
            // Valid input, start the timer
            isWorkoutRunning = true;
            binding.buttonStartStop.setText(getString(R.string.stop)); // change "Start" to "Stop"
            binding.textTimer.setText("00:00:00"); // reset timer display to 00:00:00
            startTime = System.currentTimeMillis();
            timerHandler.post(timerRunnable);  // begin updating the timer
        } else {
            // End the current workout session
            isWorkoutRunning = false;
            binding.buttonStartStop.setText(getString(R.string.start)); // change "Stop" back to "Start"
            timerHandler.removeCallbacks(timerRunnable); // stop updating the timer

            // Calculate the total elapsed time
            long elapsedMillis = System.currentTimeMillis() - startTime;
            int totalSeconds = (int) (elapsedMillis / 1000);
            int minutes = totalSeconds / 60;
            int hours = minutes / 60;
            int seconds = totalSeconds % 60;
            minutes = minutes % 60;
            // Format the duration as HH:MM:SS
            String duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            // Calculate calories burned using formula MET * 3.5 * 70 / 200 * minutes&#8203;:contentReference[oaicite:3]{index=3}&#8203;:contentReference[oaicite:4]{index=4}
            double MET;
            switch (workoutType.toLowerCase()) {
                case "walking":
                    MET = 3.5;
                    break;
                case "running":
                    MET = 9.8;
                    break;
                case "cycling":
                    MET = 11.5;
                    break;
                default:
                    MET = 1.0; // default MET (should not happen due to validation)
            }
            double calPerMin = MET * 3.5 * 70 / 200.0;  // 70kg assumed weight&#8203;:contentReference[oaicite:5]{index=5}
            double totalMinutes = elapsedMillis / 60000.0; // convert milliseconds to minutes
            double caloriesBurned = calPerMin * totalMinutes;
            // Round off calories to one decimal for readability
            double caloriesRounded = Math.round(caloriesBurned * 10) / 10.0;

            // Save workout session data to SharedPreferences
            saveWorkoutSession(workoutType, duration, caloriesRounded);

            // Reset the timer display for the next session
            binding.textTimer.setText(getString(R.string.timer_initial));

            // Navigate to the list screen to show all sessions
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Saves the workout session details (type, duration, calories) to SharedPreferences.
     */
    private void saveWorkoutSession(String type, String duration, double calories) {
        SharedPreferences prefs = getSharedPreferences("WorkoutSessions", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Retrieve existing sessions (if any)
        String existingData = prefs.getString("sessions", "");
        // Format new session as "Type,duration,calories"
        String newSession = type + "," + duration + "," + calories;
        String updatedData;
        if (existingData.isEmpty()) {
            updatedData = newSession;
        } else {
            // Append new session with a delimiter
            updatedData = existingData + ";" + newSession;
        }
        editor.putString("sessions", updatedData);
        editor.apply(); // save changes asynchronously

        Toast.makeText(this, "Workout session saved!", Toast.LENGTH_SHORT).show();
    }
}
