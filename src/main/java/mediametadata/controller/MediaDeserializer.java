package mediametadata.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import mediametadata.model.Movie;
import mediametadata.model.Series;
import mediametadata.model.Media;

import java.io.IOException;

/**
 * Custom deserializer class to distinguish between {@link Movie} and {@link Series}.
 */
public class MediaDeserializer extends StdDeserializer<Media> {

    public MediaDeserializer() {
        this(null);
    }

    public MediaDeserializer(final Class<?> vd) {
        super(vd);
    }

    /**
     * Creates a deserializer for {@link ObjectMapper} that maps correctly to {@link Movie} and {@link Series}.
     *
     * @param jsonParser             Parsed used for reading JSON content
     * @param deserializationContext Context that can be used to access information about
     *                               this deserialization activity.
     * @return {@link Media}
     * @throws IOException
     */
    @Override
    public Media deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final JsonNode mediaNode = mapper.readTree(jsonParser);

        Media media = null;

        if (mediaNode.has("director")) {
            media = mapper.treeToValue(mediaNode, Movie.class);
        }
        if (mediaNode.has("numberOfEpisodes")) {
            media = mapper.treeToValue(mediaNode, Series.class);
        }

        return media;
    }

}
