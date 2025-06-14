// --------------------------------------
// sqlite-jdbc Project
//
// BackupTest.java
// Since: Feb 18, 2009
//
// $URL$
// $Author$
// --------------------------------------
package org.rfresh.sqlite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.rfresh.sqlite.core.DB;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class RecoverTest {
    @TempDir File tempDir;

    @Test
    public void recoverTest() throws SQLException, IOException {
        // create a memory database
        File tmpFile = File.createTempFile("recover-test", ".sqlite", tempDir);
        File dest = tmpFile.toPath().getParent().resolve("recovered-" + UUID.randomUUID() + ".db").toFile();

        try (Connection conn = DriverManager.getConnection("jdbc:rfresh_sqlite:")) {
            // memory DB to file
            try (Statement stmt = conn.createStatement()) {
                createTableAndInsertRows(stmt);

                stmt.executeUpdate("recover to " + dest.getAbsolutePath());
            }
        }
        System.out.println(dest.getAbsolutePath());
        assertThat(dest.exists()).isTrue();

        try (Connection conn = DriverManager.getConnection("jdbc:rfresh_sqlite:" + dest.getAbsolutePath())) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("select * from sample");
                AtomicInteger count = new AtomicInteger();
                while (rs.next()) {
                    assertThat(rs.getInt(1)).isEqualTo(count.incrementAndGet());
                    assertThat(rs.getString(2)).isIn("leo", "yui");
                }
                assertThat(count.get()).isEqualTo(2);
            }
        }
    }

    private void createTableAndInsertRows(Statement stmt) throws SQLException {
        stmt.executeUpdate("create table sample(id, name)");
        stmt.executeUpdate("insert into sample values(1, \"leo\")");
        stmt.executeUpdate("insert into sample values(2, \"yui\")");
    }
}
