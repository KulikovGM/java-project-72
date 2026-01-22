package hexlet.code;

import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);
    }

    public static Javalin getApp() {
        Javalin jvl = null;
        return jvl;
    }
}