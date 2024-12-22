package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;
import com.smart.repository.UserRepository;
import com.smart.service.Email_service;

import jakarta.servlet.http.HttpSession;





@Controller
public class ForgotController {

	
	@Autowired
	BCryptPasswordEncoder bcryptPasswordEncoder;
	
	
	
	
	@Autowired
	private Email_service emailservice;
	
	@Autowired
	private UserRepository userrepository;
	
	
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		
		return "forgot_email_form";	
	}
	
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session)
	{
		System.out.print("EMAIL"+email);
		
		Random random=new Random(100);
		int otp=random.nextInt(9999);
		
		System.out.print("OTP"+otp);
		
		
		String subject="OTP FROM Smart Contact Manager";
		String message="<h1> OTP="+otp+"</h1>";
		String to=email;
		
		
		
		boolean flag=emailservice.sendEmail(subject, message, to);
		
		if(flag)
		{
		
			session.setAttribute("myotp",otp);
			session.setAttribute("email", email);
			
			
			return "verify_otp";
		}
		else {
			session.setAttribute("message","Check your email id!!");
			return "forgot_email_form";
		}
		
	}
	
	
	
	
	
		
		
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session )
	{ 
		// user jo otp dalege wo hm store kar liye h 'otp' me and
		// jo otp gya h wo session me h and dono ko match karna h.
		
		// getting actual otp sent from session
		int myOtp = (int) session.getAttribute("myotp");
		// gettting the entered email from session
		String email= (String)session.getAttribute("email");
		
		if(myOtp == otp)
		{	
			// check if this is valid user i.e this user exist in our database
			// for this get the user by username, if not none means valid user else not.
			User user = this.userrepository.getUserByUserName(email);
			
			if(user == null)
			{
				// send error message
				session.setAttribute("message", "Invalid User!!");
				return "forgot_email_form";
			}
			else
			{
				// send new password form
				
			}
			return "password_change_form";
		}else
		{	
			// print error message
			session.setAttribute("message", "You have entered wrong otp");
			return "verify_otp";
		}
		
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newpassword, HttpSession session)
	{	
		// first get the user for which we have to change the password
		String email= (String)session.getAttribute("email");
		User user = this.userrepository.getUserByUserName(email);
		
		// set the password
		user.setPassword(this.bcryptPasswordEncoder.encode(newpassword));
		// now save the user in database
		this.userrepository.save(user);
		
		// redirecting to login page with message
		return "redirect:/login?change=password changed succesfully...."; //param.change me msg bhej denge change password successfully
		// 'signin' wala url pe chla jayega ans hmara message 
		// i.e 'password changed succesfully' 'change' variable me rhega
		
		
		
	}
	
	
	
	
	
}
