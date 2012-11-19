function setHiddenField(){
	var employee_id = $("#employee-id").val();
	$("#hidden-employee-id").val(employee_id);
}

$(document).ready(function(){

	$('.employee-id').focus();

	$('.employee-id').keypress(function(event){

	   	//if enter pressed, don't do anything
	    if (event.keyCode == 10 || event.keyCode == 13){
	    	event.preventDefault();
	    }
	        
	    //if 'i' is pressed, clock in
	    if (event.keyCode == 105){
	    	$('.clock-in').click();
	    }
	    
	    //if 'o' is pressed, clock out	
	    if (event.keyCode == 111){
	    	setHiddenField();
	    	$('.clock-out').click();
	    }

	    if($('.employee-id').val().length == 2){
	    	$('.help-text').html("(Press <b>'I</b> to clock in, <b>'O'</b> to clock out)");
	    }

    	
  });
 });

