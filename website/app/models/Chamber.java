package models;

public enum Chamber {
    HOUSE("House", "HD"),
    SENATE("Senate", "SD");

    private String string;
    private String abbr;

    private Chamber(String string, String abbr) {
        this.string = string;
        this.abbr = abbr;
    }

    public String getString() {
        return this.string;
    }

    public String getAbbr() {
        return this.abbr;
    }
}
