package com.example.zpi;

import static com.example.zpi.ChatListActivity.CHAT_KEY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Message;
import com.example.zpi.models.User;
import com.example.zpi.repositories.MessageDao;
import com.example.zpi.repositories.UserDao;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {

    User loggedUser;
    User otherUser;
    RecyclerView rvMessages;
    MessageListAdapter messageListAdapter;
    EditText text;
    ImageButton buttonSend;
    private static final int REFRESH_TIME=30000;//30s
    TextView otherUserName;
    TextView initials;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        otherUser=(User) getIntent().getSerializableExtra(CHAT_KEY);
        text=findViewById(R.id.et_message);
        buttonSend=findViewById(R.id.btn_send);
        buttonSend.setOnClickListener(c->sendMessage());
        otherUserName=findViewById(R.id.tv_other_name);
        otherUserName.setText(otherUser.getName()+" "+ otherUser.getSurname());

        String sInitials=otherUser.getName().substring(0,1).toUpperCase()+otherUser.getSurname().substring(0,1).toUpperCase();
        initials=findViewById(R.id.tv_initialsCircle);
        initials.setText(sInitials);

        rvMessages=findViewById(R.id.rv_chat);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        displayChat();

        /*Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayChat();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();*/
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){

                displayChat();
            }
        }, 0, 60000);

    }

    public void displayChat(){
        final List<Message> returnList=new ArrayList<>();
        new Thread(()->{
            try {
                MessageDao mdao = new MessageDao(BaseConnection.getConnectionSource());
                UserDao userDao = new UserDao(BaseConnection.getConnectionSource());
                List<Message> results = mdao.getMessagesForConvo(loggedUser, otherUser);
                if (results != null) {
                    for (Message m : results) {
                        returnList.add(m);
                    }
                }

                runOnUiThread(()->{
                    MessageListAdapter adapter=new MessageListAdapter(this,returnList);
                    rvMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    rvMessages.scrollToPosition(returnList.size()-1);
                });

            }catch(SQLException throwables){
                throwables.printStackTrace();
            }
        }).start();
    }

    public void finishChat(){super.finish();}

    public void sendMessage(){
        String content=text.getText().toString();
        User sender=loggedUser;
        User receiver=otherUser;
        Date sendDate=new Date();

        new Thread(()->{
            try{
                MessageDao mdao=new MessageDao(BaseConnection.getConnectionSource());
                Message currentMessage=new Message(content, sendDate, sender, receiver);
                mdao.create(currentMessage);
                displayChat();//refresh ui
                text.getText().clear();
            }catch(SQLException throwables){
                throwables.printStackTrace();
            }

        }).start();

    }

    private class MessageListAdapter extends RecyclerView.Adapter{

        private static  final int VIEW_TYPE_MESSAGE_SENT=1;
        private static final int VIEW_TYPE_MESSAGE_RECEIVED=2;
        private Context context;
        private List<Message> messageList;

        public MessageListAdapter(Context context, List<Message> messageList){
            this.context=context;
            this.messageList=messageList;
        }

        @Override
        public int getItemCount(){
            return messageList.size();
        }

        @Override
        public int getItemViewType(int position){
            Message message=(Message) messageList.get(position);

            if(message.getSender().getID()==loggedUser.getID()){
                return VIEW_TYPE_MESSAGE_SENT;
            }else{
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view;

            if(viewType==VIEW_TYPE_MESSAGE_SENT){
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent, parent, false);
                return new SentMessageHolder(view);
            }else if (viewType==VIEW_TYPE_MESSAGE_RECEIVED){
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);
                return new ReceivedMessageHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
            Message message=messageList.get(position);
            Message prevMessage = null;
            if (position!=0) prevMessage=messageList.get(position-1);
            switch (holder.getItemViewType()){
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SentMessageHolder) holder).bind(message, prevMessage);
                    break;
                case  VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder) holder).bind(message, prevMessage);
            }
        }

        private boolean isSameDay(Date firstDate, Date secondDate){
            if (firstDate.getYear() != secondDate.getYear()) return false;
            if (firstDate.getMonth() != secondDate.getMonth()) return false;
            return firstDate.getDate() == secondDate.getDate();
        }

        private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
            TextView messageText;
            TextView timeText;
            TextView nameText;
            TextView dateText;


            ReceivedMessageHolder(View itemView){
                super(itemView);
                messageText=(TextView) itemView.findViewById(R.id.tv_message_other);
                timeText=itemView.findViewById(R.id.tv_time_other);
                nameText=itemView.findViewById(R.id.tv_username_other);
                dateText=itemView.findViewById(R.id.tv_date_other);
            }

            void bind(Message message, Message prevMessage){
                if (prevMessage!= null && isSameDay(prevMessage.getSendingDate(),message.getSendingDate())) {
                    dateText.setVisibility(View.GONE);
                }
                messageText.setText(message.getContent());
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                DateFormat dateFormat1=new SimpleDateFormat("dd.MM");
                String time = dateFormat.format(message.getSendingDate());
                String date=dateFormat1.format(message.getSendingDate());
                timeText.setText(time);
                nameText.setText(otherUser.getName());
                dateText.setText(date);
            }
        }

        private class SentMessageHolder extends RecyclerView.ViewHolder{
            TextView messageText;
            TextView timeText;
            TextView dateText;

            SentMessageHolder(View itemView){
                super(itemView);

                messageText=itemView.findViewById(R.id.tv_message_loggeduser);
                timeText=itemView.findViewById(R.id.tv_time_loggeduser);
                dateText=itemView.findViewById(R.id.tv_date_loggeduser);
            }

            void bind(Message message, Message prevMessage){
                if (prevMessage != null && isSameDay(prevMessage.getSendingDate(),message.getSendingDate())) {
                    dateText.setVisibility(View.GONE);
                }
                messageText.setText(message.getContent());
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                DateFormat dateFormat1=new SimpleDateFormat("dd.MM");
                String time = dateFormat.format(message.getSendingDate());
                String date=dateFormat1.format(message.getSendingDate());
                timeText.setText(time);
                dateText.setText(date);
            }
        }
    }
}