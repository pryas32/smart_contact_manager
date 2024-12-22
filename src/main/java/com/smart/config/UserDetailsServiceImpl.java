package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.smart.entities.User;
import com.smart.repository.UserRepository;

@Component
public class UserDetailsServiceImpl  implements UserDetailsService{

	@Autowired
	private UserRepository userrepository;

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// fetching user from databaze 
		User user=userrepository.getUserByUserName(username);
		
		
		if(user==null)
		{
			throw new UsernameNotFoundException("Could not found user !!");
		}
		
		CustomUserDetails customUserDetails= new CustomUserDetails(user);
		
		return customUserDetails;
		
		
		
		
		
		
	}

}
