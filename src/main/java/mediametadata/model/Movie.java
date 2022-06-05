package mediametadata.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@JsonTypeName("Movie")
public class Movie extends Media {

    @JsonProperty("director")
    private String director;

    @JsonProperty("releaseDate")
    @JsonFormat(pattern = "yyyy")
    private Date releaseDate;

    /**
     * Creator constructor.
     */
    public Movie() {
    }

    /**
     * Constructor to create Movie object.
     *
     * @param id          {@link UUID}.
     * @param title       Movie title.
     * @param labels      Genre labels.
     * @param director    Movie director.
     * @param releaseDate Release year of the Movie.
     */
    public Movie(UUID id,
                 String title,
                 List<String> labels,
                 String director,
                 Date releaseDate) {
        super(id, title, labels);
        super.setMediaType(MediaType.MOVIE);
        this.director = director;
        this.releaseDate = releaseDate;
    }

}
