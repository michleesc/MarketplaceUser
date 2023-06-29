package comp.finalproject.user.service;

import comp.finalproject.user.dto.UserDto;
import comp.finalproject.user.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    User findUserByEmail(String email);
    List<UserDto> findAllUsers();
}