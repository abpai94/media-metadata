package mediametadata;

import mediametadata.controller.MediaRepository;
import mediametadata.model.Media;
import mediametadata.model.Movie;
import mediametadata.model.Series;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = MediaMetadataApplication.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class MediaMetadataApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    private MediaMetadataApplication mediaMetadataApplication;

    List<Media> mediaMetadata = Stream.of(
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

    String testJSON;

    @Before
    public void setUp() {

    }

    @Test
    public void testPost() throws Exception {
        MediaRepository mediaRepository1 = new MediaRepository();
        String ordersJson = mediaRepository1.objectMapper().writeValueAsString(mediaMetadata);
        this.mockMvc.perform(post("/media")
                        .content(ordersJson)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(ordersJson));

    }

    @Test
    public void testGet() throws Exception {
        MediaRepository mediaRepository1 = new MediaRepository();
        String ordersJson = mediaRepository1.objectMapper().writeValueAsString(mediaMetadata);
        this.mockMvc.perform(post("/media")
                .content(ordersJson)
                .contentType("application/json"));

        this.mockMvc.perform(get("/media")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(content().json(ordersJson));
    }

}
