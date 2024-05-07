package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


	@Bean
	public UserDetailsService getUserDetailsService() {
	
			return new UserDetailsServiceImpl();  //object ban jayega
	
	}
	 @Bean
	 public BCryptPasswordEncoder passwordEncoder() {
		 
		 
		 return new BCryptPasswordEncoder();
	 }
	 
	 @Bean
	 public DaoAuthenticationProvider authenticationProvider() {
		 
		 
		 DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
		 daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		 daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		 return daoAuthenticationProvider;
	 }
	
	
       @Bean
	 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception  {
    	   
			
    	   http
    	   .authorizeHttpRequests((authz) -> authz.requestMatchers("/admin/**")
           		.hasRole("ADMIN").requestMatchers("/user/**")
                .hasRole("USER").requestMatchers("/**").permitAll()).formLogin(form -> form
				.loginPage("/login").loginProcessingUrl("/dologin").defaultSuccessUrl("/user/index").failureUrl("/user/fail")
				
			).csrf(crsf-> crsf.disable());
			
    	   http.authenticationProvider(authenticationProvider());  
			
			return http.build();
			// ...
		}
	
	
	
	
	

}
