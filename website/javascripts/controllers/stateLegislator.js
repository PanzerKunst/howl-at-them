CBR.Controllers.StateLegislator = new Class({
    Extends: CBR.Controllers.BaseController,

    initialize: function (options) {
        this.parent(options);
    },

    run: function () {
        this.initElements();
        this._initValidation();
        this._initEvents();
    },

    initElements: function () {
        this.parent();

        this.$committeesList = jQuery("#committees > ul");

        this.$authorName = jQuery("#author-name");

        this.$mppRadios = jQuery("[name='MPP']");
        this.$yesMppRadio = this.$mppRadios.filter("[value='" + CBR.Models.Report.radioAnswer.yes + "']");
        this.$noMppRadio = this.$mppRadios.filter("[value='" + CBR.Models.Report.radioAnswer.no + "']");

        this.$safiRadios = jQuery("[name='SAFI']");
        this.$yesSafiRadio = this.$safiRadios.filter("[value='" + CBR.Models.Report.radioAnswer.yes + "']");
        this.$noSafiRadio = this.$safiRadios.filter("[value='" + CBR.Models.Report.radioAnswer.no + "']");

        this.$ocuRadios = jQuery("[name='OCU']");
        this.$yesOcuRadio = this.$ocuRadios.filter("[value='" + CBR.Models.Report.radioAnswer.yes + "']");
        this.$noOcuRadio = this.$ocuRadios.filter("[value='" + CBR.Models.Report.radioAnswer.no + "']");

        this.$pvcRadios = jQuery("[name='PVC']");
        this.$yesPvcRadio = this.$pvcRadios.filter("[value='" + CBR.Models.Report.radioAnswer.yes + "']");
        this.$noPvcRadio = this.$pvcRadios.filter("[value='" + CBR.Models.Report.radioAnswer.no + "']");

        this.$form = jQuery("form");
        this.$submitBtn = jQuery("[type=submit]");

        this._initForm();
    },

    _getStateLegislator: function () {
        return this.options.stateLegislator;
    },

    _getLatestReport: function () {
        return this.options.latestReport;
    },

    _getAction: function () {
        return this.options.action;
    },

    _initValidation: function () {
        this.validator = new CBR.Services.Validator({
            fieldIds: [
                "author-name",
                "notes"
            ]
        });
    },

    _initEvents: function () {
        jQuery("#committees-toggle").click(jQuery.proxy(this._toggleCommittees, this));
        jQuery("#new-report-toggle").click(jQuery.proxy(this._toggleNewReport, this));

        this.$form.submit(jQuery.proxy(this._doSubmit, this));

        jQuery(".delete-report").click(jQuery.proxy(this._showDeleteReportModal, this));
    },

    _initForm: function() {
        this.$authorName.val(this.getFromLocalStorage(this.$authorName.attr("id")));

        var action = this._getAction();

        if (action === "savedReport" || action === "deletedReport") {
            this.$form.hide();
        }
    },

    _toggleCommittees: function (e) {
        if (this.$committeesList.is(":visible")) {
            this.$committeesList.slideUpCustom();
        } else {
            this.$committeesList.slideDownCustom();
        }
    },

    _toggleNewReport: function (e) {
        if (this.$form.is(":visible")) {
            this.$form.slideUpCustom();
        } else {
            this.$form.slideDownCustom();
        }
    },

    _doSubmit: function (e) {
        if (e)
            e.preventDefault();

        if (this.validator.isValid()) {
            this.$submitBtn.button('loading');

            var authorName = this.$authorName.val();
            var selectedSupportLevel = jQuery("#support-level").val();

            var isMoneyInPoliticsAProblem = null;
            if (this.$yesMppRadio.prop("checked")) {
                isMoneyInPoliticsAProblem = true;
            } else if (this.$noMppRadio.prop("checked")) {
                isMoneyInPoliticsAProblem = false;
            }

            var isSupportingAmendmentToFixIt = null;
            if (this.$yesSafiRadio.prop("checked")) {
                isSupportingAmendmentToFixIt = true;
            } else if (this.$noSafiRadio.prop("checked")) {
                isSupportingAmendmentToFixIt = false;
            }

            var isOpposingCitizensUnited = null;
            if (this.$yesOcuRadio.prop("checked")) {
                isOpposingCitizensUnited = true;
            } else if (this.$noOcuRadio.prop("checked")) {
                isOpposingCitizensUnited = false;
            }

            var hasPreviouslyVotedForConvention = null;
            if (this.$yesPvcRadio.prop("checked")) {
                hasPreviouslyVotedForConvention = true;
            } else if (this.$noPvcRadio.prop("checked")) {
                hasPreviouslyVotedForConvention = false;
            }

            var report = {
                candidateId: this._getStateLegislator().id,
                authorName: authorName,
                contact: jQuery("#contact").val(),
                isMoneyInPoliticsAProblem: isMoneyInPoliticsAProblem,
                isSupportingAmendmentToFixIt: isSupportingAmendmentToFixIt,
                isOpposingCitizensUnited: isOpposingCitizensUnited,
                hasPreviouslyVotedForConvention: hasPreviouslyVotedForConvention,
                supportLevel: selectedSupportLevel ? selectedSupportLevel : null,
                notes: jQuery("#notes").val()
            };

            this.saveInLocalStorage(this.$authorName.attr("id"), authorName);

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                url: "/api/reports",
                data: CBR.JsonUtil.stringifyModel(report),
                onSuccess: function (responseText, responseXML) {
                    location.replace("/state-legislators/" + this._getStateLegislator().id + "?action=savedReport");
                }.bind(this),
                onFailure: function (xhr) {
                    this.$submitBtn.button('reset');
                    alert("AJAX fail :(");
                }.bind(this)
            }).post();
        }
    },

    _showDeleteReportModal: function(e) {
        var $a = jQuery(e.currentTarget);

        var idOfReportToDelete = $a.closest("article").data("id");

        this.getEl().append(
            CBR.Templates.deleteReportModal()
        );

        this.$confirmModalBtn = jQuery("#confirm-modal");

        this.$confirmModalBtn.click(jQuery.proxy(function() {
            this._doDeleteReport(idOfReportToDelete);
        }, this));

        jQuery("#delete-report-modal").modal();
    },

    _doDeleteReport: function(reportId) {
        this.$confirmModalBtn.button('loading');

        new Request({
            urlEncoded: false,
            emulation: false, // Otherwise PUT and DELETE requests are sent as POST
            url: "/api/reports/" + reportId,
            onSuccess: function (responseText, responseXML) {
                location.replace("/state-legislators/" + this._getStateLegislator().id + "?action=deletedReport");
            }.bind(this),
            onFailure: function (xhr) {
                this.$confirmModalBtn.button('reset');
                alert("AJAX fail :(");
            }.bind(this)
        }).delete();
    }
});
