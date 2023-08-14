package com.happidreampets.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    // @Bean
    // public AuthTokenFilter authenticationJwtTokenFilter() {
    // return new AuthTokenFilter();
    // }

    // @Override
    // public void configure(AuthenticationManagerBuilder
    // authenticationManagerBuilder) throws Exception {
    // authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    // }

    // @Bean
    // public DaoAuthenticationProvider authenticationProvider() {
    // DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

    // authProvider.setUserDetailsService(userDetailsService);
    // authProvider.setPasswordEncoder(passwordEncoder());

    // return authProvider;
    // }

    // @Bean
    // @Override
    // public AuthenticationManager authenticationManagerBean() throws Exception {
    // return super.authenticationManagerBean();
    // }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Override
    // protected void configure(HttpSecurity http) throws Exception {
    // http.cors().and().csrf().disable()
    // .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
    // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    // .authorizeRequests().antMatchers("/api/auth/**").permitAll()
    // .antMatchers("/api/test/**").permitAll()
    // .anyRequest().authenticated();
    //
    // http.addFilterBefore(authenticationJwtTokenFilter(),
    // UsernamePasswordAuthenticationFilter.class);
    // }

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // List<AccessEnum> accessList = new ArrayList<>();
    // accessList.add(AccessEnum.USER);
    // accessList.add(AccessEnum.ADMIN);
    // final String[] authenticationUrls;
    // List<String> authUrlsList = URLData.getURLsWithAccessLevel(accessList);
    // if (authUrlsList.size() == 0) {
    // authenticationUrls = new String[1];
    // authenticationUrls[0] = "";
    // } else {
    // authenticationUrls = authUrlsList.toArray(new String[0]);
    // }
    // final String[] nonAuthenticationUrls;
    // List<String> nonAuthUrlsList = URLData.getUrlsWithoutAccessLevelAnnotation();
    // if (nonAuthUrlsList.size() == 0) {
    // nonAuthenticationUrls = new String[1];
    // nonAuthenticationUrls[0] = "";
    // } else {
    // nonAuthenticationUrls = nonAuthUrlsList.toArray(new String[0]);
    // }
    // http.csrf(csrf -> csrf.disable())
    // .exceptionHandling(exception ->
    // exception.authenticationEntryPoint(unauthorizedHandler))
    // .sessionManagement(session ->
    // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers(nonAuthenticationUrls).permitAll()
    // .requestMatchers(authenticationUrls).authenticated());

    // http.authenticationProvider(authenticationProvider());

    // http.addFilterBefore(authenticationJwtTokenFilter(),
    // UsernamePasswordAuthenticationFilter.class);

    // return http.build();
    // }
}
