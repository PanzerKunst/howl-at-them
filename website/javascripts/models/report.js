CBR.Models.Report = new Class({
    Extends: CBR.Models.JsonSerializable,

    options: {  // Defaults
    },

    getId: function () {
        return this.options.id;
    },

    getCandidateId: function () {
        return this.options.candidateId;
    },

    getAuthorName: function () {
        return this.options.authorName;
    },

    getContact: function () {
        return this.options.contact;
    },

    isMoneyInPoliticsAProblem: function () {
        return this.options.isMoneyInPoliticsAProblem;
    },

    isSupportingAmendmentToFixIt: function () {
        return this.options.isSupportingAmendmentToFixIt;
    },

    isOpposingCitizensUnited: function () {
        return this.options.isOpposingCitizensUnited;
    },

    hasPreviouslyVotedForConvention: function () {
        return this.options.hasPreviouslyVotedForConvention;
    },

    getSupportLevel: function () {
        return this.options.supportLevel;
    },

    getNotes: function () {
        return this.options.notes;
    },

    getCreationTimestamp: function() {
        return this.options.creationTimestamp;
    },

    getReadableSupportLevel: function () {
        switch (this.getSupportLevel()) {
            case CBR.Models.Report.supportLevel.supportive.code:
                return CBR.Models.Report.supportLevel.supportive.label;
            case CBR.Models.Report.supportLevel.needsConvincing.code:
                return CBR.Models.Report.supportLevel.needsConvincing.label;
            case CBR.Models.Report.supportLevel.notSupportive.code:
                return CBR.Models.Report.supportLevel.notSupportive.label;
        }

        return CBR.Models.Report.supportLevel.unknown.label;
    },

    getSupportLevelSpan: function() {
        return '<span class="support-level ' + this.getSupportLevel() + '">' + this.getReadableSupportLevel() + '</span>';
    },

    getReadableContact: function () {
        switch (this.getContact()) {
            case CBR.Models.Report.contact.metLegislator.code:
                return CBR.Models.Report.contact.metLegislator.label;
            case CBR.Models.Report.contact.talkedToLegislator.code:
                return CBR.Models.Report.contact.talkedToLegislator.label;
            case CBR.Models.Report.contact.contactWithStaff.code:
                return CBR.Models.Report.contact.contactWithStaff.label;
            case CBR.Models.Report.contact.waitingForCallback.code:
                return CBR.Models.Report.contact.waitingForCallback.label;
            case CBR.Models.Report.contact.leftVoicemail.code:
                return CBR.Models.Report.contact.leftVoicemail.label;
        }

        return CBR.Models.Report.contact.noContact.label;
    },

    getReadableCreationTimestamp: function() {
        return moment(this.getCreationTimestamp()).format("MM/DD/YYYY h:mm A");
    },

    getNotesForWeb: function() {
        var notes = this.getNotes();
        return notes ? notes.replace(/\n/g, "<br />") : null;
    }
});

CBR.Models.Report.radioAnswer = {
    unknown: null,
    yes: "true",
    no: "false"
};

CBR.Models.Report.supportLevel = {
    supportive: {
        code: "SUPPORTIVE",
        label: "Supportive"
    },
    needsConvincing: {
        code: "NEEDS_CONVINCING",
        label: "Needs convincing"
    },
    notSupportive: {
        code: "NOT_SUPPORTIVE",
        label: "Not supportive"
    },
    unknown: {
        code: "UNKNOWN",
        label: "Unknown"
    }
};

CBR.Models.Report.contact = {
    metLegislator: {
        code: "MET_LEGISLATOR",
        label: "Met legislator"
    },
    talkedToLegislator: {
        code: "TALKED_TO_LEGISLATOR",
        label: "Talked to legislator"
    },
    contactWithStaff: {
        code: "CONTACT_WITH_STAFF",
        label: "Contact with staff"
    },
    waitingForCallback: {
        code: "WAITING_FOR_CALLBACK",
        label: "Waiting for callback"
    },
    leftVoicemail: {
        code: "LEFT_VOICEMAIL",
        label: "Left voicemail"
    },
    noContact: {
        code: "NO_CONTACT",
        label: "No contact"
    }
};
