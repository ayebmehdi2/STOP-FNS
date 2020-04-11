package com.mehdi.fakenews.DATA;

public class Post {

    private String authName, authId, postId, title, desc, img;
    private int fake, real;

    public Post(String authName, String authId, String postId, String title, String desc, String img, int fake, int real) {
        this.authName = authName;
        this.authId = authId;
        this.title = title;
        this.desc = desc;
        this.img = img;
        this.fake = fake;
        this.real = real;
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Post() {
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setFake(int fake) {
        this.fake = fake;
    }

    public void setReal(int real) {
        this.real = real;
    }

    public String getAuthName() {
        return authName;
    }

    public String getAuthId() {
        return authId;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImg() {
        return img;
    }

    public int getFake() {
        return fake;
    }

    public int getReal() {
        return real;
    }
}
