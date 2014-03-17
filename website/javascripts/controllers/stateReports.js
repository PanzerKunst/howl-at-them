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

        if (!this.isAdmin()) {
            this.removeEditAndDeleteLinksForReportsCreatedByOthers();
        }
    },

    _getStateLegislators: function () {
        if (!this.options.firstLegislator) {
            return [];
        }

        var result = [new CBR.Models.StateLegislator(this.options.firstLegislator)];

        this.options.nextLegislators.forEach(function (item, index) {
            result.push(new CBR.Models.StateLegislator(item));
        });

        return result;
    },

    _initValidation: function () {
        this.validator = new CBR.Services.Validator({
            fieldIds: [
                "us-state"
            ]
        });

        this.initEditReportValidation();
    },

    _initEvents: function () {
        this.$form.submit(jQuery.proxy(this._doSubmit, this));

        jQuery("tr.clickable").click(jQuery.proxy(this._navigateToStateLegislatorPage, this));

        jQuery(".edit-report").click(jQuery.proxy(this._showEditReportModal, this));
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

    _showEditReportModal: function (e) {
        var $a = jQuery(e.currentTarget);
        var report = this._getReportFromId($a.closest("article").data("id"));
        var successUrl = "/state-reports?action=savedReport";

        this.showEditReportModal(report, successUrl);
    },

    _showDeleteReportModal: function (e) {
        var $a = jQuery(e.currentTarget);
        var reportId = $a.closest("article").data("id");
        var successUrl = "/state-reports?action=deletedReport";

        this.showDeleteReportModal(reportId, successUrl);
    },

    _getReportFromId: function (reportId) {
        var legislators = this._getStateLegislators();

        for (var i = 0; i < legislators.length; i++) {
            var reports = legislators[i].getReports();

            for (var j = 0; j < reports.length; j++) {
                var report = reports[i];

                if (report.getId() === reportId) {
                    return report;
                }
            }
        }

        return null;
    }
});
