function displayOffer() {
  document.getElementById('egg-section').style.display = 'none';
  document.getElementById('button-section').style.display = '';
};
  
function clickRedeem(bizcode) {      
  document.getElementById("egg-image").style.backgroundImage = "url(images/animated-once.gif)";
  document.getElementById('egg-text').style.display = 'none';
  
  // Indicate that the offer has been displayed
  $.ajax({
      type : "put",
      url : "offer?Code=" + bizcode,
      success : function(msg) {        	
      	
      }
  });    
  
  window.setTimeout(function() {
    displayOffer();
    }, 4000);
};   