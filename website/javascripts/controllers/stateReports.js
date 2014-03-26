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

        this.$filter = jQuery(".table-filter input");
        this.$results = jQuery("#search-results > article");

        this.getEl().addClass("legislator-listing");

        this.addEditAndDeleteReportLinks();
        this.fadeOutFloatingAlerts();
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

        jQuery("tr.clickable").click(jQuery.proxy(this.navigateToStateLegislatorPage, this));

        jQuery(".edit-report").click(jQuery.proxy(this._showEditReportModal, this));
        jQuery(".delete-report").click(jQuery.proxy(this._showDeleteReportModal, this));

        this.$filter.keyup(_.debounce(jQuery.proxy(this._doFilterResults, this), 100));
    },

    _doSubmit: function (e) {
        e.preventDefault();

        if (this.validator.isValid()) {
            this.$submitBtn.button('loading');
            location.replace("/state-reports?usStateId=" + this.$usStateSelect.val());
        }
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
                var report = reports[j];

                if (report.getId() === reportId) {
                    return report;
                }
            }
        }

        return null;
    },

    _doFilterResults: function(e) {
        var filter = this.$filter.val();

        if (filter.length < 2) {
            this.$results.show();
        } else {
            this.$results.each(function (index, element) {
                var isResultMatchedByFilter = false;

                var $article = jQuery(element);
                var tds = $article.find("td");

                // Title
                var $td = jQuery(tds.get(0));
                var value = jQuery($td.children().get(0)).attr("title");
                if(value.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
                    isResultMatchedByFilter = true;
                }

                // Name
                $td = jQuery(tds.get(1));
                value = $td.html();
                if(value.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
                    isResultMatchedByFilter = true;
                }

                // Party
                $td = jQuery(tds.get(2));
                value = jQuery($td.find("abbr")).attr("title");
                if(value.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
                    isResultMatchedByFilter = true;
                }

                // District
                $td = jQuery(tds.get(3));
                value = $td.html();
                if(value.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
                    isResultMatchedByFilter = true;
                }

                // Support level
                $td = jQuery(tds.get(4));
                var $span = $td.children();
                if ($span.size() > 0) {
                    value = $span.get(0).innerHTML;
                    if(value.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
                        isResultMatchedByFilter = true;
                    }
                }

                if (isResultMatchedByFilter) {
                    $article.show();
                } else {
                    $article.hide();
                }
            });
        }
    }
});
