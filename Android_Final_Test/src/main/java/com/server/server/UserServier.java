package com.server.server;

import com.server.bean.User;

public interface UserServier {
    public boolean checkAccountExisted(String name);

    public void register(User user);

    public boolean login(User user);
}
