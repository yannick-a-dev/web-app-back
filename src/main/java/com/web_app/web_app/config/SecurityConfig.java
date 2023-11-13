package com.web_app.web_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		  httpSecurity
		  .csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests(
	        	authorize -> authorize
	        	                     .requestMatchers("/api/auth/**").permitAll()
	        	                     .anyRequest().authenticated()
	        )
          .sessionManagement(sessionManagement ->
              sessionManagement
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .exceptionHandling(exceptionHandling ->
              exceptionHandling
                  .authenticationEntryPoint((request, response, ex) ->
                      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage())
                  )
          )
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

      return httpSecurity.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception { // pour l'autentification
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) { // pour l'accès à la
																									// BDD
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
		return daoAuthenticationProvider;
	}
}
