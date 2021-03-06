package buzzplay.oauth2.server.common.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

public class WebApiSecurityOAuth2ConfigAdapter extends AuthorizationServerConfigurerAdapter {

    private Boolean checkUserScopes;
    private DataSource dataSource;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private TokenStore tokenStore;
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    private OAuth2RequestFactory oAuth2RequestFactory;
    private AuthenticationManager authenticationManager;
    private ClientDetailsService clientDetailsService;

    public WebApiSecurityOAuth2ConfigAdapter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                                             ClientDetailsService clientDetailsService, JwtAccessTokenConverter jwtAccessTokenConverter,
                                             TokenStore tokenStore, OAuth2RequestFactory oAuth2RequestFactory, PasswordEncoder passwordEncoder,
                                             Boolean checkUserScopes, DataSource dataSource) {

        this.clientDetailsService = clientDetailsService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
        this.tokenStore = tokenStore;
        this.oAuth2RequestFactory = oAuth2RequestFactory;
        this.passwordEncoder = passwordEncoder;
        this.checkUserScopes = checkUserScopes;
        this.dataSource = dataSource;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore)
                .tokenEnhancer(jwtAccessTokenConverter)
                .accessTokenConverter(jwtAccessTokenConverter)
                .authenticationManager(authenticationManager).userDetailsService(userDetailsService);
        if (checkUserScopes)
            endpoints.requestFactory(oAuth2RequestFactory);
    }
}
