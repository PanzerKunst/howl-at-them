CBR.Controllers.SearchLegislators = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this._initValidation();
        this._initEvents();

        if (!this._areAllFiltersEmpty()) {
            this._doSubmit(null);
        }
    },

    initElements: function () {
        this.parent();

        this.$form = jQuery("form");

        this.$firstName = jQuery("#first-name");
        this.$lastName = jQuery("#last-name");
        this.$usStateSelect = jQuery("#us-state");

        this.$advancedSearchFields = jQuery("#advanced-search-fields");
        this.$committeeSelect = jQuery("#committee");
        this.$priorityTargetCheckbox = jQuery("#priority-target");

        this.$otherInputError = jQuery(".other-form-error");
        this.$submitBtn = jQuery("[type=submit]");

        this.$tableWrapper = jQuery("#table-wrapper");

        this.getEl().addClass("legislator-listing");
    },

    _areAllFiltersEmpty: function() {
        return !this.$firstName.val() && !this.$lastName.val() && !this.$usStateSelect.val() && !this.$priorityTargetCheckbox.is(":checked");
    },

    _initValidation: function () {
        this.validator = new CBR.Services.Validator({
            fieldIds: [
                "first-name",
                "last-name",
                "us-state"
            ]
        });
    },

    _initEvents: function () {
        jQuery("#advanced-toggle").click(jQuery.proxy(this._toggleAdvancedSearch, this));
        this.$usStateSelect.change(jQuery.proxy(this._populateCommitteesSelect, this));

        this.$form.submit(jQuery.proxy(this._doSubmit, this));
    },

    _toggleAdvancedSearch: function (e) {
        if (this.$advancedSearchFields.is(":visible")) {
            this.$advancedSearchFields.slideUpCustom();
        } else {
            this.$advancedSearchFields.slideDownCustom();
        }
    },

    _populateCommitteesSelect: function(e) {
        var selectedUsStateId = this.$usStateSelect.val();

        if (selectedUsStateId) {
            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                url: "/api/committees",
                data: {usStateId: selectedUsStateId},    // GET request doesn't require JSON.stringify()
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
        }
        else {
            this.$committeeSelect.prop("disabled", true);
            this.$committeeSelect.html(jQuery('<option value="">Please select a state first</option>'));
        }
    },

    _doSubmit: function (e) {
        if (e)
            e.preventDefault();

        /* TODO if (this._areAllFiltersEmpty()) {
            this.$otherInputError.slideDownCustom();
        }
        else */if (this.validator.isValid()) {
            this.$otherInputError.slideUpCustom();
            this.$submitBtn.button('loading');
            this.$tableWrapper.html('<div class="data-loading"></div>');

            var inputFirstName = this.$firstName.val().toLowerCase();
            var inputLastName = this.$lastName.val().toLowerCase();
            var selectedUsStateId = this.$usStateSelect.val();
            var selectedCommitteeName = this.$committeeSelect.val();

            var stateLegislatorSearch = {
                firstName: inputFirstName ? inputFirstName : null,
                lastName: inputLastName ? inputLastName : null,
                usStateId: selectedUsStateId ? selectedUsStateId : null,
                committeeName: selectedCommitteeName ? selectedCommitteeName : null,
                isAPriorityTarget: this.$priorityTargetCheckbox.is(":checked")
            };

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                url: "/api/state-legislators",
                data: stateLegislatorSearch,    // GET request doesn't require JSON.stringify()
                onSuccess: function (responseText, responseXML) {
                    this.$submitBtn.button('reset');
                    this._storeMatchingStateLegislators(JSON.parse(responseText));
                    this._createResultsTable();
                }.bind(this),
                onFailure: function (xhr) {
                    this.$submitBtn.button('reset');
                    alert("AJAX fail :(");
                }.bind(this)
            }).get();
        }
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
                    "sTitle": "Title"
                },
                {
                    "mData": function (source, type, val) {
                        return source.getFirstName() + " " + source.getLastName();
                    },
                    "sTitle": "Name",
                    "sWidth": "17%"
                },
                {
                    "mData": function (source, type, val) {
                        return '<span class="centered-contents">' + source.getPoliticalPartiesAbbr() + '</span>';
                    },
                    "sTitle": "Party"
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
                    "bSortable": false
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
                    "sTitle": '<span class="yes-no-answer">Previous<br />vote for<br />convention</span>'
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();
                        return latestReport ?
                            latestReport.getReadableContact() :
                            null;
                    },
                    "sTitle": "Last contact"
                }
            ],
            "oLanguage": {
                "sEmptyTable": "No matching state legislator",
                "sSearch": "Filter"
            }
        });

        // To make it look like Bootstrap inputs
        jQuery(".dataTables_filter input").addClass("form-control");

        jQuery("tr.clickable").click(jQuery.proxy(this.navigateToStateLegislatorPage, this));
    }
});
