package buzzplay.oauth2.common;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

public class OAuth2ApiSecurityConfigAdapter extends AuthorizationServerConfigurerAdapter {

    private Boolean checkUserScopes;
    private DataSource dataSource;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private TokenStore tokenStore;
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    private OAuth2RequestFactory oAuth2RequestFactory;
    private AuthenticationManager authenticationManager;

    public OAuth2ApiSecurityConfigAdapter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                                          JwtAccessTokenConverter jwtAccessTokenConverter, TokenStore tokenStore,
                                          OAuth2RequestFactory oAuth2RequestFactory, PasswordEncoder passwordEncoder, Boolean checkUserScopes,
                                          DataSource dataSource) {

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
        clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore).tokenEnhancer(jwtAccessTokenConverter)
                .authenticationManager(authenticationManager).userDetailsService(userDetailsService);
        if (checkUserScopes)
            endpoints.requestFactory(oAuth2RequestFactory);
    }
}
