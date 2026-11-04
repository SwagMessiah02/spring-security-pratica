package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class V1_1__Create_join_table_user_role extends BaseJavaMigration  {
	public void migrate(Context context) throws Exception {
		new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true))
			.execute("CREATE TABLE tb_user_role (\r\n"
					+ "user_fk INTEGER,\r\n"
					+ "role_fk INTEGER,\r\n"
					+ "FOREIGN KEY (user_fk) REFERENCES users (id),\r\n"
					+ "FOREIGN KEY (role_fk) REFERENCES roles (id)"
					+ ");");
	}
}