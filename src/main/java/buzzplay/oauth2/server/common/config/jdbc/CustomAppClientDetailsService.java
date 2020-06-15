package buzzplay.oauth2.server.common.config.jdbc;

import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

public class CustomAppClientDetailsService extends JdbcClientDetailsService {

    private static final String CLIENT_FIELDS_FOR_UPDATE = "resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";
    private static final String CLIENT_FIELDS = "client_secret, " + CLIENT_FIELDS_FOR_UPDATE;
    private static final String BASE_FIND_STATEMENT = "select client_id, " + CLIENT_FIELDS
            + " from oauth_app_client";

    public static final String DEFAULT_FIND_STATEMENT
            = BASE_FIND_STATEMENT + " order by client_id";

    public static final String DEFAULT_SELECT_STATEMENT
            = BASE_FIND_STATEMENT + " where client_id = ?";

    public static final String DEFAULT_INSERT_STATEMENT
            = "insert into oauth_app_client (" + CLIENT_FIELDS + ", client_id) values (?,?,?,?,?,?,?,?,?,?,?)";

    public static final String DEFAULT_UPDATE_STATEMENT
            = "update oauth_app_client set " + CLIENT_FIELDS_FOR_UPDATE.replaceAll(", ", "=?, ") + "=? where client_id = ?";

    public static final String DEFAULT_UPDATE_SECRET_STATEMENT
            = "update oauth_app_client set client_secret = ? where client_id = ?";

    public static final String DEFAULT_DELETE_STATEMENT
            = "delete from oauth_app_client where client_id = ?";


    public CustomAppClientDetailsService(DataSource dataSource) {
        this(dataSource, DEFAULT_SELECT_STATEMENT, DEFAULT_FIND_STATEMENT, DEFAULT_INSERT_STATEMENT,
                DEFAULT_UPDATE_STATEMENT, DEFAULT_UPDATE_SECRET_STATEMENT, DEFAULT_DELETE_STATEMENT);
    }

    public CustomAppClientDetailsService(DataSource dataSource, String selectClientDetailsSql, String findClientDetailsSql,
                                         String insertClientDetailsSql, String updateClientDetailsSql, String updateClientSecretSql,
                                         String deleteClientDetailsSql) {

        super(dataSource);

        super.setSelectClientDetailsSql(selectClientDetailsSql);
        super.setFindClientDetailsSql(findClientDetailsSql);
        super.setInsertClientDetailsSql(insertClientDetailsSql);
        super.setUpdateClientDetailsSql(updateClientDetailsSql);
        super.setUpdateClientSecretSql(updateClientSecretSql);
        super.setDeleteClientDetailsSql(deleteClientDetailsSql);
    }


}
