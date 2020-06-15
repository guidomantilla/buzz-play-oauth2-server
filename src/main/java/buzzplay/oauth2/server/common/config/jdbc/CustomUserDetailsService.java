package buzzplay.oauth2.server.common.config.jdbc;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import javax.sql.DataSource;
import java.util.Objects;

public class CustomUserDetailsService extends JdbcDaoImpl implements UserDetailsService {

    public static final String USERS_BY_USERNAME_QUERY
            = "SELECT username, password, enabled FROM auth_user WHERE username = ?";

    public static final String AUTHORITIES_BY_USERNAME_QUERY
            = "SELECT username, authority FROM auth_authority WHERE username = ?";

    public static final String GROUP_AUTHORITIES_BY_USERNAME_QUERY = "SELECT g.id, g.group_name, ga.authority FROM groups g, group_members gm, group_authorities ga "
            + "WHERE gm.username = ? " + " AND g.id = ga.group_id AND g.id = gm.group_id";

    public CustomUserDetailsService(DataSource dataSource) {
        this(dataSource, USERS_BY_USERNAME_QUERY, AUTHORITIES_BY_USERNAME_QUERY, null);
    }

    public CustomUserDetailsService(DataSource dataSource, boolean enableGroups) {
        this(dataSource, USERS_BY_USERNAME_QUERY, AUTHORITIES_BY_USERNAME_QUERY, enableGroups ? GROUP_AUTHORITIES_BY_USERNAME_QUERY : null);
    }

    public CustomUserDetailsService(DataSource dataSource, String usersByUsernameQuery, String authoritiesByUsernameQuery) {
        this(dataSource, usersByUsernameQuery, authoritiesByUsernameQuery, null);
    }

    public CustomUserDetailsService(DataSource dataSource, String usersByUsernameQuery, String authoritiesByUsernameQuery, String groupAuthoritiesByUsernameQuery) {

        super();

        super.setUsersByUsernameQuery(usersByUsernameQuery);
        super.setAuthoritiesByUsernameQuery(authoritiesByUsernameQuery);
        super.setGroupAuthoritiesByUsernameQuery(groupAuthoritiesByUsernameQuery);

        super.setEnableAuthorities(!Objects.isNull(authoritiesByUsernameQuery));
        super.setEnableGroups(!Objects.isNull(groupAuthoritiesByUsernameQuery));

        super.setUsernameBasedPrimaryKey(true);

        super.setDataSource(dataSource);
    }

}
