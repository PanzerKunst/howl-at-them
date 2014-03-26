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

        this.$otherPhoneNumber = jQuery("#other-phone-number");
        this.$priorityTargetCheckbox = jQuery("#priority-target");

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

        this.addEditAndDeleteReportLinks();
        this.fadeOutFloatingAlerts();
    },

    _getStateLegislator: function () {
        return new CBR.Models.StateLegislator(this.options.stateLegislator);
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

        this.initEditReportValidation();
    },

    _initEvents: function () {
        this.$otherPhoneNumber.keyup(_.debounce(jQuery.proxy(function () {
            this._updateStateLegislator("other-phone-number-saved", "Phone number saved");
        }, this), 1000));
        this.$otherPhoneNumber.blur(jQuery.proxy(function () {
            this._updateStateLegislator("other-phone-number-saved", "Phone number saved");
        }, this));
        this.$priorityTargetCheckbox.change(jQuery.proxy(function () {
            this._updateStateLegislator("is-a-priority-target-saved", "Priority status saved");
        }, this));

        jQuery("#committees-toggle").click(jQuery.proxy(this._toggleCommittees, this));
        jQuery("#new-report-toggle").click(jQuery.proxy(this._toggleNewReport, this));

        this.$form.submit(jQuery.proxy(this._doSubmit, this));

        jQuery(".edit-report").click(jQuery.proxy(this._showEditReportModal, this));
        jQuery(".delete-report").click(jQuery.proxy(this._showDeleteReportModal, this));
    },

    _initForm: function () {
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
                candidateId: this._getStateLegislator().getId(),
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
                data: JSON.stringify(report),
                onSuccess: function (responseText, responseXML) {
                    this._addReportIdToLocalStorage(parseInt(responseText, 10));
                    location.replace("/state-legislators/" + report.candidateId + "?action=savedReport");
                }.bind(this),
                onFailure: function (xhr) {
                    this.$submitBtn.button('reset');
                    alert("AJAX fail :(");
                }.bind(this)
            }).post();
        }
    },

    _updateStateLegislator: function (floatingAlertId, floatingAlertText) {
        var stateLegislator = this._getStateLegislator();

        var otherPhoneNumber = this.$otherPhoneNumber.val();

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
            otherPhoneNumber: otherPhoneNumber ? otherPhoneNumber : null,
            isAPriorityTarget: this.$priorityTargetCheckbox.is(":checked")
        };

        new Request({
            urlEncoded: false,
            headers: { "Content-Type": "application/json" },
            emulation: false, // Otherwise PUT and DELETE requests are sent as POST
            url: "/api/state-legislators/",
            data: JSON.stringify(updatedStateLegislator),
            onSuccess: function (responseText, responseXML) {
                this.showAlert(floatingAlertId, floatingAlertText);
            }.bind(this),
            onFailure: function (xhr) {
                alert("AJAX fail :(");
            }
        }).put();
    },

    _addReportIdToLocalStorage: function (reportId) {
        var idOfCreatedReports = this.getIdOfCreatedReports();
        idOfCreatedReports.push(reportId);

        var isGlobalScope = true;
        this.saveInLocalStorage("idOfCreatedReports", JSON.stringify(idOfCreatedReports), isGlobalScope);
    },

    _showEditReportModal: function (e) {
        var $a = jQuery(e.currentTarget);
        var report = this._getReportFromId($a.closest("article").data("id"));
        var successUrl = "/state-legislators/" + report.getCandidateId() + "?action=savedReport";

        this.showEditReportModal(report, successUrl);
    },

    _showDeleteReportModal: function (e) {
        var $a = jQuery(e.currentTarget);
        var report = this._getReportFromId($a.closest("article").data("id"));
        var successUrl = "/state-legislators/" + report.getCandidateId() + "?action=deletedReport";

        this.showDeleteReportModal(report.getId(), successUrl);
    },

    _getReportFromId: function (reportId) {
        var reports = this._getStateLegislator().getReports();

        for (var i = 0; i < reports.length; i++) {
            var report = reports[i];

            if (report.getId() === reportId) {
                return report;
            }
        }

        return null;
    }
});
