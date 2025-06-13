package org.sqlite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RecoverTest {
    @TempDir File tempDir;

    @Test
    public void recoverTest() throws SQLException, IOException {
        // create a memory database
        File tmpFile = File.createTempFile("recover-test", ".sqlite", tempDir);
        File dest = tmpFile.toPath().getParent().resolve("recovered-" + UUID.randomUUID() + ".db").toFile();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:")) {
            // memory DB to file
            try (Statement stmt = conn.createStatement()) {
                createTableAndInsertRows(stmt);

                stmt.executeUpdate("recover to " + dest.getAbsolutePath());
            }
        }
        System.out.println(dest.getAbsolutePath());
        assertThat(dest.exists()).isTrue();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dest.getAbsolutePath())) {
            try (Statement stmt = conn.createStatement()) {
                // assert expected table and rows exist
                ResultSet rs = stmt.executeQuery("select * from sample");
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("id")).isEqualTo(1);
                assertThat(rs.getString("name")).isEqualTo("leo");
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("id")).isEqualTo(2);
                assertThat(rs.getString("name")).isEqualTo("yui");
            }
        }
    }

    private void createTableAndInsertRows(Statement stmt) throws SQLException {
        stmt.executeUpdate("create table sample(id, name)");
        stmt.executeUpdate("insert into sample values(1, \"leo\")");
        stmt.executeUpdate("insert into sample values(2, \"yui\")");
    }
}
