package ru.practicum.ewm.service.user;

import ru.practicum.ewm.params.AdminUserParam;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest request);

    List<UserDto> getUsers(AdminUserParam param);

    void delete(Long userId);
}
