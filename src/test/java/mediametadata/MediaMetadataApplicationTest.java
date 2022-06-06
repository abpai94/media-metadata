package mediametadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediametadata.controller.MediaRepository;
import mediametadata.model.Media;
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

    private MediaRepository mediaRepository = new MediaRepository();

    private String input;

    private String output;

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

    String testJSON;

    @Before
    public void setUp() throws JsonProcessingException {
        input = "{\r\n    \"id\": \"e307def8-395e-4590-8984-6af13a6a5c8f\"," +
                "\r\n    \"title\": \"Sopranos\"," +
                "\r\n    \"labels\": [\r\n        \"Drama\"\r\n    ]," +
                "\r\n    \"numberOfEpisodes\": 50\r\n}";
        output = mediaRepository.objectMapper().writeValueAsString(
                new Series(
                        UUID.fromString("e307def8-395e-4590-8984-6af13a6a5c8f"),
                        "Sopranos",
                        new ArrayList<>(Arrays.asList("Drama")),
                        50));
    }

    @Test
    public void testPost() throws Exception {
        this.mockMvc.perform(post("/media")
                        .content(input)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(output));

    }

    @Test
    public void testGet() throws Exception {
        this.mockMvc.perform(post("/media")
                        .content(input)
                        .contentType("application/json"))
                .andDo(print());
        MvcResult mvcResult = this.mockMvc.perform(get("/media")
                        .contentType("application/json"))
                .andDo(print()).andReturn();
        Assert.assertEquals("[" + output + "]", mvcResult.getResponse().getContentAsString());
    }

}
