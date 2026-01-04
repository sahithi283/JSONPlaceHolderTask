package ServiceController;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public final class TestResponseSpecFactory {
    private TestResponseSpecFactory() {
    }

    public static ResponseSpecification getOk200Spec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
    }
}
