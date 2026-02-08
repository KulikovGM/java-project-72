
package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.UrlsController;
import hexlet.code.dto.BasePage;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class App {
    public static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    public static String readResourceFile(String fileName) throws IOException {
        InputStream inputStream = Optional.ofNullable(App.class.getClassLoader().getResourceAsStream(fileName))
                .orElseThrow(() -> new FileNotFoundException("Resource file not found: " + fileName));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        var app = getApp();
        app.start(getPort());
    }

    public static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static Javalin getApp() throws IOException, SQLException {

        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());

        var dataSource = new HikariDataSource(hikariConfig); //try-catch and close finaly

        var sql = readResourceFile("schema.sql");

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get("/", ctx -> {
            var page = new BasePage();
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
            ctx.render("index.jte", model("page", page));
        });

        app.get(NamedRoutes.urlsPath(), UrlsController::index);

        app.post(NamedRoutes.urlsPath(), UrlsController::create);

        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);

        app.post(NamedRoutes.urlCheckPath("{id}"), UrlsController::check);

        return app;
    }

    public static String getParsedCreatedAt(Instant createdAt) {
        LocalDateTime parsedCreatedAt = LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return parsedCreatedAt.format(formatter);
    }

    public static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1");
    }
}
