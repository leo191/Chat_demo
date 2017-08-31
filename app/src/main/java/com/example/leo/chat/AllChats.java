package com.example.leo.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leo.chat.Adapters.RecViewAdapter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class AllChats extends AppCompatActivity {



    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private  ArrayList<Person> data;
    private RecViewAdapter recViewAdapter;
    public static OnItemClickListener myOnClickListener;
    private Toolbar mToolbar;
    private static String username;

    public static Socket socket;
    {
        try{
            socket = IO.socket("http://192.168.0.7:3000");
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);


        mToolbar = (Toolbar)findViewById(R.id.chats_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        pop(true);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        data = new ArrayList<Person>();
        for (int i = 0; i < Dummy.nameArray.length; i++) {
            data.add(new Person(Dummy.nameArray[i],Dummy.drawableArray[i]));
        }
        recViewAdapter = new RecViewAdapter(data, new OnItemClickListener() {
            @Override
            public void onItemClick(Person item) {
                    Intent chatTo = new Intent(AllChats.this,ChatActivity.class);
                    chatTo.putExtra("reciever",item.getName());
                    chatTo.putExtra("sender",username);
                    startActivity(chatTo);
            }
        });
        recyclerView.setAdapter(recViewAdapter);



        socket.connect();
        socket.on("userExists",userexist);
        socket.on("userSet",userset);



    }






    String list="";
    private Emitter.Listener userset = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        JSONObject object = (JSONObject)args[0];
                        JSONArray array = object.getJSONArray("userslist");
                        for(int i=0;i<array.length();i++)
                        {
                            list += array.get(i).toString()+"\n";
                            Toast.makeText(getApplicationContext(),list,Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException yh)
                    {

                    }
                    /*try {
                       for(int i=0;i<array.length();i++)
                       {
                           list += array.get(i).toString();
                       }
                        listofUser.setText(list);

                    }
                    catch (JSONException ed)
                    {

                    }*/
                    Toast.makeText(getApplicationContext(),"User added",Toast.LENGTH_SHORT).show();

                }
            });

        }
    };




    private Emitter.Listener userexist = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getApplicationContext(),args[0].toString(),Toast.LENGTH_SHORT).show();

                }
            });
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId())
        {

            case R.id.addchat:
                pop(false);
                break;



        }


        return super.onOptionsItemSelected(item);
    }












    private void setUsername(String username){

        //
        socket.emit("setUsername", username);

    }


    private  void pop(final boolean fl)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.nickname,null);
        final EditText nickName_edt = (EditText) view.findViewById(R.id.nickName);


        alert.setView(view);
        alert.setTitle("Enter nickname");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                username = nickName_edt.getText().toString();
                Toast.makeText(getApplicationContext(),username,Toast.LENGTH_SHORT).show();

                if(fl)
                {
                    setUsername(username);
                }
                else {
                    //setUsername(username);

                    data.add(new Person(username, R.drawable.dssf));
                    recViewAdapter.notifyDataSetChanged();
                }

              /*  AlertDialog.Builder newalert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Group or one to one");
                alert.setPositiveButton("Group", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        flag = true;
                    }
                });

                alert.setNegativeButton("One to one", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mtoWhom.setVisibility(View.GONE);
                        flag=false;
                    }
                });
                AlertDialog dl = alert.create();
                dl.show();*/

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

}
