package com.example.demo.config;

import com.example.demo.security.CustomUserDetailService;
import com.example.demo.security.handler.Custom403Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Deprecated(since = "6.1", forRemoval = true)
public class CustomSecurityConfig {

    //주입 필요
    private final DataSource dataSource;
    private final CustomUserDetailService userDetailService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        log.info("----------- Configure ------------");

        //커스텀 로그인 페이지
        http.formLogin((formLogin)-> formLogin.loginPage("/member/login"));
        //CSRF 토큰 비활성화
        http.csrf(csrf->csrf.disable());

        http.rememberMe()
                .key("12345678")
                .tokenRepository(persistenceTokenRepository())
                .userDetailsService(userDetailService)
                .tokenValiditySeconds(60*60*24*40);

        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());//403
        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new Custom403Handler();
    }

    @Bean
    public PersistentTokenRepository persistenceTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        log.info("------------- web configure --------------");

        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());

    }



}