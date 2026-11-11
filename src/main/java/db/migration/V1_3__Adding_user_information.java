package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class V1_3__Adding_user_information extends BaseJavaMigration {
	public void migrate(Context context) throws Exception {
		new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true))
			.execute("INSERT INTO users (username, email, password) VALUES ('TesteB2', 'teste@gmail.com', '$2a$12$5In4iNx5f5s4ovSxUN8ikuZIx7AFqPTFp43DwWDemRNadQxHHX9KS');"
					+ "INSERT INTO roles (role) VALUES ('USER');"
					+ "INSERT INTO tb_user_role (user_fk, role_fk) VALUES (1,1);");
	}
}
