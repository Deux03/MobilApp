package com.example.mobilapp;

public class User {
    private String username;
    private String email;
    private String phone;
    private String phoneType;
    private String address;
    private String accountType;

    // No-argument constructor required for Firestore deserialization
    public User() {}

    // Constructor with all fields
    public User(String username, String email, String phone, String phoneType, String address, String accountType) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.phoneType = phoneType;
        this.address = address;
        this.accountType = accountType;
    }

    // Getters and setters for each field
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
