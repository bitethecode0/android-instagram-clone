package com.example.joon.instagramclone.Model;

public class UserAccountSettings {
    private String description;
    private String display_name;
    private long followers;
    private long following;
    private long post;
    private String username;
    private String profile_photo;
    private String website;
    private String user_id;


    public UserAccountSettings() {
    }

    public UserAccountSettings(String description, String display_name, long followers,
                               long following, long post, String username, String profile_photo,
                               String website, String user_id) {
        this.description = description;
        this.display_name = display_name;
        this.followers = followers;
        this.following = following;
        this.post = post;
        this.username = username;
        this.profile_photo = profile_photo;
        this.website = website;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getPost() {
        return post;
    }

    public void setPost(long post) {
        this.post = post;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "user=" + username +
                ", display name=" + display_name +
                '}';
    }
}
