package mediametadata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import mediametadata.model.Media;
import mediametadata.model.MediaType;
import mediametadata.model.Movie;
import mediametadata.model.Series;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that represents the repository, converting string to {@link Media},
 * storing it in datastructures and finding them when needed.
 */
@Configuration
@Repository
public class MediaRepository {

    List<Media> mediaMetadata = new ArrayList<>();

    List<Media> deletedMediaMetadata = new ArrayList<>();

    /**
     * Constructor adds a few movies and TV series to the repository to initialize the repository.
     */
    public MediaRepository() {
        mediaMetadata = Stream.of(
                        new Movie(UUID.randomUUID(),
                                "Spider-man",
                                new ArrayList<>(Arrays.asList("Sci-Fi")),
                                "Sam Ramy",
                                new Date()),
                        new Movie(UUID.randomUUID(),
                                "Indiana Jones",
                                new ArrayList<>(Arrays.asList("Adventure")),
                                "George Lucas",
                                new Date()),
                        new Series(UUID.randomUUID(),
                                "Game of Thrones",
                                new ArrayList<>(Arrays.asList("Fantasy")),
                                73),
                        new Series(UUID.randomUUID(),
                                "Breaking Bad",
                                new ArrayList<>(Arrays.asList("Drama", "Tragedy", "Crime")),
                                62))
                .collect(Collectors.toList());
    }

    /**
     * Finds all the Media objects of {@link MediaType} MOVIES and SERIES or ALL.
     *
     * @param mediaType can be MOVIE, SERIES or ALL.
     * @return {@link List} requested {@link MediaType}.
     */
    public List<Media> findAll(MediaType mediaType) {
        switch (mediaType) {
            case MOVIE -> {
                return mediaMetadata.stream().filter(
                        x -> x.getClass().equals(Movie.class)).collect(Collectors.toList());
            }
            case SERIES -> {
                return mediaMetadata.stream().filter(
                        x -> x.getClass().equals(Series.class)).collect(Collectors.toList());
            }
            case ALL -> {
                return mediaMetadata.stream().toList();
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Finds only deleted {@link Media} based on {@link MediaType}.
     *
     * @param mediaType can be MOVIE, SERIES or ALL.
     * @return {@link List} of deleted {@link Media}.
     */
    public List<Media> findDeleted(MediaType mediaType) {
        switch (mediaType) {
            case MOVIE -> {
                return deletedMediaMetadata.stream().filter(
                        x -> x.getClass().equals(Movie.class)).collect(Collectors.toList());
            }
            case SERIES -> {
                return deletedMediaMetadata.stream().filter(
                        x -> x.getClass().equals(Series.class)).collect(Collectors.toList());
            }
            case ALL -> {
                return deletedMediaMetadata.stream().toList();
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Find {@link Media} based on {@link UUID}.
     *
     * @param id UUID of {@link Media}.
     * @return {@link List} of {@link Media}
     */
    public Media findById(String id) {
        return mediaMetadata.stream().filter(
                x -> x.getId().equals(UUID.fromString(id))).findFirst().get();
    }

    /**
     * Find {@link Media} based on characters present in title.
     *
     * @param characters string of characters present in the title.
     * @return {@link List} of {@link Media} that were found.
     */
    public List<Media> findByCharacters(String characters) {
        return mediaMetadata.stream().filter(
                x -> x.getTitle().contains(characters)).collect(Collectors.toList());
    }

    /**
     * Store {@link Media} in the repository.
     *
     * @param media String representing {@link Media} data.
     * @return parsed and serialised {@link Media} object.
     */
    public Media addMedia(String media) {
        Media convertedMedia = null;
        try {
            convertedMedia = convertStringToMedia(media);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Media findingMedia = convertedMedia;
        if (mediaMetadata.stream().noneMatch(
                x -> x.getTitle().equals(findingMedia.getTitle()))) {
            mediaMetadata.add(convertedMedia);
        }
        return mediaMetadata.stream().filter(
                x -> x.getTitle().equals(findingMedia.getTitle())).findFirst().get();
    }

    /**
     * Use {@link UUID} to delete {@link Media} and store in a separate datastructures.
     *
     * @param id {@link UUID} of {@link Media}.
     * @return true if it was deleted.
     */
    public boolean deleteMedia(String id) {
        Media media = mediaMetadata.stream().filter(
                x -> x.getId().equals(UUID.fromString(id))).findFirst().get();
        return deletedMediaMetadata.add(media) && mediaMetadata.remove(media);
    }

    /**
     * Find {@link List} of {@link Media} that have matching labels.
     *
     * @param id UUID of {@link Media}.
     * @return {@link List} of {@link Media}.
     */
    public List<Media> findByLabel(String id) {
        Media findMedia = mediaMetadata.stream().filter(
                x -> x.getId().equals(UUID.fromString(id))).findFirst().get();
        return mediaMetadata.stream().filter(
                m -> m.getLabels().stream().anyMatch(
                        l -> findMedia.getLabels().contains(l))).collect(Collectors.toList());
    }

    /**
     * Convert JSON string into Serialized {@link Media}.
     *
     * @param media String data from POST request.
     * @return {@link Media} object.
     * @throws IOException thrown if the {@link ObjectMapper} fails to parse string.
     */
    private Media convertStringToMedia(String media) throws IOException {
        ObjectMapper objectMapper = objectMapper();
        Reader reader = new StringReader(media);
        Media newMedia = null;
        newMedia = objectMapper.readValue(reader, Media.class);
        return newMedia;
    }

    /**
     * Create a deserializer that recognises {@link Media}.
     *
     * @return {@link ObjectMapper}.
     */
    @Bean
    public ObjectMapper objectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Media.class, new MediaDeserializer());
        return new ObjectMapper().registerModule(simpleModule);
    }

}