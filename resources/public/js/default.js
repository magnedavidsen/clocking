Date.daysBetween = function( date1, date2 ) {
  //Get 1 day in milliseconds
  var one_day=1000*60*60*24;

  // Convert both dates to milliseconds
  var date1_ms = date1.getTime();
  var date2_ms = date2.getTime();

  // Calculate the difference in milliseconds
  var difference_ms = date2_ms - date1_ms;
    
  // Convert back to days and return
  return Math.round(difference_ms/one_day); 
}

Date.isThisYear = function(date){
	var today = new Date();
	return date.getFullYear() == today.getFullYear();
}

Date.isThisMonth = function(date){
	var today = new Date();
	return date.getMonth() == today.getMonth();
}

Date.isToday = function(date){
	var today = new Date();
	if(Date.isThisYear(date) &&
		Date.isThisMonth(date) &&
		date.getDate() == today.getDate()){
		return true;
	}
	return false;
}

Date.isYesterday = function(date){
	var today = new Date();
	if(	Date.isThisYear(date) &&
		Date.isThisMonth(date) &&
		date.getDate() == (today.getDate() - 1)){
		return true;
	}
	return false;
}

Date.isLessThanWeekAgo = function(date){
	var today = new Date();
	var daysAgo = Date.daysBetween(date, today);
	console.log(daysAgo);
	return daysAgo < 7;
}

function formatDate(datestring){
	console.log(datestring);
	var date = new Date(datestring);
	var weekday = date.toLocaleDateString().split(",")[0];
	var month = date.toLocaleDateString().split(",")[1].split(" ")[1].toLowerCase();

	if(Date.isToday(date)){
		return "Today";
	} else if(Date.isYesterday(date)){
		return "Yesterday";
	} else if(Date.isLessThanWeekAgo(date)){
		return weekday;
	} else if(Date.isThisYear(date)){
		return date.getDate() + ". " + month;
	}else{
		return date.getDate() + ". " + month + date.getFullYear();
	}
}

function formatTime(datestring){
	return
}

function formatTimestamp(datestring){
	if(datestring){
		var timestamp = new Date(datestring);
		return formatDate(datestring) + ", " + timestamp.toLocaleTimeString();
	}
}

function setHiddenField(){
	var employee_id = $("#employee-id").val();
	$("#hidden-employee-id").val(employee_id);
}

$(document).ready(function(){


	$('.timestamp').each(function(index, data){
		var date = $(data).html();
		$(data).html(formatTimestamp(date));
	});

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

