package mediametadata.model;

public enum MediaType {

    ALL("All"),
    MOVIE("Movie"),
    SERIES("Series");
    private final String value;

    /**
     * Construct MediaType enum.
     */
    MediaType(String value) {
        this.value = value;
    }

}