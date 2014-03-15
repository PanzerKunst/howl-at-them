CBR.Controllers.BaseController = new Class({
    initialize: function (options) {
        this.options = options;
    },

    getEl: function () {
        return jQuery(this.options.el);
    },

    setActivePill: function (e) {
        e.preventDefault();

        var $a = jQuery(e.currentTarget);
        var $li = $a.parent();
        var $ul = $li.parent();
        $ul.children().removeClass("active");

        $li.addClass("active");

        $a.trigger("active-toggled");
    },

    initElements: function () {
        this._applyModernizrRules();
    },

    isBrowserMediumScreen: function () {
        return Modernizr.mq("screen and (min-width: " + CBR.mediumScreenBreakPoint + ")");
    },

    isBrowserLargeScreen: function () {
        return Modernizr.mq("screen and (min-width: " + CBR.largeScreenBreakPoint + ")");
    },

    saveInLocalStorage: function (key, value) {
        if (Modernizr.localstorage) {
            var pageId = jQuery("body").attr("id");

            var pageDataInLocalStorage = JSON.parse(localStorage.getItem(pageId)) || {};
            pageDataInLocalStorage[key] = value;

            localStorage.setItem(pageId, JSON.stringify(pageDataInLocalStorage));
        }
    },

    getFromLocalStorage: function (key) {
        if (Modernizr.localstorage) {
            var pageId = jQuery("body").attr("id");

            var pageDataInLocalStorage = JSON.parse(localStorage.getItem(pageId)) || {};

            return pageDataInLocalStorage[key];
        }
    },

    showEditReportModal: function (report, successUrl) {
        if (this.$editReportModal) {
            this.$editReportModal.remove();
        }

        var reportNotes = report.getNotes();

        this.getEl().append(
            CBR.Templates.editReportModal({
                ContactWithLegislator: CBR.Models.Report.contact,
                SupportLevel: CBR.Models.Report.supportLevel,
                report: {
                    authorName: report.getAuthorName(),
                    contact: report.getContact(),
                    isMoneyInPoliticsAProblem: report.isMoneyInPoliticsAProblem(),
                    isSupportingAmendmentToFixIt: report.isSupportingAmendmentToFixIt(),
                    isOpposingCitizensUnited: report.isOpposingCitizensUnited(),
                    hasPreviouslyVotedForConvention: report.hasPreviouslyVotedForConvention(),
                    supportLevel: report.getSupportLevel(),
                    notes: reportNotes ? reportNotes.replace("\\n", "&#13;&#10;") : null
                },
                isContact: {
                    metLegislator: report.getContact() === "MET_LEGISLATOR",
                    talkedToLegislator: report.getContact() === "TALKED_TO_LEGISLATOR",
                    contactWithStaff: report.getContact() === "CONTACT_WITH_STAFF",
                    waitingForCallback: report.getContact() === "WAITING_FOR_CALLBACK",
                    leftVoiceMail: report.getContact() === "LEFT_VOICEMAIL",
                    none: report.getContact() === "NONE"
                },
                isSupportLevel: {
                    supportive: report.getSupportLevel() === "SUPPORTIVE",
                    needsConfincing: report.getSupportLevel() === "NEEDS_CONVINCING",
                    notSupportive: report.getSupportLevel() === "NOT_SUPPORTIVE"
                },
                isYesNoAnwerUndefined: {
                    moneyInPoliticsAProblem: report.isMoneyInPoliticsAProblem() === null,
                    supportingAmendmentToFixIt: report.isSupportingAmendmentToFixIt() === null,
                    opposingCitizensUnited: report.isOpposingCitizensUnited() === null,
                    previousVoteForConvention: report.hasPreviouslyVotedForConvention() === null
                },
                isFalse: {
                    moneyInPoliticsAProblem: report.isMoneyInPoliticsAProblem() === false,
                    supportingAmendmentToFixIt: report.isSupportingAmendmentToFixIt() === false,
                    opposingCitizensUnited: report.isOpposingCitizensUnited() === false,
                    previousVoteForConvention: report.hasPreviouslyVotedForConvention() === false
                }
            })
        );

        this.$editReportModal = jQuery("#edit-report-modal");

        this.$confirmEditBtn = jQuery("#confirm-edit");

        this.$confirmEditBtn.click(jQuery.proxy(function () {
            this._doEditReport(report, successUrl);
        }, this));

        // TODO: remove this._addModalHandlerForScrollbar(this.$editReportModal);

        this.$editReportModal.modal();
    },

    showDeleteReportModal: function (reportId, successUrl) {
        if (this.$deleteReportModal) {
            this.$deleteReportModal.remove();
        }

        this.getEl().append(
            CBR.Templates.deleteReportModal()
        );

        this.$deleteReportModal = jQuery("#delete-report-modal");

        this.$confirmDeleteBtn = jQuery("#confirm-delete");

        this.$confirmDeleteBtn.click(jQuery.proxy(function () {
            this._doDeleteReport(reportId, successUrl);
        }, this));

        // TODO: remove this._addModalHandlerForScrollbar(this.$deleteReportModal);

        this.$deleteReportModal.modal();
    },

    _doEditReport: function (initialReport, successUrl) {
        if (this.editReportValidator.isValid()) {
            this.$confirmEditBtn.button('loading');

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

            var updatedReport = {
                id: initialReport.getId(),
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

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                emulation: false, // Otherwise PUT and DELETE requests are sent as POST
                url: "/api/reports/",
                data: CBR.JsonUtil.stringifyModel(updatedReport),
                onSuccess: function (responseText, responseXML) {
                    location.replace(successUrl);
                }.bind(this),
                onFailure: function (xhr) {
                    this.$confirmEditBtn.button('reset');
                    alert("AJAX fail :(");
                }.bind(this)
            }).put();
        }
    },

    _doDeleteReport: function (reportId, successUrl) {
        this.$confirmDeleteBtn.button('loading');

        new Request({
            urlEncoded: false,
            emulation: false, // Otherwise PUT and DELETE requests are sent as POST
            url: "/api/reports/" + reportId,
            onSuccess: function (responseText, responseXML) {
                location.replace(successUrl);
            },
            onFailure: function (xhr) {
                this.$confirmDeleteBtn.button('reset');
                alert("AJAX fail :(");
            }.bind(this)
        }).delete();
    },

    /* TODO: remove _addModalHandlerForScrollbar: function($modal) {
        var $html = jQuery("html");
        $html.addClass("with-modal");
        $modal.on("hide.bs.modal", function (e) {
            $html.removeClass("with-modal");
        });
    }, */

    initEditReportValidation: function() {
        this.editReportValidator = new CBR.Services.Validator({
            fieldIds: [
                "edit-author-name",
                "edit-notes"
            ]
        });
    },

    _applyModernizrRules: function () {
        if (!Modernizr.input.placeholder) {
            jQuery(".mdnz-polyfill.placeholder").show();
        }
    },

    httpStatusCode: {
        noContent: 204,
        unauthorized: 401
    }
});