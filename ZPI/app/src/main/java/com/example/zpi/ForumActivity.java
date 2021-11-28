package com.example.zpi;

import static com.example.zpi.ForumListActivity.THREAD_KEY;
import static com.example.zpi.SingleTripFragment.TRIP_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Comment;
import com.example.zpi.models.ForumThread;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.CommentDao;
import com.example.zpi.repositories.ForumThreadDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ForumActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    User loggedUser;
    ForumThread current;
    TextView trip;
    TextView thread;
    TextView thread2;
    Trip currentTrip;
    ConstraintLayout response;
    ImageView addResponse;
    ImageView cancel1;
    ImageView respond;
    TextView respCount;
    TextView initials;
    TextView name;
    View darkened;
    TextView assignedElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        recyclerView=findViewById(R.id.rv_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        trip=findViewById(R.id.tv_nameOfTrip1);
        thread=findViewById(R.id.tv_nameOfThread);
        thread2=findViewById(R.id.tv_nameOfThread2);
        respond=findViewById(R.id.respond);
        darkened = findViewById(R.id.darkenedView);

        current=(ForumThread) getIntent().getSerializableExtra(THREAD_KEY);
        thread.setText(current.getTitle());
        thread2.setText(current.getTitle());

        assignedElement = findViewById(R.id.tv_assignedElement2);
        assignedElement.setText(current.getSummary());

        currentTrip=(Trip) getIntent().getSerializableExtra(TRIP_KEY);
        trip.setText(currentTrip.getName());

        response=findViewById(R.id.response_layout);

        addResponse=findViewById(R.id.btn_send_response);
        cancel1=findViewById(R.id.btn_cancel1);

        hideResponseLayout();

        respCount=findViewById(R.id.tv_respCount);

        initials=findViewById(R.id.tv_initials3);
        name=findViewById(R.id.tv_personName3);
        initials.setText(loggedUser.getName().substring(0,1).toUpperCase()+loggedUser.getSurname().substring(0,1).toUpperCase());
        name.setText(loggedUser.getName()+" "+ loggedUser.getSurname());

        getCommentsForThreadInitially();
        getResponseCount();

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){

                getCommentsForThread();
                getResponseCount();
            }
        }, 0, 60000);

    }

    public void hideResponseLayout(){
        response.setVisibility(View.GONE);
        addResponse.setVisibility(View.GONE);
        cancel1.setVisibility(View.GONE);
        darkened.setVisibility(View.GONE);
    }

    public void getCommentsForThreadInitially(){
        List<Comment> list=new ArrayList<>();
        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Pobieranie danych...");
        progressDialog.show();
        new Thread(()->{
            try{
                ForumThreadDao ftdao=new ForumThreadDao(BaseConnection.getConnectionSource());
                UserDao udao=new UserDao(BaseConnection.getConnectionSource());

                List<Comment> coms=ftdao.getCommentsForThread(current);
                if(coms.size()!=0){
                    for(Comment c:coms){
                        list.add(c);
                        User u=c.getUser();
                        udao.refresh(u);
                    }
                }
                runOnUiThread(()->{
                    ForumAdapter adapter=new ForumAdapter(list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                });
            }catch (SQLException throwables){
                throwables.printStackTrace();
                progressDialog.dismiss();
            }
        }).start();
    }

    public void getCommentsForThread(){
        List<Comment> list=new ArrayList<>();
        new Thread(()->{
            try{
                ForumThreadDao ftdao=new ForumThreadDao(BaseConnection.getConnectionSource());
                UserDao udao=new UserDao(BaseConnection.getConnectionSource());

                List<Comment> coms=ftdao.getCommentsForThread(current);
                if(coms.size()!=0){
                    for(Comment c:coms){
                        list.add(c);
                        User u=c.getUser();
                        udao.refresh(u);
                    }
                }
                runOnUiThread(()->{
                    ForumAdapter adapter=new ForumAdapter(list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            }catch (SQLException throwables){
                throwables.printStackTrace();
            }
        }).start();
    }

    public void getResponseCount(){
        new Thread(()->{
            try{
                ForumThreadDao ftdao=new ForumThreadDao(BaseConnection.getConnectionSource());
                int resps = ftdao.getResponsesCount(current);
                runOnUiThread(()->{
                    if (resps == 1) respCount.setText(resps + " odpowiedź");
                    else respCount.setText(resps + " odpowiedzi");
                });
            }catch(SQLException throwables){
                throwables.printStackTrace();
            }
        }).start();
    }

    public void respond(View v) {
        response.setVisibility(View.VISIBLE);
        addResponse.setVisibility(View.VISIBLE);
        cancel1.setVisibility(View.VISIBLE);
        darkened.setVisibility(View.VISIBLE);
    }

    public void addResponse(View v){
        EditText content= findViewById(R.id.et_comment3);
        if(content!=null && !content.equals("")){
            new Thread(()->{
                try{
                    CommentDao cdao=new CommentDao(BaseConnection.getConnectionSource());
                    cdao.create(new Comment(content.getText().toString(), current, loggedUser));
                    runOnUiThread(()->{
                        content.getEditableText().clear();
                        getCommentsForThread();
                        getResponseCount();
                        hideResponseLayout();
                    });
                }catch (SQLException throwables){
                    throwables.printStackTrace();
                }
            }).start();
        }
    }

    public void cancelResp(View v){
        response.setVisibility(View.GONE);
        addResponse.setVisibility(View.GONE);
        cancel1.setVisibility(View.GONE);
        darkened.setVisibility(View.GONE);
    }

    public void deleteComment(Comment comment){
        new Thread(() -> {
            try {
                CommentDao cDao = new CommentDao(BaseConnection.getConnectionSource());
                cDao.delete(comment);
                runOnUiThread(()->{
                    getCommentsForThread();
                    getResponseCount();
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    private class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumVh> {


        private List<Comment> comments;

        public ForumAdapter(List<Comment> list){
            this.comments=list;
        }

        @NonNull
        @Override
        public ForumAdapter.ForumVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_in_forum,parent, false);
            return new ForumVh(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ForumVh holder, int position){
            Comment comment=comments.get(position);
            User u=comment.getUser();
            new Thread(()->{
                try {
                    UserDao udao=new UserDao(BaseConnection.getConnectionSource());
                    udao.refresh(u);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }).start();


            String personInitials=u.getName().substring(0,1).toUpperCase()+u.getSurname().substring(0,1).toUpperCase();
            String personName=u.getName()+ " "+u.getSurname();
            holder.initials.setText(personInitials);
            holder.person.setText(personName);
            holder.comment.setText(comment.getContent());
            if(u.getID()==loggedUser.getID()){
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(c->deleteComment(comment));
            }
        }

        @Override
        public int getItemCount(){
            return comments.size();
        }

        private class ForumVh extends RecyclerView.ViewHolder{
            TextView comment;
            TextView initials;
            TextView person;
            TextView btnDelete;


            public ForumVh(@NonNull View itemView){
                super(itemView);
                comment=itemView.findViewById(R.id.tv_comment);
                initials=itemView.findViewById(R.id.tv_initials1);
                person=itemView.findViewById(R.id.tv_personName1);
                btnDelete=itemView.findViewById(R.id.deleteComment);
                btnDelete.setVisibility(View.GONE);
            }
        }
    }
}