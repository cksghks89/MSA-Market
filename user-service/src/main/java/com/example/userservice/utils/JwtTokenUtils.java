package com.example.userservice.utils;

import com.example.userservice.dto.UserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenUtils {


    private static final String jwtSecretKey = "thisIsASecretKeyUsedForJwtTokenGenerationAndItIsLongEnoughToMeetTheRequirementOf256Bits";
    // jwtSecretKey를 바이트 배열로 변환하고, 이를 사용하여 HMAC-SHA256 알고리즘에 사용할 키를 생성한다.
    private static final Key key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    private static final String JWT_TYPE = "JWT";
    private static final String ALGORITHM = "HS256";

    private static final String USER_ID = "loginId";
    private static final String NAME = "name";
    private static final String EMAIL = "email";

    private static final long EXPIRED_DATE = 60L * 24L * 30L;    // 30일 동안 유효한 토큰 생성

    /**
     * 사용자 pk를 기준으로 JWT 토큰을 발급하여 반환해 준다.
     */
    public static String generateJwtToken(UserDto userDto) {

        JwtBuilder builder = Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(userDto))
                .setSubject(userDto.getUserId())
                .setIssuer("profile")
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(createExpiredDate());

        return builder.compact();
    }

    /**
     * 토큰을 기반으로 사용자의 정보를 반환해주는 메서드
     */
    public static boolean isValidToken(String token) {
        try {
            Claims claims = getClaimsFormToken(token);

            log.info("expireTime : {}", claims.getExpiration());
            log.info("loginId : {}", claims.get(USER_ID));
            log.info("username : {}", claims.get(NAME));
            log.info("email : {}", claims.get(EMAIL));

            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("Token Expired", expiredJwtException);
            return false;
        } catch (JwtException jwtException) {
            log.error("Token Tampered", jwtException);
            return false;
        } catch (NullPointerException npe) {
            log.error("Token is null", npe);
            return false;
        }
    }

    /**
     * 토큰의 만료기간을 지정하는 함수
     *
     * @return Date
     */
    private static Date createExpiredDate() {
        // 토큰의 만료기간은 8시간으로 지정
        Instant now = Instant.now();
        Instant expiryDate = now.plus(Duration.ofMinutes(EXPIRED_DATE));
        return Date.from(expiryDate);
    }

    /**
     * JWT의 헤더값을 생성해주는 메서드
     */
    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("typ", JWT_TYPE);
        header.put("alg", ALGORITHM);
        header.put("regDate", System.currentTimeMillis());
        return header;
    }

    /**
     * 사용자 정보를 기반으로 클래임을 생성해주는 메서드
     */
    private static Map<String, Object> createClaims(UserDto userDto) {
        // 비즈니스 로직에 따라 Claims 구성 요소 변경
        Map<String, Object> claims = new HashMap<>();

        log.info("loginId : {}", userDto.getUserId());
        log.info("username : {}", userDto.getName());
        log.info("email : {}", userDto.getEmail());

        claims.put(USER_ID, userDto.getUserId());
        claims.put(NAME, userDto.getName());
        claims.put(EMAIL, userDto.getEmail());

        return claims;
    }

    /**
     * 토큰 정보를 기반으로 Claims 정보를 반환받는 메서드
     *
     * @return Claims : Claims
     */
    private static Claims getClaimsFormToken(String token) {
        return Jwts.parser().setSigningKey(key)
                .parseClaimsJws(token).getBody();
    }

    /**
     * 토큰을 기반으로 사용자 정보를 반환받는 메서드
     *
     * @return String : 사용자 아이디
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return claims.get(USER_ID).toString();
    }

}
