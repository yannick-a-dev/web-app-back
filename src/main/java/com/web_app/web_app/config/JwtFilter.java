package com.web_app.web_app.config;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.web_app.web_app.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// Get authorization header and validate
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if( (!StringUtils.hasText(header) || StringUtils.hasText(header) && !header.startsWith("Bearer"))) {
			chain.doFilter(request, response);
			return;
		}
		final String token = header.split("")[1].trim();
		//Get user identity and set it on the spring security context
		UserDetails userDetails = userRepository.findByUsername(jwtUtil.getUsernameFromToken(token)).orElse(null);
		
		//Get jwt token and validate
		if(!jwtUtil.validateToken(token,userDetails)) {
			chain.doFilter(request, response);
			return;
		}
		
		UsernamePasswordAuthenticationToken
		     authentication = new UsernamePasswordAuthenticationToken(
		    		 userDetails,
		    		 userDetails.getPassword(),
		    		 userDetails == null ? 
		    				 List.of(): userDetails.getAuthorities());
		
		authentication.setDetails(
		   new WebAuthenticationDetailsSource().buildDetails(request)
		);
		
		//this is where the authentication magic happens and the user is now valid!
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		chain.doFilter(request, response);
	}
}
