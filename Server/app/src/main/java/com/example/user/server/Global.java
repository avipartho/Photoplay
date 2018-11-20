package com.example.user.server;

/**
 * Created by User on 12/19/2017.
 */

public class Global {
    private static Global instance;

    // Global string variable
    String uhp[] = new String[3];

    // Restrict the constructor from being instantiated
    private Global(){}

    public void setData(String u, String h, String p){
        this.uhp[0] = u;
        this.uhp[1] = h;
        this.uhp[2] = p;
    }
    public String[] getData(){
        return this.uhp;
    }

    public static synchronized Global getInstance(){
        if(instance==null){
            instance=new Global();
        }
        return instance;
    }

}
