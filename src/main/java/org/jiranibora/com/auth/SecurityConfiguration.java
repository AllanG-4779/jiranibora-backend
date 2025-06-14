package org.jiranibora.com.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

    private final PasswordEncoderConfig passwordEncoderConfig;
    private final AppUserService appUserService;
    private final JwtFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/auth/login", "/application/apply").permitAll()
                        .requestMatchers("/application/**", "/update/role", "/update/status/**", "/members/**").permitAll()
                        .requestMatchers("/member/earning").hasAuthority("USER")
                        .requestMatchers("/admin/treasurer/**").hasAuthority("TRE")
                        .requestMatchers("/fine/apply", "/sms/**", "/fine/get/**", "/meeting/**", "/member/to/**", "/fine-category/add", "/fine-category/get").hasAuthority("SEC")
                        .requestMatchers("/loan/**").hasAnyAuthority("USER", "TRE")
                        .requestMatchers("/pay/**").hasAnyAuthority("TRE", "USER", "SEC")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(appUserService);
        provider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager()  {
        ProviderManager providerManager = new ProviderManager(authenticationProvider());
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }
}
