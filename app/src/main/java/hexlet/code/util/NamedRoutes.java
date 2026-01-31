package hexlet.code.util;

public class NamedRoutes {

    public static String rootPath() {
        return "/";
    }

    public static String urlsPath() {
        return "/urls";
    }

    public static String urlPath(int id) {
        return urlPath(String.valueOf(id));
    }

    public static String urlPath(String id) {
        return "/urls/" + id;
    }

    public static String urlCheckPath(int id) {
        return urlCheckPath(String.valueOf(id));
    }

    public static String urlCheckPath(String id) {
        return urlPath(id) + "/checks";
    }
}
