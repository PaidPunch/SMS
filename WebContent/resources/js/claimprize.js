$(document).ready(function() {
    
    $('#claimprize-btn').click(function() {
        var phone = $('#phone').val();
        var email = $('#email').val();
        
        if (!isValidString(phone)) 
        {
            alert("Please enter your phone number");
        }
        else if (!isValidEmail(email)) 
        {
            alert("Please enter a proper email address");
        }
        else 
        {
            $.ajax({
                type : "post",
                url : "prizes",
                data : {
                    "phone" : phone,
                    "email" : email,
                    "prizeid" : window.prizeid,
                },
                success : function(msg) {
                	document.getElementById('form-section').style.display = 'none';
              	  	document.getElementById('thankyou-section').style.display = '';
                },
                error: function(msg) {
                    alert(msg.responseText);
                }
            });
        }
        return false;
    });
    
});