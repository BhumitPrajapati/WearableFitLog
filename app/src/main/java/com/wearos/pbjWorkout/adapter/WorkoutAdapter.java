package com.wearos.pbjWorkout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.wearos.pbjWorkout.R;
import com.wearos.pbjWorkout.model.WorkoutSession;
import java.util.List;

/** Adapter for displaying workout sessions in a WearableRecyclerView list. */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.SessionViewHolder> {

    private List<WorkoutSession> sessionList;

    // ViewHolder inner class to hold reference to item views
    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView textType, textDetails;
        SessionViewHolder(View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textType);
            textDetails = itemView.findViewById(R.id.textDetails);
        }
    }

    public WorkoutAdapter(List<WorkoutSession> sessions) {
        this.sessionList = sessions;
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        // Bind data for each session item
        WorkoutSession session = sessionList.get(position);
        holder.textType.setText(session.getType());
        // Format details as "Duration: X, Calories: Y kcal"
        String details = "Duration: " + session.getDuration()
                + ", Calories: " + session.getCalories() + " kcal";
        holder.textDetails.setText(details);
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }
}
