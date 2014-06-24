CBR.Models.StateLegislator = new Class({
    Extends: CBR.Models.JsonSerializable,

    options: {  // Defaults
    },

    getId: function () {
        return this.options.id;
    },

    getFirstName: function () {
        return this.options.firstName;
    },

    getLastName: function () {
        return this.options.lastName;
    },

    getTitle: function () {
        return this.options.title;
    },

    getPoliticalParties: function () {
        return this.options.politicalParties;
    },

    getUsState: function () {
        return this.options.usState;
    },

    getDistrict: function () {
        return this.options.district;
    },

    getLeadershipPosition: function() {
        return this.options.leadershipPosition;
    },

    getOffices: function() {
        return this.options.offices;
    },

    getCommittees: function() {
        return this.options.committees;
    },

    getReports: function () {
        return this.options.reports.map(function (report) {
            return new CBR.Models.Report(report);
        });
    },

    getOtherPhoneNumber: function () {
        return this.options.otherPhoneNumber;
    },

    isAPriorityTarget: function () {
        return this.options.isAPriorityTarget;
    },

    isMissingUrgentReport: function () {
        return this.options.isMissingUrgentReport;
    },

    getLatestReport: function() {
        var reports = this.getReports();
        if (reports.length > 0) {
            return reports[0];
        }
        return null;
    },

    getLatestContact: function() {
        var latestReport = this.getLatestReport();
        return latestReport ? latestReport.getReadableContact() : CBR.Models.Report.contact.NO_CONTACT;
    },

    getReportCount: function() {
        return this.getReports().length;
    },

    getTitleAbbr: function () {
        switch (this.getTitle().toLowerCase()) {
            case "representative":
                return "<abbr title=\"" + this.getTitle() + "\">Rep.</abbr>";
            case "senator":
                return "<abbr title=\"" + this.getTitle() + "\">Sen.</abbr>";
            case "assembly member":
                return "<abbr title=\"" + this.getTitle() + "\">Asm.</abbr>";
            default:
                return this.getTitle();
        }
    },

    getPoliticalPartiesAbbr: function () {
        if (this.getPoliticalParties().length === 0) {
            return null;
        }

        switch (this.getPoliticalParties()[0].toLowerCase()) {
            case "democratic":
                return "<abbr title=\"" + this.getPoliticalParties().join(", ") + "\">D</abbr>";
            case "republican":
                return "<abbr title=\"" + this.getPoliticalParties().join(", ") + "\">R</abbr>";
            default:
                var abbr = this.getPoliticalParties().map(function (politicalParty) {
                    return politicalParty.substring(0, 1);
                }).join("");
                return "<abbr title=\"" + this.getPoliticalParties().join(", ") + "\">" + abbr + "</abbr>";
        }
    },

    getChamber: function () {
        if (this.getTitle().toLowerCase() == "senator") {
            return "SD";
        }
        return "HD";
    },

    getCurrentSupportLevelSpan: function() {
        var cssClass = "UNKNOWN";
        var label = CBR.Models.Report.supportLevel.UNKNOWN;

        var latestReport = this.getLatestReport();
        if (latestReport) {
            cssClass = latestReport.getSupportLevel();
            label = latestReport.getReadableSupportLevel();
        }

        return '<span class="support-level ' + cssClass + '">' + label + '</span>';
    }
});
