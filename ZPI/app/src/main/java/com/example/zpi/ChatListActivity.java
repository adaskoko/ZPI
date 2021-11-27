package com.example.zpi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Message;
import com.example.zpi.models.User;
import com.example.zpi.repositories.MessageDao;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatListActivity extends AppCompatActivity {

    public final static String CHAT_KEY="CHAT_KEY";
    RecyclerView recyclerView;
    List<User> list=new ArrayList<>();
    ChatListAdapter chatListAdapter;
    User loggedUser;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerView=findViewById(R.id.rvChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        getChatsInitially();
        search=findViewById(R.id.et_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){

                getChatsForUser();
            }
        }, 0, 60000);

    }

    @Override
    protected void onResume() {

        super.onResume();
        getChatsForUser();
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                getChatsForUser();
            }
        }, 0, 60000);
    }

    public void finish(View v){
        super.finish();
    }

    public void newMessage(View v){
        Intent i = new Intent(this, SearchUserForNewConversationActivity.class);
        startActivity(i);
    }

    private void filter(String text){
        ArrayList<User> filteredList=new ArrayList<>();
        for(User user: list){
            String fullName = user.getName()+" "+user.getSurname();
            if(fullName.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(user);
            }
        }

        chatListAdapter.filterList(filteredList);
    }

    private void getChatsInitially(){
        Map<Integer, Message> dictionary =new HashMap<Integer, Message>();
        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Pobieranie danych...");
        progressDialog.show();
        new Thread(()->{
            try{
                ArrayList<User> convos = new ArrayList<>();
                MessageDao mdao=new MessageDao(BaseConnection.getConnectionSource());
                List<User> res=mdao.getConvosForUser(loggedUser);
                if(res.size()!=0){
                    for(User user :res){
                        convos.add(user);
                        List<Message> mes=mdao.getMessagesForConvo(loggedUser, user);
                        Message latest=mdao.getLastMessageFromList(mes);
                        dictionary.put(user.getID(), latest);
                    }
                }
                list = convos;
                runOnUiThread(()->{
                    ChatListAdapter adapter=new ChatListAdapter(list, dictionary);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                });
            }catch(SQLException throwables){
                throwables.printStackTrace();
                progressDialog.dismiss();
            }
        }).start();
    }

    private void getChatsForUser(){
        Map<Integer, Message> dictionary =new HashMap<Integer, Message>();
        new Thread(()->{
            try{
                ArrayList<User> convos = new ArrayList<>();
                MessageDao mdao=new MessageDao(BaseConnection.getConnectionSource());
                List<User> res=mdao.getConvosForUser(loggedUser);
                if(res.size()!=0){
                  for(User user :res){
                      convos.add(user);
                      List<Message> mes=mdao.getMessagesForConvo(loggedUser, user);
                      Message latest=mdao.getLastMessageFromList(mes);
                      dictionary.put(user.getID(), latest);
                  }
                }
                list = convos;
                runOnUiThread(()->{
                    ChatListAdapter adapter=new ChatListAdapter(list, dictionary);
                    this.chatListAdapter = adapter;
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            }catch(SQLException throwables){
                throwables.printStackTrace();
            }
        }).start();;
    }


    private class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListAdapterVh> implements Filterable{

        private List<User> chatFriendsForUser;
        private Map<Integer, Message> content;
        private Context context;

        public ChatListAdapter(List<User> list, Map<Integer, Message> map){
            this.chatFriendsForUser=list;
            this.content=map;
        }

        @Override
        public Filter getFilter() {
            return null;
        }

        @NonNull
        @Override
        public ChatListAdapter.ChatListAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context=parent.getContext();
            //return new ChatListAdapterVh(LayoutInflater.from(context).inflate(R.layout.row_chat, null));
            View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
            return new ChatListAdapterVh(itemView);
        }

        public void filterList(List<User> filtered){
            chatFriendsForUser=filtered;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull ChatListAdapter.ChatListAdapterVh holder, int position) {
            User user=chatFriendsForUser.get(position);
            Message current=content.get(user.getID());
            holder.user=user;
            User receiver=current.getReceiver();

            String initials=user.getName().substring(0,1).toUpperCase()+user.getSurname().substring(0,1).toUpperCase();
            String name=user.getName()+" "+ user.getSurname();

            holder.tvName.setText(name);
            holder.tvInitials.setText(initials);
            String mes=current.getContent();

            holder.tvMsg.setText(mes);
            if(current.isRead()==false && loggedUser.getID()==receiver.getID()){
                holder.tvMsg.setTypeface(holder.tvMsg.getTypeface(), Typeface.BOLD);
                holder.tvNew.setVisibility(View.VISIBLE);
                Log.i("msg", "is not read");
            }

            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String date = dateFormat.format(current.getSendingDate());
            holder.tvTime.setText(date);

        }


        @Override
        public int getItemCount() {
            return chatFriendsForUser.size();
        }

        private class ChatListAdapterVh extends RecyclerView.ViewHolder{
            private User user;
            TextView tvInitials;
            TextView tvName;
            TextView tvMsg;
            TextView tvTime;
            TextView tvNew;

            public ChatListAdapterVh(@NonNull View itemView){
                super(itemView);
                tvInitials= itemView.findViewById(R.id.tvInitials);
                tvName=itemView.findViewById(R.id.tvName);
                tvMsg=itemView.findViewById(R.id.tvMsg);
                tvTime=itemView.findViewById(R.id.tvTime);
                tvNew=itemView.findViewById(R.id.tvNew);
                tvNew.setVisibility(View.INVISIBLE);


                itemView.setOnClickListener(v -> {
                    User u=chatFriendsForUser.get(getAbsoluteAdapterPosition());
                    Intent i=new Intent(ChatListActivity.this,ChatActivity.class);
                    i.putExtra(CHAT_KEY, u);
                    startActivity(i);
                });
            }
        }
    }
}