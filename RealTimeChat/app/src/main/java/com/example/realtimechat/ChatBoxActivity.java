package com.example.realtimechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.github.nkzawa.emitter.Emitter;

//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatBoxActivity extends AppCompatActivity {
    public RecyclerView myRecylerView ;
    public List<Message> MessageList ;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messagetxt ;
    public Button send ;
    public String Nickname ;
    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        messagetxt = (EditText) findViewById(R.id.message) ;
        send = (Button)findViewById(R.id.send);
        // get the nickame of the user
        Nickname= (String)getIntent().getExtras().getString(MainActivity.NICKNAME);
        //connect you socket client to the servertry {

//connect you socket client to the server

        try {
//if you are using a phone device you should connect to same local network as your laptop and disable your pubic firewall as well
            socket = IO.socket("http://192.168.0.107:3000");
//            socket = IO.socket("http://localhost:3000");
            socket.connect();
            //create connection

// emit the event join along side with the nickname
            socket.emit("new-user",Nickname);

        MessageList = new ArrayList<>();
        myRecylerView = (RecyclerView) findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());



        // message send action
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve the nickname and the message content and fire the event send-chat-message(!messagetxt.getText().toString().isEmpty()){
                socket.emit("send-chat-message",messagetxt.getText().toString());
                Message m = new Message(Nickname+": ",messagetxt.getText().toString());
                //add the message to the messageList
                MessageList.add(m);
                // add the new updated list to the dapter
                chatBoxAdapter = new ChatBoxAdapter(MessageList);
                // notify the adapter to update the recycler view
                chatBoxAdapter.notifyDataSetChanged();
                //set the adapter for the recycler view
                myRecylerView.setAdapter(chatBoxAdapter);
                messagetxt.setText(" ");
            }
        });

    //implementing socket listeners
        socket.on("user-connected", new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];
                    Toast.makeText(ChatBoxActivity.this, data, Toast.LENGTH_SHORT).show();
                }
            });
        }
        });

        socket.on("userdisconnect", new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];
                    Toast.makeText(ChatBoxActivity.this,data,Toast.LENGTH_SHORT).show();
                }
            });
        }
        });

        socket.on("chat-message", new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        //extract data from fired event
                        String nickname = data.getString("name");
                        String message = data.getString("msg");
                        Toast.makeText(ChatBoxActivity.this, nickname+": "+message,Toast.LENGTH_SHORT).show();
                        // make instance of message
                        Message m = new Message(nickname+": ",message);
                        //add the message to the messageList
                        MessageList.add(m);
                        // add the new updated list to the dapter
                        chatBoxAdapter = new ChatBoxAdapter(MessageList);
                        // notify the adapter to update the recycler view
                        chatBoxAdapter.notifyDataSetChanged();
                        //set the adapter for the recycler view
                        myRecylerView.setAdapter(chatBoxAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}