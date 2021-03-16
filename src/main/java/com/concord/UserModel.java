package com.concord;

public class UserModel {

  public String fio;

  public UserModel(User user) {
    this.fio = user.getFio();
  }
}
