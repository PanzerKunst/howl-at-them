CBR.Controllers.LegislatorListing = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    initElements: function () {
        this.parent();

        this.$usStateSelect = jQuery("#us-state");

        this.$filterSection = jQuery(".table-filter");
        this.$textFilters = this.$filterSection.find("input[type=text]");
        this.$titleFilter = jQuery("#title-filter");
        this.$nameFilter = jQuery("#name-filter");
        this.$partyFilter = jQuery("#party-filter");
        this.$districtFilter = jQuery("#district-filter");
        this.$supportLevelFilter = jQuery("#support-level-filter");
        this.$mppFilter = jQuery("#mpp-filter");
        this.$safiFilter = jQuery("#safi-filter");
        this.$ocuFilter = jQuery("#ocu-filter");
        this.$pvcFilter = jQuery("#pvc-filter");
        this.$isMissingUrgentReportFilter = jQuery("#is-missing-urgent-report-filter");
        this.$isAPriorityTargetFilter = jQuery("#is-a-priority-target-filter");

        this.$stickyTableHeader = jQuery("#sticky-table-header");

        this.$searchResultsSection = jQuery("#search-results");
    },

    initEvents: function () {
        this.$usStateSelect.change(jQuery.proxy(this.doSubmit, this));

        this.$textFilters.keyup(_.debounce(jQuery.proxy(this.filterResults, this), 100));
        this.$isMissingUrgentReportFilter.change(jQuery.proxy(this.filterResults, this));
        this.$isAPriorityTargetFilter.change(jQuery.proxy(this.filterResults, this));
        this.$filterSection.find(".close").click(jQuery.proxy(this._resetFilter, this));

        jQuery(window).scroll(_.debounce(jQuery.proxy(this.toggleStickyTableHeader, this), 15));
    },

    filterResults: function (e) {
        var reportColIndex = 9;
        var targetColIndex = 10;

        if (jQuery("body").attr("id") === "search-legislators") {
            reportColIndex++;
            targetColIndex++;
        }

        this.$results.each(jQuery.proxy(function (index, element) {
            var isResultMatchedByFilter = true;

            var $row = jQuery(element);
            var tds = $row.find("td");

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

            // Latest contact
            if (isResultMatchedByFilter && this.$latestContactFilter) {
                filter = this.$latestContactFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds.get(9));
                    value = $td.html();
                    if (value.substring(0, 1).toLowerCase() !== filter.substring(0, 1).toLowerCase()) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // Is missing urgent report
            if (isResultMatchedByFilter) {
                if (this.$isMissingUrgentReportFilter.prop("checked")) {
                    $td = jQuery(tds.get(reportColIndex));
                    value = $td.children().prop("checked");
                    if (!value) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // Is a priority target
            if (isResultMatchedByFilter) {
                if (this.$isAPriorityTargetFilter.prop("checked")) {
                    $td = jQuery(tds.get(targetColIndex));
                    value = $td.children().prop("checked");
                    if (!value) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            if (isResultMatchedByFilter) {
                $row.show();
            } else {
                $row.hide();
            }
        }, this));
    },

    _resetFilter: function (e) {
        this.$textFilters.val("");
        this.$isMissingUrgentReportFilter.prop("checked", false);
        this.$isAPriorityTargetFilter.prop("checked", false);
        this.filterResults(null);
    },

    onFullWidthBreakpointMatch: function () {
        this.isBrowserFullWidth = true;

        if (!this.$filterSection.visible(true)) {
            this.$stickyTableHeader.show();
        }
    },

    onFullWidthBreakpointExit: function () {
        this.isBrowserFullWidth = false;

        this.$stickyTableHeader.hide();
    },

    toggleStickyTableHeader: function () {
        if (this.isBrowserFullWidth && !this.$filterSection.visible(true)) {
            this.$stickyTableHeader.show();
        } else {
            this.$stickyTableHeader.hide();
        }
    },

    disableRowClick: function (e) {
        var $tr = jQuery(e.currentTarget).parent();
        $tr.removeClass("clickable");
    },

    enableRowClick: function (e) {
        var $tr = jQuery(e.currentTarget).parent();
        $tr.addClass("clickable");
    },

    onTableRowClick: function (e) {
        var $tr = jQuery(e.currentTarget);
        if ($tr.hasClass("clickable")) {
            this.navigateToStateLegislatorPage(e);
        }
    }
});
