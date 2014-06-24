CBR.Controllers.SearchLegislators = new Class({
    Extends: CBR.Controllers.LegislatorListing,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this.initEvents();

        this.parent();
    },

    initElements: function () {
        this.parent();

        this.$advancedSearchFields = jQuery("#advanced-search-fields");
        this.$leadershipPositionSelect = jQuery("#leadership-position");
        this.$committeeSelect = jQuery("#committee");

        this.$latestContactFilter = jQuery("#latest-contact-filter");
    },

    initEvents: function () {
        this.parent();

        jQuery("#advanced-toggle").click(jQuery.proxy(this._toggleAdvancedSearch, this));

        this.$usStateSelect.change(jQuery.proxy(function (e) {
            this._populateLeadershipPositionsSelect(e);
            this._populateCommitteesSelect(e);
        }, this));

        this.$usStateSelect.change(jQuery.proxy(this.doSubmit, this));

        this.$leadershipPositionSelect.change(jQuery.proxy(function (e) {
            this.$committeeSelect[0].selectedIndex = 0;
            this.doSubmit(null);
        }, this));

        this.$committeeSelect.change(jQuery.proxy(function (e) {
            this.$leadershipPositionSelect[0].selectedIndex = 0;
            this.doSubmit(null);
        }, this));

        Breakpoints.on({
            name: "SEARCH_LEGISLATORS_FULL_WIDTH_BREAKPOINT",
            matched: jQuery.proxy(this.onFullWidthBreakpointMatch, this),
            exit: jQuery.proxy(this.onFullWidthBreakpointExit, this)
        });
    },

    _toggleAdvancedSearch: function (e) {
        if (this.$advancedSearchFields.is(":visible")) {
            this.$advancedSearchFields.slideUpCustom();
        } else {
            this.$advancedSearchFields.slideDownCustom();
        }
    },

    _populateLeadershipPositionsSelect: function (e) {
        new Request({
            urlEncoded: false,
            headers: { "Content-Type": "application/json" },
            url: "/api/leadership-positions",
            data: {usStateId: this.$usStateSelect.val()}, // GET request doesn't require JSON.stringify()
            onSuccess: function (responseText, responseXML) {
                this.$leadershipPositionSelect.html(
                    CBR.Templates.leadershipPositionSelect({
                        leadershipPositions: JSON.parse(responseText)
                    })
                );
                this.$leadershipPositionSelect.prop("disabled", false);
            }.bind(this),
            onFailure: function (xhr) {
                alert("AJAX fail :(");
            }
        }).get();
    },

    _populateCommitteesSelect: function (e) {
        new Request({
            urlEncoded: false,
            headers: { "Content-Type": "application/json" },
            url: "/api/committees",
            data: {usStateId: this.$usStateSelect.val()}, // GET request doesn't require JSON.stringify()
            onSuccess: function (responseText, responseXML) {
                this.$committeeSelect.html(
                    CBR.Templates.committeeSelect({
                        committeeNames: JSON.parse(responseText)
                    })
                );
                this.$committeeSelect.prop("disabled", false);
            }.bind(this),
            onFailure: function (xhr) {
                alert("AJAX fail :(");
            }
        }).get();
    },

    createResultsTable: function () {
        this.$searchResultsSection.html(
            CBR.Templates.searchLegislatorsResults({
                legislators: this.getStateLegislators()
            })
        );

        this.parent();
    },

    updateResultsTable: function () {
        this.$results = this.$searchResultsSection.find("tr");

        this.$results.each(function (index, element) {
            var $tr = jQuery(element);

            var isDataChanged = false;

            var legislatorWithUpdatedData = this.getStateLegislators()[index];
            if (legislatorWithUpdatedData) {
                var latestReport = legislatorWithUpdatedData.getLatestReport();

                if (latestReport) {
                    // Support level
                    var oldSupportLevel = $tr.find("span.support-level").html();
                    var latestSupportLevel = latestReport.getReadableSupportLevel();

                    if (oldSupportLevel !== latestSupportLevel) {
                        isDataChanged = true;
                    }

                    var oldYesNoLabel, latestYesNo, oldReportOrTargetStatus, latestReportOrTargetStatus;

                    // MPP
                    if (!isDataChanged) {
                        oldYesNoLabel = $tr.children(".mpp").children().html();
                        latestYesNo = latestReport.isMoneyInPoliticsAProblem();

                        if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                            (oldYesNoLabel === "N" && latestYesNo !== false) ||
                            (oldYesNoLabel === "?" && latestYesNo !== null)) {
                            isDataChanged = true;
                        }
                    }

                    // SAFI
                    if (!isDataChanged) {
                        oldYesNoLabel = $tr.children(".safi").children().html();
                        latestYesNo = latestReport.isSupportingAmendmentToFixIt();

                        if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                            (oldYesNoLabel === "N" && latestYesNo !== false) ||
                            (oldYesNoLabel === "?" && latestYesNo !== null)) {
                            isDataChanged = true;
                        }
                    }

                    // OCU
                    if (!isDataChanged) {
                        oldYesNoLabel = $tr.children(".ocu").children().html();
                        latestYesNo = latestReport.isOpposingCitizensUnited();

                        if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                            (oldYesNoLabel === "N" && latestYesNo !== false) ||
                            (oldYesNoLabel === "?" && latestYesNo !== null)) {
                            isDataChanged = true;
                        }
                    }

                    // PVC
                    if (!isDataChanged) {
                        oldYesNoLabel = $tr.children(".pvc").children().html();
                        latestYesNo = latestReport.hasPreviouslyVotedForConvention();

                        if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                            (oldYesNoLabel === "N" && latestYesNo !== false) ||
                            (oldYesNoLabel === "?" && latestYesNo !== null)) {
                            isDataChanged = true;
                        }
                    }

                    // Missing urgent report
                    if (!isDataChanged) {
                        oldReportOrTargetStatus = $tr.children(".is-missing-urgent-report").children().prop("checked");
                        latestReportOrTargetStatus = legislatorWithUpdatedData.isMissingUrgentReport();

                        if (oldReportOrTargetStatus !== latestReportOrTargetStatus) {
                            isDataChanged = true;
                        }
                    }

                    // Priority target
                    if (!isDataChanged) {
                        oldReportOrTargetStatus = $tr.children(".is-a-priority-target").children().prop("checked");
                        latestReportOrTargetStatus = legislatorWithUpdatedData.isAPriorityTarget();

                        if (oldReportOrTargetStatus !== latestReportOrTargetStatus) {
                            isDataChanged = true;
                        }
                    }
                }

                if (isDataChanged) {
                    $tr.html(
                        CBR.Templates.searchLegislatorsResultRow(this.getStateLegislators()[index])
                    );

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
                }
            }
        }.bind(this));
    }
});
