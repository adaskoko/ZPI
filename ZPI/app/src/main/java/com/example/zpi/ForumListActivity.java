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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.ForumThread;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.ForumThreadDao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.zpi.SingleTripFragment.TRIP_KEY;

public class ForumListActivity extends AppCompatActivity {

    public final static String THREAD_KEY="THREAD_KEY";
    User loggedUser;
    RecyclerView recyclerView;
    List<ForumThread> threads;
    ForumListAdapter forumListAdapter;
    Trip currentTrip;
    TextView tripname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_list);
        recyclerView=findViewById(R.id.rvThreads);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        currentTrip= (Trip) getIntent().getSerializableExtra(TRIP_KEY);
        tripname = findViewById(R.id.tv_nameOfTheTrip);
        tripname.setText(currentTrip.getName());
        getThreadsForTrip();

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){

                getThreadsForTrip();
            }
        }, 0, 60000);

    }

    @Override
    public void onResume(){
        super.onResume();
        getThreadsForTrip();

    }

    public void finishForumList(View v){
        super.finish();
    }

    public void newThread(View v){
        Intent intent =new Intent(this, AddForumThreadActivity.class);
        intent.putExtra(THREAD_KEY, currentTrip);
        startActivity(intent);

    }

    public void getThreadsForTrip(){
        new Thread(()->{
            try{
                Map<Integer, Integer> threadsResopnses=new HashMap<>();
                ForumThreadDao ftdao=new ForumThreadDao(BaseConnection.getConnectionSource());
                List<ForumThread> threads=ftdao.getThreadsForTrip(currentTrip);
                for(ForumThread ft : threads){
                    threadsResopnses.put(ft.getID(), ftdao.getResponsesCount(ft));
                }
                runOnUiThread(()->{
                    ForumListAdapter adapter=new ForumListAdapter(threads, threadsResopnses);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            }catch(SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    private class ForumListAdapter extends RecyclerView.Adapter<ForumListAdapter.ForumListAdapterVh> implements Filterable {

        private List<ForumThread> threadsForList;
        private Map<Integer, Integer> map;

        private Context context;

        public ForumListAdapter(List<ForumThread> list, Map<Integer, Integer> map){
            this.threadsForList=list;
            this.map=map;
        }

        @Override
        public Filter getFilter(){
            return null;
        }

        @NonNull
        @Override
        public ForumListAdapter.ForumListAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int position){
            context=parent.getContext();
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_label, parent, false);
            return new ForumListAdapterVh(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ForumListAdapter.ForumListAdapterVh holder, int position){
            ForumThread thread=threadsForList.get(position);
            //String planName=content.get(thread.getID());
            holder.tvPlanType.setText(thread.getSummary());
            holder.tvSummary.setText(thread.getTitle());
            String responses=String.valueOf(map.get(thread.getID()))+" odpowiedzi";
            Log.i("ODP:", responses);
            holder.responses.setText(responses);

        }

        @Override
        public int getItemCount(){
            return threadsForList.size();
        }

        private class ForumListAdapterVh extends RecyclerView.ViewHolder{
            TextView tvSummary;//title
            TextView tvPlanType;//summary
            TextView responses;

            public ForumListAdapterVh(@NonNull View itemView){
                super(itemView);
                tvSummary=itemView.findViewById(R.id.tv_threadName);//title
                tvPlanType=itemView.findViewById(R.id.tv_planName);//summary
                responses=itemView.findViewById(R.id.tv_responses);

                itemView.setOnClickListener(v->{
                    Toast.makeText(ForumListActivity.this,tvSummary.getText(), Toast.LENGTH_SHORT).show();
                    ForumThread ft=threadsForList.get(getAbsoluteAdapterPosition());
                    Intent intent=new Intent(ForumListActivity.this, ForumActivity.class);
                    intent.putExtra(THREAD_KEY, ft);
                    intent.putExtra(TRIP_KEY, currentTrip);
                    startActivity(intent);
                });
            }
        }
    }
}