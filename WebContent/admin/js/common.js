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