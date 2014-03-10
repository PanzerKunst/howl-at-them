package models;

public enum ContactWithLegislator {
    MET_LEGISLATOR("Met legislator"),
    TALKED_TO_LEGISLATOR("Talked to legislator"),
    CONTACT_WITH_STAFF("Contact with staff"),
    WAITING_FOR_CALLBACK("Waiting for callback"),
    LEFT_VOICEMAIL("Left voicemail"),
    NONE("None");

    private String string;

    private ContactWithLegislator(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }
}
