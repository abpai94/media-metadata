package mediametadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mediametadata.controller.MediaRepository;
import mediametadata.model.MediaType;
import mediametadata.model.Movie;
import mediametadata.model.Series;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(classes = MediaMetadataApplication.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class MediaMetadataApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    private MediaRepository mediaRepository = new MediaRepository();

    private ObjectMapper objectMapper = mediaRepository.objectMapper();

    private String input = "";

    private String output = "";

    public void setUp(MediaType mediaType) throws JsonProcessingException {
        switch (mediaType) {
            case MOVIE -> {
                input = "{\r\n    \"id\": \"e407def8-395e-4590-8984-6af13a6a5c8f\"," +
                        "\r\n    \"title\": \"Spiderman\"," +
                        "\r\n    \"labels\": [\r\n        \"Comics\"\r\n    ]," +
                        "\r\n    \"director\": \"Sam Ramy\"," +
                        "\r\n    \"releaseDate\": 2002\r\n}";
                output = objectMapper.writeValueAsString(
                        new Movie(
                                UUID.fromString("e407def8-395e-4590-8984-6af13a6a5c8f"),
                                "Spiderman",
                                new ArrayList<>(Arrays.asList("Comics")),
                                "Sam Ramy",
                                new Date(2002)));
            }
            case SERIES -> {
                input = "{\r\n    \"id\": \"e307def8-395e-4590-8984-6af13a6a5c8f\"," +
                        "\r\n    \"title\": \"Daredevil\"," +
                        "\r\n    \"labels\": [\r\n        \"Comics\"\r\n    ]," +
                        "\r\n    \"numberOfEpisodes\": 30\r\n}";
                output = objectMapper.writeValueAsString(
                        new Series(
                                UUID.fromString("e307def8-395e-4590-8984-6af13a6a5c8f"),
                                "Daredevil",
                                new ArrayList<>(Arrays.asList("Comics")),
                                30));
            }
            default -> {
                input = "";
                output = "";
            }
        }
    }

    public void cleanUp(MediaType mediaType) throws Exception {
        switch (mediaType) {
            case MOVIE -> {
                mockMvc.perform(delete("/media/e407def8-395e-4590-8984-6af13a6a5c8f"));
            }
            case SERIES -> {
                mockMvc.perform(delete("/media/e307def8-395e-4590-8984-6af13a6a5c8f"));
            }
            case ALL -> {
                mockMvc.perform(delete("/media/e407def8-395e-4590-8984-6af13a6a5c8f"));
                mockMvc.perform(delete("/media/e307def8-395e-4590-8984-6af13a6a5c8f"));
            }
        }
    }

    @Test
    public void testGetAll() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        String combinedOutput = "[" + output + ",";
        setUp(MediaType.SERIES);
        combinedOutput += output + "]";
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = mockMvc.perform(get("/media")
                        .contentType("application/json"))
                .andDo(print()).andReturn();
        Assert.assertEquals(combinedOutput, mvcResult.getResponse().getContentAsString());
        cleanUp(MediaType.ALL);
    }

    @Test
    public void testGetByTitle() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = mockMvc.perform(get("/media?title=Spiderman")
                        .contentType("application/json"))
                .andDo(print()).andReturn();
        Assert.assertEquals("[" + output + "]", mvcResult.getResponse().getContentAsString());
        cleanUp(MediaType.MOVIE);
    }

    @Test
    public void testGetById() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = mockMvc.perform(get("/media/e407def8-395e-4590-8984-6af13a6a5c8f")
                        .contentType("application/json"))
                .andDo(print()).andReturn();
        Assert.assertEquals(output, mvcResult.getResponse().getContentAsString());
        cleanUp(MediaType.MOVIE);
    }

    @Test
    public void testGetMovie() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = mockMvc.perform(get("/media/movies")
                        .contentType("application/json"))
                .andDo(print()).andReturn();
        Assert.assertEquals("[" + output + "]", mvcResult.getResponse().getContentAsString());
        cleanUp(MediaType.MOVIE);
    }

    @Test
    public void testGetSeries() throws Exception {
        setUp(MediaType.SERIES);
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = mockMvc.perform(get("/media/series")
                        .contentType("application/json"))
                .andDo(print()).andReturn();
        Assert.assertEquals("[" + output + "]", mvcResult.getResponse().getContentAsString());
        cleanUp(MediaType.SERIES);
    }

    @Test
    public void testPostMovie() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/media")
                        .content(input)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(output));
        cleanUp(MediaType.MOVIE);
    }

    @Test
    public void testPostSeries() throws Exception {
        setUp(MediaType.SERIES);
        mockMvc.perform(post("/media")
                        .content(input)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(output));
        cleanUp(MediaType.SERIES);
    }

    @Test
    public void testFindCommon() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        String combinedOutput = "[" + output + ",";
        setUp(MediaType.SERIES);
        combinedOutput += output + "]";
        mockMvc.perform(post("/media")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = this.mockMvc.perform(
                        get("/media/related/e407def8-395e-4590-8984-6af13a6a5c8f")
                                .contentType("application/json"))
                .andDo(print())
                .andReturn();
        Assert.assertEquals(combinedOutput, mvcResult.getResponse().getContentAsString());
        cleanUp(MediaType.MOVIE);
        cleanUp(MediaType.SERIES);
    }

}