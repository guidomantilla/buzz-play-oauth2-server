package buzzplay.oauth2.server;

import buzzplay.oauth2.server.common.config.WebApiSecurityOAuth2ConfigAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

/**
 * An instance of Legacy Authorization Server (spring-security-oauth2) that uses a single,
 * not-rotating key and exposes a JWK endpoint.
 * <p>
 * See
 * <a
 * target="_blank"
 * href="https://docs.spring.io/spring-security-oauth2-boot/docs/current-SNAPSHOT/reference/htmlsingle/">
 * Spring Security OAuth Autoconfig's documentation</a> for additional detail
 */
@Configuration
@EnableAuthorizationServer
public class WebSecurityOAuth2Config extends WebApiSecurityOAuth2ConfigAdapter {

    @Autowired
    public WebSecurityOAuth2Config(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                                   @Qualifier("customAppClientDetailsService") ClientDetailsService clientDetailsService,
                                   JwtAccessTokenConverter jwtAccessTokenConverter, TokenStore tokenStore,
                                   OAuth2RequestFactory oAuth2RequestFactory, PasswordEncoder passwordEncoder,
                                   @Value("${toolbox.oauth2.check-user-scopes}") Boolean checkUserScopes, DataSource dataSource) {

        super(authenticationManager, userDetailsService, clientDetailsService, jwtAccessTokenConverter, tokenStore,
                oAuth2RequestFactory, passwordEncoder, checkUserScopes, dataSource);
    }
}
