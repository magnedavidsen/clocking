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

Date.minBetween = function( date1, date2 ) {
  //Get 1 min in milliseconds
  var one_min=1000*60;

  // Convert both dates to milliseconds
  var date1_ms = date1.getTime();
  var date2_ms = date2.getTime();

  // Calculate the difference in milliseconds
  var difference_ms = date2_ms - date1_ms;
    
  // Convert back to mins and return
  return Math.round(difference_ms/one_min); 
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