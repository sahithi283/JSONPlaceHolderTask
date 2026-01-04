package Tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Post;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.http.Method;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ServiceController.TestApiClient;
import ServiceController.TestRequestSpecFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Posts Tests Epic")
@Feature("Get and Posts Comments Features")
public class PostTests {

    JsonNode jsonFromFile;
    JsonNode jsonFromResponse;

    @BeforeAll
    static void beforeAll() {
        // warm up
        given().spec(TestRequestSpecFactory.getDefaultSpec()).when().get("/posts");
    }

    @Test
    @Tag("getAllPosts")
    @Story("Verify GET /posts returns 200 and non-empty list")
    @Description("This test verifies that a GET request to /posts returns a 200 status code and a non-empty list of posts conforming to the post schema.")
    public void shouldReturn200AndNonEmptyListWhenGetAllPosts() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/posts");
        assertThat(response.statusCode(), is(200));
        List<Post> posts = response.jsonPath().getList("", Post.class);
        assertThat(posts, is(not(empty())));
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    @Test
    @Tag("getAllPosts")
    public void verifyTheContentReturnedByGetAllPosts() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/posts");
        assertThat(response.statusCode(), is(200));
        List<Post> posts = response.jsonPath().getList("", Post.class);
        assertThat(posts, is(not(empty())));
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/post-schema.json"));
        File jsonFile = new File("src/test/resources/JSONFiles/ListOfPosts.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonFromFile = objectMapper.readTree(jsonFile);
            jsonFromResponse = objectMapper.readTree(response.getBody().asString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(jsonFromFile, jsonFromResponse);
    }

    @Test
    @Tag("getPostById")
    void shouldReturn200AndValidPostWhenGetPostById() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/posts/1");
        assertThat(response.statusCode(), is(200));
        Post postObject = response.as(Post.class);
        assertThat(postObject.getId(), is(1));
        assertThat(postObject.getUserId(), is(notNullValue()));
        assertThat(postObject.getTitle(), is(not(emptyString())));
    }

    @Test
    @Tag("getPostById")
    void shouldReturnEmptyOr404WhenGetNonExistingPostId() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/posts/0");
        String body = response.getBody().asString().trim();
        assertThat(response.statusCode(), is(404));
        assertThat(body.length(), lessThanOrEqualTo(2));
    }

    @Test
    @Tag("createPost")
    void shouldReturn201AndCreatePost() {
        Post newPost = new Post(1, "the leadership journey", "types and styles of leadership and its essentials");
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.POST, "/posts", newPost);
        assertThat(response.statusCode(), is(201));
        Post postObject = response.as(Post.class);
        assertThat(postObject.getId(), is(notNullValue()));
        assertThat(postObject.getTitle(), is("the leadership journey"));
    }

    @Test
    @Tag("putPost")
    void shouldReturn200AndUpdatedWhenUpdatePost() {
        Post update = new Post(1, "put-title", "put-body");
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.PUT, "/posts/1", update);
        assertThat(response.statusCode(), is(200));
        Post postObject = response.as(Post.class);
        assertThat(postObject.getTitle(), is("put-title"));
    }

    @Test
    @Tag("patchPost")
    void shouldReturn200AndPatchPost() {
        String patchJson = "{\"title\":\"patched\"}";
        Response r = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.PATCH, "/posts/1", patchJson);
        assertThat(r.statusCode(), is(200));
        assertThat(r.jsonPath().getString("title"), is("patched"));
    }

    @Test
    @Tag("deletePost")
    void shouldReturn200Or204WhenDeletePost() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.DELETE, "/posts/1");
        assertThat(response.statusCode(), anyOf(is(200), is(204)));
    }

    @Test
    @Tag("negative")
    void shouldReturn400WhenInvalidPostData() {
        String invalidPostJson = "{\"invalidField\":\"value\"}";
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.POST, "/posts", invalidPostJson);
        assertThat(response.statusCode(), is(400));
    }

    @Test
    @Tag("postNotFound")
    void shouldReturn404WhenPostNotFound() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/posts/99999");
        assertThat(response.statusCode(), is(404));
    }

    @Test
    @Tag("serverError")
    void shouldReturn500WhenServerErrorOccurs() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/posts?triggerServerError=true");
        assertThat(response.statusCode(), is(500));
    }
}
