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
        this.$otherInputError = jQuery(".other-input-error");
        this.$submitBtn = jQuery("[type=submit]");

        this.$tableWrapper = jQuery("#table-wrapper");
    },

    _areAllFiltersEmpty: function() {
        return !this.$firstName.val() && !this.$lastName.val() && !this.$usStateSelect.val();
    },

    _initValidation: function () {
        this.validator = new CBR.Services.Validator({
            fieldIds: [
                "first-name",
                "last-name"
            ]
        });
    },

    _initEvents: function () {
        jQuery("#advanced-toggle").click(jQuery.proxy(this._toggleAdvancedSearch, this));

        this.$form.submit(jQuery.proxy(this._doSubmit, this));
    },

    _toggleAdvancedSearch: function (e) {
        /* TODO if (this.$form.is(":visible")) {
            this.$form.slideUpCustom();
        } else {
            this.$form.slideDownCustom();
        } */
    },

    _doSubmit: function (e) {
        if (e)
            e.preventDefault();

        if (this._areAllFiltersEmpty()) {
            this.$otherInputError.slideDownCustom();
        }
        else if (this.validator.isValid()) {
            this.$otherInputError.slideUpCustom();
            this.$submitBtn.button('loading');
            this.$tableWrapper.html('<div class="data-loading"></div>');

            var inputFirstName = this.$firstName.val().toLowerCase();
            var inputLastName = this.$lastName.val().toLowerCase();
            var selectedUsStateId = this.$usStateSelect.val();

            var stateLegislatorSearch = {
                firstName: inputFirstName ? inputFirstName : null,
                lastName: inputLastName ? inputLastName : null,
                usStateId: selectedUsStateId ? selectedUsStateId : null
            };

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                url: "/api/state-legislators",
                data: stateLegislatorSearch,
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

    _navigateToStateLegislatorPage: function(e) {
        location.href = "/state-legislators/" + jQuery(e.currentTarget).data("id");
    },

    _storeMatchingStateLegislators: function (stateLegislatorsWithLatestReportWithNbReports) {
        this.matchingStateLegislators = stateLegislatorsWithLatestReportWithNbReports.map(function (stateLegislatorWithLatestReportWithNbReports) {
            return new CBR.Models.StateLegislatorWithLatestReportWithNbReports({
                stateLegislator: new CBR.Models.StateLegislator(stateLegislatorWithLatestReportWithNbReports.stateLegislator),
                latestReport: stateLegislatorWithLatestReportWithNbReports.latestReport ? new CBR.Models.Report(stateLegislatorWithLatestReportWithNbReports.latestReport) : null,
                nbReports: stateLegislatorWithLatestReportWithNbReports.nbReports
            });
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
                $row.attr("data-id", aData.getStateLegislator().getId());
                $row.addClass("clickable");
            },
            "aoColumns": [
                {
                    "mData": function (source, type, val) {
                        return source.getStateLegislator().getTitleAbbr();
                    },
                    "sTitle": "Title"
                },
                {
                    "mData": function (source, type, val) {
                        var stateLegislator = source.getStateLegislator();
                        return stateLegislator.getLastName() + " " + stateLegislator.getFirstName();
                    },
                    "sTitle": "Name",
                    "sWidth": "20%"
                },
                {
                    "mData": function (source, type, val) {
                        return source.getStateLegislator().getPoliticalPartiesAbbr();
                    },
                    "sTitle": "P",
                    "bSortable": false
                },
                {
                    "mData": function (source, type, val) {
                        var stateLegislator = source.getStateLegislator();
                        return stateLegislator.getUsState().id + " " + stateLegislator.getDistrict();
                    },
                    "sTitle": "District"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();
                        return latestReport ?
                            '<span class="support-level ' + latestReport.getSupportLevel() + '">' + latestReport.getReadableSupportLevel() + '</span>(' + source.getNbReports() + ")" :
                            null;
                    },
                    "sTitle": "Support (nb reports)",
                    "bSortable": false
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();

                        if (latestReport) {
                            var isMoneyInPoliticsAProblem = latestReport.isMoneyInPoliticsAProblem();
                            if (isMoneyInPoliticsAProblem === true) {
                                return "Y";
                            } else if (isMoneyInPoliticsAProblem === false) {
                                return "N";
                            } else {
                                return "?";
                            }
                        } else {
                            return null;
                        }
                    },
                    "sTitle": '<span class="question-column-header">Money in<br />politics is<br />a problem</span>',
                    "sWidth": "7.5%"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();

                        if (latestReport) {
                            var isSupportingAmendmentToFixIt = latestReport.isSupportingAmendmentToFixIt();
                            if (isSupportingAmendmentToFixIt === true) {
                                return "Y";
                            } else if (isSupportingAmendmentToFixIt === false) {
                                return "N";
                            } else {
                                return "?";
                            }
                        } else {
                            return null;
                        }
                    },
                    "sTitle": '<span class="question-column-header">Supports<br />amendment<br />to fix it</span>',
                    "sWidth": "8.5%"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();

                        if (latestReport) {
                            var isOpposingCitizensUnited = latestReport.isOpposingCitizensUnited();
                            if (isOpposingCitizensUnited === true) {
                                return "Y";
                            } else if (isOpposingCitizensUnited === false) {
                                return "N";
                            } else {
                                return "?";
                            }
                        } else {
                            return null;
                        }
                    },
                    "sTitle": '<span class="question-column-header">Opposes<br />Citizens<br />United</span>',
                    "sWidth": "6.5%"
                },
                {
                    "mData": function (source, type, val) {
                        var latestReport = source.getLatestReport();

                        if (latestReport) {
                            var hasPreviouslyVotedForConvention = latestReport.hasPreviouslyVotedForConvention();
                            if (hasPreviouslyVotedForConvention === true) {
                                return "Y";
                            } else if (hasPreviouslyVotedForConvention === false) {
                                return "N";
                            } else {
                                return "?";
                            }
                        } else {
                            return null;
                        }
                    },
                    "sTitle": '<span class="question-column-header">Previous<br />vote for<br />convention</span>'
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
                "sEmptyTable": "No matching state legislator"
            }
        });

        jQuery("tr.clickable").click(jQuery.proxy(this._navigateToStateLegislatorPage, this));
    }
});
