package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.razorpay.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entity.Contact;
import com.smart.entity.MyOrder;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.Contactrepository;
import com.smart.repository.MyOrderRepository;
import com.smart.repository.StudentRepo;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private StudentRepo studentrepo;
	
	@Autowired
	private Contactrepository contactrepository;
	
	@Autowired
	private MyOrderRepository myorderrepository;
	
	@ModelAttribute
	public void addcommonData(Model model, Principal principal)
	{
		String username=principal.getName();
		//System.out.println("user "+ username);
		
	User user=studentrepo.getUserByUserName(username);
	//System.out.println("user"+user);
		
	model.addAttribute("user", user);
	}
	
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
		
		model.addAttribute("title", "User Desk_board");
	
		return "normal/desk";
	}

	@GetMapping("/add-contact")
	public String openAddcontactForm(Model model)
	{
		model.addAttribute("title", " User Add_Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add-contact-form";
	}
	
	
	@PostMapping("/process-contact")
	public String processcontact(@ModelAttribute Contact contact,@RequestParam("profileimage") MultipartFile file,  Principal principal
			,HttpSession session) {
		
		
		try {
		
		String name=principal.getName();//name come
		User user=this.studentrepo.getUserByUserName(name);//row come
		
		if(file.isEmpty())
		{
			
			System.out.println("file is empty");
			contact.setImage("contact.png");
			
		}
		else {
			
			contact.setImage(file.getOriginalFilename());
			
	File savefile= new ClassPathResource("static/image").getFile();
			
          Path path=	Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
	
	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	
	System.out.println("image is uploaded");
		}
		
		
		contact.setUser(user);
		
		user.getContacts().add(contact);
		
		this.studentrepo.save(user);
		
		System.out.println("show "+contact);
		
		System.out.println(user);
		
		
		session.setAttribute("message", new Message("Your contact is added  !!", "success"));
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("error "+e.getMessage());
			
			session.setAttribute("message", new Message("Some thing went wrong try again !!","danger"));
		}
		return "normal/add-contact-form";
	}
	
	@GetMapping("/show_contact/{page}")
	public String showcontact(@PathVariable("page") Integer page,Model m,Principal principal)
	{
		m.addAttribute("title", "Show Contact");
		
          String username=principal.getName();
		           
                 User user=this.studentrepo.getUserByUserName(username);
    //page show             
                 
          Pageable pageable= PageRequest.of(page, 5);        
                 
     Page<Contact> contact= this.contactrepository.findContactByUser(user.getId(),pageable);
          
		m.addAttribute("contacts", contact);
      m.addAttribute("currentpage", page);
      m.addAttribute("totalPages", contact.getTotalPages());
      
		return "normal/show_contact";
	}
	
	@RequestMapping("/{cId}/contact")
	public String showcontactdetail(@PathVariable("cId") Integer cId,Model model, Principal principal) {
		
		 Optional<Contact> contaoptional= this.contactrepository.findById(cId);
		Contact contact=contaoptional.get();
		    
	String username=	principal.getName();
	   User user=  this.studentrepo.getUserByUserName(username);	
	   
	if(user.getId()==contact.getUser().getId())
		
		 model.addAttribute("contact", contact);
		 
		return "normal/contact_detail";
	}
	
	@GetMapping("/delete/{cid}")
	public String deletecontact(@PathVariable("cid")Integer cId,Model model,HttpSession session) {
		
		// Optional<Contact> contaoptional= this.contactrepository.findById(cId);
	Contact contact=	this.contactrepository.findById(cId).get();
		  
	
	                contact.setUser(null);
		
		    this.contactrepository.delete(contact);
		    
		  session.setAttribute("message", new Message("contact delete successfully..","success"));
		  
		return "redirect:/user/show_contact/0";
	}
	
	
	// show update contact
	
	@PostMapping("/update_contact/{cid}")
    public String updateform(@PathVariable("cid") Integer cId,Model model) {
    	
		
		model.addAttribute("title", "Update_form");
		
	         Contact contact=this.contactrepository.findById(cId).get();
		
	         model.addAttribute("contact", contact);
    	return "normal/update_form";
    }	
	
	//update contact
	
	@PostMapping("/update_contac")
	public String updatehndler(@ModelAttribute Contact contact,@RequestParam("profileimage") 
	MultipartFile file,Model model,HttpSession session,Principal principal) {
		
		
		                   
		
		try {
			
		 Contact oldcontact= this.contactrepository.findById(contact.getcId()).get();
			
		 
		 
			//image
			if(!file.isEmpty())
				{
				//old delete imgage
				
				File deletefile= new ClassPathResource("static/image").getFile();
				File file1=new File(deletefile,oldcontact.getImage());
				
				file1.delete();
				
				
			// save image
				File savefile= new ClassPathResource("static/image").getFile();
				
		          Path path=	Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			contact.setImage(file.getOriginalFilename());
				
			}else {
				
				contact.setImage(oldcontact.getImage());
			}
			
	            User user= this.studentrepo.getUserByUserName(principal.getName());
			
	            contact.setUser(user);
	            
			this.contactrepository.save(contact);
			
		}catch (Exception e) {
            e.printStackTrace();
		}
		
		session.setAttribute("message",new Message("Contact Update successfully !!","success"));
		return "redirect:/user/show_contact/0";
	}
	
	
	
	@RequestMapping("/view_profile")
	    public String viewprofile(Model model, Principal principal) {
	    	
	            String viewname=principal.getName();
	    	
	            User user= this.studentrepo.getUserByUserName(viewname);           
     	            model.addAttribute("view", user);
	            		
	    	return "normal/view_profile";
	    }
	
	
	@PostMapping("/create_order")
	@ResponseBody
	public String createOder(@RequestBody Map<String, Object> data,Principal principal) throws Exception
	{
		
		
		System.out.println(data);
		
		int amt=Integer.parseInt(data.get("amount").toString());
		 
		 var client=  new RazorpayClient("rzp_test_4xOJiuPbQ8GlQA", "qZNEUVLZPz9p7aTxVAayL5rT");
		
		    JSONObject ob=new JSONObject();
		    ob.put("amount", amt);
		    ob.put("currency", "INR");
		    ob.put("receipt", "txn_235425");
		 
		 //create new order
		    
		    Order order=client.Orders.create(ob);
		    
		    System.out.println(order);
		   
		   
		MyOrder myorder=new MyOrder();
		    
		myorder.setAmount(order.get("amount")+"");
		myorder.setOrderId(order.get("id"));
		myorder.setPaymentId(null);
		myorder.setStatus("created");
		
		myorder.setUser(this.studentrepo.getUserByUserName(principal.getName())) ;
		
		myorder.setReceipt(order.get("receipt"));
		
		this.myorderrepository.save(myorder);
		
		
		return order.toString();
		
	}
	
	@PostMapping("/update_order")
	public String updateOrder(@ModelAttribute Contact contact,@RequestBody Map<String, Object> data1,Principal principal){
		
		
		String username=principal.getName();
		System.out.println("user "+ username);
		
	User user=studentrepo.getUserByUserName(username);
	System.out.println("user"+user);
		
	MyOrder myorder=new MyOrder();
	//MyOrder myorder= myorderrepository.findByOrderId(principal.getName());
		
	System.out.println("user"+myorder);
	
	//=   this.myorderrepository.getReferenceById(principal.getName());
		myorder.setAmount(data1.get("amount")+"");
	         
		    myorder.setPaymentId(data1.get("payment_id")+"");
		    myorder.setStatus(data1.get("status")+"");
		   
		    
		    this.myorderrepository.save(myorder);
		
		
		//System.out.println(data);
		
		return data1.toString();
		
	}
	
	
	

	@RequestMapping("/fail")
	public String dashboard1()
	{
		
		return "login-fail";
	}
}
