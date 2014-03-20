CBR.Controllers.FindYourLegislator = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this._initValidation();
        this._initEvents();
    },

    initElements: function () {
        this.parent();

        this.$form = jQuery("form");
        this.$submitBtn = jQuery("[type=submit]");

        this.getEl().addClass("legislator-listing");
    },

    _initValidation: function () {
        this.validator = new CBR.Services.Validator({
            fieldIds: [
                "address"
            ]
        });
    },

    _initEvents: function () {
        this.$form.submit(jQuery.proxy(this._doSubmit, this));
        jQuery("tr.clickable").click(jQuery.proxy(this.navigateToStateLegislatorPage, this));
    },

    _doSubmit: function (e) {
        e.preventDefault();

        if (this.validator.isValid()) {
            this.$submitBtn.button('loading');
            location.replace("/find-your-legislator?address=" + jQuery("#address").val());
        }
    }
});
