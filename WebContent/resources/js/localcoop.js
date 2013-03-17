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
        if (!isValidName(businessName)) {
            alert("Please enter your business name");
        }
        if (isValidName(businessName)) {
            $.ajax({
                type : "post",
                url : "businesses",
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
                }
            });
        }
        return false;
    });
    
});

function isValidEmail(email) {
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\.+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function isValidName(string) {
    if (string) {
        return true;
    }
    return false;
}

function isValidInvitationCode(invitationCode) {
    if (invitationCode.length > 4) {
        return true;
    }
    return false;
}

function isValidPassword(password) {
    errorMessage = "";
    if (password.length < 6) {
        errorMessage = "Password must have at least 6 characters.";
    }
    return errorMessage;
}