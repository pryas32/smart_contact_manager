console.log("this is script file")

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



//irst request to serve er to create order 


const paymentStart = () => {
	console.log("payment started..");
	let amount =$("#payment_field").val();
	console.log(amount);
	if(amount==''||amount==null){
		Swal.fire({
		  icon: "error",
		  title: "Oops",
		  text: "Amount is required!",
		
		});
	return;
	}
	}
	
	
	//we will use ajax to send request to server to create order
	
$.ajax(
	{
	     url:"/user/create_order", //request kaha bhejni hain
		data:JSON.stringify({amount:amount,info:'order_request'}),// we are sending json request
		contentType:"application/json",//data kaisa bhejna chahteh hain 
		type:"POST",
		dataType:"json",
		success:function(response)//this function will be invoked when success
		{
		console.log(response);
		if(response.status=='created')
			{
			  //open payment form
			  let opotions={
				key:'rzp_test_60GjiYMDdR7Twd',
				ampunt:response.amount,
				currency:'INR',
				name:'Smart Contact Manager',
				description:'Donation',
				image:"https://www.google.com/imgres?q=donation%20images&imgurl=https%3A%2F%2Fmedia.istockphoto.com%2Fid%2F1353332258%2Fphoto%2Fdonation-concept-the-volunteer-giving-a-donate-box-to-the-recipient-standing-against-the-wall.jpg%3Fs%3D612x612%26w%3D0%26k%3D20%26c%3D9AL8Uj9TTtrbHpM78kMp9fqjT_8EqpEekjdixeKUzDw%3D&imgrefurl=https%3A%2F%2Fwww.istockphoto.com%2Fphotos%2Fcharitable-donation&docid=FRC2FHvZ930uoM&tbnid=jKRNeUhLO56zCM&vet=12ahUKEwibyrGh54iJAxWJfWwGHc3TOh4QM3oECB0QAA..i&w=612&h=383&hcb=2&ved=2ahUKEwibyrGh54iJAxWJfWwGHc3TOh4QM3oECB0QAA"
			order_id:response.id,
			handler:function(response){
				
				console.log(response.razorpay_payment_id);
				console.log(response.razorpay_order_id);
				console.log(response.log(response.razorpay_signature));
			   console.log("payment successful !!");
				alert("congrats!! payment successful!!");
			} ,
			
			"prefill":
			   {
				name:"",
			     email: "",
			     "contact": "",
			   },
			
			notes:{
			address:"Donation corporate",
			},
			
			theme:{
				color:"#3399cc",
			},
		}; 
			  
		let rzp=new Razorpay(options);
		
		rzp.on('payment.failed', function (response){
		    console.log(response.error.code);
		    console.log(response.error.description);
		     console.log(response.error.source);
		     console.log(response.error.step);
		     console.log(response.error.reason);
		     console.log(response.error.metadata.order_id);
		     console.log(response.error.metadata.payment_id);
			 alert("oops payment failed!!");
		});
		rzp.open();
			}
		},
		error: function(error){
			console.log(error)
			alert("something went wrong!!")
		},
		});
	
	
	