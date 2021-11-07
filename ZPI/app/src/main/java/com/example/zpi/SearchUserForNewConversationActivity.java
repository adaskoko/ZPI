package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;

public class SearchUserForNewConversationActivity extends AppCompatActivity {

    public static final String NEW_CHAT_KEY="NEW_CHAT_KEY";
    User loggedUser;
    ImageButton searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_for_new_conversation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        searchButton=findViewById(R.id.btnSearch);
        searchButton.setOnClickListener(c->search());
    }

    public void finishSUFNC(View v){
        super.finish();
    }

    public void search(){

        EditText searchInput=(EditText) findViewById(R.id.etSearchPerson);
        String[] input=searchInput.getText().toString().split("\\s+");
        Toast.makeText(this, "search", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(this, MatchingUsersForChatActivity.class);
        intent.putExtra(NEW_CHAT_KEY, input);
        startActivity(intent);
        searchInput.getText().clear();
    }
}