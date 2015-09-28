CBR.Controllers.Admin = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this.initEvents();
    },

    initElements: function () {
        this.parent();

        this.$updateDataBtn = jQuery("#update-data");
    },

    initEvents: function () {
        this.parent();

        this.$updateDataBtn.click(jQuery.proxy(this._doUpdateData, this));
    },

    _doUpdateData: function (e) {
        e.preventDefault();

        this.$updateDataBtn.button("loading");

        new Request({
            urlEncoded: false,
            url: "/api/db/update-vote-smart-data",
            onSuccess: function () {
                this.$updateDataBtn.button("reset");
            }.bind(this),
            onFailure: function (xhr) {
                // Failing silently in case of a timeout. Don't ask me why we get 403 in case of timeout...
                if (xhr.status !== this.httpStatusCode.forbidden) {
                    this.$updateDataBtn.button("reset");
                    alert("AJAX fail :(");
                }
            }.bind(this)
        }).post();
    }
});
