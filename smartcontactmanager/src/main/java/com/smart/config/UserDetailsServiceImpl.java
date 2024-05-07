package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.entity.User;
import com.smart.repository.StudentRepo;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private StudentRepo  studenRepo;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//fetching user from database
		
        User user=studenRepo.getUserByUserName(username);
        
        if(user==null)
        {
        	
        	throw new UsernameNotFoundException("Could not found user !!");
        }
		
        CustomUserDetails customUserDetails=new CustomUserDetails(user);
        
		return customUserDetails;
	}

	
	
}
