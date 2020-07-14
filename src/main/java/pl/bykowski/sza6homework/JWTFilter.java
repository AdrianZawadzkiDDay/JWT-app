package pl.bykowski.sza6homework;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

            String authorization = httpServletRequest.getHeader("Authorization");
            System.out.println(authorization);

            String encodedPublicKey = httpServletRequest.getHeader("Certification");
            System.out.println(encodedPublicKey);
            Base64.Decoder decoder = Base64.getDecoder();

            RSAPublicKey publicKey = null;

            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                publicKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(decoder.decode(encodedPublicKey)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            JWTVerifier jwtVerifier = JWT
                    .require(Algorithm.RSA512(publicKey, null))
                    .build();

            DecodedJWT decode = jwtVerifier
                    .verify(authorization.substring(7));

            Claim name = decode.getClaim("name");
            Claim role = decode.getClaim("role");

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(name.asString(),"", Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.asString()))));

        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}