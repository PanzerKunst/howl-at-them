CBR.Controllers.StateReports = new Class({
    Extends: CBR.Controllers.LegislatorListing,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this._initValidation();
        this.initEvents();
    },

    initElements: function () {
        this.parent();

        this.$whipCountListItem = jQuery("#whip-count-per-chamber li");

        this.$results = jQuery("#search-results > article");
        this.$tableHeaders = jQuery(jQuery("#search-results thead"));

        this.$clickableTableRows = jQuery("tr.clickable");

        this.$tableCellsContainingIsAPriorityTargetCheckbox = jQuery("td.is-a-priority-target");
        this.$tableCellsContainingIsMissingUrgentReportCheckbox = jQuery("td.is-missing-urgent-report");

        this.$isMissingUrgentReportCheckboxes = this.$tableCellsContainingIsMissingUrgentReportCheckbox.children();
        this.$isAPriorityTargetCheckboxes = this.$tableCellsContainingIsAPriorityTargetCheckbox.children();

        this.addEditAndDeleteReportLinks();
        this.fadeOutFloatingAlerts();
    },

    getStateLegislators: function () {
        var result = [];

        this.options.legislators.forEach(function (item, index) {
            result.push(new CBR.Models.StateLegislator(item));
        });

        return result;
    },

    _initValidation: function () {
        this.initEditReportValidation();
    },

    initEvents: function () {
        this.parent();

        this.$whipCountListItem.mouseenter(jQuery.proxy(this._showWhipCountPercentage, this));
        this.$whipCountListItem.mouseleave(jQuery.proxy(this._showWhipCountCount, this));

        this.$clickableTableRows.click(jQuery.proxy(this.onTableRowClick, this));

        this.$tableCellsContainingIsAPriorityTargetCheckbox.mouseenter(this.disableRowClick);
        this.$tableCellsContainingIsAPriorityTargetCheckbox.mouseleave(this.enableRowClick);
        this.$tableCellsContainingIsMissingUrgentReportCheckbox.mouseenter(this.disableRowClick);
        this.$tableCellsContainingIsMissingUrgentReportCheckbox.mouseleave(this.enableRowClick);

        this.$isMissingUrgentReportCheckboxes.change(jQuery.proxy(this.saveNewMissingUrgentReportStatus, this));
        this.$isAPriorityTargetCheckboxes.change(jQuery.proxy(this.saveNewPriorityTargetStatus, this));

        jQuery(".edit-report").click(jQuery.proxy(this._showEditReportModal, this));
        jQuery(".delete-report").click(jQuery.proxy(this._showDeleteReportModal, this));

        Breakpoints.on({
            name: "STATE_REPORTS_FULL_WIDTH_BREAKPOINT",
            matched: jQuery.proxy(this.onFullWidthBreakpointMatch, this),
            exit: jQuery.proxy(this.onFullWidthBreakpointExit, this)
        });
    },

    doSubmit: function (e) {
        this.$searchResultsSection.html('<div class="data-loading"></div>');
        location.replace("/state-reports?usStateId=" + this.$usStateSelect.val());
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

    onFullWidthBreakpointMatch: function () {
        this.$tableHeaders.hide();
        this.parent();
    },

    onFullWidthBreakpointExit: function () {
        this.parent();
        this.$tableHeaders.show();
    },

    _showWhipCountPercentage: function (e) {
        var $li = jQuery(e.currentTarget);
        $li.addClass("hover");
        $li.children(".count").hide();
        $li.children(".percentage").show();
    },

    _showWhipCountCount: function (e) {
        var $li = jQuery(e.currentTarget);
        $li.removeClass("hover");
        $li.children(".percentage").hide();
        $li.children(".count").show();
    }
});
