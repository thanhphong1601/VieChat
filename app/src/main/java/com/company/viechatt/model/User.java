package com.company.viechatt.model;

public class User {
    private String userId;
    private String name;
    private String email;
    private String password;
    private boolean isOnline;
//    private List<Post> userPosts;

    public User(){}

    public User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isOnline = false;
        //this.userPosts = new ArrayList<>();
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

//    public List<Post> getUserPosts() {
//        return userPosts;
//    }
//
//    public void addPost(Post post) {
//        userPosts.add(post);
//    }
//
//    public void removePost(Post post) {
//        userPosts.remove(post);
//    }

}

