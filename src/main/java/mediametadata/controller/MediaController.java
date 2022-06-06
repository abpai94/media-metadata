package mediametadata.controller;

import mediametadata.model.Media;
import mediametadata.model.MediaType;
import mediametadata.model.Movie;
import mediametadata.model.Series;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that manages REST calls.
 */
@RestController
public class MediaController {

    /**
     * {@link MediaRepository}.
     */
    private final MediaRepository mediaRepository;

    /**
     * Initialises the {@link MediaRepository}.
     *
     * @param mediaRepository repository used to {@link Media}.
     */
    MediaController(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    /**
     * GET for /media. Optionally include string using title variable
     * to fetch character matched {@link List} of {@link Media}.
     *
     * @param title Optional variable to return List of matched characters in title.
     * @return {@link List} of {@link Media}.
     */
    @GetMapping("/media")
    List<Media> all(@RequestParam(required = false, value = "title") String title) {
        return title != null ?
                mediaRepository.findByCharacters(title) :
                mediaRepository.findAll(MediaType.ALL);
    }

    /**
     * Find {@link Media} based on {@link java.util.UUID}.
     *
     * @param id {@link java.util.UUID} of {@link Media}.
     * @return {@link Media}.
     */
    @GetMapping("/media/{id}")
    Media findById(@PathVariable String id) {
        return mediaRepository.findById(id);
    }

    /**
     * Find all {@link Movie}.
     *
     * @return {@link List} of all {@link Movie}.
     */
    @GetMapping("/media/movies")
    List<Media> allMovies() {
        return mediaRepository.findAll(MediaType.MOVIE);
    }

    /**
     * Fina all {@link Series}.
     *
     * @return {@link List} of all {@link Series}.
     */
    @GetMapping("/media/series")
    List<Media> allSeries() {
        return mediaRepository.findAll(MediaType.SERIES);
    }

    /**
     * Find {@link List} of deleted {@link Media}.
     *
     * @return {@link List}.
     */
    @GetMapping("/media/deleted")
    List<Media> findDeletedAll() {
        return mediaRepository.findDeleted(MediaType.ALL);
    }

    /**
     * Find {@link List} of deleted {@link Movie}.
     *
     * @return {@link List}.
     */
    @GetMapping("/media/movie/deleted")
    List<Media> findDeletedMovies() {
        return mediaRepository.findDeleted(MediaType.MOVIE);
    }

    /**
     * Find {@link List} of deleted {@link Series}.
     *
     * @return {@link List}.
     */
    @GetMapping("/media/series/deleted")
    List<Media> findDeletedSeries() {
        return mediaRepository.findDeleted(MediaType.SERIES);
    }

    /**
     * Find {@link Media} based on related labels.
     *
     * @param id {@link java.util.UUID}.
     * @return {@link List} of {@link Media}.
     */
    @GetMapping("/media/related/{id}")
    List<Media> findCommon(@PathVariable String id) {
        return mediaRepository.findByLabel(id);
    }

    /**
     * Create a new {@link Media}.
     *
     * @param media JSON string from HTTP data.
     * @return {@link Media} object after serializing.
     */
    @PostMapping("/media")
    Media addMedia(@RequestBody String media) {
        return mediaRepository.addMedia(media);
    }

    /**
     * Delete {@link Media} based on {@link java.util.UUID}.
     *
     * @param id {@link java.util.UUID}.
     * @return true if deleted from repository.
     */
    @DeleteMapping("/media/{id}")
    boolean deleteMedia(@PathVariable String id) {
        return mediaRepository.deleteMedia(id);
    }

}
