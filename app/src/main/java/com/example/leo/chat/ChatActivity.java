package com.example.leo.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.example.leo.chat.AllChats.socket;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mGrpBtn,mOneBtn;
    private EditText mInputMessageView,mtoWhom;
    private RecyclerView mMessagesView;
    private ChatFragment.OnFragmentInteractionListener mListener;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private static String username,sender;
    private boolean flag;
    private TextView listofUser;
    String imgDecodableString;
    /*private Socket socket;
    {
        try{
            socket = IO.socket("http://192.168.0.103:3000");
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //socket.connect();
        socket.on("message", handleIncomingMessages);
        socket.on("messageToRoom", handleIncomingRoomMessages);
        socket.on("records",oldloading);
        mAdapter = new MessageAdapter( mMessages);
        mToolbar = (Toolbar)findViewById(R.id.chatsNameToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sender = getIntent().getStringExtra("sender");
        username = getIntent().getStringExtra("reciever");
        getSupportActionBar().setTitle(username);

        mMessagesView = (RecyclerView) findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(this));
        mMessagesView.setAdapter(mAdapter);
        listofUser =(TextView) findViewById(R.id.active_users);
        final ImageButton sendButton = (ImageButton)findViewById(R.id.send_button);
        mInputMessageView = (EditText) findViewById(R.id.message_input);
        mtoWhom = (EditText)findViewById(R.id.toWho);
        mGrpBtn =(Button)findViewById(R.id.group_btn);
        mOneBtn =(Button)findViewById(R.id.one_btn);
        mInputMessageView.setVisibility(View.VISIBLE);
        mtoWhom.setVisibility(View.GONE);
        sendButton.setVisibility(View.VISIBLE);
        mGrpBtn.setVisibility(View.GONE);
        mOneBtn.setVisibility(View.GONE);
        mGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=true;
                mGrpBtn.setVisibility(View.GONE);
                mOneBtn.setVisibility(View.GONE);
                mInputMessageView.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.VISIBLE);
            }
        });
        mOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGrpBtn.setVisibility(View.GONE);
                mOneBtn.setVisibility(View.GONE);
                mtoWhom.setVisibility(View.VISIBLE);
                mInputMessageView.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.VISIBLE);
                flag=false;
            }
        });


        //pop();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage(sender);
            }
        });



    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intentMain = new Intent(ChatActivity.this, AllChats.class);
        startActivity(intentMain );
        return false;
    }

    private Emitter.Listener oldloading = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONArray jobj = (JSONArray) args[0];



                    for(int i =0;i<jobj.length();i++)
                    {
                        try {
                            String msg = ((JSONObject)jobj.get(i)).getString("msg");
                            addMessage(msg);
                        } catch (JSONException e) {

                        }
                    }


                }
            });
        }
    };

    private Emitter.Listener handleIncomingRoomMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    String imageText;
                    try {
                        message = data.getString("message").toString();
                        addMessage(message);

                    } catch (JSONException e) {
                        // return;
                    }


                }
            });
        }
    };


    private Emitter.Listener handleIncomingMessages = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    String imageText;
                    try {
                        message = data.getString("message").toString();
                        addMessage(message);

                    } catch (JSONException e) {
                        // return;
                    }
                    try {
                        imageText = data.getString("image");
                        addImage(decodeImage(imageText));
                    } catch (JSONException e) {
                        //retur
                    }


                }
            });
        }
    };




/*
    public void groupPop()
    {
        AlertDialog.Builder newalert = new AlertDialog.Builder(this);
        newalert.setTitle("Group name");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.nickname,null);
        final EditText groupname = (EditText) view.findViewById(R.id.nickName);
        newalert.setView(view);
        newalert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mtoWhom.setVisibility(View.GONE);
                socket.emit("create",groupname.getText().toString());


            }
        });

        newalert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        AlertDialog dl = newalert.create();
        dl.show();
    }*/




    private void sendMessage(String user){
        String message = mInputMessageView.getText().toString().trim();
        mInputMessageView.setText("");
        addMessage(message);
        if(flag)
        {
            socket.emit("messageToRoom", message);

        }
        else {
            socket.emit("message", message, user);
        }
//        JSONObject sendText = new JSONObject();

//        try{
//            sendText.put("text",message);
//        }catch(JSONException e){
//
//        }

    }



    public void sendImage(String path)
    {
        JSONObject sendData = new JSONObject();
        try{
            sendData.put("image", encodeImage(path));
            Bitmap bmp = decodeImage(sendData.getString("image"));
            addImage(bmp);
            socket.emit("message",sendData);
        }catch(JSONException e){

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attachments,menu);
        return  true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.addimage)
        {
            openGallary();
        }

        return super.onOptionsItemSelected(item);
    }



    public void openGallary()
    {
        Intent gallary  = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallary,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            //Log.d("onActivityResult",imgDecodableString);
           sendImage(imgDecodableString);
        }
    }



    private void addImage(Bitmap bmp){
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .image(bmp).build());
        mAdapter = new MessageAdapter( mMessages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }

    private void addMessage(String message) {

        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .message(message).build());
        // mAdapter = new MessageAdapter(mMessages);
        mAdapter = new MessageAdapter( mMessages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }



    private String encodeImage(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

    private Bitmap decodeImage(String data)
    {
        byte[] b = Base64.decode(data,Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b,0,b.length);
        return bmp;
    }


    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

}
