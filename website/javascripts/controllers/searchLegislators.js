CBR.Controllers.SearchLegislators = new Class({
    Extends: CBR.Controllers.LegislatorListing,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this.initEvents();

        this.doSubmit(null);
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
        this.$usStateSelect.change(jQuery.proxy(function(e) {
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

    getStateLegislators: function() {
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

        this.$results = jQuery("#search-results tr");

        jQuery("tr.clickable").click(jQuery.proxy(this.onTableRowClick, this));

        var $tableCellsContainingIsAPriorityTargetCheckbox = jQuery("td.is-a-priority-target");
        var $tableCellsContainingIsMissingUrgentReportCheckbox = jQuery("td.is-missing-urgent-report");

        $tableCellsContainingIsAPriorityTargetCheckbox.mouseenter(this.disableRowClick);
        $tableCellsContainingIsAPriorityTargetCheckbox.mouseleave(this.enableRowClick);
        $tableCellsContainingIsMissingUrgentReportCheckbox.mouseenter(this.disableRowClick);
        $tableCellsContainingIsMissingUrgentReportCheckbox.mouseleave(this.enableRowClick);

        var $isMissingUrgentReportCheckboxes = $tableCellsContainingIsMissingUrgentReportCheckbox.children();
        var $isAPriorityTargetCheckboxes = $tableCellsContainingIsAPriorityTargetCheckbox.children();

        $isMissingUrgentReportCheckboxes.change(jQuery.proxy(this.saveNewMissingUrgentReportStatus, this));
        $isAPriorityTargetCheckboxes.change(jQuery.proxy(this.saveNewPriorityTargetStatus, this));
    }
});
