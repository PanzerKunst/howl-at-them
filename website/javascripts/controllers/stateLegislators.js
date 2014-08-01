CBR.Controllers.StateLegislators = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this._initValidation();
        this._initEvents();

        this._doSubmit(null);
        window.setInterval(jQuery.proxy(this._doPeriodicSearch, this), 1000);
    },

    initElements: function () {
        this.parent();

        this.$usStateSelect = jQuery("#us-state");

        this.$chamberFilterRadios = jQuery("[name='chamber-filter']");
        this.$houseChamberFilterRadio = this.$chamberFilterRadios.filter("[value='" + CBR.Models.StateLegislator.chamber.house.abbr + "']");
        this.$senateChamberFilterRadio = this.$chamberFilterRadios.filter("[value='" + CBR.Models.StateLegislator.chamber.senate.abbr + "']");

        this.$leadershipPositionSelect = jQuery("#leadership-position");
        this.$committeeSelect = jQuery("#committee");

        this.$whipCountListItem = jQuery("#whip-count-per-chamber li");

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
        this.$tableHeaderVisibleEvenWhenNoResults = jQuery("#table-header-visible-even-when-no-results");

        this.$searchResultsSection = jQuery("#search-results");

        var selectedChamberFilter = this.getFromLocalStorage("selectedChamberFilter", true);

        if (_.isEqual(selectedChamberFilter, CBR.Models.StateLegislator.chamber.house)) {
            this.$houseChamberFilterRadio.prop('checked', true);
        } else if (_.isEqual(selectedChamberFilter, CBR.Models.StateLegislator.chamber.senate)) {
            this.$senateChamberFilterRadio.prop('checked', true);
        }

        this.fadeOutFloatingAlerts();
    },

    _initValidation: function () {
        this.initEditReportValidation();
    },

    _initEvents: function () {
        jQuery(window).scroll(_.debounce(jQuery.proxy(this._toggleStickyTableHeader, this), 15));

        this.$usStateSelect.change(function (e) {
            this._populateLeadershipPositionsSelect(e);
            this._populateCommitteesSelect(e);
            this._doSubmit(e);
        }.bind(this));

        this.$chamberFilterRadios.change(jQuery.proxy(this._onChamberFilterChanged, this));

        this.$leadershipPositionSelect.change(function (e) {
            this.$committeeSelect[0].selectedIndex = 0;
            this._doSubmit(e);
        }.bind(this));

        this.$committeeSelect.change(function (e) {
            this.$leadershipPositionSelect[0].selectedIndex = 0;
            this._doSubmit(e);
        }.bind(this));

        this.$whipCountListItem.mouseenter(jQuery.proxy(this._showWhipCountPercentage, this));
        this.$whipCountListItem.mouseleave(jQuery.proxy(this._showWhipCountCount, this));

        this.$textFilters.keyup(_.debounce(jQuery.proxy(this._filterResults, this), 100));
        this.$isMissingUrgentReportFilter.change(jQuery.proxy(this._filterResults, this));
        this.$isAPriorityTargetFilter.change(jQuery.proxy(this._filterResults, this));
        this.$filterSection.find(".close").click(jQuery.proxy(this._resetFilter, this));

        Breakpoints.on({
            name: "STATE_LEGISLATORS_FULL_WIDTH_BREAKPOINT",
            matched: jQuery.proxy(this._onFullWidthBreakpointMatch, this),
            exit: jQuery.proxy(this._onFullWidthBreakpointExit, this)
        });
    },

    _filterResults: function (e) {
        this.$results.each(jQuery.proxy(function (index, element) {
            var isResultMatchedByFilter = true;

            var $row = jQuery(element);
            var tds = $row.find("td");

            var $td, value, filter;

            // Name
            filter = this.$nameFilter.val();
            if (filter.length > 0) {
                $td = jQuery(tds[2]);
                value = $td.html();
                if (!value.toLowerCase().startsWith(filter.toLowerCase())) {
                    isResultMatchedByFilter = false;
                }
            }

            // Party
            if (isResultMatchedByFilter) {
                filter = this.$partyFilter.val();
                if (filter.length > 0) {
                    $td = jQuery(tds[3]);
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
                    $td = jQuery(tds[4]);
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
                    $td = jQuery(tds[5]);
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
                    $td = jQuery(tds[6]);
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
                    $td = jQuery(tds[7]);
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
                    $td = jQuery(tds[8]);
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
                    $td = jQuery(tds[9]);
                    value = $td.children().html();
                    if (value.toLowerCase() !== filter.toLowerCase()) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // Is missing urgent report
            if (isResultMatchedByFilter) {
                if (this.$isMissingUrgentReportFilter.prop("checked")) {
                    $td = jQuery(tds[10]);
                    value = $td.children().prop("checked");
                    if (!value) {
                        isResultMatchedByFilter = false;
                    }
                }
            }

            // Is a priority target
            if (isResultMatchedByFilter) {
                if (this.$isAPriorityTargetFilter.prop("checked")) {
                    $td = jQuery(tds[11]);
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

    _doSubmit: function (e) {
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
                this._createResultsTable();
            }.bind(this),
            onFailure: function (xhr) {
                alert("AJAX fail :(");
            }
        }).get();
    },

    _createResultsTable: function () {
        this.$searchResultsSection.html(
            CBR.Templates.stateLegislatorsResults({
                legislators: this._getStateLegislators(),
                isAdmin: this.isAdmin()
            })
        );

        this.$results = this.$searchResultsSection.children();

        this._filterChamber();

        this._toggleStickyTableHeader();

        this.$searchResultsSection.find("tr.clickable").click(jQuery.proxy(this._onTableRowClick, this));

        var $tableCellsContainingIsMissingUrgentReportCheckbox = this.$results.find(".is-missing-urgent-report");
        var $tableCellsContainingIsAPriorityTargetCheckbox = this.$results.find(".is-a-priority-target");

        $tableCellsContainingIsMissingUrgentReportCheckbox.mouseenter(this._disableRowClick);
        $tableCellsContainingIsMissingUrgentReportCheckbox.mouseleave(this._enableRowClick);
        $tableCellsContainingIsAPriorityTargetCheckbox.mouseenter(this._disableRowClick);
        $tableCellsContainingIsAPriorityTargetCheckbox.mouseleave(this._enableRowClick);

        var $isAPriorityTargetCheckboxes = $tableCellsContainingIsAPriorityTargetCheckbox.children();
        var $isMissingUrgentReportCheckboxes = $tableCellsContainingIsMissingUrgentReportCheckbox.children();

        $isAPriorityTargetCheckboxes.change(jQuery.proxy(this._saveNewPriorityTargetStatus, this));
        $isMissingUrgentReportCheckboxes.change(jQuery.proxy(this._saveNewMissingUrgentReportStatus, this));

        this.addEditAndDeleteReportLinks();

        jQuery(".edit-report").click(jQuery.proxy(this._showEditReportModal, this));
        jQuery(".delete-report").click(jQuery.proxy(this._showDeleteReportModal, this));

        this.$tableHeaders = jQuery("#search-results thead");

        if (jQuery.browser.mobile || !this._isBrowserFullWidth()) {
            this.$tableHeaderVisibleEvenWhenNoResults.hide();
            this.$tableHeaders.show();
        }

        this._updateWhipCounts();
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

    _resetFilter: function (e) {
        this.$textFilters.val("");
        this.$isMissingUrgentReportFilter.prop("checked", false);
        this.$isAPriorityTargetFilter.prop("checked", false);
        this._filterResults(null);
    },

    _updateResultsTable: function () {
        var isWhipCountOutdated = false;

        this.$results.each(function (index, element) {
            var $article = jQuery(element);
            var $tr = $article.find("tr");

            if (this._isDataChangedForRow(index, $tr)) {
                isWhipCountOutdated = true;

                $article.html(
                    CBR.Templates.stateLegislatorsResultRow({
                        legislator: this._getStateLegislators()[index],
                        isAdmin: this.isAdmin()
                    })
                );

                $tr = $article.find("tr");

                $tr.click(jQuery.proxy(this._onTableRowClick, this));

                var $tableCellsContainingIsMissingUrgentReportCheckbox = $tr.children(".is-missing-urgent-report");
                var $tableCellsContainingIsAPriorityTargetCheckbox = $tr.children(".is-a-priority-target");

                $tableCellsContainingIsMissingUrgentReportCheckbox.mouseenter(this._disableRowClick);
                $tableCellsContainingIsMissingUrgentReportCheckbox.mouseleave(this._enableRowClick);
                $tableCellsContainingIsAPriorityTargetCheckbox.mouseenter(this._disableRowClick);
                $tableCellsContainingIsAPriorityTargetCheckbox.mouseleave(this._enableRowClick);

                var $isAPriorityTargetCheckboxes = $tableCellsContainingIsAPriorityTargetCheckbox.children();
                var $isMissingUrgentReportCheckboxes = $tableCellsContainingIsMissingUrgentReportCheckbox.children();

                $isAPriorityTargetCheckboxes.change(jQuery.proxy(this._saveNewPriorityTargetStatus, this));
                $isMissingUrgentReportCheckboxes.change(jQuery.proxy(this._saveNewMissingUrgentReportStatus, this));

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

        if (isWhipCountOutdated) {
            this._updateWhipCounts();
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

    _showEditReportModal: function (e) {
        var $a = jQuery(e.currentTarget);
        var report = this._getReportFromId($a.closest("article").data("id"));
        var successUrl = "/state-legislators?action=savedReport";

        this.showEditReportModal(report, successUrl);
    },

    _showDeleteReportModal: function (e) {
        var $a = jQuery(e.currentTarget);
        var reportId = $a.closest("article").data("id");
        var successUrl = "/state-legislators?action=deletedReport";

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

    _toggleStickyTableHeader: function () {
        if (this.isBrowserFullWidth && !this.$filterSection.visible(true)) {
            this.$stickyTableHeader.show();
        } else {
            this.$stickyTableHeader.hide();
        }
    },

    _disableRowClick: function (e) {
        var $tr = jQuery(e.currentTarget).parent();
        $tr.removeClass("clickable");
    },

    _enableRowClick: function (e) {
        var $tr = jQuery(e.currentTarget).parent();
        $tr.addClass("clickable");
    },

    _onTableRowClick: function (e) {
        var $tr = jQuery(e.currentTarget);
        if ($tr.hasClass("clickable")) {
            this.navigateToStateLegislatorPage(e);
        }
    },

    _saveNewPriorityTargetStatus: function (e) {
        var $checkbox = jQuery(e.currentTarget);

        var isAPriorityTarget = $checkbox.prop("checked");
        var stateLegislatorId = $checkbox.parent().parent().data("id");

        this._updateStateLegislator(this._getStateLegislatorOfId(stateLegislatorId), isAPriorityTarget, null, "Target status saved");
    },

    _saveNewMissingUrgentReportStatus: function (e) {
        var $checkbox = jQuery(e.currentTarget);

        var isMissingUrgentReport = $checkbox.prop("checked");
        var stateLegislatorId = $checkbox.parent().parent().data("id");

        this._updateStateLegislator(this._getStateLegislatorOfId(stateLegislatorId), null, isMissingUrgentReport, "Report status saved");
    },

    _getStateLegislators: function () {
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
        return _.find(this._getStateLegislators(), function (legislator) {
            return legislator.getId() === id;
        });
    },

    _isDataChangedForRow: function (index, $tr) {
        var legislatorWithUpdatedData = this._getStateLegislators()[index];
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
    },

    _onFullWidthBreakpointMatch: function () {
        if (!jQuery.browser.mobile && this.$tableHeaders) {
            this.$tableHeaders.hide();
            this.$tableHeaderVisibleEvenWhenNoResults.show();
        }

        this.isBrowserFullWidth = true;

        if (!this.$filterSection.visible(true)) {
            this.$stickyTableHeader.show();
        }
    },

    _onFullWidthBreakpointExit: function () {
        this.isBrowserFullWidth = false;

        this.$stickyTableHeader.hide();

        if (!jQuery.browser.mobile && this.$tableHeaders) {
            this.$tableHeaderVisibleEvenWhenNoResults.hide();
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

        // In some browsers like Firefox, "content" is wrapped by double-quotes, that's why doing "return content === "STATE_LEGISLATORS_FULL_WIDTH_BREAKPOINT" would be false.
        return _.contains(content, "STATE_LEGISLATORS_FULL_WIDTH_BREAKPOINT");
    },

    _calculateWhipCountForChamber: function (chamber) {
        var nbLegislators = 0;
        var nbLegislatorsSupportive = 0;
        var nbLegislatorsNeedingConvincing = 0;
        var nbLegislatorsNotSupportive = 0;

        this._getStateLegislators().forEach(function (legislator) {
            if (_.isEqual(legislator.getChamber(), chamber)) {
                nbLegislators++;

                var latestReport = legislator.getLatestReport();
                if (latestReport) {
                    var supportLevel = latestReport.getSupportLevel();

                    if (supportLevel === CBR.Models.Report.supportLevel.primarySponsor.code ||
                        supportLevel === CBR.Models.Report.supportLevel.coSponsor.code ||
                        supportLevel === CBR.Models.Report.supportLevel.supportive.code) {
                        nbLegislatorsSupportive++;
                    } else if (supportLevel === CBR.Models.Report.supportLevel.needsConvincing.code) {
                        nbLegislatorsNeedingConvincing++;
                    } else if (supportLevel === CBR.Models.Report.supportLevel.notSupportive.code) {
                        nbLegislatorsNotSupportive++;
                    }
                }
            }
        });

        // Supportive
        var whipCountSupportive = new CBR.Models.WhipCount({
            supportLevel: CBR.Models.Report.supportLevel.supportive,
            count: nbLegislatorsSupportive,
            percentage: nbLegislators === 0 ? 0 : Math.round(nbLegislatorsSupportive / nbLegislators * 100)
        });

        // Needing convincing
        var whipCountNeedingConvincing = new CBR.Models.WhipCount({
            supportLevel: CBR.Models.Report.supportLevel.needsConvincing,
            count: nbLegislatorsNeedingConvincing,
            percentage: nbLegislators === 0 ? 0 : Math.round(nbLegislatorsNeedingConvincing / nbLegislators * 100)
        });

        // Not supportive
        var whipCountNotSupportive = new CBR.Models.WhipCount({
            supportLevel: CBR.Models.Report.supportLevel.notSupportive,
            count: nbLegislatorsNotSupportive,
            percentage: nbLegislators === 0 ? 0 : Math.round(nbLegislatorsNotSupportive / nbLegislators * 100)
        });

        // Unknown
        var nbLegislatorsWhoseSupportLevelIsUnknown = nbLegislators - nbLegislatorsSupportive - nbLegislatorsNeedingConvincing - nbLegislatorsNotSupportive;
        var whipCountUnknown = new CBR.Models.WhipCount({
            supportLevel: CBR.Models.Report.supportLevel.unknown,
            count: nbLegislatorsWhoseSupportLevelIsUnknown,
            percentage: nbLegislators === 0 ? 0 : Math.round(nbLegislatorsWhoseSupportLevelIsUnknown / nbLegislators * 100)
        });

        return [whipCountSupportive,
            whipCountNeedingConvincing,
            whipCountNotSupportive,
            whipCountUnknown];
    },

    _calculateWhipCountForBothChambers: function (whipCountForHouse, whipCountForSenate) {
        var nbLegislatorsSupportive = whipCountForHouse[0].getCount() + whipCountForSenate[0].getCount();
        var nbLegislatorsNeedingConvincing = whipCountForHouse[1].getCount() + whipCountForSenate[1].getCount();
        var nbLegislatorsNotSupportive = whipCountForHouse[2].getCount() + whipCountForSenate[2].getCount();
        var nbLegislatorsUnknown = whipCountForHouse[3].getCount() + whipCountForSenate[3].getCount();
        var nbLegislators = nbLegislatorsSupportive + nbLegislatorsNeedingConvincing + nbLegislatorsNotSupportive + nbLegislatorsUnknown;

        // Supportive
        var whipCountSupportive = new CBR.Models.WhipCount({
            supportLevel: CBR.Models.Report.supportLevel.supportive,
            count: nbLegislatorsSupportive,
            percentage: nbLegislators === 0 ? 0 : Math.round(nbLegislatorsSupportive / nbLegislators * 100)
        });

        // Needing convincing
        var whipCountNeedingConvincing = new CBR.Models.WhipCount({
            supportLevel: CBR.Models.Report.supportLevel.needsConvincing,
            count: nbLegislatorsNeedingConvincing,
            percentage: nbLegislators === 0 ? 0 : Math.round(nbLegislatorsNeedingConvincing / nbLegislators * 100)
        });

        // Not supportive
        var whipCountNotSupportive = new CBR.Models.WhipCount({
            supportLevel: CBR.Models.Report.supportLevel.notSupportive,
            count: nbLegislatorsNotSupportive,
            percentage: nbLegislators === 0 ? 0 : Math.round(nbLegislatorsNotSupportive / nbLegislators * 100)
        });

        // Unknown
        var nbLegislatorsWhoseSupportLevelIsUnknown = nbLegislators - nbLegislatorsSupportive - nbLegislatorsNeedingConvincing - nbLegislatorsNotSupportive;
        var whipCountUnknown = new CBR.Models.WhipCount({
            supportLevel: CBR.Models.Report.supportLevel.unknown,
            count: nbLegislatorsWhoseSupportLevelIsUnknown,
            percentage: nbLegislators === 0 ? 0 : Math.round(nbLegislatorsWhoseSupportLevelIsUnknown / nbLegislators * 100)
        });

        return [whipCountSupportive,
            whipCountNeedingConvincing,
            whipCountNotSupportive,
            whipCountUnknown];
    },

    _updateWhipCounts: function () {
        var whipCountForHouse = this._calculateWhipCountForChamber(CBR.Models.StateLegislator.chamber.house);
        jQuery(this.$whipCountListItem[0]).html(CBR.Templates.whipCountListItem(whipCountForHouse[0]));
        jQuery(this.$whipCountListItem[1]).html(CBR.Templates.whipCountListItem(whipCountForHouse[1]));
        jQuery(this.$whipCountListItem[2]).html(CBR.Templates.whipCountListItem(whipCountForHouse[2]));
        jQuery(this.$whipCountListItem[3]).html(CBR.Templates.whipCountListItem(whipCountForHouse[3]));

        var whipCountForSenate = this._calculateWhipCountForChamber(CBR.Models.StateLegislator.chamber.senate);
        jQuery(this.$whipCountListItem[4]).html(CBR.Templates.whipCountListItem(whipCountForSenate[0]));
        jQuery(this.$whipCountListItem[5]).html(CBR.Templates.whipCountListItem(whipCountForSenate[1]));
        jQuery(this.$whipCountListItem[6]).html(CBR.Templates.whipCountListItem(whipCountForSenate[2]));
        jQuery(this.$whipCountListItem[7]).html(CBR.Templates.whipCountListItem(whipCountForSenate[3]));

        var whipCountForBoth = this._calculateWhipCountForBothChambers(whipCountForHouse, whipCountForSenate);
        jQuery(this.$whipCountListItem[8]).html(CBR.Templates.whipCountListItem(whipCountForBoth[0]));
        jQuery(this.$whipCountListItem[9]).html(CBR.Templates.whipCountListItem(whipCountForBoth[1]));
        jQuery(this.$whipCountListItem[10]).html(CBR.Templates.whipCountListItem(whipCountForBoth[2]));
        jQuery(this.$whipCountListItem[11]).html(CBR.Templates.whipCountListItem(whipCountForBoth[3]));
    }
});
