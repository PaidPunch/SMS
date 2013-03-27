$(document).ready(function() {
    
    $('#feedback-btn').click(function() {
        var feedback = $('#feedback').val();
        
        $.ajax({
            type : "post",
            url : "feedback",
            data : {
                "offerid" : window.offerid,
                "type" : window.type,
                "feedback" : feedback
            },
            success : function(msg) {
        	  document.getElementById('feedback-section').style.display = 'none';
        	  document.getElementById('thankyou-section').style.display = '';
        	  alert(msg);
            }
        });
        return false;
    });
    
});