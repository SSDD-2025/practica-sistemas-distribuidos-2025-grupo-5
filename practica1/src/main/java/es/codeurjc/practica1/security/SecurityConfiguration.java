package es.codeurjc.practica1.security;

import org.springframework.beans.factory.annotation.Value;
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

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    //Datos de usuario en fichero
    @Value("${security.user}")
    private String username;
    @Value("${security.encodedPassword}")
    private String encodedPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        //UserDetails user = User.builder().username(username).password(passwordEncoder().encode(encodedPassword)).roles("USER").build(); //Sacardatos del usuario de fichero
        UserDetails user = User.builder()
        .username("user")
        .password(passwordEncoder().encode("pass"))
        .roles("USER")
        .build();
        UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("adminpass"))
        .roles("USER","ADMIN")
        .build();

        return new InMemoryUserDetailsManager(user,admin);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/").permitAll().anyRequest().authenticated()).formLogin(formLogin -> formLogin
        .loginPage("/login")
        .failureUrl("/loginerror")
        .defaultSuccessUrl("/private")
        .permitAll())
        .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/")
        .permitAll());
        // Disable CSRF at the moment
        http.csrf(csrf -> csrf.disable());
        //Igual da fallo
        http.authenticationProvider(authenticationProvider());
        http
        .authorizeHttpRequests(authorize -> authorize
        // PUBLIC PAGES
        .requestMatchers("/").permitAll()
        .requestMatchers("/css/**").permitAll()
        .requestMatchers("/images/public/**").permitAll()
        // PRIVATE PAGES
        .requestMatchers("/private").hasAnyRole("USER")
        .requestMatchers("/admin").hasAnyRole("ADMIN"));
        //.anyRequest().authenticated());
        //Final posible fallo
        return http.build();
    }
}
