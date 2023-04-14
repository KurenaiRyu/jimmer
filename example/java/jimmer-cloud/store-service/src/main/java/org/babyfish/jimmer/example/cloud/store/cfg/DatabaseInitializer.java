package org.babyfish.jimmer.example.cloud.store.cfg;

import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.Initializer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseInitializer implements Initializer {

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void initialize(JSqlClient sqlClient) throws Exception {
        try (Connection con = dataSource.getConnection()) {
            InputStream inputStream = JimmerConfig.class
                    .getClassLoader()
                    .getResourceAsStream("store.sql");
            if (inputStream == null) {
                throw new RuntimeException("no `store.sql`");
            }
            try (Reader reader = new InputStreamReader(inputStream)) {
                char[] buf = new char[1024];
                StringBuilder sqlBuilder = new StringBuilder();
                while (true) {
                    int len = reader.read(buf);
                    if (len == -1) {
                        break;
                    }
                    sqlBuilder.append(buf, 0, len);
                }
                con.createStatement().execute(sqlBuilder.toString());
            }
        }
    }
}
