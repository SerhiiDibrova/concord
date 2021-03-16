package com.concord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

  @Autowired private UserService userService;

  @PostMapping(value = "api/v1/users", produces = "application/json")
  public ResponseEntity<UserModel> getById(@RequestBody User user) {
    User userFound = userService.findById(user.getId());
    UserModel userModel = userFound != null ? new UserModel(userFound) : null;
    return new ResponseEntity<>(userModel, HttpStatus.OK);
  }
}
