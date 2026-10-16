package course.course_distributor.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import course.course_distributor.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration}")
    private long jwtExpirationDate;

    @Autowired
    private UserRepository userRepo;

    public String generateToken(Authentication authentication){

        String id = userRepo.findByUsername(authentication.getName()).orElseThrow(()-> new UsernameNotFoundException(authentication.getName())).getId().toString();

        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                    .subject(id)
                    .issuedAt(new Date())
                    .expiration(expireDate)
                    .signWith(key())
                    .compact();

        return token;

    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsername(String token){

        String id = Jwts.parser()
            .verifyWith((SecretKey) key())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
        return userRepo.findById(Long.parseLong(id)).orElseThrow(()-> new UsernameNotFoundException(id)).getUsername();
    }

    public boolean validateToken(String token){

        Jwts.parser()
            .verifyWith((SecretKey) key())
            .build()
            .parseSignedClaims(token);

        return true;

    }


}
