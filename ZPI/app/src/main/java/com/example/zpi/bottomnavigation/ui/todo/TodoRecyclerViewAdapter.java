package com.example.zpi.bottomnavigation.ui.todo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.repositories.PreparationPointDao;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoViewHolder> {

    private List<PreparationPoint> todoList;
    private final OnTodoListener onTodoListener;

    public TodoRecyclerViewAdapter(List<PreparationPoint> todoList, OnTodoListener onTodoListener) {
        this.todoList = todoList;
        this.onTodoListener = onTodoListener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(itemView, onTodoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        PreparationPoint todo = todoList.get(position);
        holder.mItem = todo;
        holder.todoChB.setActivated(todo.isDone());
        holder.personTV.setText(todo.getUser().getName());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(todo.getDeadline());
        holder.deadlineTV.setText(strDate);
        holder.titleTV.setText(todo.getName());
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void deleteTodoPosition(int position) {
        PreparationPoint point = todoList.get(position);
        new Thread(() -> {
            try {
                PreparationPointDao pointDao = new PreparationPointDao(BaseConnection.getConnectionSource());
                pointDao.delete(point);
                Log.i("todo", "todo delete");
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        todoList.remove(position);
        //notifyDataSetChanged();
        notifyItemRemoved(position);
    }

    public PreparationPoint getTodo(int position) {
        return todoList.get(position);
    }

    class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PreparationPoint mItem;
        private final CheckBox todoChB;
        private final TextView personTV;
        private final TextView titleTV;
        private final TextView deadlineTV;
        private final OnTodoListener onTodoListener;

        public TodoViewHolder(@NonNull View itemView, OnTodoListener onTodoListener) {
            super(itemView);
            todoChB = itemView.findViewById(R.id.todoChB);
            personTV = itemView.findViewById(R.id.personTV);
            titleTV = itemView.findViewById(R.id.titleTV);
            deadlineTV = itemView.findViewById(R.id.deadlineTV);

            this.onTodoListener = onTodoListener;
            itemView.setOnClickListener(this);
            todoChB.setOnClickListener(c -> mItem.setDone(!mItem.isDone()));
        }

        @Override
        public void onClick(View v) {
            onTodoListener.onTodoClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnTodoListener {
        void onTodoClick(int position);
    }
}
