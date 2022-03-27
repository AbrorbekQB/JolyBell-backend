package uz.greenstar.jolybell.utils;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.api.jwt.JwtDTO;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class JwtUtils {
    @Autowired
    private Environment environment;

    public String getJwt(JwtDTO dto) {
        String secretKey = environment.getProperty("jwt.secret.key");
        int time = Instant.now().atOffset(ZoneOffset.ofHours(5)).getNano();

        String jwt = Jwts.builder()
                .setSubject(new Gson().toJson(dto))
                .setIssuedAt(new Date(time))
                .setExpiration(new Date(time + 86400))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return jwt;
    }

    public JwtDTO isValidToken(String token) {
        String secretKey = environment.getProperty("jwt.secret.key");
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return new Gson().fromJson(claims.getSubject(), JwtDTO.class);
    }
}
