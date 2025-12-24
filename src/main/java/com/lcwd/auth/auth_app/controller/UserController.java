package com.lcwd.auth.auth_app.controller;

import com.lcwd.auth.auth_app.dtos.UserDtos;
import com.lcwd.auth.auth_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

private final UserService userService;

@PostMapping("/save")
public ResponseEntity<UserDtos> saveUser(@RequestBody UserDtos userDtos){
    UserDtos savedUser=userService.createUser(userDtos);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
}
@GetMapping("/getAll")
public ResponseEntity<Iterable<UserDtos>> getAllUser(){
   Iterable<UserDtos> savedUser=userService.getAllUsers();
   return ResponseEntity.status(HttpStatus.OK).body(savedUser);
}

@GetMapping("/email/{email}")
public ResponseEntity<UserDtos> getUserByEmail(@PathVariable String email){
   UserDtos userByemail=userService.getUserByEmail(email);
   return ResponseEntity.status(HttpStatus.OK).body(userByemail);
}

@PutMapping("/update/{userId}")
public ResponseEntity<UserDtos> getUserByEmail(@PathVariable UUID userId, @RequestBody UserDtos userDtos){
    UserDtos userDtos1 = userService.updateUser(userDtos, userId);
    return ResponseEntity.status(HttpStatus.OK).body(userDtos1);
}

@DeleteMapping("/delete/{userId}")
public ResponseEntity<String> deleteUserById(@PathVariable UUID userId){
  userService.deleteUser(userId);
   return ResponseEntity.status(HttpStatus.OK).body("User deleted Succesfully");
}

}
