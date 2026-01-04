package core;

// Minimal placeholder used only to satisfy main-source compilation in environments
// where test dependencies (RestAssured) are not available. Real helpers for tests
// live under src/test/java/testcore.
public final class ApiClient {
    private ApiClient() {
    }

    // Placeholder signature retained to avoid compile errors where used by main code.
    public static Object send(Object spec, Object method, String path, Object body) {
        throw new UnsupportedOperationException("Test helper only");
    }

    public static Object send(Object spec, Object method, String path) {
        return send(spec, method, path, null);
    }
}
