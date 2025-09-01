package ru.yandex.practicum.filmorate.mapper;


import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public final class UserMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setBirthday(user.getBirthday());
        userDto.setLogin(user.getLogin());
        return userDto;
    }

}
