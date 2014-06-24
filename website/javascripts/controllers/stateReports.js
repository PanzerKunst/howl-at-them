CBR.Controllers.StateReports = new Class({
    Extends: CBR.Controllers.LegislatorListing,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this._initValidation();
        this.initEvents();

        this.parent();
    },

    initElements: function () {
        this.parent();

        this.$whipCountListItem = jQuery("#whip-count-per-chamber li");

        this.fadeOutFloatingAlerts();
    },

    _initValidation: function () {
        this.initEditReportValidation();
    },

    initEvents: function () {
        this.parent();

        this.$whipCountListItem.mouseenter(jQuery.proxy(this._showWhipCountPercentage, this));
        this.$whipCountListItem.mouseleave(jQuery.proxy(this._showWhipCountCount, this));

        Breakpoints.on({
            name: "STATE_REPORTS_FULL_WIDTH_BREAKPOINT",
            matched: jQuery.proxy(this.onFullWidthBreakpointMatch, this),
            exit: jQuery.proxy(this.onFullWidthBreakpointExit, this)
        });
    },

    createResultsTable: function () {
        this.$searchResultsSection.html(
            CBR.Templates.stateReportsResults({
                legislators: this.getStateLegislators()
            })
        );

        this.parent();

        this.addEditAndDeleteReportLinks();

        jQuery(".edit-report").click(jQuery.proxy(this._showEditReportModal, this));
        jQuery(".delete-report").click(jQuery.proxy(this._showDeleteReportModal, this));

        this.$tableHeaders = jQuery("#search-results thead");

        if (jQuery.browser.mobile || !this._isBrowserFullWidth()) {
            this.$tableHeaders.show();
        }
    },

    updateResultsTable: function () {
        this.$results = this.$searchResultsSection.children();

        this.$results.each(function (index, element) {
            var $article = jQuery(element);
            var $tr = $article.find("tr");

            if (this.isDataChangedForRow(index, $tr)) {
                $article.html(
                    CBR.Templates.stateReportsResultRow({
                        isFirstRow: index === 0,
                        legislator: this.getStateLegislators()[index]
                    })
                );

                $tr = $article.find("tr");

                var $tableCellsContainingIsMissingUrgentReportCheckbox = $tr.children(".is-missing-urgent-report");
                var $tableCellsContainingIsAPriorityTargetCheckbox = $tr.children(".is-a-priority-target");

                $tableCellsContainingIsMissingUrgentReportCheckbox.mouseenter(this.disableRowClick);
                $tableCellsContainingIsMissingUrgentReportCheckbox.mouseleave(this.enableRowClick);
                $tableCellsContainingIsAPriorityTargetCheckbox.mouseenter(this.disableRowClick);
                $tableCellsContainingIsAPriorityTargetCheckbox.mouseleave(this.enableRowClick);

                var $isAPriorityTargetCheckboxes = $tableCellsContainingIsAPriorityTargetCheckbox.children();
                var $isMissingUrgentReportCheckboxes = $tableCellsContainingIsMissingUrgentReportCheckbox.children();

                $isAPriorityTargetCheckboxes.change(jQuery.proxy(this.saveNewPriorityTargetStatus, this));
                $isMissingUrgentReportCheckboxes.change(jQuery.proxy(this.saveNewMissingUrgentReportStatus, this));

                // Edit and Delete report links
                $article.children(".reports").children().each(function (index, element) {
                    var $reportArticle = jQuery(element);
                    var idOfCreatedReports = this.getIdOfCreatedReports();

                    var $editLink = jQuery('<a class="edit-report">Edit</a>');
                    var $deleteLink = jQuery('<a class="delete-report">Delete</a>');

                    $editLink.click(jQuery.proxy(this._showEditReportModal, this));
                    $deleteLink.click(jQuery.proxy(this._showDeleteReportModal, this));

                    if (this.isAdmin()) {
                        jQuery($reportArticle.children("div")[0]).append($deleteLink);
                        jQuery($reportArticle.children("div")[0]).append($editLink);
                    }
                    else {
                        var reportId = parseInt($reportArticle.data("id"), 10);

                        var isCreatedByUser = false;

                        for (var i = 0; i < idOfCreatedReports.length; i++) {
                            if (idOfCreatedReports[i] === reportId) {
                                isCreatedByUser = true;
                                break;
                            }
                        }

                        if (isCreatedByUser) {
                            jQuery($reportArticle.children("div")[0]).append($deleteLink);
                            jQuery($reportArticle.children("div")[0]).append($editLink);
                        }
                    }
                }.bind(this));
            }
        }.bind(this));
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
        var legislators = this.getStateLegislators();

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
        if (!jQuery.browser.mobile && this.$tableHeaders) {
            this.$tableHeaders.hide();
        }

        this.parent();
    },

    onFullWidthBreakpointExit: function () {
        this.parent();

        if (!jQuery.browser.mobile && this.$tableHeaders) {
            this.$tableHeaders.show();
        }
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
    },

    _isBrowserFullWidth: function () {
        var content = window.getComputedStyle(
            document.querySelector("body"), ":after"
        ).getPropertyValue("content");

        // In some browsers like Firefox, "content" is wrapped by double-quotes, that's why doing "return content === "GLOBAL_MEDIUM_SCREEN_BREAKPOINT" would be false.
        return _.contains(content, "STATE_REPORTS_FULL_WIDTH_BREAKPOINT");
    }
});
