CBR.Controllers.SearchLegislators = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this._initValidation();
        this._initEvents();

        if (this._getSelectedUsStateId()) {
            this._doSubmit(null);
        }
    },

    initElements: function () {
        this.parent();

        this.$form = jQuery("form");
        this.$submitBtn = jQuery("[type=submit]");
        this.$tableWrapper = jQuery("#table-wrapper");
    },

    _getSelectedUsStateId: function () {
        return this.options.selectedUsStateId;
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
        jQuery("#advanced-link").click(jQuery.proxy(this._toggleAdvancedSearch, this));

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

        if (this.validator.isValid()) {
            this.$submitBtn.button('loading');

            var inputFirstName = jQuery("#first-name").val().toLowerCase();
            var inputLastName = jQuery("#last-name").val().toLowerCase();
            var selectedUsStateId = jQuery("#us-state").val();

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
                        return source.getLastName() + " " + source.getFirstName();
                    },
                    "sTitle": "Name"
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
                        return source.getUsStateId() + " " + source.getDistrict();
                    },
                    "sTitle": "District"
                }
            ],
            "oLanguage": {
                "sEmptyTable": "No matching state legislator"
            }
        });

        jQuery("tr.clickable").click(jQuery.proxy(this._navigateToStateLegislatorPage, this));
    }
});
