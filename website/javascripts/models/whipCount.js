CBR.Models.WhipCount = new Class({
    Extends: CBR.Models.JsonSerializable,

    options: {  // Defaults
    },

    getSupportLevel: function() {
        return this.options.supportLevel;
    },

    getCount: function() {
        return this.options.count;
    },

    getPercentage: function() {
        return this.options.percentage;
    }
});
