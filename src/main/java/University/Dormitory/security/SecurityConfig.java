package University.Dormitory.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/swagger-resources/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/webjars/**").permitAll()
                                .requestMatchers("/login/**").permitAll()
                                .requestMatchers("/ASSISTANT/**").hasAnyRole("ASSISTANT", "SCHEDULE_ASSISTANT", "PERFECT")
                                .requestMatchers("/SCHEDULE_ASSISTANT/**").hasAnyRole("SCHEDULE_ASSISTANT", "PERFECT")
                                .requestMatchers("/PERFECT/**").hasRole("PERFECT")
                                .anyRequest().permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/") // 로그아웃 성공 후 리다이렉트할 URL
                                .permitAll()
                )
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied") // 권한 없을 시 접근할 페이지
                )
                .csrf().disable()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}