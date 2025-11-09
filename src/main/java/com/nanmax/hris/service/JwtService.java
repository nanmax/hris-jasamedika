package com.nanmax.hris.service;
import com.nanmax.hris.entity.Pegawai;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map;
@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long expirationMs = 24 * 60 * 60 * 1000; // 24 hours
    public JwtService(@Value("${app.jwt.secret:defaultSecretKeyForHRISApplication12345}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateToken(Pegawai pegawai) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUser", pegawai.getIdUser());
        claims.put("profile", pegawai.getProfile());
        claims.put("namaLengkap", pegawai.getNamaLengkap());
        claims.put("email", pegawai.getEmail());
        claims.put("kdDepartemen", pegawai.getKdDepartemen());
        claims.put("namaDepartemen", pegawai.getNamaDepartemen());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(pegawai.getIdUser())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    public String getIdUserFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }
    public String getProfileFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? (String) claims.get("profile") : null;
    }
    public Integer getKdDepartemenFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? (Integer) claims.get("kdDepartemen") : null;
    }
    public String getNamaDepartemenFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? (String) claims.get("namaDepartemen") : null;
    }
}