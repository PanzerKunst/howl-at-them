CBR.Controllers.SearchLegislators = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this._initEvents();

        this._doSubmit(null);
    },

    initElements: function () {
        this.parent();

        this.$usStateSelect = jQuery("#us-state");
        this.$advancedSearchFields = jQuery("#advanced-search-fields");
        this.$leadershipPositionSelect = jQuery("#leadership-position");
        this.$committeeSelect = jQuery("#committee");
        this.$priorityTargetCheckbox = jQuery("#priority-target");

        this.$tableWrapper = jQuery("#table-wrapper");

        this.$stickyTableHeader = jQuery("#sticky-table-header");
    },

    _initEvents: function () {
        jQuery("#advanced-toggle").click(jQuery.proxy(this._toggleAdvancedSearch, this));
        this.$usStateSelect.change(jQuery.proxy(function(e) {
            this._populateLeadershipPositionsSelect(e);
            this._populateCommitteesSelect(e);
        }, this));

        this.$usStateSelect.change(jQuery.proxy(this._doSubmit, this));
        this.$leadershipPositionSelect.change(jQuery.proxy(this._doSubmit, this));
        this.$committeeSelect.change(jQuery.proxy(this._doSubmit, this));
        this.$priorityTargetCheckbox.change(jQuery.proxy(this._doSubmit, this));

        Breakpoints.on({
            name: "SEARCH_LEGISLATORS_FULL_WIDTH_BREAKPOINT",
            matched: jQuery.proxy(this._onFullWidthBreakpointMatch, this),
            exit: jQuery.proxy(this._onFullWidthBreakpointExit, this)
        });

        jQuery(window).scroll(_.debounce(jQuery.proxy(this._toggleStickyTableHeader, this), 15));
    },

    _toggleAdvancedSearch: function (e) {
        if (this.$advancedSearchFields.is(":visible")) {
            this.$advancedSearchFields.slideUpCustom();
        } else {
            this.$advancedSearchFields.slideDownCustom();
        }
    },

    _populateLeadershipPositionsSelect: function(e) {
        new Request({
            urlEncoded: false,
            headers: { "Content-Type": "application/json" },
            url: "/api/leadership-positions",
            data: {usStateId: this.$usStateSelect.val()},    // GET request doesn't require JSON.stringify()
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

    _populateCommitteesSelect: function(e) {
        new Request({
            urlEncoded: false,
            headers: { "Content-Type": "application/json" },
            url: "/api/committees",
            data: {usStateId: this.$usStateSelect.val()},    // GET request doesn't require JSON.stringify()
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

    _doSubmit: function (e) {
        this.$tableWrapper.html('<div class="data-loading"></div>');

        var selectedLeadershipPositionId = this.$leadershipPositionSelect.val();
        var selectedCommitteeName = this.$committeeSelect.val();

        var stateLegislatorSearch = {
            usStateId: this.$usStateSelect.val(),
            leadershipPositionId: selectedLeadershipPositionId ? selectedLeadershipPositionId : null,
            committeeName: selectedCommitteeName ? selectedCommitteeName : null,
            isAPriorityTarget: this.$priorityTargetCheckbox.is(":checked")
        };

        new Request({
            urlEncoded: false,
            headers: { "Content-Type": "application/json" },
            url: "/api/state-legislators",
            data: stateLegislatorSearch,    // GET request doesn't require JSON.stringify()
            onSuccess: function (responseText, responseXML) {
                this._storeMatchingStateLegislators(JSON.parse(responseText));
                this._createResultsTable();
            }.bind(this),
            onFailure: function (xhr) {
                alert("AJAX fail :(");
            }
        }).get();
    },

    _storeMatchingStateLegislators: function (stateLegislators) {
        this.matchingStateLegislators = stateLegislators.map(function (stateLegislator) {
            return new CBR.Models.StateLegislator(stateLegislator);
        });
    },

    _createResultsTable: function () {
        this.$tableWrapper.empty();
        this.$tableWrapper.append('<table class="table table-striped table-bordered table-condensed"></table>');

        this.$tableWrapper.children("table").dataTable({
            "iDisplayLength": 9999,
            "aaData": this.matchingStateLegislators,
            "fnRowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                var $row = jQuery(nRow);
                $row.attr("data-id", aData.getId());
                $row.addClass("clickable");
            },
            "aoColumns": [
                {
                    "mData": function (source, type, val) {
                        return source.getTitleAbbr();
                    },
                    "sTitle": "Title",
                    "sWidth": "5.9%"
                },
                {
                    "mData": function (source, type, val) {
                        return source.getLastName() + " " + source.getFirstName();
                    },
                    "sTitle": "Name",
                    "sWidth": "17.1%"
                },
                {
                    "mData": function (source, type, val) {
                        return '<span class="centered-contents">' + source.getPoliticalPartiesAbbr() + '</span>';
                    },
                    "sTitle": "Party",
                    "sWidth": "4%"
                },
                {
                    "mData": function (source, type, val) {
                        return source.getUsState().id + " " + source.getChamber() + " " + source.getDistrict();
                    },
                    "sTitle": "District",
                    "sWidth": "13%"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();
                        return latestReport ?
                            '<span class="support-level ' + latestReport.getSupportLevel() + '">' + latestReport.getReadableSupportLevel() + '</span>(' + source.getReportCount() + ")" :
                            null;
                    },
                    "sTitle": "Support (nb reports)",
                    "bSortable": false,
                    "sWidth": "16.5%"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();
                        var result = '<span class="centered-contents">';

                        if (latestReport) {
                            var isMoneyInPoliticsAProblem = latestReport.isMoneyInPoliticsAProblem();
                            if (isMoneyInPoliticsAProblem === true) {
                                result += "Y";
                            } else if (isMoneyInPoliticsAProblem === false) {
                                result += "N";
                            } else {
                                result += "?";
                            }
                            return result + "</span>";
                        } else {
                            return null;
                        }
                    },
                    "sTitle": '<span class="yes-no-answer">Money in<br />politics is<br />a problem</span>',
                    "sWidth": "7.5%"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();
                        var result = '<span class="centered-contents">';

                        if (latestReport) {
                            var isSupportingAmendmentToFixIt = latestReport.isSupportingAmendmentToFixIt();
                            if (isSupportingAmendmentToFixIt === true) {
                                result += "Y";
                            } else if (isSupportingAmendmentToFixIt === false) {
                                result += "N";
                            } else {
                                result += "?";
                            }
                            return result + "</span>";
                        } else {
                            return null;
                        }
                    },
                    "sTitle": '<span class="yes-no-answer">Supports<br />amendment<br />to fix it</span>',
                    "sWidth": "8.5%"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();
                        var result = '<span class="centered-contents">';

                        if (latestReport) {
                            var isOpposingCitizensUnited = latestReport.isOpposingCitizensUnited();
                            if (isOpposingCitizensUnited === true) {
                                result += "Y";
                            } else if (isOpposingCitizensUnited === false) {
                                result += "N";
                            } else {
                                result += "?";
                            }
                            return result + "</span>";
                        } else {
                            return null;
                        }
                    },
                    "sTitle": '<span class="yes-no-answer">Opposes<br />Citizens<br />United</span>',
                    "sWidth": "6.5%"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();

                        if (latestReport) {
                            var hasPreviouslyVotedForConvention = latestReport.hasPreviouslyVotedForConvention();
                            var result = '<span class="centered-contents">';

                            if (hasPreviouslyVotedForConvention === true) {
                                result += "Y";
                            } else if (hasPreviouslyVotedForConvention === false) {
                                result += "N";
                            } else {
                                result += "?";
                            }
                            return result + "</span>";
                        } else {
                            return null;
                        }
                    },
                    "sTitle": '<span class="yes-no-answer">Previous<br />vote for<br />convention</span>',
                    "sWidth": "7.5%"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();
                        return latestReport ?
                            latestReport.getReadableContact() :
                            null;
                    },
                    "sTitle": "Last contact",
                    "sWidth": "13.5%"
                }
            ],
            "oLanguage": {
                "sEmptyTable": "No matching state legislator",
                "sSearch": "Filter"
            }
        });

        // To make it look like Bootstrap inputs
        jQuery(".dataTables_filter input").addClass("form-control");

        this.$filterSection = jQuery(".dataTables_filter");
        this._toggleStickyTableHeader();

        jQuery("tr.clickable").click(jQuery.proxy(this.navigateToStateLegislatorPage, this));
    },

    _onFullWidthBreakpointMatch: function() {
        this.isBrowserFullWidth = true;

        if (this.$filterSection && !this.$filterSection.visible(true)) {
            this.$stickyTableHeader.show();
        }
    },

    _onFullWidthBreakpointExit: function() {
        this.isBrowserFullWidth = false;
        this.$stickyTableHeader.hide();
    },

    _toggleStickyTableHeader: function () {
        if (this.isBrowserFullWidth &&
                this.$filterSection &&
                !this.$filterSection.visible(true)) {
            this.$stickyTableHeader.show();
        } else {
            this.$stickyTableHeader.hide();
        }
    }
});
