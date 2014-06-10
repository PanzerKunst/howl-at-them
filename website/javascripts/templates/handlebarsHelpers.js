Handlebars.registerHelper("getUsStateId", function(legislator) {
    return legislator.getUsState().id;
});

Handlebars.registerHelper("getCurrentSupportLevelSpan", function(legislator) {
    var reports = legislator.getReports();

    var cssClass = "UNKNOWN";
    var label = CBR.Models.Report.supportLevel.UNKNOWN;

    if (reports.length > 0) {
        cssClass = reports[0].getSupportLevel();
        label = reports[0].getReadableSupportLevel();
    }

    return '<span class="support-level ' + cssClass + '">' + label + '</span>';
});

Handlebars.registerHelper("getLatestContact", function(legislator) {
    var reports = legislator.getReports();

    return reports.length > 0 ? reports[0].getReadableContact() : CBR.Models.Report.contact.NO_CONTACT;
});

Handlebars.registerHelper("getReportCount", function(legislator) {
    return legislator.getReports().length;
});

Handlebars.registerHelper("getSpanForYesNoAnswer", function(question, legislator) {
    var cssClass = "UNKNOWN";
    var letter = "?";

    var reports = legislator.getReports();
    if (reports.length > 0) {
        var answer = null;

        switch (question) {
            case "MPP":
                answer = reports[0].isMoneyInPoliticsAProblem();
                break;
            case "SAFI":
                answer = reports[0].isSupportingAmendmentToFixIt();
                break;
            case "OCU":
                answer = reports[0].isOpposingCitizensUnited();
                break;
            case "PVC":
                answer = reports[0].hasPreviouslyVotedForConvention();
        }

        if (answer === true) {
            cssClass = "YES";
            letter = "Y";
        } else if (answer === false) {
            cssClass = "NO";
            letter = "N";
        }
    }

    return '<span class="yes-no-answer ' + cssClass + '">' + letter + '</span>';
});
