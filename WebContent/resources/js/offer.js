function displayOffer() {
  document.getElementById('egg-section').style.display = 'none';
  document.getElementById('button-section').style.display = '';
};
  
function clickRedeem() {      
  document.getElementById("egg-image").style.backgroundImage = "url(images/animated-once.gif)";
  document.getElementById('egg-text').style.display = 'none';
  
  window.setTimeout(function() {
    displayOffer();
    }, 4000);
};   