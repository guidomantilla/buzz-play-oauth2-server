package buzzplay.oauth2.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class OAuth2BeanConfig {

    private final DataSource dataSource;
    private final ClientDetailsService clientDetailsService;
    private final String jwtKeyStore;
    private final String jwtKeyStorePassword;
    private final String jwtKeyAlias;

    @Autowired
    public OAuth2BeanConfig(ClientDetailsService clientDetailsService, DataSource dataSource,
                            @Value("${toolbox.oauth2.jwt.key-store}") String jwtKeyStore,
                            @Value("${toolbox.oauth2.jwt.key-store-password}") String jwtKeyStorePassword,
                            @Value("${toolbox.oauth2.jwt.key-alias}") String jwtKeyAlias) {

        this.clientDetailsService = clientDetailsService;
        this.dataSource = dataSource;

        this.jwtKeyStore = jwtKeyStore.replace("classpath:", "");
        this.jwtKeyStorePassword = jwtKeyStorePassword;
        this.jwtKeyAlias = jwtKeyAlias;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(new KeyStoreKeyFactory(new ClassPathResource(jwtKeyStore), jwtKeyStorePassword.toCharArray()).getKeyPair(jwtKeyAlias));
        return converter;
    }

    @Bean
    @Autowired
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    public OAuth2RequestFactory oAuth2RequestFactory() {
        DefaultOAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
        requestFactory.setCheckUserScopes(true);
        return requestFactory;
    }

    @Bean
    @Autowired
    public TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter(AuthenticationManager authenticationManager, OAuth2RequestFactory requestFactory) {
        return new TokenEndpointAuthenticationFilter(authenticationManager, requestFactory);
    }
}
