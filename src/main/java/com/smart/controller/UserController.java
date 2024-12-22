package com.smart.controller;

import com.razorpay.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import com.razorpay.*;
import com.razorpay.*;
import com.razorpay.*;


@Controller
@RequestMapping("/user")
public class UserController {

	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactrepository;
	
	
	
	
	@ModelAttribute
	public void addCommonData(Model m,Principal principal) {
		String userName=principal.getName();
		
		
		System.out.println("USERNAME"+userName);
User user=userRepository.getUserByUserName(userName);
		
		System.out.print("USER"+ userName);
		
		m.addAttribute("user", user);
		
	}
	
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		
		model.addAttribute("title","User Dashboard");
		
		return "normal/user_dashboard";
	}
	
	
	
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
	
	

	
	@PostMapping("/process-contact")
	public String addcontact(@Valid @ModelAttribute("contact") Contact con,
	                         BindingResult result,
	                         @RequestParam("image") MultipartFile file,
	                         Principal principal,
	                         HttpSession session,
	                         Model model) {
	    try {
	        // Retrieve the username from the Principal
	        String name = principal.getName();
	        User user = userRepository.getUserByUserName(name);

	        // Handle the file upload
	        if (file.isEmpty()) {
	            System.out.print("File is Empty");
	            con.setImage("contact.png");
	        } else {
	            con.setImage(file.getOriginalFilename());
	            File saveFile = new ClassPathResource("static/image").getFile();
	            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            System.out.print("Image is uploaded");
	        }

	        // Associate the contact with the user
	        con.setUser(user);
	        user.getContact().add(con);
	        userRepository.save(user);

	        System.out.println("Contact details: " + con);
	        System.out.print("Added to database");

	        // Set success message
	        session.setAttribute("message", new Message("Your contact is added !!", "success"));

	    } catch (Exception e) {
	        System.out.print("Error: " + e.getMessage());
	        e.printStackTrace();
	        // Set error message
	       session.setAttribute("message", new Message("Your contact failed !! Try again", "danger"));
	    }
	    return "normal/add_contact_form";
	}
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,  Model model, Principal principal)
	{
		
		model.addAttribute("title", "Add Contact");
		
		// getting user id
		// principal will give the userName and from username we can get the id
		String userName = principal.getName();
		
		User user = userRepository.getUserByUserName(userName);
		
		// pagination
		// per page = 5([n])
		// current page = 0 ([page])
		Pageable pageable = PageRequest.of(page, 5);
		
		// now we can get the contact using pagination
		Page<Contact> contacts = contactrepository.findContactsByUser(user.getId(), pageable);
		
		model.addAttribute("contacts", contacts); // sending the contact
		model.addAttribute("currentPage", page);  // 'page':cur page no
		model.addAttribute("totalPages", contacts.getTotalPages());//total pages to be made
		
		
		return "normal/show-contacts";
	}
	
	
	@RequestMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable("cid")Integer cid,Model model,Principal principal)
	{
		
		System.out.print("CID"+cid);
		
		
	Optional<Contact> contactOptional=contactrepository.findById(cid);
		Contact contact=contactOptional.get();
	
		
		String userName=principal.getName();
		
	User user=userRepository.getUserByUserName(userName);
		
	if(user.getId()==contact.getUser().getId())
	{
		
		model.addAttribute("contact",contact);
		model.addAttribute("title", contact.getName());
	}
	
	
	
	
		
		return "normal/contact_detail";
	}
	
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, HttpSession session, Principal principal)
	{
		Optional<Contact> contactOptional = this.contactrepository.findById(cId);
		Contact contact = contactOptional.get();
		
		
		String userName=principal.getName();
		
		User user=userRepository.getUserByUserName(userName);
			
		if(user.getId()==contact.getUser().getId())
		{
			contact.setUser(null);
			contactrepository.delete(contact);
			
			
			// print message through session
			session.setAttribute("message", new Message("Contact deleted successfully...","alert-success"));
			
		}
			
		
	
		return "redirect:/user/show-contacts/0";
	}
	
	
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model m )
	{
		
		m.addAttribute("title", "Update Contact");
		
		Contact contact=contactrepository.findById(cid).get();
		
		m.addAttribute("contact", contact);
		
		
		
		
		return "normal/update_form";
	}
	
	
	/* update contact handler */
	
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,Model m,Principal principal,HttpSession session)
	{
		
		try {
			
			Contact oldcontactDetail=contactrepository.findById(contact.getCid()).get();
			
			if(!file.isEmpty())
			{
				
				
				File deleteFile  = new ClassPathResource("static/image").getFile();
				File file1=new File(deleteFile, oldcontactDetail.getImage());
				
				file1.delete();
				
				
				
				
				
			    File saveFile = new ClassPathResource("static/image").getFile();
	            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	           contact.setImage(file.getOriginalFilename());
			}else {
				
				
				contact.setImage(oldcontactDetail.getImage());//contact ke database me save image with same name.
				
				
				
				
			}
				
			User user=userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			contactrepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated...","success"));
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	System.out.print("CONTACT NAME"+ contact.getName());
	
	System.out.print("CONTACT ID"+ contact.getCid());
	
	
	return "redirect:/user/"+contact.getCid()+"/contact";
	
	}
	
	
	//your profile handler
	
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("title", "profile");
		return "normal/profile";
	}
	
	
	
	//open setting handler
	@GetMapping("/settings")
	public String openSettings()
	{
		return "normal/settings";
	}
	
	
	/*
	 * change-password-handler
	 */
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword ,@RequestParam("newPassword") String newPassword, Principal principal,HttpSession session)
	{
		System.out.print("OLD PASSWORD"+oldPassword);
		System.out.print("OLD PASSWORD"+newPassword);
		
		String userName=principal.getName();
		User currentuser=userRepository.getUserByUserName(userName);
		
		System.out.print(currentuser.getPassword());
		
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentuser.getPassword()))
		{
			currentuser.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(currentuser);
			session.setAttribute("message",new Message("Your password is updated!!","success"));
		}
		else {
			
			session.setAttribute("message",new Message("Wrong Password entered!! Try again","danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/index";
	}
	
	//creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String,Object>data)throws Exception {

		
		System.out.print(data);
		
		
		int amt=Integer.parseInt(data.get("amount").toString());
		
		
		var client=new RazorpayClient("rzp_test_60GjiYMDdR7Twd","HzuRz8McJs9ODNWWHnIcExld ");//syntax of java 11
		
		
		JSONObject ob=new JSONObject();
		ob.put("amount", amt*100);
		ob.put("curreny", "INR");
		ob.put("reciept","texn_235425");
		
		
		
		Order order=client.orders.create(ob);
		System.out.print(order);
		
		
		return order.toString();
		
		
		

	}
	
}
	
