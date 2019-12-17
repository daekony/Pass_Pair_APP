package edu.nccu.mis.passpair.Gift.Common;

public class ItemObject {
    private String name;
    private int photo;
    private String time;
    private boolean sent_or_not = false;

    public ItemObject(String name, int photo,String time) {
        this.name = name;
        this.photo = photo;
        this.time = time;
    }
    public ItemObject(String name ,int photo){
        this.name = name;
        this.photo = photo;
    }

    public boolean isSent() {
        return sent_or_not;
    }

    public void setToSend(boolean sent) {
        this.sent_or_not = sent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
