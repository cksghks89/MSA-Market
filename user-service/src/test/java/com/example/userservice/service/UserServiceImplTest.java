package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceImplTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Test
    void getUserByUserIdSuccessTest() {
        // given
        UserEntity userEntity = new UserEntity(1L, "abc@abc.com", "song", "id", "password");
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // when
        UserDto result = userService.getUserByUserId("id");
        result.setOrders(null);

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(savedUserEntity, UserDto.class);

        // then
        assertThat(result.getName()).isEqualTo("song");
        assertThat(result).isEqualTo(userDto);
    }

}