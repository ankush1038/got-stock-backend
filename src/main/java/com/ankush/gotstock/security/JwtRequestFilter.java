//package com.ankush.gotstock.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class JwtRequestFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtility jwtUtility;
//
//    @Autowired
//    private JwtUserDetailsService jwtUserDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException{
//
//        // fetching the header value from authorization header
//        final String authorizationHeader = request.getHeader("Authorization");
//
//        // inititalizing username and token as null
//        String email = null;
//        String jwt = null;
//
//        // fetching token value and username and saving it in variables
//        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            jwt = authorizationHeader.substring(7);
//            email=jwtUtility.extractEmail(jwt);
//        }
//
//        // if security context in null then we will fetch username and validate user based on that.
//        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email);
//
//            // validating the token and username
//            if(jwtUtility.validateJwt(jwt,email)){
//
//                // if validation is true then build a security badge(authentication object) for user
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                // adding extra details to security badge like IP, session id, session metadata, custom decisions etc.
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // adding security badge to Security context holder from where spring authorize the user.
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        // call doFilter to tell spring that the authentication is done now other components can do their work.
//        chain.doFilter(request, response);
//    }
//
//    private static final List<String> PUBLIC_PATHS = List.of(
//            "/register", "/login", "/forgot-password", "/reset-password",
//            "/swagger-ui", "/v3/api-docs", "/swagger-resources", "/webjars"
//    );
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
//    }
//}
package com.ankush.gotstock.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip JWT filter for public endpoints like auth and Swagger
        if (path.startsWith("/register") || path.startsWith("/login") ||
                path.startsWith("/forgot-password") || path.startsWith("/reset-password") ||
                path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") || path.startsWith("/webjars")) {
            chain.doFilter(request, response);
            return;
        }

        // fetching the header value from authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        // inititalizing username and token as null
        String email = null;
        String jwt = null;

        // fetching token value and username and saving it in variables
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtUtility.extractEmail(jwt);
        }

        // if security context is null then we will fetch username and validate user based on that.
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email);

            // validating the token and username
            if(jwtUtility.validateJwt(jwt, email)){

                // if validation is true then build a security badge(authentication object) for user
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // adding extra details to security badge like IP, session id, session metadata, custom decisions etc.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // adding security badge to Security context holder from where spring authorize the user.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // call doFilter to tell spring that the authentication is done now other components can do their work.
        chain.doFilter(request, response);
    }
}