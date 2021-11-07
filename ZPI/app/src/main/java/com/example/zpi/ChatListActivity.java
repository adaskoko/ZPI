package com.example.zpi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Message;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories.MessageDao;
import com.example.zpi.repositories.TripPartcicipantDao;
import com.example.zpi.repositories.TripParticipantDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {

    public final static String CHAT_KEY="CHAT_KEY";
    RecyclerView recyclerView;
    List<User> chatWith=new ArrayList<User>();
    ChatListAdapter chatListAdapter;
    User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerView=findViewById(R.id.rvChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        getChatsForUser();

        //chatWith.add(new User("Aleksandra", "Rzepecka", "123"));
        //chatWith.add(new User("Jan", "Kowalski", "345"));
        //Log.i("tag", "Początkowa lista: "+ chatWith.size());
        //for(User u:chatWith){
           // Log.i("user z listy", String.valueOf(u.getSurname()));
        //}
        //chatWith=getSomeUsers();
        //for(User u:chatWith){
           // Log.i("user z listy", String.valueOf(u.getSurname()));
       // }


        //chatListAdapter=new ChatListAdapter(chatWith);
        //recyclerView.setAdapter(chatListAdapter);
        //getSomeUsers();
    }

    public void finish(View v){
        super.finish();
    }

    public void newMessage(View v){
        Intent i = new Intent(this, SearchUserForNewConversationActivity.class);
        startActivity(i);
    }

    private void getChatsForUser(){
        List<User> list=new ArrayList<>();
        Map<Integer, Message> dictionary =new HashMap<Integer, Message>();

        new Thread(()->{
            try{
                MessageDao mdao=new MessageDao(BaseConnection.getConnectionSource());
                //User u= (User) new UserDao(BaseConnection.getConnectionSource()).queryForEq("ID", 24).get(0);
                List<User> res=mdao.getConvosForUser(loggedUser);
                if(res.size()!=0){
                  for(User user :res){
                      list.add(user);
                      //Log.i("dao z convosow", String.valueOf(user.getEmail()));
                      List<Message> mes=mdao.getMessagesForConvo(loggedUser, user);
                      //Log.i("mes z dao", String.valueOf(mes.size()));
                      Message latest=mdao.getLastMessageFromList(mes);
                      dictionary.put(user.getID(), latest);
                  }
                }
                runOnUiThread(()->{
                    ChatListAdapter adapter=new ChatListAdapter(list, dictionary);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });

            }catch(SQLException throwables){
                throwables.printStackTrace();
            }
        }).start();;
    }

    //just a test method, please do not delete
    private List<User> getSomeUsers(){
        List<User> ret=new ArrayList<>();
        new Thread(() -> {
            try {
                TripParticipantDao tpDao=new TripParticipantDao(BaseConnection.getConnectionSource());
                UserDao userDao=new UserDao(BaseConnection.getConnectionSource());
                List<TripParticipant> tripParticipants=tpDao.getForFirstTrip();
                User u;
                if(tripParticipants!=null && tripParticipants.size()!=0) {
                    for (TripParticipant tp:tripParticipants) {
                        u=tp.getUser();
                        userDao.refresh(u);
                        ret.add(u);
                    }
                }
                //runOnUiThread(()->{ChatListAdapter adapter=new ChatListAdapter(ret);
                    //recyclerView.setAdapter(adapter);
                   //adapter.notifyDataSetChanged();});

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        return ret;
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

        @Override
        public void onBindViewHolder(@NonNull ChatListAdapter.ChatListAdapterVh holder, int position) {
            User user=chatFriendsForUser.get(position);
            Message current=content.get(user.getID());
            holder.user=user;

            String initials=user.getName().substring(0,1).toUpperCase()+user.getSurname().substring(0,1).toUpperCase();
            String name=user.getName()+" "+ user.getSurname();

            holder.tvName.setText(name);
            holder.tvInitials.setText(initials);
            String mes="";

            holder.tvMsg.setText(mes);

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

            public ChatListAdapterVh(@NonNull View itemView){
                super(itemView);
                tvInitials= itemView.findViewById(R.id.tvInitials);
                tvName=itemView.findViewById(R.id.tvName);
                tvMsg=itemView.findViewById(R.id.tvMsg);
                tvTime=itemView.findViewById(R.id.tvTime);


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