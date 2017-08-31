package com.example.leo.chat;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button mGrpBtn,mOneBtn;
    private EditText mInputMessageView,mtoWhom;
    private RecyclerView mMessagesView;
    private OnFragmentInteractionListener mListener;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private static String username;
    private boolean flag;
    private TextView listofUser;
    private Toolbar mToolbar;
    public static Socket socket;
    {
        try{
            socket = IO.socket("http://192.168.0.103:3000");
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1) {
        ChatFragment fragment = new ChatFragment();
        username = param1;
        return fragment;
    }

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        socket.connect();
        socket.on("message", handleIncomingMessages);
        socket.on("messageToRoom", handleIncomingRoomMessages);
        socket.on("records",oldloading);
        socket.on("userExists",userexist);
        socket.on("userSet",userset);


    }















    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new MessageAdapter( mMessages);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);
        listofUser =(TextView) view.findViewById(R.id.active_users);
        final ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
        mtoWhom = (EditText)view.findViewById(R.id.toWho);
        mGrpBtn =(Button)view.findViewById(R.id.group_btn);
        mOneBtn =(Button)view.findViewById(R.id.one_btn);
        mInputMessageView.setVisibility(View.GONE);
        mtoWhom.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);

        mGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=true;
                groupPop();
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


        pop();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mtoWhom.getText().toString();
                sendMessage(user);
            }
        });


    }





    private  void pop()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.nickname,null);
        final EditText nickName_edt = (EditText) view.findViewById(R.id.nickName);


        alert.setView(view);
        alert.setTitle("Enter nickname");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                username = nickName_edt.getText().toString();
                Toast.makeText(getActivity(),username,Toast.LENGTH_SHORT).show();

                setUsername(username);



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


    public void groupPop()
    {
        AlertDialog.Builder newalert = new AlertDialog.Builder(getActivity());
        newalert.setTitle("Group name");

        LayoutInflater inflater = getActivity().getLayoutInflater();
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
    }











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

    private void setUsername(String username){

        //
         socket.emit("setUsername", username);

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

    private void addMessage(String message) {

        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .message(message).build());
        // mAdapter = new MessageAdapter(mMessages);
        mAdapter = new MessageAdapter( mMessages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }

    private void addImage(Bitmap bmp){
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .image(bmp).build());
        mAdapter = new MessageAdapter( mMessages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }
    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
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







    String list="";
    private Emitter.Listener userset = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   try
                   {
                      JSONObject object = (JSONObject)args[0];
                       JSONArray array = object.getJSONArray("userslist");
                       for(int i=0;i<array.length();i++)
                       {
                           list += array.get(i).toString()+"\n";
                       }
                       listofUser.setText(list);

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
                   Toast.makeText(getActivity(),"User added",Toast.LENGTH_SHORT).show();

                }
            });

        }
    };




    private Emitter.Listener userexist = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getActivity(),args[0].toString(),Toast.LENGTH_SHORT).show();

                }
            });
        }
    };




    private Emitter.Listener handleIncomingRoomMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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
            getActivity().runOnUiThread(new Runnable() {
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




    private Emitter.Listener oldloading = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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














    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

}