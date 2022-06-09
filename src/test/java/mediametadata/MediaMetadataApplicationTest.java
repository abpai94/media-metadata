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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class MediaMetadataApplicationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private MediaRepository mediaRepository = new MediaRepository();

    protected ObjectMapper objectMapper = mediaRepository.objectMapper();

    String input = "";

    String output = "";

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    public void setUp(MediaType mediaType) throws JsonProcessingException, ParseException {
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
        }
    }

    /**
     * Gets all the {@link Media} objects in repository.
     *
     * @throws Exception
     */
    @Test
    public void testGetAll() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/movie")
                .content(input)
                .contentType("application/json"));
        String combinedOutput = "[" + output + ",";
        setUp(MediaType.SERIES);
        mockMvc.perform(post("/series")
                .content(input)
                .contentType("application/json"));
        combinedOutput += output + "]";
        MvcResult mvcResult = mockMvc.perform(get("/media")
                        .contentType("application/json"))
                .andDo(print())
                .andDo(document("get-all"))
                .andReturn();
        Assert.assertEquals(combinedOutput, mvcResult.getResponse().getContentAsString());
    }

    /**
     * Gets {@link Media} object by title.
     *
     * @throws Exception
     */
    @Test
    public void testGetByTitle() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/movie")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = mockMvc.perform(get("/media?title=Spiderman")
                        .contentType("application/json"))
                .andDo(print())
                .andDo(document("get-by-title"))
                .andReturn();
        Assert.assertEquals("[" + output + "]", mvcResult.getResponse().getContentAsString());
    }

    /**
     * Get {@link Media} object by Id.
     *
     * @throws Exception
     */
    @Test
    public void testGetById() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/movie")
                .content(input)
                .contentType("application/json"));
        mockMvc.perform(get("/media/{id}",
                        "e407def8-395e-4590-8984-6af13a6a5c8f")
                        .contentType("application/json"))
                .andDo(print())
                .andDo(document("get-by-id"))
                .andExpect(MockMvcResultMatchers.content().json(output));
    }

    /**
     * Get all {@link Movie} objects.
     *
     * @throws Exception
     */
    @Test
    public void testGetMovie() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/movie")
                .content(input)
                .contentType("application/json"));
        MvcResult mvcResult = mockMvc.perform(get("/media/movies")
                        .contentType("application/json"))
                .andDo(print())
                .andDo(document("get-movie"))
                .andReturn();
        Assert.assertEquals("[" + output + "]", mvcResult.getResponse().getContentAsString());
    }

    /**
     * Get all {@link Series} objects.
     *
     * @throws Exception
     */
    @Test
    public void testGetSeries() throws Exception {
        setUp(MediaType.SERIES);
        mockMvc.perform(post("/series")
                .content(input)
                .contentType("application/json"));
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(objectMapper.readValue(new StringReader(output), Media.class));
        MvcResult mvcResult = mockMvc.perform(get("/media/series")
                        .contentType("application/json"))
                .andDo(print())
                .andDo(document("get-series"))
                .andReturn();
        Assert.assertEquals("[" + output + "]", mvcResult.getResponse().getContentAsString());
    }

    /**
     * POST {@link Movie} JSON data to store in repository.
     *
     * @throws Exception
     */
    @Test
    public void testPostMovie() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/movie")
                        .content(input)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(output))
                .andDo(document("post-movie",
                        responseFields(
                                fieldWithPath("id").description("The UUID of Movie object."),
                                fieldWithPath("title").description("The Movie title."),
                                fieldWithPath("labels").description("Genre labels for Movie."),
                                fieldWithPath("director").description("Director of Movie."),
                                fieldWithPath("releaseDate").description("Year of release for the Movie"))));
    }

    /**
     * POST {@link Series} JSON data to store in repository.
     *
     * @throws Exception
     */
    @Test
    public void testPostSeries() throws Exception {
        setUp(MediaType.SERIES);
        mockMvc.perform(post("/series")
                        .content(input)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(output))
                .andDo(document("post-series",
                        responseFields(
                                fieldWithPath("id").description("The UUID of Series object."),
                                fieldWithPath("title").description("The Series title."),
                                fieldWithPath("labels").description("Genre labels for Series."),
                                fieldWithPath("numberOfEpisodes").description("Number of release episodes of the Series."))));
    }

    /**
     * Get {@link Media} objects with commons labels.
     *
     * @throws Exception
     */
    @Test
    public void testGetCommon() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/movie")
                .content(input)
                .contentType("application/json"));
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(objectMapper.readValue(new StringReader(output), Media.class));
        setUp(MediaType.SERIES);
        mediaList.add(objectMapper.readValue(new StringReader(output), Media.class));
        mockMvc.perform(post("/series")
                .content(input)
                .contentType("application/json"));
        this.mockMvc.perform(
                        get("/media/related/{id}",
                                "e407def8-395e-4590-8984-6af13a6a5c8f")
                                .contentType("application/json"))
                .andDo(document("get-common"))
                .andExpectAll(jsonPath("$[0].id", containsString("e407def8-395e-4590-8984-6af13a6a5c8f")),
                        jsonPath("$[0].title", containsString("Spiderman")),
                        jsonPath("$[0].labels", isA(List.class)),
                        jsonPath("$[0].director", containsString("Sam Ramy")),
                        jsonPath("$[0].releaseDate", containsString("2002")))
                .andExpectAll(jsonPath("$[1].id", containsString("e307def8-395e-4590-8984-6af13a6a5c8f")),
                        jsonPath("$[1].title", containsString("Daredevil")),
                        jsonPath("$[1].labels", isA(List.class)),
                        jsonPath("$[1].numberOfEpisodes", comparesEqualTo(30)))
//                        pathParameters(parameterWithName("{id}").description("UUID of media object"))))
                .andReturn();
    }

    @Test
    public void testDelete() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(post("/movie")
                .content(input)
                .contentType("application/json"));
        mockMvc.perform(delete("/media/{id}",
                        "e407def8-395e-4590-8984-6af13a6a5c8f")
                        .contentType("application/json"))
                .andDo(document("delete-media"));
    }
}
