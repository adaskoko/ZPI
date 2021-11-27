package com.example.zpi;

import static com.example.zpi.ChatListActivity.CHAT_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchUserForNewConversationActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    User loggedUser;
    User chosenUser;
    ImageButton searchButton;

    ListView list;
    ListViewAdapter adapter;
    SearchView editSearch;
    ArrayList<User> arraylist = new ArrayList<>();
    TextView chosenUserTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_for_new_conversation);

        list = findViewById(R.id.listview);
        chosenUserTV = findViewById(R.id.chosen_user);

        new Thread(() -> {
            try {
                UserDao userDao = new UserDao(BaseConnection.getConnectionSource());
                ArrayList<User> al = (ArrayList<User>) userDao.getAllUsers();
                runOnUiThread(() -> {
                    this.arraylist = al;
                    adapter = new ListViewAdapter(this, arraylist);
                    list.setAdapter(adapter);
                    editSearch = findViewById(R.id.search);
                    editSearch.setOnQueryTextListener(this);
                    editSearch.setOnClickListener(v -> editSearch.setIconified(false));
                    adapter.notifyDataSetChanged();
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        list.setOnItemClickListener((arg0, arg1, position, arg3) -> {
            User data = (User) arg0.getAdapter().getItem(position);
            chosenUserTV.setText(data.getName() + " " + data.getSurname());
            chosenUser = data;
            list.setVisibility(View.GONE);
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        list.setVisibility(View.VISIBLE);
        adapter.filter(newText);
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        searchButton = findViewById(R.id.btnSearch);
        searchButton.setOnClickListener(arg -> {
            Intent i = new Intent(SearchUserForNewConversationActivity.this, ChatActivity.class);
            i.putExtra(CHAT_KEY, chosenUser);
            startActivity(i);
        });
    }

    public void finishSUFNC(View v) {
        super.finish();
    }

    public static class ListViewAdapter extends BaseAdapter {

        Context mContext;
        LayoutInflater inflater;
        private final List<User> users;
        private final ArrayList<User> arraylist;

        public ListViewAdapter(Context context, List<User> users) {
            mContext = context;
            this.users = users;
            inflater = LayoutInflater.from(mContext);
            this.arraylist = new ArrayList<>();
            this.arraylist.addAll(users);
        }

        public static class ViewHolder {
            TextView name;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public User getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.user_item, null);
                holder.name = view.findViewById(R.id.name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.name.setText(users.get(position).getName() + " " + users.get(position).getSurname());
            return view;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            users.clear();
            if (charText.length() == 0) {
                users.addAll(arraylist);
            } else {
                for (User wp : arraylist) {
                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        users.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }


    }
}