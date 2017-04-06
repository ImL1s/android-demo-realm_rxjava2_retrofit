package com.johnsontechinc.learnrealm.model;

/**
 * Created by ImL1s on 2017/4/6.
 * <p>
 * DESC:
 */

public class User {

    private int id;
    private String name;
    private int age;
    private int sessionId;

    public User(UserRealm realmUser) {
        id = realmUser.getId();
        name = realmUser.getName();
        age = realmUser.getAge();
        sessionId = realmUser.getSessionId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
