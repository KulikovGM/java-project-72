package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

class AppTest {

    private Javalin app;

    @BeforeEach
    final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Бесплатно проверяйте сайты");
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("https://www.example.com:5432");
            UrlRepository.save(url);
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testUrlCreate() {
        JavalinTest.test(app, (server, client) -> {
            var link = "url=https://www.example.com:5432/12345";
            var response = client.post(NamedRoutes.urlsPath(), link);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com:5432");
        });
    }

    @Test
    void testDuplicateUrlCreate() throws SQLException {

        UrlRepository.save(new Url("https://example.com"));
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            client.post(NamedRoutes.urlsPath(), requestBody);
            var allUrls = UrlRepository.getEntities();
            assertThat(allUrls).hasSize(1);
        });
    }

    @Test
    void testUrlWithoutProtocolCreate() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            var allUrls = UrlRepository.getEntities();
            assertThat(allUrls).isEmpty();
        });
    }
}

