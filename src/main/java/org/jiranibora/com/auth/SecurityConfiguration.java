package org.jiranibora.com.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final AppUserService appUserService;
    private final  JwtFilter filter;
    @Autowired
    public SecurityConfiguration(AppUserService appUserService,
                                 PasswordEncoderConfig passwordEncoderConfig, JwtFilter filter){
        this.passwordEncoderConfig = passwordEncoderConfig;

         this.appUserService = appUserService;
        this.filter = filter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable().cors().disable()
                .authorizeRequests((authz)->authz

                                .antMatchers("/application/**").hasRole("ADMIN-CHR")

                                .antMatchers(HttpMethod.DELETE).hasAnyRole("ADMIN","ADMIN-CHR")
                                .antMatchers("/auth/login").permitAll()
                        )
                .authenticationProvider(authenticationProvider())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
     }
     @Bean
    public AuthenticationProvider authenticationProvider(){
         DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
         daoAuthenticationProvider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder());
         daoAuthenticationProvider.setUserDetailsService(appUserService);
         return daoAuthenticationProvider;
 }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
