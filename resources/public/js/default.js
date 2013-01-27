function formatDate(datestring, showRelativeDate){
	console.log(datestring);
	var date = new Date(datestring);
	var weekday = date.toLocaleDateString().split(",")[0];
	var month = date.toLocaleDateString().split(",")[1].split(" ")[1].toLowerCase();
	var monthnumber = date.getMonth() + 1;

	if(Date.isToday(date) && showRelativeDate){
		return "Today";
	} else if(Date.isYesterday(date) && showRelativeDate){
		return "Yesterday";
	} else if(Date.isLessThanWeekAgo(date) && showRelativeDate){
		return weekday;
	} else if(Date.isThisYear(date)){
		return date.getDate() + "/" + monthnumber ;
	}else{
		return date.getDate() + "/" + monthnumber + "/" + date.getFullYear();
	}
}

function formatTime(datestring){
	if(datestring){
		return new Date(datestring).toLocaleTimeString();
	}
}

function formatTimestamp(datestring){
	if(datestring){
		return formatDate(datestring, true) + ", " + formatTime(datestring);
	}
}

function formatMinutes(minutes){	
	var newMinutes = minutes % 60;
	var hours = (minutes - newMinutes)/60;
	return hours + "h " + newMinutes + "m";
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

	$('.date').each(function(index, data){
		var date = $(data).html();
		$(data).html(formatDate(date, false));
	});

	$('.time').each(function(index, data){
		var date = $(data).html();
		$(data).html(formatTime(date));
	});

	$('.interval-minutes').each(function(index, data){
		var date = $(data).html();
		$(data).html(formatMinutes(date));
	});

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

