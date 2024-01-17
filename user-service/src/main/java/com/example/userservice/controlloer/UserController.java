package com.example.userservice.controlloer;

import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.utils.JwtTokenUtils;
import com.example.userservice.vo.RequestLogin;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.ResponseUser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final Greeting greeting;
    private final UserService userService;
    private final Environment env;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service on PORT %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody RequestUser user) {
        log.info("[CREATE USER] user : {}", user);

        ResponseUser createUser = userService.createUser(UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .pwd(user.getPwd())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser user = new ModelMapper().map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody RequestLogin requestLogin, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestLogin.getEmail(), requestLogin.getPassword());

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if (authenticate != null) {
            log.info("[login] success");

            UserDto userDto = UserDto.builder()
                    .userId(authenticate.getPrincipal().toString())
                    .email(requestLogin.getEmail())
                    .build();

            String token = JwtTokenUtils.generateJwtToken(userDto);

            response.setHeader("token", token);
            response.setHeader("userId", authenticate.getPrincipal().toString());

            return ResponseEntity.status(HttpStatus.OK).body("login success");
        } else {
            log.info("[login] fail");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("login failed");
        }
    }
}
