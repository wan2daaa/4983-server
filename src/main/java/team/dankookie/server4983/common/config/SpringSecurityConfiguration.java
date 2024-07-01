package team.dankookie.server4983.common.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.dankookie.server4983.common.filter.JwtAuthenticationFilter;
import team.dankookie.server4983.member.service.MemberDetailsService;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MemberDetailsService memberDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((req) -> req
                        .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(antMatcher("/docs/**")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/token/valid")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/token/update")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/login")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/members/password/certification-number")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/members/password")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/register/duplicate/studentId")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/register/duplicate/nickname")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/my-pages/certification-number")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/register")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/admin/login")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/admin/**")).hasAuthority("ADMIN")
                        .requestMatchers(antMatcher("/api/v1/**")).hasAuthority("USER")
                        .anyRequest().denyAll()
                )
                .headers((headers) -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(memberDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }


}
