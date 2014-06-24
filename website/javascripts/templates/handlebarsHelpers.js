Handlebars.registerHelper("getSpanForYesNoAnswerLegislatorLevel", function(question, legislator) {
    var cssClass = "UNKNOWN";
    var letter = "?";

    var latestReport = legislator.getLatestReport();
    if (latestReport) {
        var answer = null;

        switch (question) {
            case "MPP":
                answer = latestReport.isMoneyInPoliticsAProblem();
                break;
            case "SAFI":
                answer = latestReport.isSupportingAmendmentToFixIt();
                break;
            case "OCU":
                answer = latestReport.isOpposingCitizensUnited();
                break;
            case "PVC":
                answer = latestReport.hasPreviouslyVotedForConvention();
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

Handlebars.registerHelper("getSpanForYesNoAnswerReportLevel", function(question, report) {
    var cssClass = "UNKNOWN";
    var letter = "?";
    var answer = null;

    switch (question) {
        case "MPP":
            answer = report.isMoneyInPoliticsAProblem();
            break;
        case "SAFI":
            answer = report.isSupportingAmendmentToFixIt();
            break;
        case "OCU":
            answer = report.isOpposingCitizensUnited();
            break;
        case "PVC":
            answer = report.hasPreviouslyVotedForConvention();
    }

    if (answer === true) {
        cssClass = "YES";
        letter = "Y";
    } else if (answer === false) {
        cssClass = "NO";
        letter = "N";
    }

    return '<span class="yes-no-answer ' + cssClass + '">' + letter + '</span>';
});
