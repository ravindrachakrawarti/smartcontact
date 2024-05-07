package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.StudentRepo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class Homecontroller {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@Autowired
	private StudentRepo studentrepo;
	
	@RequestMapping("/")
	public String Home(Model model)
	{
		
	model.addAttribute("title", "home smart contact Manager");	
		
		return "home";
	}
	

	
	@RequestMapping("/about")
	public String about(Model model)
	{
		
	model.addAttribute("title", "about smart contact Manager");	
		
		return "about";
	}
	

	@RequestMapping("/signup")
	public String signup(Model model)
	{
		
	model.addAttribute("title", "signup smart contact Manager");	
	model.addAttribute("user", new User());
		
		return "signup";
	}
	
	
	@RequestMapping(value="/do_register", method=RequestMethod.POST)
	public String registerUser(@Valid  @ModelAttribute("user") User user,  BindingResult res, @RequestParam(value="agreement",defaultValue = "false") boolean agreement,Model model,
			    HttpSession session)
	{
		
		
		try {
		
		
		if(!agreement)
		{
			
			System.out.println("you have not agreed terms and conditions");
			throw new Exception("you have not agreed terms and conditions");
		}
		
		if(res.hasErrors())
		{
			model.addAttribute("user", user);
			
			return "signup";
		}
		
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setImgaeUrl("defult.png");
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		System.out.println("agreement  "+agreement);
		System.out.println("USER   "+user);
		
	   User result=this.studentrepo.save(user);
		
		model.addAttribute("user",new User());
		session.setAttribute("message", new Message("successfully submit !!","alert-success"));
		
		return "signup";
	}catch(Exception e)
		{
		e.printStackTrace();
		model.addAttribute("user", user);
		session.setAttribute("message", new Message("something went wrong !!" +e.getMessage(),"alert-danger"));
		
		return "signup";
		}
	}
	
	
	@Controller
	class LoginController {
		@GetMapping("/login")
		String login() {
			return "login";
		}
	}
	
	
	
	
	
}
