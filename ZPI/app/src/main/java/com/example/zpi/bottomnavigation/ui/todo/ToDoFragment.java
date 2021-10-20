package com.example.zpi.bottomnavigation.ui.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.databinding.FragmentToDoBinding;
import com.example.zpi.models.PreparationPoint;

import java.util.List;


public class ToDoFragment extends Fragment {

    private ToDoViewModel toDoViewModel;
    private FragmentToDoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        toDoViewModel = new ViewModelProvider(this).get(ToDoViewModel.class);
        binding = FragmentToDoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView todoRV = binding.recyclerViewToDo;
        todoRV.setLayoutManager(new LinearLayoutManager(getContext()));
        todoRV.setHasFixedSize(true);

        TodoRecyclerViewAdapter todoRecyclerViewAdapter = new TodoRecyclerViewAdapter();
        todoRV.setAdapter(todoRecyclerViewAdapter);

        toDoViewModel.getPreparationPointList().observe(getViewLifecycleOwner(), new Observer<List<PreparationPoint>>() {
            @Override
            public void onChanged(@Nullable List<PreparationPoint> list) {
                // update  list
                Toast.makeText(getContext(), "Updated list", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}