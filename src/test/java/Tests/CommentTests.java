package Tests;

import domain.Comment;
import io.restassured.http.Method;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ServiceController.TestApiClient;
import ServiceController.TestRequestSpecFactory;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommentTests {

    @Test
    @Tag("commentsByPostId")
    void shouldReturnListWhenGetCommentsByPostId() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/comments?postId=1");
        assertThat(response.statusCode(), is(200));
        List<Comment> comments = response.jsonPath().getList("", Comment.class);
        assertThat(comments, is(not(empty())));
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/comment-schema.json"));
    }

    @Test
    @Tag("commentsByInvalidPostId")
    void shouldReturnEmptyListWhenGetCommentsByInvalidPostId() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/comments?postId=0");
        assertThat(response.statusCode(), is(200));
        List<Comment> comments = response.jsonPath().getList("", Comment.class);
        assertThat(comments, is(empty()));
    }

    @Test
    @Tag("commentsByPostId")
    void shouldReturnBadRequestWhenInvalidQueryParam() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/comments?invalidParam=1000");
        assertThat(response.statusCode(), is(400));
    }

    @Test
    @Tag("commentsByPostId")
    void shouldReturnNotFoundWhenInvalidEndpoint() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/invalidEndpoint");
        assertThat(response.statusCode(), is(404));
    }

    @Test
    @Tag("commentsByPostId")
    void shouldReturnServerErrorWhenServerFails() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/comments?triggerServerError=true");
        assertThat(response.statusCode(), is(500));
    }

    @Test
    @Tag("commentsByPostId")
    void validateListReturnedByGetCommentsByPostId() {
        boolean flag = false;
        for (int postId = 1; postId <= 100; postId++) {
            Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/comments?postId=" + postId);
            assertThat(response.statusCode(), is(200));
            List<Comment> commentsListOne = response.jsonPath().getList("", Comment.class);
            response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/posts/" + postId + "/comments");
            assertThat(response.statusCode(), is(200));
            List<Comment> commentsListTwo = response.jsonPath().getList("", Comment.class);
            if (commentsListOne.equals(commentsListTwo)) {
                flag = true;
            } else {
                flag = false;
                break;
            }
        }
        assertTrue(flag);
    }

    @Test
    @Tag("commentsByPostId")
    void validateListReturnedByGetCommentsByPostIdPathParameter() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/posts/" + 1 + "/comments");
        assertThat(response.statusCode(), is(200));
        List<Comment> comments = response.jsonPath().getList("", Comment.class);
        assertThat(comments, is(not(empty())));
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/comment-schema.json"));
    }

    @Test
    @Tag("commentsByPostId")
    void validateListReturnedByGetCommentsByPostIdInvalidPathParameter() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/posts/" + 1000 + "/comments");
        assertThat(response.statusCode(), is(200));
        assertTrue(response.getBody().jsonPath().getList("$").isEmpty());
    }

    @Test
    @Tag("commentsByPostId")
    void validateListReturnedByGetCommentsByPostIdInvalidPath() {
        Response response = TestApiClient.send(TestRequestSpecFactory.getDefaultSpec(), Method.GET, "/post/" + 1000 + "/commt");
        assertThat(response.statusCode(), is(404));
    }
}
