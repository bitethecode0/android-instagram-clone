package com.example.joon.instagramclone.Model;

public class UserSettings {

    private User user;
    private UserAccountSettings settings;
    private UserSettings mUserSettings;

    public UserSettings(User user, UserAccountSettings settings) {
        this.user = user;
        this.settings = settings;

    }


    public UserSettings() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSettings getSettings() {
        return settings;
    }

    public void setSettings(UserAccountSettings settings) {
        this.settings = settings;
    }



    @Override
    public String toString() {
        return "UserSettings{" +
                "user=" + user.getUser_id() +
                ", settings=" + settings.getDisplay_name() +
                '}';
    }
}