CBR.Controllers.LegislatorListing = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.doSubmit(null);
        window.setInterval(jQuery.proxy(this._doPeriodicSearch, this), 1000);
    },

    initElements: function () {
        this.parent();

        this.$usStateSelect = jQuery("#us-state");

        this.$chamberFilterRadios = jQuery("[name='chamber-filter']");
        this.$houseChamberFilterRadio = this.$chamberFilterRadios.filter("[value='" + CBR.Models.StateLegislator.chamber.house.abbr + "']");
        this.$senateChamberFilterRadio = this.$chamberFilterRadios.filter("[value='" + CBR.Models.StateLegislator.chamber.senate.abbr + "']");

        this.$filterSection = jQuery(".table-filter");
        this.$textFilters = this.$filterSection.find("input[type=text]");
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

        var selectedChamberFilter = this.getFromLocalStorage("selectedChamberFilter", true);

        if (_.isEqual(selectedChamberFilter, CBR.Models.StateLegislator.chamber.house)) {
            this.$houseChamberFilterRadio.prop('checked', true);
        } else if (_.isEqual(selectedChamberFilter, CBR.Models.StateLegislator.chamber.senate)) {
            this.$senateChamberFilterRadio.prop('checked', true);
        }
    },

    initEvents: function () {
        this.$usStateSelect.change(jQuery.proxy(this.doSubmit, this));

        this.$chamberFilterRadios.change(jQuery.proxy(this._onChamberFilterChanged, this));

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

            // Name
            filter = this.$nameFilter.val();
            if (filter.length > 0) {
                $td = jQuery(tds[1]);
                value = $td.html();
                if (!value.toLowerCase().startsWith(filter.toLowerCase())) {
                    isResultMatchedByFilter = false;
                }
            }

            // Party
            if (isResultMatchedByFilter) {
                filter = this.$partyFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds[2]);
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
                    $td = jQuery(tds[3]);
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
                    $td = jQuery(tds[4]);
                    value = $td.children().html();
                    if (!value.toLowerCase().startsWith(filter.toLowerCase())) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // MPP
            if (isResultMatchedByFilter) {
                filter = this.$mppFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds[5]);
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
                    $td = jQuery(tds[6]);
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
                    $td = jQuery(tds[7]);
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
                    $td = jQuery(tds[8]);
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
                    $td = jQuery(tds[9]);
                    value = $td.html();
                    if (value.substring(0, 1).toLowerCase() !== filter.substring(0, 1).toLowerCase()) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // Is missing urgent report
            if (isResultMatchedByFilter) {
                if (this.$isMissingUrgentReportFilter.prop("checked")) {
                    $td = jQuery(tds[reportColIndex]);
                    value = $td.children().prop("checked");
                    if (!value) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // Is a priority target
            if (isResultMatchedByFilter) {
                if (this.$isAPriorityTargetFilter.prop("checked")) {
                    $td = jQuery(tds[targetColIndex]);
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

    doSubmit: function (e) {
        this.$searchResultsSection.html('<div class="data-loading"></div>');

        var selectedLeadershipPositionId = this.$leadershipPositionSelect ? this.$leadershipPositionSelect.val() : null;
        var selectedCommitteeName = this.$committeeSelect ? this.$committeeSelect.val() : null;

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
                this.createResultsTable();
            }.bind(this),
            onFailure: function (xhr) {
                alert("AJAX fail :(");
            }
        }).get();
    },

    createResultsTable: function () {
        this._filterChamber();

        this.toggleStickyTableHeader();

        this.$searchResultsSection.find("tr.clickable").click(jQuery.proxy(this.onTableRowClick, this));

        var $tableCellsContainingIsMissingUrgentReportCheckbox = this.$results.children(".is-missing-urgent-report");
        var $tableCellsContainingIsAPriorityTargetCheckbox = this.$results.children(".is-a-priority-target");

        $tableCellsContainingIsMissingUrgentReportCheckbox.mouseenter(this.disableRowClick);
        $tableCellsContainingIsMissingUrgentReportCheckbox.mouseleave(this.enableRowClick);
        $tableCellsContainingIsAPriorityTargetCheckbox.mouseenter(this.disableRowClick);
        $tableCellsContainingIsAPriorityTargetCheckbox.mouseleave(this.enableRowClick);

        var $isAPriorityTargetCheckboxes = $tableCellsContainingIsAPriorityTargetCheckbox.children();
        var $isMissingUrgentReportCheckboxes = $tableCellsContainingIsMissingUrgentReportCheckbox.children();

        $isAPriorityTargetCheckboxes.change(jQuery.proxy(this.saveNewPriorityTargetStatus, this));
        $isMissingUrgentReportCheckboxes.change(jQuery.proxy(this.saveNewMissingUrgentReportStatus, this));
    },

    _onChamberFilterChanged: function(e) {
        var selectedValue = e.currentTarget.value;

        if (selectedValue === CBR.Models.StateLegislator.chamber.house.abbr) {
            this.saveInLocalStorage("selectedChamberFilter", CBR.Models.StateLegislator.chamber.house, true);
        } else if (selectedValue === CBR.Models.StateLegislator.chamber.senate.abbr) {
            this.saveInLocalStorage("selectedChamberFilter", CBR.Models.StateLegislator.chamber.senate, true);
        } else {
            this.removeFromLocalStorage("selectedChamberFilter", true);
        }

        this._filterChamber();
    },

    _filterChamber: function () {
        var selectedChamberFilter = null;
        if (this.$houseChamberFilterRadio.prop("checked")) {
            selectedChamberFilter = CBR.Models.StateLegislator.chamber.house;
        } else if (this.$senateChamberFilterRadio.prop("checked")) {
            selectedChamberFilter = CBR.Models.StateLegislator.chamber.senate;
        }

        // Reset: show all
        this.$results.show();

        if (selectedChamberFilter) {
            this.$results.each(function (index, element) {
                var $row = jQuery(element);

                var legislator = this._getStateLegislatorOfId($row.data("id"));

                if (!_.isEqual(legislator.getChamber(), selectedChamberFilter)) {
                    $row.hide();
                }
            }.bind(this));
        }
    },

    _doPeriodicSearch: function () {
        if (!this.isPeriodicSearchRunning) {
            this.isPeriodicSearchRunning = true;

            var selectedLeadershipPositionId = this.$leadershipPositionSelect ? this.$leadershipPositionSelect.val() : null;
            var selectedCommitteeName = this.$committeeSelect ? this.$committeeSelect.val() : null;

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
                    this.updateResultsTable();
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
    },

    saveNewPriorityTargetStatus: function (e) {
        var $checkbox = jQuery(e.currentTarget);

        var isAPriorityTarget = $checkbox.prop("checked");
        var stateLegislatorId = $checkbox.parent().parent().data("id");

        this._updateStateLegislator(this._getStateLegislatorOfId(stateLegislatorId), isAPriorityTarget, null, "Target status saved");
    },

    saveNewMissingUrgentReportStatus: function (e) {
        var $checkbox = jQuery(e.currentTarget);

        var isMissingUrgentReport = $checkbox.prop("checked");
        var stateLegislatorId = $checkbox.parent().parent().data("id");

        this._updateStateLegislator(this._getStateLegislatorOfId(stateLegislatorId), null, isMissingUrgentReport, "Report status saved");
    },

    getStateLegislators: function () {
        return this.matchingStateLegislators;
    },

    _storeMatchingStateLegislators: function (stateLegislators) {
        this.matchingStateLegislators = stateLegislators.map(function (stateLegislator) {
            return new CBR.Models.StateLegislator(stateLegislator);
        });
    },

    _updateStateLegislator: function (stateLegislator, isAPriorityTarget, isMissingUrgentReport, floatingAlertText) {
        var updatedStateLegislator = {
            id: stateLegislator.getId(),
            firstName: stateLegislator.getFirstName(),
            lastName: stateLegislator.getLastName(),
            title: stateLegislator.getTitle(),
            politicalParties: stateLegislator.getPoliticalParties(),
            usState: stateLegislator.getUsState(),
            district: stateLegislator.getDistrict(),
            leadershipPosition: stateLegislator.getLeadershipPosition(),
            offices: stateLegislator.getOffices(),
            committees: stateLegislator.getCommittees(),
            reports: stateLegislator.getReports(),
            otherPhoneNumber: stateLegislator.getOtherPhoneNumber(),
            isAPriorityTarget: isAPriorityTarget !== null ? isAPriorityTarget : stateLegislator.isAPriorityTarget(),
            isMissingUrgentReport: isMissingUrgentReport !== null ? isMissingUrgentReport : stateLegislator.isMissingUrgentReport()
        };

        new Request({
            urlEncoded: false,
            headers: { "Content-Type": "application/json" },
            emulation: false, // Otherwise PUT and DELETE requests are sent as POST
            url: "/api/state-legislators/",
            data: JSON.stringify(updatedStateLegislator),
            onSuccess: function (responseText, responseXML) {
                this.showAlert(floatingAlertText);
            }.bind(this),
            onFailure: function (xhr) {
                alert("AJAX fail :(");
            }
        }).put();
    },

    _getStateLegislatorOfId: function (id) {
        return _.find(this.getStateLegislators(), function (legislator) {
            return legislator.getId() === id;
        });
    },

    isDataChangedForRow: function (index, $tr) {
        var legislatorWithUpdatedData = this.getStateLegislators()[index];
        if (legislatorWithUpdatedData) {
            var latestReport = legislatorWithUpdatedData.getLatestReport();

            if (latestReport) {
                // Support level
                var oldSupportLevel = $tr.find("span.support-level").html();
                var latestSupportLevel = latestReport.getReadableSupportLevel();

                if (oldSupportLevel !== latestSupportLevel) {
                    return true;
                }

                // MPP
                var oldYesNoLabel = $tr.children(".mpp").children().html();
                var latestYesNo = latestReport.isMoneyInPoliticsAProblem();

                if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                    (oldYesNoLabel === "N" && latestYesNo !== false) ||
                    (oldYesNoLabel === "?" && latestYesNo !== null)) {
                    return true;
                }

                // SAFI
                oldYesNoLabel = $tr.children(".safi").children().html();
                latestYesNo = latestReport.isSupportingAmendmentToFixIt();

                if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                    (oldYesNoLabel === "N" && latestYesNo !== false) ||
                    (oldYesNoLabel === "?" && latestYesNo !== null)) {
                    return true;
                }

                // OCU
                oldYesNoLabel = $tr.children(".ocu").children().html();
                latestYesNo = latestReport.isOpposingCitizensUnited();

                if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                    (oldYesNoLabel === "N" && latestYesNo !== false) ||
                    (oldYesNoLabel === "?" && latestYesNo !== null)) {
                    return true;
                }

                // PVC
                oldYesNoLabel = $tr.children(".pvc").children().html();
                latestYesNo = latestReport.hasPreviouslyVotedForConvention();

                if ((oldYesNoLabel === "Y" && latestYesNo !== true) ||
                    (oldYesNoLabel === "N" && latestYesNo !== false) ||
                    (oldYesNoLabel === "?" && latestYesNo !== null)) {
                    return true;
                }

                // Missing urgent report
                var oldReportOrTargetStatus = $tr.children(".is-missing-urgent-report").children().prop("checked");
                var latestReportOrTargetStatus = legislatorWithUpdatedData.isMissingUrgentReport();

                if (oldReportOrTargetStatus !== latestReportOrTargetStatus) {
                    return true;
                }

                // Priority target
                oldReportOrTargetStatus = $tr.children(".is-a-priority-target").children().prop("checked");
                latestReportOrTargetStatus = legislatorWithUpdatedData.isAPriorityTarget();

                if (oldReportOrTargetStatus !== latestReportOrTargetStatus) {
                    return true;
                }
            }
        }
        return false;
    }
});
