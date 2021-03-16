package com.concord;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
  private static List<User> USERS = new ArrayList<>();

  @PostConstruct
  private void init() {
    randomUsersAdded();
  }

  public User findById(final Long id) {
    for (User user : USERS) {
      if (user.getId().equals(id)) {
        return user;
      }
    }
    return null;
  }

  private void randomUsersAdded() {
    User user1 = new User(1L, "Test Test");
    User user2 = new User(2L, "Test2 Test2");
    User user3 = new User(3L, "Test3 Test3");
    User user4 = new User(4L, "Test4 Test4");
    User user5 = new User(5L, "Test5 Test5");
    USERS.add(user1);
    USERS.add(user2);
    USERS.add(user3);
    USERS.add(user4);
    USERS.add(user5);
  }
}
