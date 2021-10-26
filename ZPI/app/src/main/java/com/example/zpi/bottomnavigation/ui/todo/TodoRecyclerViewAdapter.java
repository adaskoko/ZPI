package com.example.zpi.bottomnavigation.ui.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.R;
import com.example.zpi.models.PreparationPoint;

import java.util.ArrayList;
import java.util.List;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoViewHolder> {

    private List<PreparationPoint> todoList = new ArrayList<>();

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        PreparationPoint todo = todoList.get(position);
        holder.todoChB.setActivated(false); // jak bazie jest informacja czy rzecz została juz zrobiona
        holder.personTV.setText(todo.getUser().getName());
        holder.deadlineTV.setText(todo.getDeadline().toString());
        holder.titleTV.setText(todo.getName());
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void setTodo(List<PreparationPoint> list) {
        this.todoList = list;
        notifyDataSetChanged();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox todoChB;
        private final TextView personTV;
        private final TextView titleTV;
        private final TextView deadlineTV;
        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            todoChB = itemView.findViewById(R.id.todoChB);
            personTV = itemView.findViewById(R.id.personTV);
            titleTV = itemView.findViewById(R.id.titleTV);
            deadlineTV = itemView.findViewById(R.id.deadlineTV);
        }
    }
}
