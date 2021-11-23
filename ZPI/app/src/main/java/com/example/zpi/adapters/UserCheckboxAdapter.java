package com.example.zpi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.zpi.R;
import com.example.zpi.models.Debt;
import com.example.zpi.models.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserCheckboxAdapter extends RecyclerView.Adapter<UserCheckboxAdapter.ViewHolder> {

    List<User> users;
    private ClickListener clickListener;
    List<User> debtUsers;

    public UserCheckboxAdapter(List<User> users, List<User> debtUsers) {
        this.users = users;
        this.debtUsers = debtUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_person_checkbox, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        CheckBox userCB = holder.user;

        userCB.setText(user.getName() + " " + user.getSurname());
        if(debtUsers != null){
            for(User us : debtUsers){
                if(us.getID() == user.getID()){
                    userCB.setChecked(true);
                }
            }
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CheckBox user;

        public ViewHolder(View itemView) {
            super(itemView);
            user = (CheckBox) itemView.findViewById(R.id.checkBox);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }


}
