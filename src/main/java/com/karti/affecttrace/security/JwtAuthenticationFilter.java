package course.course_distributor.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostConstruct
    public void init(){
        logger.info("JWT FILTER INITIALIZED");
    }

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain
    ) throws IOException, ServletException {

        logger.info("===========JWT AUTHENTICATION STARTS==========");

        String token = getTokenFromRequest(request);

        logger.info("=========TOKEN: {}", token);

        if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)){

            String username = jwtTokenProvider.getUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            logger.info("================JWT AUTHENTICATION COMPLETED=============");
        }
        else{
            logger.error("==============JWT AUTHENTICATION FAILED============");
        }

        filterChain.doFilter(request, response);


    }

    private String getTokenFromRequest(HttpServletRequest request){

        String bearerToken =  request.getHeader("Authorization");
        logger.info("==========BEARER TOKEN: {}", bearerToken);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7, bearerToken.length());
        }

        return null;

    }


    
}
