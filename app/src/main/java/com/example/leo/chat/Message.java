package com.example.leo.chat;

import android.graphics.Bitmap;

/**
 * Created by sreejeshpillai on 10/05/15.
 */
public class Message {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    private int mType;
    private String mMessage;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    private String sender;
    private Bitmap mImage;

    private Message() {}

    public int getType() {
        return mType;
    };

    public String getMessage() {
        return mMessage;
    };

    public Bitmap getImage() {
        return mImage;
    };


    public static class Builder {
        private final int mType;
        private Bitmap mImage;
        private String mMessage;

        public Builder(int type) {
            mType = type;
        }

        public Builder image(Bitmap image) {
            mImage = image;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mImage = mImage;
            message.mMessage = mMessage;
            return message;
        }
    }
}