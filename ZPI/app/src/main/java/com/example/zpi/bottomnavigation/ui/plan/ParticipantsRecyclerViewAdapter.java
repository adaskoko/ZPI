package com.example.zpi.bottomnavigation.ui.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.R;
import com.example.zpi.models.TripPointParticipant;
import java.util.List;

public class ParticipantsRecyclerViewAdapter extends RecyclerView.Adapter<ParticipantsRecyclerViewAdapter.ParticipantViewHolder> {
    private List<TripPointParticipant> participants;
    private final OnParticipantListener onParticipantListener;

    public ParticipantsRecyclerViewAdapter(List<TripPointParticipant> participants, OnParticipantListener onParticipantListener) {
        this.participants = participants;
        this.onParticipantListener = onParticipantListener;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant_edit, parent, false);
        return new ParticipantViewHolder(view, onParticipantListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        TripPointParticipant participant = participants.get(position);
        holder.nameTV.setText(participant.getUser().getName());
        holder.emailTV.setText(participant.getUser().getEmail());
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public void deleteParticipant(int position) {
        participants.remove(position);
        notifyItemRemoved(position);
    }

    class ParticipantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTV;
        TextView emailTV;
        TextView deleteTV;
        OnParticipantListener onParticipantListener;

        public ParticipantViewHolder(@NonNull View itemView, OnParticipantListener onParticipantListener) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            emailTV = itemView.findViewById(R.id.emailTV);
            deleteTV = itemView.findViewById(R.id.deleteTV);

            this.onParticipantListener = onParticipantListener;
            deleteTV.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onParticipantListener.onParticipantClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnParticipantListener {
        void onParticipantClick(int position);
    };
}
