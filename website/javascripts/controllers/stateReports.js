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

        this.$usStateSelect = jQuery("#us-state");

        this.$whipCountListItem = jQuery("#whip-count-per-chamber li");

        this.$filterSection = jQuery(".table-filter");
        this.$inputFilters = this.$filterSection.find("input");
        this.$titleFilter = jQuery("#title-filter");
        this.$nameFilter = jQuery("#name-filter");
        this.$partyFilter = jQuery("#party-filter");
        this.$districtFilter = jQuery("#district-filter");
        this.$supportLevelFilter = jQuery("#support-level-filter");
        this.$mppFilter = jQuery("#mpp-filter");
        this.$safiFilter = jQuery("#safi-filter");
        this.$ocuFilter = jQuery("#ocu-filter");
        this.$pvcFilter = jQuery("#pvc-filter");

        this.$stickyTableHeader = jQuery("#sticky-table-header");
        this.$results = jQuery("#search-results > article");
        this.$secondOrSubsequentResultTableHeaders = jQuery(_.rest(jQuery("#search-results thead")));

        this.addEditAndDeleteReportLinks();
        this.fadeOutFloatingAlerts();
    },

    _getStateLegislators: function () {
        var result = [];

        this.options.legislators.forEach(function (item, index) {
            result.push(new CBR.Models.StateLegislator(item));
        });

        return result;
    },

    _initValidation: function () {
        this.initEditReportValidation();
    },

    _initEvents: function () {
        this.$usStateSelect.change(jQuery.proxy(this._doSubmit, this));

        this.$whipCountListItem.mouseenter(jQuery.proxy(this._showWhipCountPercentage, this));
        this.$whipCountListItem.mouseleave(jQuery.proxy(this._showWhipCountCount, this));

        this.$inputFilters.keyup(_.debounce(jQuery.proxy(this._filterResults, this), 100));
        this.$filterSection.find(".close").click(jQuery.proxy(this._resetFilter, this));

        jQuery("tr.clickable").click(jQuery.proxy(this.navigateToStateLegislatorPage, this));

        jQuery(".edit-report").click(jQuery.proxy(this._showEditReportModal, this));
        jQuery(".delete-report").click(jQuery.proxy(this._showDeleteReportModal, this));

        Breakpoints.on({
            name: "STATE_REPORTS_LARGE_SCREEN_BREAKPOINT",
            matched: jQuery.proxy(this._onLargeScreenBreakpointMatch, this),
            exit: jQuery.proxy(this._onLargeScreenBreakpointExit, this)
        });

        jQuery(window).scroll(_.debounce(jQuery.proxy(this._toggleStickyTableHeader, this), 15));
    },

    _doSubmit: function (e) {
        this.$filterSection.remove();
        jQuery("#table-header-visible-even-when-no-results").remove();
        jQuery("#search-results").html('<div class="data-loading"></div>');

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

    _filterResults: function (e) {
        this.$results.each(jQuery.proxy(function (index, element) {
            var isResultMatchedByFilter = true;

            var $article = jQuery(element);
            var tds = $article.find("td");

            var $td, value, filter;

            // Title
            filter = this.$titleFilter.val();
            if (filter.length > 0) {
                $td = jQuery(tds.get(0));
                value = jQuery($td.children().get(0)).attr("title");
                if (value.substring(0, 1).toLowerCase() !== filter.substring(0, 1).toLowerCase()) {
                    isResultMatchedByFilter = false;
                }
            }

            // Name
            if (isResultMatchedByFilter) {
                filter = this.$nameFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds.get(1));
                    value = $td.html();
                    if (!value.toLowerCase().startsWith(filter.toLowerCase())) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // Party
            if (isResultMatchedByFilter) {
                filter = this.$partyFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds.get(2));
                    value = jQuery($td.find("abbr")).html();
                    if (value.toLowerCase() !== filter.toLowerCase()) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // District
            if (isResultMatchedByFilter) {
                filter = this.$districtFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds.get(3));
                    value = $td.html();
                    if (value.toLowerCase().indexOf(filter.toLowerCase()) === -1) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // Support level
            if (isResultMatchedByFilter) {
                filter = this.$supportLevelFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds.get(4));
                    value = $td.children().html();
                    if (value.substring(0, 2).toLowerCase() !== filter.substring(0, 2).toLowerCase()) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // MPP
            if (isResultMatchedByFilter) {
                filter = this.$mppFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds.get(5));
                    value = $td.children().html();
                    if (value.toLowerCase() !== filter.toLowerCase()) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // SAFI
            if (isResultMatchedByFilter) {
                filter = this.$safiFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds.get(6));
                    value = $td.children().html();
                    if (value.toLowerCase() !== filter.toLowerCase()) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // OCU
            if (isResultMatchedByFilter) {
                filter = this.$ocuFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds.get(7));
                    value = $td.children().html();
                    if (value.toLowerCase() !== filter.toLowerCase()) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // PVC
            if (isResultMatchedByFilter) {
                filter = this.$pvcFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds.get(8));
                    value = $td.children().html();
                    if (value.toLowerCase() !== filter.toLowerCase()) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            if (isResultMatchedByFilter) {
                $article.show();
            } else {
                $article.hide();
            }
        }, this));
    },

    _resetFilter: function (e) {
        this.$inputFilters.val("");
        this._filterResults();
    },

    _onLargeScreenBreakpointMatch: function () {
        this.isBrowserLargeScreen = true;

        this.$secondOrSubsequentResultTableHeaders.hide();
        if (!this.$filterSection.visible(true)) {
            this.$stickyTableHeader.show();
        }
    },

    _onLargeScreenBreakpointExit: function () {
        this.isBrowserLargeScreen = false;

        this.$stickyTableHeader.hide();
        this.$secondOrSubsequentResultTableHeaders.show();
    },

    _toggleStickyTableHeader: function () {
        if (this.isBrowserLargeScreen && !this.$filterSection.visible(true)) {
            this.$stickyTableHeader.show();
        } else {
            this.$stickyTableHeader.hide();
        }
    },

    _showWhipCountPercentage: function(e) {
        var $li = jQuery(e.currentTarget);
        $li.addClass("hover");
        $li.children(".count").hide();
        $li.children(".percentage").show();
    },

    _showWhipCountCount: function(e) {
        var $li = jQuery(e.currentTarget);
        $li.removeClass("hover");
        $li.children(".percentage").hide();
        $li.children(".count").show();
    }
});
