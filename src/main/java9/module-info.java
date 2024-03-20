module org.rfresh.sqlitejdbc {

    requires org.slf4j;
    requires transitive java.sql;
    requires transitive java.sql.rowset;
    requires static org.graalvm.nativeimage;

    exports org.rfresh.sqlite;
    exports org.rfresh.sqlite.core;
    exports org.rfresh.sqlite.date;
    exports org.rfresh.sqlite.javax;
    exports org.rfresh.sqlite.jdbc3;
    exports org.rfresh.sqlite.jdbc4;
    exports org.rfresh.sqlite.util;

    provides java.sql.Driver with org.rfresh.sqlite.JDBC;

}
