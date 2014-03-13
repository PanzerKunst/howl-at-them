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

        this.getEl().addClass("report-listing");
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
                    "sWidth": "20%"
                },
                {
                    "mData": function (source, type, val) {
                        return source.getPoliticalPartiesAbbr();
                    },
                    "sTitle": "P",
                    "bSortable": false
                },
                {
                    "mData": function (source, type, val) {
                        return source.getUsState().id + " " + source.getDistrict();
                    },
                    "sTitle": "District"
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
                        var result = '<span class="yes-no-answer">';

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
                        var result = '<span class="yes-no-answer">';

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
                        var result = '<span class="yes-no-answer">';

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
                            var result = '<span class="yes-no-answer">';

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
                "sEmptyTable": "No matching state legislator"
            }
        });

        jQuery("tr.clickable").click(jQuery.proxy(this._navigateToStateLegislatorPage, this));
    },

    _navigateToStateLegislatorPage: function(e) {
        location.href = "/state-legislators/" + jQuery(e.currentTarget).data("id");
    }
});
