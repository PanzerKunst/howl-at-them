CBR.Models.JsonSerializable = new Class({
    initialize: function (options) {
        this.options = options;
    },

    toJSON: function () {
        return this.options;
    }
});
