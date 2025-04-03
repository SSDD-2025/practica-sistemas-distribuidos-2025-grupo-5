package es.codeurjc.practica1.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
