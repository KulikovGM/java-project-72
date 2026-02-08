package hexlet.code.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Url {
    private int id;
    private String name;
    private Instant createdAt;
    private List<UrlCheck> urlChecks = new ArrayList<>();

    public Url(String name) {
        this.name = name;
    }
}
