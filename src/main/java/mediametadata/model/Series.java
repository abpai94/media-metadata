package mediametadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.UUID;

@JsonTypeName("Series")
public class Series extends Media {

    @JsonProperty("numberOfEpisodes")
    private int numberOfEpisodes;

    /**
     * Creator constructor.
     */
    public Series() {
    }

    /**
     * Constructor to create Series object.
     *
     * @param id               {@link UUID}.
     * @param title            Series title.
     * @param labels           Genre labels.
     * @param numberOfEpisodes Number of episodes of the Series.
     */
    public Series(UUID id,
                  String title,
                  List<String> labels,
                  int numberOfEpisodes) {
        super(id, title, labels);
        super.setMediaType(MediaType.SERIES);
        this.numberOfEpisodes = numberOfEpisodes;
    }

}
