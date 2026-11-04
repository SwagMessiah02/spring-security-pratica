package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration; 
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class V1_2__Alter_user_and_role_id_column_name extends BaseJavaMigration {
	public void migrate(Context context) throws Exception {
		new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true))
			.execute("ALTER TABLE users RENAME COLUMN id TO user_id;"
					+ "ALTER TABLE roles RENAME COLUMN id TO role_id;");
	}
}
