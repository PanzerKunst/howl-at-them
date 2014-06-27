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

        this.$leadershipPositionSelect = jQuery("#leadership-position");
        this.$committeeSelect = jQuery("#committee");

        this.$latestContactFilter = jQuery("#latest-contact-filter");
    },

    initEvents: function () {
        this.parent();

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

        this.$results = this.$searchResultsSection.find("tr");

        this.parent();
    },

    updateResultsTable: function () {
        this.$results.each(function (index, element) {
            var $tr = jQuery(element);

            if (this.isDataChangedForRow(index, $tr)) {
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
        }.bind(this));
    }
});
