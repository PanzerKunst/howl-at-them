CBR.Models.Account = new Class({
    Extends: CBR.Models.JsonSerializable,

    options: {  // Defaults
    },

    getEmailAddress: function() {
        return this.options.emailAddress;
    },

    setPassword: function(password) {
        this.options.password = password;
    }
});
