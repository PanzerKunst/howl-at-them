package models;

public enum SupportLevel {
    SUPPORTIVE("Supportive"),
    NEEDS_CONVINCING("Needs convincing"),
    NOT_SUPPORTIVE("Not supportive");

    private String string;

    private SupportLevel(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }
}
