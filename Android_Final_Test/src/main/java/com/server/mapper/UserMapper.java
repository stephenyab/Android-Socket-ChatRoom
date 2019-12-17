package com.server.mapper;

import com.server.bean.User;

import java.util.List;

public interface UserMapper {
    public void add(User user);

    public List<User> queryAll();

    public User findUserByName(String name);

    public User checkUser(User user);
}
