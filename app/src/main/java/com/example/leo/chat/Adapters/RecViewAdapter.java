package com.example.leo.chat.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leo.chat.AllChats;
import com.example.leo.chat.OnItemClickListener;
import com.example.leo.chat.Person;
import com.example.leo.chat.R;

import java.util.ArrayList;

/**
 * Created by leo on 20/8/17.
 */

public class RecViewAdapter  extends RecyclerView.Adapter<RecViewAdapter.ChatPersonViewHolder>{



    ArrayList<Person> persons;
    private final OnItemClickListener listener;

    public RecViewAdapter(ArrayList<Person> persons, OnItemClickListener listener)
    {
        this.persons = persons;
        this.listener = listener;
    }




    @Override
    public ChatPersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_cards, parent, false);

        ChatPersonViewHolder viewHolder = new ChatPersonViewHolder(view);




        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatPersonViewHolder holder, int position) {


        holder.bind(persons.get(position), listener);


    }

    @Override
    public int getItemCount() {
        return persons.size();
    }



    public static class ChatPersonViewHolder extends RecyclerView.ViewHolder
    {
        TextView person_name;
        ImageView pro_pic;
        public ChatPersonViewHolder(View itemView) {
            super(itemView);
            person_name = (TextView)itemView.findViewById(R.id.person_name);
            pro_pic = (ImageView)itemView.findViewById(R.id.person_photo);

        }


        public void bind(final Person item, final OnItemClickListener listener) {

            person_name.setText(item.getName());
            pro_pic.setImageResource(item.getProfile_image_id());
            //Picasso.with(itemView.getContext()).load(item.imageUrl).into(image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);

                }

            });

        }
    }
}
