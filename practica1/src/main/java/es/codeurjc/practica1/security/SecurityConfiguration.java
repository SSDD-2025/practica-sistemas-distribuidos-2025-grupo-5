package es.codeurjc.practica1.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import es.codeurjc.practica1.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
        .username("user")
        .password(passwordEncoder().encode("pass"))
        .roles("USER")
        .build();
        UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("adminpass"))
        .roles("ADMIN")
        .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(authorize -> authorize
        // PUBLIC PAGES
        .requestMatchers("/").permitAll()
        .requestMatchers("/products/**").permitAll()
        .requestMatchers("/products/product/").permitAll()
        .requestMatchers("/css/**").permitAll()
        .requestMatchers("/images/public/**").permitAll()
        .requestMatchers("/error").permitAll()

        // PRIVATE PAGES
        .requestMatchers("/private").hasAnyRole("USER")
        .requestMatchers("/checkoutOne/**").hasAnyRole("USER")
        .requestMatchers("/checkout/**").hasAnyRole("USER")
        .requestMatchers("/edit/**").hasAnyRole("USER")
        .requestMatchers("/add-to-cart/**").hasAnyRole("USER")
        .requestMatchers("/cart/**").hasAnyRole("USER")
        .requestMatchers("/newproduct/**").hasAnyRole("USER")
        .requestMatchers("/remove-from-products/**").hasAnyRole("USER")
        .requestMatchers("/remove-from-cart/**").hasAnyRole("USER")
        .requestMatchers("/showOrders/**").hasAnyRole("USER")
        .requestMatchers("/removeOrder/**").hasAnyRole("USER")

        .requestMatchers("/products/1/gateway").hasAnyRole("USER")
        .requestMatchers("/gateway").hasAnyRole("USER")
        .requestMatchers("/admin").hasAnyRole("ADMIN"))
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
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }
}
