package toolbox.spring.oauth2.common;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class WebApiSecurityConfigAdapter extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private AuthenticationProvider authenticationProvider;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    public WebApiSecurityConfigAdapter() {
    }

    public WebApiSecurityConfigAdapter(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public WebApiSecurityConfigAdapter(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public WebApiSecurityConfigAdapter(AuthenticationProvider authenticationProvider, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.authenticationProvider = authenticationProvider;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    //----------------------------
    // Init - WebMvcConfigurer
    //----------------------------

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**", "/css/**", "/js/**")
                .addResourceLocations("classpath:/static/img/", "classpath:/static/css/", "classpath:/static/js/");
    }

    //----------------------------
    // End - WebMvcConfigurer
    //----------------------------


    //----------------------------
    // Init - WebSecurityConfigurerAdapter
    //----------------------------

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        if (!Objects.isNull(authenticationProvider))
            auth.authenticationProvider(authenticationProvider);
        else
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring()
                .antMatchers("/**/js/**", "/**/css/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        turnOnSecurity(http, "/", "/error", "/oauth/authorize");
        sessionBehavior(http);
        errorHandling(http);
    }

    //----------------------------
    // End - WebSecurityConfigurerAdapter
    //----------------------------

    public void turnOnSecurity(HttpSecurity http, String... allowAntPatterns) throws Exception {

        http.httpBasic();
        http.csrf().disable();
        http.cors();

        http.authorizeRequests().antMatchers(allowAntPatterns).permitAll();
        http.authorizeRequests().anyRequest().authenticated();
    }

    public void turnOffSecurity(HttpSecurity http, String... allowAntPatterns) throws Exception {

        http.httpBasic().disable();
        http.csrf();
        http.cors().disable();

        http.authorizeRequests().antMatchers(allowAntPatterns).permitAll();
        http.authorizeRequests().anyRequest().permitAll();
    }

    public void sessionBehavior(HttpSecurity http) throws Exception {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    public void errorHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler(
                        (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
    }
}
