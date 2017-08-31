package com.example.leo.chat;

/**
 * Created by leo on 20/8/17.
 */

public class Person {

    private String name;
    private int profile_image_id;

    public Person() {
    }

    public Person(String name, int profile_image_id) {
        this.name = name;
        this.profile_image_id = profile_image_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProfile_image_id() {
        return profile_image_id;
    }

    public void setProfile_image_id(int profile_image_id) {
        this.profile_image_id = profile_image_id;
    }
}
