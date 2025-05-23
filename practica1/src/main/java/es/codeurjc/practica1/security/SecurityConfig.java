package es.codeurjc.practica1.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.codeurjc.practica1.security.jwt.JwtRequestFilter;
import es.codeurjc.practica1.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PRIVATE ENDPOINTS
                        .requestMatchers("/style.css/**").permitAll()
                        // PRODUCT
                        .requestMatchers(HttpMethod.POST, "/api/products/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // USER
                        // ---user---
                        .requestMatchers(HttpMethod.GET, "/api/users/me").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/me").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/me").hasRole("USER")

                        // ---admin---
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")

                        // REVIEW
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasAnyRole("USER", "ADMIN")

                        // ORDER
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").hasRole("USER")

                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/api/cart/**").hasRole("USER")
                        .requestMatchers("/api/checkout/**").hasRole("USER")
                        .requestMatchers("/api/gateway").hasRole("USER")

                        // PUBLIC ENDPOINTS
                        .anyRequest().permitAll());

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(authorize -> authorize

                // PUBLIC PAGES
                .requestMatchers("/").permitAll()
                .requestMatchers("/products/**").permitAll()
                .requestMatchers("/products/product/").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/images/public/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/style.css/**").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/productReviews/**").permitAll()
                .requestMatchers("/reviews/**").permitAll()
                .requestMatchers("/newReview/**").permitAll()
                .requestMatchers("/newUser/**").permitAll()
                .requestMatchers("/saveNewUser/**").permitAll()
                .requestMatchers("/updateUser/**").permitAll()
                .requestMatchers("/removeUser/**").permitAll()
                .requestMatchers("/removeUserByUser/**").permitAll()
                .requestMatchers("/removeReview/**").permitAll()
                .requestMatchers("/search").permitAll()
                .requestMatchers("/js/**").permitAll()


                // PRIVATE PAGES
                .requestMatchers("/private").hasAnyRole("USER")
                .requestMatchers("/editUserGet").hasAnyRole("USER", "ADMIN")

                .requestMatchers("/checkoutOne/**").hasAnyRole("USER")
                .requestMatchers("/checkout/**").hasAnyRole("USER")
                .requestMatchers("/edit/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/add-to-cart/**").hasAnyRole("USER")
                .requestMatchers("/showCart/**").hasAnyRole("USER")
                .requestMatchers("/newproduct/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/remove-from-products/**").hasAnyRole("ADMIN")
                .requestMatchers("/remove-from-cart/**").hasAnyRole("USER")
                .requestMatchers("/showOrders/**").hasAnyRole("USER")
                .requestMatchers("/removeOrder/**").hasAnyRole("USER")
                .requestMatchers("/update/**").hasAnyRole("ADMIN")

                .requestMatchers("/gateway").hasAnyRole("USER")
                .requestMatchers("/admin").hasAnyRole("ADMIN")
                
                // OpenAPI
				.requestMatchers("/v3/api-docs*/**").permitAll()
				.requestMatchers("/swagger-ui.html").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll()
            )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/loginerror")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());
        // Disable CSRF at the moment
        // http.csrf(csrf -> csrf.disable());
        return http.build();
    }
}
