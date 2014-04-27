CBR.Models.Report = new Class({
    Extends: CBR.Models.JsonSerializable,

    options: {  // Defaults
    },

    getId: function() {
        return this.options.id;
    },

    getCandidateId: function() {
        return this.options.candidateId;
    },

    getAuthorName: function() {
        return this.options.authorName;
    },

    getContact: function() {
        return this.options.contact;
    },

    isMoneyInPoliticsAProblem: function() {
        return this.options.isMoneyInPoliticsAProblem;
    },

    isSupportingAmendmentToFixIt: function() {
        return this.options.isSupportingAmendmentToFixIt;
    },

    isOpposingCitizensUnited: function() {
        return this.options.isOpposingCitizensUnited;
    },

    hasPreviouslyVotedForConvention: function() {
        return this.options.hasPreviouslyVotedForConvention;
    },

    getSupportLevel: function() {
        return this.options.supportLevel ? this.options.supportLevel : "UNKNOWN";
    },

    getNotes: function() {
        return this.options.notes;
    },

    getReadableSupportLevel: function() {
        var supportLevel = this.options.supportLevel;
        return supportLevel ? CBR.Models.Report.supportLevel[this.getSupportLevel()] : CBR.Models.Report.supportLevel.UNKNOWN;
    },

    getReadableContact: function() {
        return this.getContact() ? CBR.Models.Report.contact[this.getContact()] : "No contact";
    }
});

CBR.Models.Report.radioAnswer = {
    unknown: null,
    yes: "true",
    no: "false"
};

CBR.Models.Report.supportLevel = {
    SUPPORTIVE: "Supportive",
    NEEDS_CONVINCING: "Needs convincing",
    NOT_SUPPORTIVE: "Not supportive",
    UNKNOWN: "Unknown"
};

CBR.Models.Report.contact = {
    MET_LEGISLATOR: "Met legislator",
    TALKED_TO_LEGISLATOR: "Talked to legislator",
    CONTACT_WITH_STAFF: "Contact with staff",
    WAITING_FOR_CALLBACK: "Waiting for callback",
    LEFT_VOICEMAIL: "Left voicemail"
};
