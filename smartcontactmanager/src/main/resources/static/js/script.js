console.log("this is script file");

const toggleSidebar = () => {
	
	if($(".sidebar").is(":visible"))
	{
		
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
	}
	else{
		
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
		
	}	
	
};


const paymentStart=()=>{

	console.log("payment started...");
    
	let amount =$("#payment_field").val();
	console.log(amount);

	if(amount=="" || amount==null)
	{
		
		
		swal("Failed !!", "amount is required !!", "error");
		return;
	}


  $.ajax({
	
		url :"/user/create_order",
		data:JSON.stringify({amount:amount,info:"order_request"}),
		contentType:"application/json",

		type:"POST",
		datatype:"json",
		success:function(response){
  
			console.log(response);
			console.log(amount)
		  
				if(amount>0)
				{
	let options={
					
					key:'rzp_test_4xOJiuPbQ8GlQA',
					callback_url: "http://localhost:5000/api/razor/PayVerification/",
					amount:amount,
					currency:"INR",
					name:"Smart Contact Manager",
					description:"Donation",
					image:"https://th.bing.com/th?id=OIP.tC10_Fzb-ZeOTlV8ATopGgHaHa&w=250&h=250&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2",
					order_id:response.id,
					handler:function(response){
						
                        console.log(response.razorpay_payment_id);
						console.log(response.razorpay_order_id);
						console.log(response.razorpay_signature);
						
						console.log('payment successful !!');
						
						updatePaymentOnserver(response.razorpay_payment_id,amount,"paid");
						
					  
					},
					
					prefill: { 
                           name: "",
                           email: "",
                           contact: "", 
                        },
					
					notes: {
                          address: "Ravindra Chakrawarti",
                      },
               
                theme: {
                         color: "#3399cc",
                        
          },
		};
				
				
			let rzp=new Razorpay(options);
			 rzp.on("payment.failed",function(response){
				 
				 console.log(response.error.code);
				 console.log(response.error.description);
				 console.log(response.error.source);
				 console.log(response.error.step);
				 console.log(response.error.reason);
				 console.log(response.error.metadata.order_id);
				 console.log(response.error.metadata.payment_id);
				 
				 
			swal("Failed","Oops payment failed !!","Error");
				 
				 
				 
				 
			 });
			
				
				rzp.open();	
			}	
				
		},
	   error:function(error){
            console.log(error);
			alert("something went wrong !!");
	   },
	});




};
   
  function   updatePaymentOnserver(payment_id,amount,status)
     {
		 
$.ajax({
	
		url :"/user/update_order",
		data:JSON.stringify({payment_id:payment_id,amount:amount,status:status}),
		
		
		contentType:"application/json",
		type:"POST",
		datatype:"json",
		success:function(response){
			
			 swal("Good job!", "payment successful !!","success");
		},
		
		error:function(response){
			
			swal("Failed","payment successful ,but we did not get on server !!","Error");
		},
		
		});
		 
	 };
             






    

