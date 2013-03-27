/* chart colors default */
var $chrt_border_color = "#efefef";
var $chrt_grid_color = "#DDD";
var $chrt_main = "#E24913";
var $chrt_second = "#4b99cb";
var $chrt_third = "#FF9F01";
var $chrt_fourth = "#87BA17";
var $chrt_fifth = "#BD362F";
var $chrt_mono = "#000";

function isValidEmail(email) {
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\.+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function isValidString(string) {
    if (string) {
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