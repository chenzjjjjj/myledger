package com.chenzj.myledger.model;

/**
 * @description: 用户
 * @author: chenzj
 * @date: 2022/2/19 17:01
 */
public class User {
    private int userId ;
    private String username;
    private String account;
    private String password;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
