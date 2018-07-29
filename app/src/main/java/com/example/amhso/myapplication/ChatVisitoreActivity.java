package com.example.amhso.myapplication;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatVisitoreActivity extends AppCompatActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    public WebSocketClient mWebSocketClient;
    public int conditionConnect=0;
    public int id_order;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);




        Bundle bundle = this.getIntent().getExtras();
        if(bundle!= null)
        {
           id_order =  this.getIntent().getExtras().getInt("id_order");

        }



        SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

        String UserId = sharedpreferences.getString(G.id_user, "");



        connectWebSocket(UserId);
        initControls();
    }








    private void initControls() {


        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("راننده");// Hard Coded
        loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }



                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");

                displayMessage(chatMessage);

                SharedPreferences sharedpreferences = getSharedPreferences(G.MyPREFERENCES, Context.MODE_PRIVATE);

                String UserId = sharedpreferences.getString(G.id_user, "");


//                try {
//                    messageText=URLEncoder.encode(messageText, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                try {
                    mWebSocketClient.send("SendMessageClient+" + UserId + "+" + id_order + "+" + messageText);

                }
                catch (Exception q){

                }

//                if(UserId.equals("34"))
//
//                     mWebSocketClient.send("TypeChat+"+34+"+"+32+"+"+messageText);
//
//                if(UserId.equals("32"))
//                     mWebSocketClient.send("TypeChat+"+32+"+"+34+"+"+messageText);

            }
        });
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();



        try {
            mWebSocketClient.close();

        }
        catch (Exception q){

        }

    }



//    @Override
//    protected void onStop()
//    {
//        super.onStop();
//
//
//
//
//        if(conditionConnect==1){
//
//
//            mWebSocketClient.close();
//
//
//        }
//
//
//    }

//
//    @Override
//    public void onResume(){
//        super.onResume();
//
//
//
//
//
//
//    }


    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }









    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }







    private void loadDummyHistory(){

        chatHistory = new ArrayList<ChatMessage>();

//        ChatMessage msg = new ChatMessage();
//        msg.setId(1);
//        msg.setMe(false);
//        msg.setMessage("Hi");
//        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//        chatHistory.add(msg);
//
//
//
//        ChatMessage msg1 = new ChatMessage();
//        msg1.setId(2);
//        msg1.setMe(false);
//        msg1.setMessage("How r u doing???");
//        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//        chatHistory.add(msg1;)

        adapter = new ChatAdapter(ChatVisitoreActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }
    }



    private void connectWebSocket(String $id_user) {
        URI uri;
        final String $Userid=$id_user;


        try {
            uri = new URI(G.urlsocket2);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Open" );

                conditionConnect=1;
                try {
                    mWebSocketClient.send("FetchMessageClient+" + $Userid+"+"+id_order);

                }
                catch (Exception q){

                }
            }

            @Override
            public void onMessage(String s) {
                final String message = s;

//                Toast.makeText(ChatVisitoreActivity.this, message, Toast.LENGTH_LONG).show();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



//                        Toast.makeText(ChatVisitoreActivity.this, message, Toast.LENGTH_LONG).show();


//                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();


                        String sss="";
                        try {
                            sss= URLDecoder.decode(message, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String ss2[]=sss.split("~");

                        if(ss2[0].equals(""+id_order)){
                            ChatMessage msg1 = new ChatMessage();
                            msg1.setId(1);
                            msg1.setMe(false);
                            msg1.setMessage(ss2[1]);
                            msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                            chatHistory.add(msg1);

                            displayMessage(msg1);
                        }



                        try {
                                JSONObject jsonObj = new JSONObject(message);

                                // Getting JSON Array node
                                JSONArray contacts = jsonObj.getJSONArray("contacts");

                                // looping through All Contacts
                                for (int i = 0; i < contacts.length(); i++) {


                                    JSONObject c = contacts.getJSONObject(i);
                                    String id = c.getString("id");
                                    final String message_fetch = c.getString("message");
                                    final String client_server = c.getString("client_server");



                                    String ss="";
                                    try {
                                        ss= URLDecoder.decode( message_fetch, "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    if (client_server.equals("1")) {
                                        ChatMessage msg1 = new ChatMessage();
                                        msg1.setId(2);
                                        msg1.setMe(true);
                                        msg1.setMessage(ss);
                                        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                                        chatHistory.add(msg1);

                                        displayMessage(msg1);
                                    } else {
                                        ChatMessage msg1 = new ChatMessage();
                                        msg1.setId(1);
                                        msg1.setMe(false);
                                        msg1.setMessage(ss);
                                        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                                        chatHistory.add(msg1);

                                        displayMessage(msg1);
                                    }


                                }
                            } catch (final JSONException e) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {




                                    }
                                });

                            }




                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                conditionConnect=0;



            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };



        try {
            mWebSocketClient.connect();

        }
        catch (Exception a){

        }


    }
}
