package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlChecksRepository extends BaseRepository {

    public static void save(UrlCheck urlCheck) throws SQLException {
        urlCheck.setCreatedAt(Instant.now());
        String sql = "INSERT INTO url_checks (status_code, title, h1, description, url_id, created_at) "
                     + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, urlCheck.getStatusCode());
            preparedStatement.setString(2, urlCheck.getTitle());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getDescription());
            preparedStatement.setInt(5, urlCheck.getUrlId());
            preparedStatement.setTimestamp(6, Timestamp.from(urlCheck.getCreatedAt()));
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Map<Integer, UrlCheck> getLatestUrlsChecks() throws SQLException {
        String sql = "SELECT DISTINCT ON (url_id) * FROM url_checks ORDER BY url_id, created_at DESC";
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);
            Map<Integer, UrlCheck> latestUrlsChecks = new HashMap<>();
            while (resultSet.next()) {
                var urlId = resultSet.getInt("url_id");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at").toInstant();
                var latestUrlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
                latestUrlCheck.setCreatedAt(createdAt);
                latestUrlsChecks.put(urlId, latestUrlCheck);
            }
            return latestUrlsChecks;
        }
    }

    public static List<UrlCheck> getEntities(int urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ?";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, urlId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<UrlCheck> entities = new ArrayList<>();
            while (resultSet.next()) {
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var id = resultSet.getInt("id");
                var createdAt = resultSet.getTimestamp("created_at").toInstant();
                UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
                urlCheck.setCreatedAt(createdAt);
                urlCheck.setId(id);
                entities.add(urlCheck);
            }
            return entities;
        }
    }

}
