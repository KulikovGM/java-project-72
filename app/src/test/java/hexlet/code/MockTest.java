package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.javalin.Javalin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

class MockTest {

    private static Javalin app;
    private static MockWebServer mockServer;
    private static String testUrlName;

    private static Path getFixturePath(String fileName) {
        return Path.of("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws IOException {
        Path path = getFixturePath(fileName);
        return Files.readString(path).trim();
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.enqueue(new MockResponse().setBody(readFixture("index.jte")));
        testUrlName = mockServer.url("/").toString();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.close();
    }

    @BeforeEach
    public final void beforeEach() throws SQLException, IOException {
        app = App.getApp();
    }

    @Test
    public void testUrlCheckCreate() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url(testUrlName);
            UrlRepository.save(url);
            client.post(NamedRoutes.urlCheckPath(url.getId()));

            var expectedCheck = UrlChecksRepository.getLatestUrlsChecks();
            assertEquals(expectedCheck.get(url.getId()).getStatusCode(), 200);
            assertEquals(expectedCheck.get(url.getId()).getTitle(), "A Sample HTML");
            assertEquals(expectedCheck.get(url.getId()).getH1(), "h1 tag test");
            assertEquals(expectedCheck.get(url.getId()).getDescription(), "Test Description");

        });
    }

}

