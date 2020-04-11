package com.mehdi.fakenews.DATA;

public class User {

    private String name, id, photo;

    public User(String name, String id, String photo) {
        this.name = name;
        this.id = id;
        this.photo = photo;
    }

    public User() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }
}


