CBR.Models.StateLegislatorWithLatestReportWithNbReports = new Class({
    Extends: CBR.Models.JsonSerializable,

    options: {  // Defaults
    },

    getStateLegislator: function() {
        return this.options.stateLegislator;
    },

    getLatestReport: function() {
        return this.options.latestReport;
    },

    getNbReports: function() {
        return this.options.nbReports;
    }
});
