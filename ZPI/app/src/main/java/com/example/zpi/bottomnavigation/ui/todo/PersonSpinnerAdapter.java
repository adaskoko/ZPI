package com.example.zpi.bottomnavigation.ui.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.zpi.R;
import com.example.zpi.models.User;

import java.util.List;

public class PersonSpinnerAdapter extends ArrayAdapter<User> {
    public PersonSpinnerAdapter(@NonNull Context context, @NonNull List<User> userList) {
        super(context, 0, userList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_spinner_row, parent, false);
        }

        TextView personName = convertView.findViewById(R.id.person_name_tv);

        User chosenUser = getItem(position);

        if (chosenUser != null) {
            String user = chosenUser.getName()+" "+chosenUser.getSurname()+" ("+chosenUser.getEmail()+")";
            personName.setText(user);
        }

        return convertView;
    }
}
