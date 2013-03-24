$(document).ready(function() {
    
    $('#createbusiness-btn').click(function() {
        var businessName = $('#business_name').val();
        var businessDesc = $('#business_desc').val();
        var businessCategory = $('#business_category').val();
        var urlPath = $('#url_path').val();
        var businessAddress = $('#business_address').val();
        var businessPhone = $('#business_phone').val();
        var businessGroup = $('#business_group').val();
        var businessCode = $('#business_code').val();
        var businessOffer = $('#business_offer').val();
        var couponCode = $('#business_coupon_code').val();
        var enabled = $('#business_enabled').val();
        
        if (!isValidString(businessName)) 
        {
            alert("Please enter your business name");
        }
        else if (!isValidString(businessDesc)) 
        {
            alert("Please enter your business description");
        }
        else if (!isValidString(businessCategory)) 
        {
            alert("Please enter your business category");
        }
        else if (!isValidString(urlPath)) 
        {
            alert("Please enter your business website");
        }
        else if (!isValidString(businessAddress)) 
        {
            alert("Please enter your business address");
        }
        else if (!isValidString(businessPhone)) 
        {
            alert("Please enter your business phone number");
        }
        else if (!isValidString(businessCode)) 
        {
            alert("Please enter your business code");
        }
        else 
        {
            $.ajax({
                type : "post",
                url : "../businesses",
                data : {
                    "name" : businessName,
                    "desc" : businessDesc,
                    "category" : businessCategory,
                    "url" : urlPath,
                    "address" : businessAddress,
                    "phone" : businessPhone,
                    "group" : businessGroup,
                    "code" : businessCode,
                    "offer" : businessOffer,
                    "couponcode" : couponCode,
                    "enabled" : enabled
                },
                success : function(msg) {
                    alert(msg);
                    window.open('admin.jsp', '_self', true);
                }
            });
        }
        return false;
    });
    
});