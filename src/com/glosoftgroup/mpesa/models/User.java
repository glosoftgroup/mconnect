package com.glosoftgroup.mpesa.models;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
public class User {
    private String name;
    private String code;
    private String username;
    private String password;
    private String position;
    private String permissions;
    private String token;
    private String user_id;
    private String Email;
    private Boolean is_new_code = false;

    public User(String id,String name,  String code, String email, String password,String position,String token) {
        this.name = name;
        this.code = code;
        this.username = email;
        this.password = password;
        this.position = position;
        this.token = token;
        this.user_id = id;
        this.Email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Boolean getIs_new_code() {
        return is_new_code;
    }

    public void setIs_new_code(Boolean is_new_code) {
        this.is_new_code = is_new_code;
    }
    
    
    
    
}
