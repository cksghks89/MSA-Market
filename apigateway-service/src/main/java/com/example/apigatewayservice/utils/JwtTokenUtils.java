package com.example.apigatewayservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
@Component
public class JwtTokenUtils {

    private static final String jwtSecretKey = "thisIsASecretKeyUsedForJwtTokenGenerationAndItIsLongEnoughToMeetTheRequirementOf256Bits";
    // jwtSecretKey를 바이트 배열로 변환하고, 이를 사용하여 HMAC-SHA256 알고리즘에 사용할 키를 생성한다.
    private static final Key key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

    private static final String USER_ID = "loginId";
    private static final String NAME = "name";
    private static final String EMAIL = "email";

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
