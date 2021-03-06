package com.example.zpi.bottomnavigation.ui.todo;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;
import static com.example.zpi.bottomnavigation.ui.todo.ToDoFragment.TODO_KEY;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentTodoDetailsBinding;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TodoDetailsFragment extends Fragment {

    private PreparationPoint actPoint;
    FragmentTodoDetailsBinding binding;
    private Trip currTrip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint = (PreparationPoint) getArguments().get(TODO_KEY);
            currTrip = (Trip) getArguments().get(TRIP_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoDetailsBinding.inflate(inflater, container, false);
        fillTextView();
        binding.btnDelete.setOnClickListener(c -> delete());
        binding.btnEdit.setOnClickListener(c -> edit());

        Date tripEndDate = currTrip.getEndDate();
        Date today = new Date();

        if (tripEndDate.before(today)) {
            binding.btnDelete.setVisibility(View.INVISIBLE);
            binding.btnEdit.setVisibility(View.INVISIBLE);
        }
        return binding.getRoot();
    }

    private void fillTextView() {
        binding.pointNameTV.setText(actPoint.getName());
        binding.tvPointDesc.setText(actPoint.getDescription());
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        binding.tvPontDate.setText(dateFormat.format(actPoint.getDeadline()));
        User responsible = actPoint.getUser();
        binding.tvPersonResponsible.setText(responsible.getName() + " " + responsible.getSurname());
        binding.cbDone.setChecked(actPoint.isDone());
    }

    private void delete() {
        new Thread(() -> {
            try {
                PreparationPointDao pointDao = new PreparationPointDao(BaseConnection.getConnectionSource());
                pointDao.delete(actPoint);
                Log.i("todo", "usunieto todo");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        NavHostFragment.findNavController(this).navigate(R.id.action_todoDetailsFragment_to_navigation_todo);
    }

    private void edit() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TODO_KEY, actPoint);
        NavHostFragment.findNavController(this).navigate(R.id.action_todoDetailsFragment_to_todoEditFragment, bundle);
    }
}