package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class HomeController {
  
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	
	@Autowired
	private UserRepository userrepository;
	
	
	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title","HomePage");	
		return "home";
		
		
	}
	
	
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("about","HomePage");	
		return "about";
		
		
	}
	
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("user",new User());
		model.addAttribute("title","Welcome to SignUp Page!!");	
		return "signup";
		
		
	}
	
	
	/* this handler for registering user */
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,@RequestParam(value="aggrement",defaultValue="false") boolean aggrement,Model model,BindingResult result1,HttpSession session) {
		try {
			System.out.println("Aggrement "+aggrement);
			System.out.println("User "+user);
			
			if(!aggrement)
			{
				System.out.print("you have not aggred terms and conditions");
				throw new Exception("you have not aggred terms and conditions");
			}
			
			if(result1.hasErrors())
			{
				System.out.println("ERROR"+result1.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			
			
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.print("Aggrement "+aggrement);
			System.out.print("User "+user);
			
			
			
			User result= userrepository.save(user);
			
			
			model.addAttribute("user",new User());//blank user sending on submitting page 
			
			session.setAttribute("message", new Message("Successfully Registered !!","alert-success"));
			return "signup";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("soemthing went wrong !!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
	
		//handler for custom login
		
		
	}
	
	
	

	@GetMapping("/login")
	public String customLogin(Model model)
	{
		model.addAttribute("title", "login_page");
		return "login";
	}
	
	
	
	
}
