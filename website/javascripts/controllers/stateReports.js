CBR.Controllers.StateReports = new Class({
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
        this.$usStateSelect = jQuery("#us-state");
        this.$submitBtn = jQuery("[type=submit]");

        this.getEl().addClass("report-listing");
    },

    _initValidation: function () {
        this.validator = new CBR.Services.Validator({
            fieldIds: [
                "us-state"
            ]
        });
    },

    _initEvents: function () {
        this.$form.submit(jQuery.proxy(this._doSubmit, this));
        jQuery("tr.clickable").click(jQuery.proxy(this._navigateToStateLegislatorPage, this));
        jQuery(".delete-report").click(jQuery.proxy(this._showDeleteReportModal, this));
    },

    _doSubmit: function (e) {
        e.preventDefault();

        if (this.validator.isValid()) {
            this.$submitBtn.button('loading');
            location.replace("/state-reports?usStateId=" + this.$usStateSelect.val());
        }
    },

    _navigateToStateLegislatorPage: function (e) {
        location.href = "/state-legislators/" + jQuery(e.currentTarget).data("id");
    },

    _showDeleteReportModal: function(e) {
        var $a = jQuery(e.currentTarget);
        var reportId = $a.closest("article").data("id");
        var successUrl = "/state-reports?action=deletedReport";

        this.showDeleteReportModal(reportId, successUrl);
    }
});
