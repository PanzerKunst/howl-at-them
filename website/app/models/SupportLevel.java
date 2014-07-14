package models;

public enum SupportLevel {
    PRIMARY_SPONSOR("Primary sponsor"),
    CO_SPONSOR("Co-sponsor"),
    SUPPORTIVE("Supportive"),
    NEEDS_CONVINCING("Needs convincing"),
    NOT_SUPPORTIVE("Not supportive"),
    UNKNOWN("Unknown");

    private String string;

    private SupportLevel(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }
}
