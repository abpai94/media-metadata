package mediametadata.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Media {

    @JsonProperty("id")
    private UUID id = UUID.randomUUID();

    @JsonProperty("title")
    private String title;

    @JsonProperty("labels")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private List<String> labels = new ArrayList<>();

    @JsonIgnore
    private MediaType mediaType = MediaType.ALL;

    /**
     * Creator constructor.
     */
    public Media() {
    }

    /**
     * Create a Media object.
     *
     * @param id     {@link UUID}.
     * @param title  title of media.
     * @param labels genre labels of media.
     */
    public Media(UUID id,
                 String title,
                 List<String> labels) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.title = title;
        this.labels = labels;
    }

    public UUID getId() {
        return id;
    }

    /**
     * Title of Media.
     *
     * @return get the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Media genre labels.
     *
     * @return List of Labels.
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Set {@link MediaType}.
     *
     * @param mediaType MOVIE, SERIES or ALL.
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

}
