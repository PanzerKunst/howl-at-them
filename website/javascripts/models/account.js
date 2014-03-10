CBR.Models.Account = new Class({
    Extends: CBR.Models.JsonSerializable,

    options: {  // Defaults
    },

    getUsername: function() {
        return this.options.username;
    },

    setPassword: function(password) {
        this.options.password = password;
    }
});
