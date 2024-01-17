package com.example.userservice.dto;

import com.example.userservice.repository.UserEntity;
import com.example.userservice.vo.ResponseOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String email;
    private String name;
    private String pwd;
    private String userId;
    private Date createdAt;

    private String encryptedPwd;

    private List<ResponseOrder> orders;

    public static UserEntity toEntity(UserDto userDto) {
        return UserEntity.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .userId(userDto.getUserId())
                .encryptedPwd(userDto.getEncryptedPwd())
                .build();
    }
}
