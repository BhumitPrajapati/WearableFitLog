package com.wearos.pbjWorkout.model;



/** Model class representing a workout session record. */
public class WorkoutSession {
    private String type;
    private String duration;
    private double calories;

    public WorkoutSession(String type, String duration, double calories) {
        this.type = type;
        this.duration = duration;
        this.calories = calories;
    }

    public String getType() {
        return type;
    }

    public String getDuration() {
        return duration;
    }

    public double getCalories() {
        return calories;
    }
}
