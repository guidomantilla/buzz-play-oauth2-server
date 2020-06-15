package buzzplay.oauth2.server;

import buzzplay.oauth2.server.common.config.SubjectAttributeUserTokenConverter;
import buzzplay.oauth2.server.common.jdbc.CustomAppClientDetailsService;
import buzzplay.oauth2.server.common.jdbc.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;

@Configuration
public class WebSecurityBeanConfig {

    private final DataSource dataSource;
    private final String jwtKeyStore;
    private final String jwtKeyStorePassword;
    private final String jwtKeyAlias;

    @Autowired
    public WebSecurityBeanConfig(DataSource dataSource,
                                 @Value("${toolbox.oauth2.jwt.key-store}") String jwtKeyStore,
                                 @Value("${toolbox.oauth2.jwt.key-store-password}") String jwtKeyStorePassword,
                                 @Value("${toolbox.oauth2.jwt.key-alias}") String jwtKeyAlias) {

        this.dataSource = dataSource;
        this.jwtKeyStore = jwtKeyStore.replace("classpath:", "");
        this.jwtKeyStorePassword = jwtKeyStorePassword;
        this.jwtKeyAlias = jwtKeyAlias;
    }

    @Bean
    public KeyPair keyPair() {
        return new KeyStoreKeyFactory(new ClassPathResource(jwtKeyStore),
                jwtKeyStorePassword.toCharArray()).getKeyPair(jwtKeyAlias);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(dataSource);
    }

    @Bean("customAppClientDetailsService")
    @Autowired
    public ClientDetailsService clientDetailsService(PasswordEncoder passwordEncoder) {
        CustomAppClientDetailsService customAppClientDetailsService = new CustomAppClientDetailsService(dataSource);
        customAppClientDetailsService.setPasswordEncoder(passwordEncoder);
        return customAppClientDetailsService;
    }

    @Bean
    @Autowired
    public JwtAccessTokenConverter jwtAccessTokenConverter(KeyPair keyPair) {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair);

        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(new SubjectAttributeUserTokenConverter());
        converter.setAccessTokenConverter(accessTokenConverter);

        return converter;
    }

    @Bean
    @Autowired
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    @Autowired
    public OAuth2RequestFactory oAuth2RequestFactory(@Qualifier("customAppClientDetailsService") ClientDetailsService clientDetailsService) {
        DefaultOAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
        requestFactory.setCheckUserScopes(true);
        return requestFactory;
    }

    @Bean
    @Autowired
    public TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter(AuthenticationManager authenticationManager,
                                                                               OAuth2RequestFactory requestFactory) {
        return new TokenEndpointAuthenticationFilter(authenticationManager, requestFactory);
    }
}
