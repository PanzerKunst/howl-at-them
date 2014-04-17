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

    isBrowserSmallScreen: function () {
        return !this.isBrowserMediumScreen();
    },

    isBrowserMediumScreen: function () {
        return Modernizr.mq("screen and (min-width: " + CBR.mediumScreenBreakPoint + ")");
    },

    isBrowserLargeScreen: function () {
        return Modernizr.mq("screen and (min-width: " + CBR.largeScreenBreakPoint + ")");
    },

    saveInLocalStorage: function (key, value, isGlobalScope) {
        if (Modernizr.localstorage) {
            if (isGlobalScope) {
                localStorage.setItem(key, JSON.stringify(value));
            } else {
                var pageId = jQuery("body").attr("id");

                var pageDataInLocalStorage = JSON.parse(localStorage.getItem(pageId)) || {};
                pageDataInLocalStorage[key] = value;

                localStorage.setItem(pageId, JSON.stringify(pageDataInLocalStorage));
            }
        }
    },

    getFromLocalStorage: function (key, isGlobalScope) {
        if (Modernizr.localstorage) {
            if (isGlobalScope) {
                return JSON.parse(localStorage.getItem(key));
            }

            var pageId = jQuery("body").attr("id");

            var pageDataInLocalStorage = JSON.parse(localStorage.getItem(pageId)) || {};

            return pageDataInLocalStorage[key];
        }
    },

    isAdmin: function () {
        return this.options.isAdmin;
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
                    moneyInPoliticsIsAProblem: report.isMoneyInPoliticsAProblem() === null,
                    supportsAmendmentToFixIt: report.isSupportingAmendmentToFixIt() === null,
                    opposesCitizensUnited: report.isOpposingCitizensUnited() === null,
                    previousVoteForConvention: report.hasPreviouslyVotedForConvention() === null
                },
                isFalse: {
                    moneyInPoliticsIsAProblem: report.isMoneyInPoliticsAProblem() === false,
                    supportsAmendmentToFixIt: report.isSupportingAmendmentToFixIt() === false,
                    opposesCitizensUnited: report.isOpposingCitizensUnited() === false,
                    previousVoteForConvention: report.hasPreviouslyVotedForConvention() === false
                }
            })
        );

        this.$editReportModal = jQuery("#edit-report-modal");

        this.$confirmEditBtn = jQuery("#confirm-edit");

        this.$confirmEditBtn.click(jQuery.proxy(function () {
            this._doEditReport(report, successUrl);
        }, this));

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

        this.$deleteReportModal.modal();
    },

    initEditReportValidation: function () {
        this.editReportValidator = new CBR.Services.Validator({
            fieldIds: [
                "edit-author-name",
                "edit-notes"
            ]
        });
    },

    addEditAndDeleteReportLinks: function () {
        var idOfCreatedReports = this.getIdOfCreatedReports();

        var links = '<a class="delete-report">Delete</a><a class="edit-report">Edit</a>';

        jQuery(".reports > article").each(function (index, element) {
            var $article = jQuery(element);

            if (this.isAdmin()) {
                jQuery($article.children("div").get(0)).append(links);
            }
            else {
                var reportId = parseInt($article.data("id"), 10);

                var isCreatedByUser = false;

                for (var i = 0; i < idOfCreatedReports.length; i++) {
                    if (idOfCreatedReports[i] === reportId) {
                        isCreatedByUser = true;
                        break;
                    }
                }

                if (isCreatedByUser) {
                    jQuery($article.children("div").get(0)).append(links);
                }
            }
        }.bind(this));
    },

    getIdOfCreatedReports: function () {
        var isGlobalScope = true;
        var asString = this.getFromLocalStorage("idOfCreatedReports", isGlobalScope);
        return asString ? JSON.parse(asString) : [];
    },

    fadeOutFloatingAlerts: function () {
        var $floatingAlerts = jQuery(".alert.floating");

        _.delay(function () {
            $floatingAlerts.fadeOut("slow", function () {
                $floatingAlerts.remove();
            });
        }, this.floatingAlertFadeOutDelay);
    },

    showAlert: function (id, text) {
        // In case another alert is displayed, we delete it
        jQuery(".alert.floating").remove();

        var $alertDiv = jQuery('<div id="' + id + '" class="alert alert-success floating">' + text + '</div>');

        this.getEl().prepend($alertDiv);
        _.delay(function () {
            $alertDiv.fadeOut("slow", function () {
                $alertDiv.remove();
            });
        }, this.floatingAlertFadeOutDelay);
    },

    _doEditReport: function (initialReport, successUrl) {
        if (this.editReportValidator.isValid()) {
            this.$confirmEditBtn.button('loading');

            var $authorName = jQuery("#edit-author-name");

            var $mppRadios = jQuery("[name='edit-MPP']");
            var $yesMppRadio = $mppRadios.filter("[value='" + CBR.Models.Report.radioAnswer.yes + "']");
            var $noMppRadio = $mppRadios.filter("[value='" + CBR.Models.Report.radioAnswer.no + "']");

            var $safiRadios = jQuery("[name='edit-SAFI']");
            var $yesSafiRadio = $safiRadios.filter("[value='" + CBR.Models.Report.radioAnswer.yes + "']");
            var $noSafiRadio = $safiRadios.filter("[value='" + CBR.Models.Report.radioAnswer.no + "']");

            var $ocuRadios = jQuery("[name='edit-OCU']");
            var $yesOcuRadio = $ocuRadios.filter("[value='" + CBR.Models.Report.radioAnswer.yes + "']");
            var $noOcuRadio = $ocuRadios.filter("[value='" + CBR.Models.Report.radioAnswer.no + "']");

            var $pvcRadios = jQuery("[name='edit-PVC']");
            var $yesPvcRadio = $pvcRadios.filter("[value='" + CBR.Models.Report.radioAnswer.yes + "']");
            var $noPvcRadio = $pvcRadios.filter("[value='" + CBR.Models.Report.radioAnswer.no + "']");

            var authorName = $authorName.val();
            var selectedSupportLevel = jQuery("#edit-support-level").val();

            var isMoneyInPoliticsAProblem = null;
            if ($yesMppRadio.prop("checked")) {
                isMoneyInPoliticsAProblem = true;
            } else if ($noMppRadio.prop("checked")) {
                isMoneyInPoliticsAProblem = false;
            }

            var isSupportingAmendmentToFixIt = null;
            if ($yesSafiRadio.prop("checked")) {
                isSupportingAmendmentToFixIt = true;
            } else if ($noSafiRadio.prop("checked")) {
                isSupportingAmendmentToFixIt = false;
            }

            var isOpposingCitizensUnited = null;
            if ($yesOcuRadio.prop("checked")) {
                isOpposingCitizensUnited = true;
            } else if ($noOcuRadio.prop("checked")) {
                isOpposingCitizensUnited = false;
            }

            var hasPreviouslyVotedForConvention = null;
            if ($yesPvcRadio.prop("checked")) {
                hasPreviouslyVotedForConvention = true;
            } else if ($noPvcRadio.prop("checked")) {
                hasPreviouslyVotedForConvention = false;
            }

            var updatedReport = {
                id: initialReport.getId(),
                candidateId: initialReport.getCandidateId(),
                authorName: authorName,
                contact: jQuery("#edit-contact").val(),
                isMoneyInPoliticsAProblem: isMoneyInPoliticsAProblem,
                isSupportingAmendmentToFixIt: isSupportingAmendmentToFixIt,
                isOpposingCitizensUnited: isOpposingCitizensUnited,
                hasPreviouslyVotedForConvention: hasPreviouslyVotedForConvention,
                supportLevel: selectedSupportLevel ? selectedSupportLevel : null,
                notes: jQuery("#edit-notes").val()
            };

            new Request({
                urlEncoded: false,
                headers: { "Content-Type": "application/json" },
                emulation: false, // Otherwise PUT and DELETE requests are sent as POST
                url: "/api/reports/",
                data: JSON.stringify(updatedReport),
                onSuccess: function (responseText, responseXML) {
                    location.replace(successUrl);
                },
                onFailure: function (xhr) {
                    this.$confirmEditBtn.button('reset');
                    alert("AJAX fail :(");
                }.bind(this)
            }).put();
        }
    },

    navigateToStateLegislatorPage: function (e) {
        location.href = "/state-legislators/" + jQuery(e.currentTarget).data("id");
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

    _applyModernizrRules: function () {
        if (!Modernizr.input.placeholder) {
            jQuery(".mdnz-polyfill.placeholder").show();
        }
    },

    httpStatusCode: {
        noContent: 204,
        unauthorized: 401
    },

    floatingAlertFadeOutDelay: 3000
});