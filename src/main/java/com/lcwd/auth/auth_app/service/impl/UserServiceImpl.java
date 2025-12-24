package com.lcwd.auth.auth_app.service.impl;

import com.lcwd.auth.auth_app.Repository.UserRepository;
import com.lcwd.auth.auth_app.dtos.UserDtos;
import com.lcwd.auth.auth_app.entity.Provider;
import com.lcwd.auth.auth_app.entity.Users;
import com.lcwd.auth.auth_app.exceptions.ResourceNotFoundException;
import com.lcwd.auth.auth_app.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDtos createUser(UserDtos userDtos) {
        if(userDtos.getEmail()==null||userDtos.getEmail().isBlank()){
            throw new IllegalArgumentException("Email can not be blank...");
        }
        if(userRepository.existsByEmail(userDtos.getEmail())){
            throw new IllegalArgumentException("Email already Exist...");
        }
       Users users = modelMapper.map(userDtos, Users.class);
       users.setProvider(userDtos.getProvider()!=null ? userDtos.getProvider(): Provider.LOCAL);
       Users savedUser=userRepository.save(users);
       UserDtos userDtos1 = modelMapper.map(savedUser, UserDtos.class);
        return userDtos1;
    }

    @Override
    public UserDtos getUserByEmail(String email) {
        boolean existsByEmail = userRepository.existsByEmail(email);
        if(!existsByEmail){
            throw new ResourceNotFoundException("Email Id does not exist");
        }
        Optional<Users> users=userRepository.findByEmail(email);
        return modelMapper.map(users, UserDtos.class);
    }

    @Override
    public UserDtos updateUser(UserDtos userDtos, UUID userId) {
        Users users = modelMapper.map(userDtos, Users.class);
        Users existuser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given Id"));
     if(userDtos.getUsername()!=null) existuser.setUsername(userDtos.getUsername());
     if(userDtos.getPassword()!=null) existuser.setPassword(userDtos.getPassword());
     if(userDtos.getUsername()!=null) existuser.setUsername(userDtos.getUsername());
     if(userDtos.getEmail()!=null) existuser.setEmail(userDtos.getEmail());
     if(userDtos.getProvider()!=null) existuser.setProvider(userDtos.getProvider());
        Users savedUser = userRepository.save(existuser);
        return modelMapper.map(savedUser, UserDtos.class);
    }

    @Override
    public Iterable<UserDtos> getAllUsers() {
        return userRepository.
                findAll().
                stream().
                map(users -> modelMapper.map(users, UserDtos.class)).
                toList();
    }

    @Override
    public void deleteUser(UUID userId) {
        boolean existsById = userRepository.existsById(userId);
        if(!existsById){
            throw new ResourceNotFoundException("Given Id Does not Exist");
        }
        userRepository.deleteById(userId);
    }


}
