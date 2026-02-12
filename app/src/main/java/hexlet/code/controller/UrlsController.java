package hexlet.code.controller;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static io.javalin.rendering.template.TemplateUtil.model;

/**
 * UrlsController.
 */
public class UrlsController {
    private static final String ACTION_1 = "flash";
    private static final String ACTION_2 = "flash-type";
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var latestUrlsChecks = UrlChecksRepository.getLatestUrlsChecks();
        var page = new UrlsPage(urls, latestUrlsChecks);
        page.setFlash(ctx.consumeSessionAttribute(ACTION_1));
        page.setFlashType(ctx.consumeSessionAttribute(ACTION_2));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));
        url.setUrlChecks(UrlChecksRepository.getEntities(id));
        var page = new UrlPage(url);
        page.setFlash(ctx.consumeSessionAttribute(ACTION_1));
        page.setFlashType(ctx.consumeSessionAttribute(ACTION_2));
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var inputUrl = ctx.formParam("url");
        URL parsedUrl;
        try {
            var uri = new URI(inputUrl);
            parsedUrl = uri.toURL();
        } catch (Exception e) {
            ctx.sessionAttribute(ACTION_1, "Некорректный URL");
            ctx.sessionAttribute(ACTION_2, "danger");
            ctx.redirect("/");
            return;
        }
        String normalizedUrl = String
                .format(
                        "%s://%s%s",
                        parsedUrl.getProtocol(),
                        parsedUrl.getHost(),
                        parsedUrl.getPort() == -1 ? "" : ":" + parsedUrl.getPort()
                )
                .toLowerCase();
        if (!UrlRepository.existsByName(normalizedUrl)) {
            Url url = new Url(normalizedUrl);
            UrlRepository.save(url);
            ctx.sessionAttribute(ACTION_1, "Страница успешно добавлена");
            ctx.sessionAttribute(ACTION_2, "success");
        } else {
            ctx.sessionAttribute(ACTION_1, "Страница уже существует");
            ctx.sessionAttribute(ACTION_2, "info");
        }
        ctx.redirect(NamedRoutes.urlsPath());
    }

    public static void check(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));
        HttpResponse<String> response;
        try {
            response = Unirest.get(url.getName()).asString();
            var statusCode = response.getStatus();
            var body = response.getBody();
            Document parsedBody = Jsoup.parse(body);
            String title = parsedBody.title();
            String h1 = parsedBody.select("h1").text();
            String description = parsedBody.select("meta[name=description]").attr("content");
            UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, id);
            UrlChecksRepository.save(urlCheck);
            ctx.sessionAttribute(ACTION_1, "Страница успешно проверена");
            ctx.sessionAttribute(ACTION_2, "success");
            ctx.redirect(NamedRoutes.urlPath(id));
        } catch (UnirestException e) {
            ctx.sessionAttribute(ACTION_1, "Некорректный адрес");
            ctx.sessionAttribute(ACTION_2, "danger");
            ctx.redirect(NamedRoutes.urlPath(id));
        }
    }
}
