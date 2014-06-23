CBR.Controllers.SearchLegislators = new Class({
    Extends: CBR.Controllers.LegislatorListing,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this.initEvents();

        this.doSubmit(null);

        window.setInterval(jQuery.proxy(this._doPeriodicSearch, this), 1000);
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
        this.$leadershipPositionSelect.change(jQuery.proxy(this.doSubmit, this));
        this.$committeeSelect.change(jQuery.proxy(this.doSubmit, this));

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

    doSubmit: function (e) {
        this.$searchResultsSection.html('<div class="data-loading"></div>');

        var selectedLeadershipPositionId = this.$leadershipPositionSelect.val();
        var selectedCommitteeName = this.$committeeSelect.val();

        var stateLegislatorSearch = {
            usStateId: this.$usStateSelect.val(),
            leadershipPositionId: selectedLeadershipPositionId ? selectedLeadershipPositionId : null,
            committeeName: selectedCommitteeName ? selectedCommitteeName : null
        };

        new Request({
            urlEncoded: false,
            headers: { "Content-Type": "application/json" },
            url: "/api/state-legislators",
            data: stateLegislatorSearch, // GET request doesn't require JSON.stringify()
            onSuccess: function (responseText, responseXML) {
                this._storeMatchingStateLegislators(JSON.parse(responseText));
                this._createResultsTable();
            }.bind(this),
            onFailure: function (xhr) {
                alert("AJAX fail :(");
            }
        }).get();
    },

    _doPeriodicSearch: function () {
        if (!this.isPeriodicSearchRunning) {
            this.isPeriodicSearchRunning = true;

            var selectedLeadershipPositionId = this.$leadershipPositionSelect.val();
            var selectedCommitteeName = this.$committeeSelect.val();

            var stateLegislatorSearch = {
                usStateId: this.$usStateSelect.val(),
                leadershipPositionId: selectedLeadershipPositionId ? selectedLeadershipPositionId : null,
                committeeName: selectedCommitteeName ? selectedCommitteeName : null
            };

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                url: "/api/state-legislators",
                data: stateLegislatorSearch, // GET request doesn't require JSON.stringify()
                onSuccess: function (responseText, responseXML) {
                    this._storeMatchingStateLegislators(JSON.parse(responseText));
                    this._updateResultsTable();
                    this.isPeriodicSearchRunning = false;
                }.bind(this),
                onFailure: function (xhr) {
                    // We do nothing here, because it's quite likely that the user leaves/refreshed the page during one of
                    // those AJAX calls, in which case it will fail, and we want that failure to be silent
                    this.isPeriodicSearchRunning = false;
                }.bind(this)
            }).get();
        }
    },

    getStateLegislators: function () {
        return this.matchingStateLegislators;
    },

    _storeMatchingStateLegislators: function (stateLegislators) {
        this.matchingStateLegislators = stateLegislators.map(function (stateLegislator) {
            return new CBR.Models.StateLegislator(stateLegislator);
        });
    },

    _createResultsTable: function () {
        this.$searchResultsSection.html(
            CBR.Templates.searchLegislatorsResults({
                legislators: this.matchingStateLegislators
            })
        );

        this.toggleStickyTableHeader();

        this.$results = this.$searchResultsSection.find("tr");

        this.$searchResultsSection.find("tr.clickable").click(jQuery.proxy(this.onTableRowClick, this));

        var $tableCellsContainingIsMissingUrgentReportCheckbox = jQuery("td.is-missing-urgent-report");
        var $tableCellsContainingIsAPriorityTargetCheckbox = jQuery("td.is-a-priority-target");

        $tableCellsContainingIsMissingUrgentReportCheckbox.mouseenter(this.disableRowClick);
        $tableCellsContainingIsMissingUrgentReportCheckbox.mouseleave(this.enableRowClick);
        $tableCellsContainingIsAPriorityTargetCheckbox.mouseenter(this.disableRowClick);
        $tableCellsContainingIsAPriorityTargetCheckbox.mouseleave(this.enableRowClick);

        var $isAPriorityTargetCheckboxes = $tableCellsContainingIsAPriorityTargetCheckbox.children();
        var $isMissingUrgentReportCheckboxes = $tableCellsContainingIsMissingUrgentReportCheckbox.children();

        $isAPriorityTargetCheckboxes.change(jQuery.proxy(this.saveNewPriorityTargetStatus, this));
        $isMissingUrgentReportCheckboxes.change(jQuery.proxy(this.saveNewMissingUrgentReportStatus, this));
    },

    _updateResultsTable: function () {
        this.$results = this.$searchResultsSection.find("tr");

        this.$results.each(function (index, element) {
            var $tr = jQuery(element);

            var hasDataChanged = false;

            var legislatorWithUpdatedData = this.matchingStateLegislators[index];
            var reports = legislatorWithUpdatedData.getReports();

            if (reports.length > 0) {
                var latestReport = reports[0];

                // Support level
                var oldSupportLevel = $tr.find("span.support-level").html();
                var latestSupportLevel = latestReport.getReadableSupportLevel();

                if (oldSupportLevel !== latestSupportLevel) {
                    hasDataChanged = true;
                }

                var oldYesNoLabel, latestYesNo, oldReportOrTargetStatus, latestReportOrTargetStatus;

                // MPP
                if (!hasDataChanged) {
                    oldYesNoLabel = $tr.children(".mpp").children().html();
                    latestYesNo = latestReport.isMoneyInPoliticsAProblem();

                    if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                        (oldYesNoLabel === "N" && latestYesNo !== false) ||
                        (oldYesNoLabel === "?" && latestYesNo !== null)) {
                        hasDataChanged = true;
                    }
                }

                // SAFI
                if (!hasDataChanged) {
                    oldYesNoLabel = $tr.children(".safi").children().html();
                    latestYesNo = latestReport.isSupportingAmendmentToFixIt();

                    if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                        (oldYesNoLabel === "N" && latestYesNo !== false) ||
                        (oldYesNoLabel === "?" && latestYesNo !== null)) {
                        hasDataChanged = true;
                    }
                }

                // OCU
                if (!hasDataChanged) {
                    oldYesNoLabel = $tr.children(".ocu").children().html();
                    latestYesNo = latestReport.isOpposingCitizensUnited();

                    if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                        (oldYesNoLabel === "N" && latestYesNo !== false) ||
                        (oldYesNoLabel === "?" && latestYesNo !== null)) {
                        hasDataChanged = true;
                    }
                }

                // PVC
                if (!hasDataChanged) {
                    oldYesNoLabel = $tr.children(".pvc").children().html();
                    latestYesNo = latestReport.hasPreviouslyVotedForConvention();

                    if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                        (oldYesNoLabel === "N" && latestYesNo !== false) ||
                        (oldYesNoLabel === "?" && latestYesNo !== null)) {
                        hasDataChanged = true;
                    }
                }

                // Missing urgent report
                if (!hasDataChanged) {
                    oldReportOrTargetStatus = $tr.children(".is-missing-urgent-report").children().prop("checked");
                    latestReportOrTargetStatus = legislatorWithUpdatedData.isMissingUrgentReport();

                    if (oldReportOrTargetStatus !== latestReportOrTargetStatus) {
                        hasDataChanged = true;
                    }
                }

                // Priority target
                if (!hasDataChanged) {
                    oldReportOrTargetStatus = $tr.children(".is-a-priority-target").children().prop("checked");
                    latestReportOrTargetStatus = legislatorWithUpdatedData.isAPriorityTarget();

                    if (oldReportOrTargetStatus !== latestReportOrTargetStatus) {
                        hasDataChanged = true;
                    }
                }
            }

            if (hasDataChanged) {
                $tr.html(
                    CBR.Templates.searchLegislatorsResultRow(this.matchingStateLegislators[index])
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
        }.bind(this));
    }
});
