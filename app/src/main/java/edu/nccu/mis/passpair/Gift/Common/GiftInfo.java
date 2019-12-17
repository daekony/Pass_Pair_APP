package edu.nccu.mis.passpair.Gift.Common;


public class GiftInfo {
    public String ruid;
    public String gtype;
    public String guid;
    public String time;

    public GiftInfo(){

    }

    public GiftInfo(String ruid, String gtype, String guid,String time) {
        this.ruid = ruid;
        this.gtype = gtype;
        this.guid = guid;
        this.time = time;
    }

    public String getRuid() {
        return ruid;
    }

    public void setRuid(String ruid) {
        this.ruid = ruid;
    }

    public String getGtype() {
        return gtype;
    }

    public void setGtype(String gtype) {
        this.gtype = gtype;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
