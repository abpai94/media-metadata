package mediametadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mediametadata.controller.MediaRepository;
import mediametadata.model.Media;
import mediametadata.model.MediaType;
import mediametadata.model.Movie;
import mediametadata.model.Series;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private MediaRepository mediaRepository = new MediaRepository();

    private ObjectMapper objectMapper = mediaRepository.objectMapper();

    private String input = "";

    private String output = "";

    List<Media> mediaMetadata = Stream.of(
                    new Movie(UUID.randomUUID(),
                            "Spider-man",
                            new ArrayList<>(Arrays.asList("Sci-Fi")),
                            "Sam Ramy",
                            new Date()))
//            new Movie(UUID.randomUUID(),
//                    "Indiana Jones",
//                    new ArrayList<>(Arrays.asList("Adventure")),
//                    "George Lucas",
//                    new Date()),
//            new Series(UUID.randomUUID(),
//                    "Game of Thrones",
//                    new ArrayList<>(Arrays.asList("Fantasy")),
//                    73),
//            new Series(UUID.randomUUID(),
//                    "Breaking Bad",
//                    new ArrayList<>(Arrays.asList("Drama", "Tragedy", "Crime")),
//                    62))
            .collect(Collectors.toList());

    public void setUp(MediaType mediaType) throws JsonProcessingException {
        switch (mediaType) {
            case MOVIE -> {
                input += "{\r\n    \"id\": \"e407def8-395e-4590-8984-6af13a6a5c8f\"," +
                        "\r\n    \"title\": \"Spiderman\"," +
                        "\r\n    \"labels\": [\r\n        \"Sci-fi\"\r\n    ]," +
                        "\r\n    \"director\": \"Sam Ramy\"," +
                        "\r\n    \"releaseDate\": 2002\r\n}";
                output += objectMapper.writeValueAsString(
                        new Movie(
                                UUID.fromString("e407def8-395e-4590-8984-6af13a6a5c8f"),
                                "Spiderman",
                                new ArrayList<>(Arrays.asList("Sci-fi")),
                                "Sam Ramy",
                                new Date(2002)));
            }
            case SERIES -> {
                input += "{\r\n    \"id\": \"e307def8-395e-4590-8984-6af13a6a5c8f\"," +
                        "\r\n    \"title\": \"Sopranos\"," +
                        "\r\n    \"labels\": [\r\n        \"Drama\"\r\n    ]," +
                        "\r\n    \"numberOfEpisodes\": 50\r\n}";
                output += objectMapper.writeValueAsString(
                        new Series(
                                UUID.fromString("e307def8-395e-4590-8984-6af13a6a5c8f"),
                                "Sopranos",
                                new ArrayList<>(Arrays.asList("Drama")),
                                50));
            }
            default -> {
                input = "";
                output = "";
            }
        }
    }

    @Test
    public void testPostSeries() throws Exception {
        setUp(MediaType.SERIES);
        mockMvc.perform(post("/media")
                        .content(input)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(output));
        mockMvc.perform(delete("/media/e307def8-395e-4590-8984-6af13a6a5c8f"));
    }

    @Test
    public void testGetSeries() throws Exception {
        setUp(MediaType.SERIES);
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = this.mockMvc.perform(get("/media")
                        .contentType("application/json"))
                .andDo(print()).andReturn();
        Assert.assertEquals("[" + output + "]", mvcResult.getResponse().getContentAsString());
        mockMvc.perform(delete("/media/e307def8-395e-4590-8984-6af13a6a5c8f"));
    }

    @Test
    public void testPostMovie() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/media")
                        .content(input)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(output));
        mockMvc.perform(delete("/media/e407def8-395e-4590-8984-6af13a6a5c8f"));
    }

    @Test
    public void testGetMovie() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = this.mockMvc.perform(get("/media")
                        .contentType("application/json"))
                .andDo(print()).andReturn();
        Assert.assertEquals("[" + output + "]", mvcResult.getResponse().getContentAsString());
        mockMvc.perform(delete("/media/e407def8-395e-4590-8984-6af13a6a5c8f"));
    }


}
