package com.server.server;

import com.server.bean.User;
import com.server.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class UserServerImpl implements UserServier {
    private static String resource = "mybatis-config.xml";
    private static InputStream inputStream;
    private static SqlSessionFactory sqlSessionFactory;
    private static SqlSession session;
    private static UserMapper userMapper;

    public UserServerImpl() throws IOException {
        inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        session = sqlSessionFactory.openSession();
        userMapper = session.getMapper(UserMapper.class);
    }

    public boolean checkAccountExisted(String name) {
        User user = userMapper.findUserByName(name);
        return user == null ? false : true;
    }

    public void register(User user) {
        userMapper.add(user);
        session.commit();
    }

    @Override
    public boolean login(User user) {
        return userMapper.checkUser(user) == null ? false : true;
    }
}
