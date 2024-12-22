package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.smart.config.UserDetailsServiceImpl;





@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

   @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	
    	
    	
    	
    	return http
    		    .authorizeHttpRequests(request -> request
    		        .requestMatchers("/admin/**").hasRole("ADMIN")
    		        .requestMatchers("/user/**").hasRole("USER")
    		        .requestMatchers("/**").permitAll()
    		    )
    		    .formLogin(form -> form
    		        .loginPage("/login") 
    		        .loginProcessingUrl("/dologin")
    		        .defaultSuccessUrl("/user/index")
    		        
    		        .permitAll() // Allow everyone to access the login page
    		    )
    		    .httpBasic(Customizer.withDefaults())
    		    .csrf(AbstractHttpConfigurer::disable)
    		    .build();

    	
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
   
}
    
    
 

//	
//	
//}
