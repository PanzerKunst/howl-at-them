CBR.Controllers.FindYourLegislator = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this.initEvents();
        this._initValidation();
    },

    initElements: function () {
        this.parent();

        this.$form = jQuery("form");
        this.$submitBtn = jQuery("[type=submit]");
    },

    initEvents: function () {
        this.parent();

        this.$form.submit(jQuery.proxy(this._doSubmit, this));
        jQuery("tr.clickable").click(jQuery.proxy(this.navigateToStateLegislatorPage, this));
    },

    _initValidation: function () {
        this.validator = new CBR.Services.Validator({
            fieldIds: [
                "address"
            ]
        });
    },

    _doSubmit: function (e) {
        e.preventDefault();

        if (this.validator.isValid()) {
            this.$submitBtn.button('loading');
            location.replace("/find-your-legislator?address=" + encodeURIComponent(jQuery("#address").val()));
        }
    }
});
