CBR.Models.StateLegislator = new Class({
    Extends: CBR.Models.JsonSerializable,

    options: {  // Defaults
    },

    getId: function() {
        return this.options.id;
    },

    getFirstName: function() {
        return this.options.firstName;
    },

    getLastName: function() {
        return this.options.lastName;
    },

    getTitle: function() {
        return this.options.title;
    },

    getPoliticalParties: function() {
        return this.options.politicalParties;
    },

    getUsState: function() {
        return this.options.usState;
    },

    getDistrict: function() {
        return this.options.district;
    },

    getReports: function() {
        return this.options.reports.map(function(report) {
            return new CBR.Models.Report(report);
        });
    },

    getReportCount: function() {
        return this.options.reports.length;
    },

    getLatestReport: function() {
        return this.options.reports.length > 0 ? new CBR.Models.Report(this.options.reports[0]) : null;
    },

    getTitleAbbr: function() {
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

    getPoliticalPartiesAbbr: function() {
        if (this.getPoliticalParties().length === 0) {
            return null;
        }

        switch (this.getPoliticalParties()[0].toLowerCase()) {
            case "democratic":
                return "<abbr title=\"" + this.getPoliticalParties().join(", ") + "\">D</abbr>";
            case "republican":
                return "<abbr title=\"" + this.getPoliticalParties().join(", ") + "\">R</abbr>";
            default:
                var abbr = this.getPoliticalParties().map(function(politicalParty) {
                    return politicalParty.substring(0, 1);
                }).join("");
                return "<abbr title=\"" + this.getPoliticalParties().join(", ") + "\">" + abbr + "</abbr>";
        }
    }
});
