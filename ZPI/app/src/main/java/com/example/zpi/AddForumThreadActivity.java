package com.example.zpi;

import static com.example.zpi.ForumListActivity.THREAD_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Comment;
import com.example.zpi.models.ForumThread;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.CommentDao;
import com.example.zpi.repositories.ForumThreadDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AddForumThreadActivity extends AppCompatActivity {

    int LAUNCH_CHOOSE_PLAN_ACTIVITY = 1;
    String resultStringForThread;
    User loggedUser;
    TextView initials;
    TextView name;
    TextView tripName;
    TextView assignedElement;
    EditText threadName;
    EditText message;
    Trip currentTrip;
    RequestQueue mRequestQueue;
    private String URL="https://fcm.googleapis.com/fcm/send";
    private String serverKey="key="+"AAAATTz1BGM:APA91bFqP2Xnkl67JXawBGQ0tpMGiQFH9QPz1yBVYV6x5LT1_DOCUmCseexqFC0guffW7qXN_ke0DgOTujrRRmYw6CijP4H0cG4VpA8Rk6bf6ovPejnRfU8dRlCbzAQhyc6ZkPZCNljY";
    private String contentType= "application/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forum_thread);
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());

        String sInitials=loggedUser.getName().substring(0,1).toUpperCase()+loggedUser.getSurname().substring(0,1).toUpperCase();
        initials=findViewById(R.id.tv_initials);
        initials.setText(sInitials);

        String nameSurname= "Ty ("+ loggedUser.getName()+" "+ loggedUser.getSurname()+")";
        name=findViewById(R.id.tv_personName);
        name.setText(nameSurname);

        threadName=findViewById(R.id.et_threadTitle);
        message=findViewById(R.id.et_comment);

        currentTrip= (Trip) getIntent().getSerializableExtra(THREAD_KEY);

        tripName = findViewById(R.id.tv_nameOfTrip);
        tripName.setText(currentTrip.getName());

        assignedElement = findViewById(R.id.tv_assignedElement);
    }

    public void chooseList(View v){

        Intent intent=new Intent(this, ChooseListForThreadActivity.class);
        startActivityForResult(intent, LAUNCH_CHOOSE_PLAN_ACTIVITY);
    }

    private String getTopicName(){
        String tripname=currentTrip.getName();
        return tripname.replaceAll("\\s+","");
    }
    private void sendNotification() throws JSONException {
        mRequestQueue= Volley.newRequestQueue(this);
        JSONObject main=new JSONObject();
        main.put("to", "/topics/"+getTopicName());
        JSONObject sub=new JSONObject();
        sub.put("title", "UWAGA");
        sub.put("message", "Dodano nowy wątek w: "+ currentTrip.getName());
        main.put("data", sub);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //oast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header=new HashMap<>();
                header.put("content-type",contentType );
                header.put("authorization", serverKey);
                return header;
            }
        };
        mRequestQueue.add(request);
    }

    public void createThread(View view){
        String sTitle=threadName.getText().toString();
        String userMsg=message.getText().toString();

        if(resultStringForThread!=null && sTitle!=null && userMsg!=null){
            new Thread(()->{
                try{
                    ForumThreadDao ftdao=new ForumThreadDao(BaseConnection.getConnectionSource());
                    ftdao.createRegulatThread(resultStringForThread, sTitle, currentTrip);
                    ForumThread created=ftdao.getByName(sTitle);
                    CommentDao cdao=new CommentDao(BaseConnection.getConnectionSource());
                    cdao.create(new Comment(userMsg, created, loggedUser));
                    sendNotification();
                    super.finish();
                }catch(SQLException | JSONException throwables){
                    throwables.printStackTrace();
                }
            }).start();
        }else{
            Toast.makeText(this, "Proszę uzupełnić wszystkie pola!", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel(View v){
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_CHOOSE_PLAN_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                resultStringForThread=data.getStringExtra("result");
                assignedElement.setText(resultStringForThread);
                assignedElement.setTextColor(Color.parseColor("#96D76F"));
                Toast.makeText(this, resultStringForThread, Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Proszę przypisać element", Toast.LENGTH_SHORT).show();
            }
        }
    }
}