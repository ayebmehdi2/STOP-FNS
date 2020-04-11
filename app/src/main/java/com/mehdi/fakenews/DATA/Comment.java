package com.mehdi.fakenews.DATA;

public class Comment {

    private String id, name, pic, comment, time;

    public Comment() {
    }

    public Comment(String id, String name, String pic, String comment, String time) {
        this.name = name;
        this.pic = pic;
        this.comment = comment;
        this.time = time;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getPic() {
        return pic;
    }

    public String getComment() {
        return comment;
    }

    public String getTime() {
        return time;
    }
}
