package org.jiranibora.com.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = false, securedEnabled = false, jsr250Enabled = true
)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final AppUserService appUserService;
    private final  JwtFilter filter;
    @Autowired
    public SecurityConfiguration(AppUserService appUserService, PasswordEncoderConfig passwordEncoderConfig, JwtFilter filter){
         this.passwordEncoderConfig = passwordEncoderConfig;
         this.appUserService = appUserService;
         this.filter = filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().disable()
                .authorizeRequests()
                .antMatchers("/auth/login").permitAll()

                .antMatchers("/application/**").hasAnyAuthority("CHAIR")

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class);

    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
         auth.authenticationProvider(authenticationProvider());
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(appUserService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
    //    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(AbstractHttpConfigurer::disable)
//                .authorizeRequests((auth)-> {
//                                    auth
//                                        .mvcMatchers("/auth/login", "/register").permitAll()
//                                        .mvcMatchers("/application/**").hasAnyAuthority("ROLE_CHAIR")
//
//                                        .and();
//                        }
//                        )
//
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
//
//
//        return http.build();
//}
//     @Bean
//    public AuthenticationProvider authenticationProvider(){
//         DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//         daoAuthenticationProvider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder());
//         daoAuthenticationProvider.setUserDetailsService(appUserService);
//
//         return daoAuthenticationProvider;
// }
//    @Bean
//
//
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
}
