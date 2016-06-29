package com.example.giang.phone_sms;

import java.io.Serializable;

/**
 * Created by Giang on 6/22/2016.
 */
public class MyContact implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String phone;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public MyContact(String name, String phone) {
        super();
        this.name = name;
        this.phone = phone;
    }
    public MyContact() {
        super();
    }
    @Override
    public String toString() {
        return this.name+"["+this.phone+"]";
    }
}
