package com.example.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUser {

    private String email;
    private String name;
    private String userId;

    private List<ResponseOrder> orders;

    @Builder
    public ResponseUser(String email, String name, String userId) {
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

}
