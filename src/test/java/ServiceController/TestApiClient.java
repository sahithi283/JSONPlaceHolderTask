package ServiceController;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public final class TestApiClient {
    private TestApiClient() {
    }

    public static Response send(RequestSpecification spec, Method method, String path, Object body) {
        RequestSpecification request = given().spec(spec);
        if (body != null) {
            request.body(body);
        }
        return request.request(method, path);
    }

    public static Response send(RequestSpecification spec, Method method, String path) {
        return send(spec, method, path, null);
    }
}
