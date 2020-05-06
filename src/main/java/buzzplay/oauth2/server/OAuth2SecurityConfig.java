package buzzplay.oauth2.server;

import toolbox.spring.oauth2.common.OAuth2ApiSecurityConfigAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class OAuth2SecurityConfig extends OAuth2ApiSecurityConfigAdapter {

    @Autowired
    public OAuth2SecurityConfig(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                                JwtAccessTokenConverter jwtAccessTokenConverter, TokenStore tokenStore,
                                OAuth2RequestFactory oAuth2RequestFactory, PasswordEncoder passwordEncoder,
                                @Value("${toolbox.oauth2.check-user-scopes}") Boolean checkUserScopes,
                                DataSource dataSource) {

        super(authenticationManager, userDetailsService, jwtAccessTokenConverter, tokenStore,
                oAuth2RequestFactory, passwordEncoder, checkUserScopes, dataSource);
    }
}
