package University.Dormitory.security;

import University.Dormitory.repository.JPARepository.UserRepository;
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
    private final UserRepository userRepository;

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
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/signin/**").permitAll()
                                .requestMatchers("/refresh").hasAnyRole("ASSISTANT", "SCHEDULE_ASSISTANT", "PERFECT")
                                .requestMatchers("/assistant/**").hasAnyRole("ASSISTANT", "SCHEDULE_ASSISTANT", "PERFECT")
                                .requestMatchers("/schedule/**").hasAnyRole("SCHEDULE_ASSISTANT", "PERFECT")
                                .requestMatchers("/perfect/**").permitAll()//hasRole("PERFECT") -> Swagger 회원가입을 위해 잠깐 모두 승인 처리
                                .anyRequest().authenticated()
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
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userRepository), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}