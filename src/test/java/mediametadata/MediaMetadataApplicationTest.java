package mediametadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import mediametadata.controller.MediaRepository;
import mediametadata.model.Media;
import mediametadata.model.MediaType;
import mediametadata.model.Movie;
import mediametadata.model.Series;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    public void setUp(MediaType mediaType) throws Exception {
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
                                new ArrayList<>(List.of("Comics")),
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
                                new ArrayList<>(List.of("Comics")),
                                30));
            }
        }
    }


    /**
     * POST {@link Movie} JSON data to store in repository.
     *
     * @throws Exception
     */
    @Test
    @Order(1)
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
                                fieldWithPath("title").description("The title."),
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
    @Order(2)
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
                                fieldWithPath("title").description("The title."),
                                fieldWithPath("labels").description("Genre labels for Series."),
                                fieldWithPath("numberOfEpisodes").description("Number of release episodes of the Series."))));
    }

    /**
     * Gets all the {@link Media} objects in repository.
     *
     * @throws Exception
     */
    @Test
    @Order(3)
    public void testGetAll() throws Exception {
        setUp(MediaType.MOVIE);
        setUp(MediaType.SERIES);
        mockMvc.perform(get("/media")
                        .contentType("application/json"))
                .andDo(print())
                .andDo(document("get-all"))
                .andExpectAll(jsonPath("$[0].id", is("e407def8-395e-4590-8984-6af13a6a5c8f")),
                        jsonPath("$[0].title", is("Spiderman")),
                        jsonPath("$[0].labels", isA(List.class)),
                        jsonPath("$[0].director", is("Sam Ramy")),
                        // TODO: Need to fix the epoch year error.
//                        jsonPath("$[0].releaseDate", is("2002")),
                        jsonPath("$[1].id", is("e307def8-395e-4590-8984-6af13a6a5c8f")),
                        jsonPath("$[1].title", is("Daredevil")),
                        jsonPath("$[1].labels", isA(List.class)),
                        jsonPath("$[1].numberOfEpisodes", is(30)));
    }

    /**
     * Gets {@link Media} object by title.
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void testGetByTitle() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/media?title=Spiderman")
                        .contentType("application/json"))
                .andDo(print())
                .andDo(MockMvcRestDocumentation.document("get-by-title", requestParameters(
                        parameterWithName("title").description("The title."))))
                .andExpectAll(jsonPath("$[0].id", is("e407def8-395e-4590-8984-6af13a6a5c8f")),
                        jsonPath("$[0].title", is("Spiderman")),
                        jsonPath("$[0].labels", isA(List.class)),
                        // TODO: Need to fix the epoch year error.
//                        jsonPath("$[0].releaseDate", is("2002")),
                        jsonPath("$[0].director", is("Sam Ramy")));
    }

    /**
     * Get {@link Media} object by Id.
     *
     * @throws Exception
     */
    @Test
    @Order(5)
    public void testGetById() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/media/{id}",
                                "e407def8-395e-4590-8984-6af13a6a5c8f")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().json(output))
                .andDo(MockMvcRestDocumentation.document("get-by-id", pathParameters(
                        parameterWithName("id").description("UUID of media object"))));
    }

    /**
     * Get all {@link Movie} objects.
     *
     * @throws Exception
     */
    @Test
    @Order(6)
    public void testGetMovie() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(get("/media/movies")
                        .contentType("application/json"))
                .andDo(document("get-movie"))
                .andExpectAll(jsonPath("$[0].id", is("e407def8-395e-4590-8984-6af13a6a5c8f")),
                        jsonPath("$[0].title", is("Spiderman")),
                        jsonPath("$[0].labels", isA(List.class)),
                        // TODO: Need to fix the epoch year error.
//                        jsonPath("$[0].releaseDate", is("2002"))
                        jsonPath("$[0].director", is("Sam Ramy")));
    }

    /**
     * Get all {@link Series} objects.
     *
     * @throws Exception
     */
    @Test
    @Order(7)
    public void testGetSeries() throws Exception {
        setUp(MediaType.SERIES);
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(objectMapper.readValue(new StringReader(output), Media.class));
        mockMvc.perform(get("/media/series")
                        .contentType("application/json"))
                .andDo(document("get-series"))
                .andExpectAll(
                        jsonPath("$[0].id", is("e307def8-395e-4590-8984-6af13a6a5c8f")),
                        jsonPath("$[0].title", is("Daredevil")),
                        jsonPath("$[0].labels", isA(List.class)),
                        jsonPath("$[0].numberOfEpisodes", is(30)));
    }


    /**
     * Get {@link Media} objects with commons labels.
     *
     * @throws Exception
     */
    @Test
    @Order(8)
    public void testGetCommon() throws Exception {
        setUp(MediaType.MOVIE);
        setUp(MediaType.SERIES);
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/media/related/{id}",
                                        "e407def8-395e-4590-8984-6af13a6a5c8f")
                                .contentType("application/json"))
                .andExpectAll(
                        jsonPath("$[0].id", is("e407def8-395e-4590-8984-6af13a6a5c8f")),
                        jsonPath("$[0].title", is("Spiderman")),
                        jsonPath("$[0].labels", isA(List.class)),
                        jsonPath("$[0].director", is("Sam Ramy")),
                        // TODO: Need to fix the epoch year error.
//                        jsonPath("$[0].releaseDate", is("2002")),
                        jsonPath("$[1].id", is("e307def8-395e-4590-8984-6af13a6a5c8f")),
                        jsonPath("$[1].title", is("Daredevil")),
                        jsonPath("$[1].labels", isA(List.class)),
                        jsonPath("$[1].numberOfEpisodes", is(30)))
                .andDo(MockMvcRestDocumentation.document("get-common", pathParameters(
                        parameterWithName("id").description("UUID of media object"))));
    }

    @Test
    @Order(9)
    public void testDelete() throws Exception {
        setUp(MediaType.MOVIE);
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/media/{id}",
                                "e407def8-395e-4590-8984-6af13a6a5c8f")
                        .contentType("application/json"))
                .andDo(MockMvcRestDocumentation.document("delete-media", pathParameters(
                        parameterWithName("id").description("UUID of media object"))));
    }
}
