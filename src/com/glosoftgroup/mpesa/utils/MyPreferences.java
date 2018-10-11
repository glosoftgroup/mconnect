/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glosoftgroup.mpesa.utils;

import com.glosoftgroup.mpesa.models.User;
import java.util.prefs.Preferences;

/**
 *
 * @author babaviz
 */
public class MyPreferences {
    
    String permissions = "";
    
    public static String id;
    
    public Preferences prefs;
    private static MyPreferences instance = null;
    
    public static MyPreferences getInstance(){
        return instance == null ? new MyPreferences() : instance;
    }

    public MyPreferences() {
        this.prefs = Preferences.userNodeForPackage(MyPreferences.class);
        instance = this;
    }
    
    public void setUser(User user){
        prefs.put("name", user.getName());
        prefs.put("code", user.getCode());
        prefs.put("password", user.getPassword());
        prefs.put("username", user.getUsername());
        prefs.put("position", user.getPosition());
        prefs.put("token", user.getToken());
        prefs.put("userid", user.getUser_id());
        prefs.put("email", user.getEmail());
        prefs.put("is_new_code", user.getIs_new_code().toString());
    }
    
    public User getUser(){
        User user = new User(
                prefs.get("userid", ""),
                prefs.get("name", ""),
                prefs.get("code", ""),
                prefs.get("email", ""),
                prefs.get("password", ""),
                prefs.get("position", ""),
                prefs.get("token","")
        );
        
        user.setIs_new_code(Boolean.parseBoolean(prefs.get("is_new_code","false")));
        return user;
    }
    
    public String getPermissions() {
        return  prefs.get("permissions", "None");
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
        prefs.put("permissions", this.permissions);
    }
    
}
