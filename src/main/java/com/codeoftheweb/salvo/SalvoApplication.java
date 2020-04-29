package com.codeoftheweb.salvo;

import jdk.internal.net.http.common.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
		System.out.println("Listo Cabron");
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	};


	//@Autowired
	//private PasswordEncoder passwordEncoder;
	@Bean
	public CommandLineRunner initData (PlayerRepository repository,
									   GameRepository gameRepository,
									   GamePlayerRepository gpReposityory,
									   ShipRepository shipRepository,
									   SalvoRepository salvoRepository,
									   ScoreRepository scoreRepository){
		return (args) -> {
		// Players

			Player p1 = new Player("Kazu", "Onishi", "kazu@gmail.com", passwordEncoder().encode("japboy321"));
			Player p2 =new Player("Alessio", "Pressano", "romanpizza@gmail.com",passwordEncoder().encode("italiano"));
			Player p3 =new Player("Clara", "Colace", "colace@gmail.com",passwordEncoder().encode("espanola"));
			Player p4 =new Player("Gonzalo", "Atleti", "vivaatleti@gmail.com",passwordEncoder().encode("viva123"));
			Player p5 =new Player("Leah", "Bugeja", "leah@gmail.com",passwordEncoder().encode("nihao999"));
			repository.save(p1);
			repository.save(p2);
			repository.save(p3);
			repository.save(p4);
			repository.save(p5);
		//Games
			Game g1 = new Game (DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
			Game g2 = new Game (DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
			Game g3 = new Game (DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));

		//GamePlayers
			GamePlayer gp1 = new GamePlayer(g1,p1);
			GamePlayer gp2 = new GamePlayer(g1,p2);
			GamePlayer gp3 = new GamePlayer(g2, p3);
			GamePlayer gp4 = new GamePlayer(g2, p1);
			GamePlayer gp5 = new GamePlayer(g3, p2);
			GamePlayer gp6 = new GamePlayer(g3,p5);

		// location
			List<String> locationList1 = Arrays.asList("H1","H2","H3");
			List<String> locationList2 = Arrays.asList("F1","F2","F3");
			List<String> locationList3 = Arrays.asList("A1","A2","A3");

		// Ships
			Ship ship1 = new Ship("Cruiser",gp1, locationList1);
			Ship ship2 = new Ship("Destroyer", gp1, locationList2);
			Ship ship3 = new Ship("Destroyer", gp1, locationList3);

		// Salvoes
			Salvo salvoes1 = new Salvo(gp1,1,locationList1);
			Salvo salvoes2 = new Salvo(gp2,1,locationList2);
			Salvo salvoes3 = new Salvo(gp1,2,locationList3);

		//Score
			Score score1 = new Score(g1,p1,1,DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
			Score score2 = new Score(g1,p2,.5,DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
			Score score3 = new Score(g2,p3,0.5,DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
			Score score4 = new Score(g2,p1,1,DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
			Score score5 = new Score(g1,p1,0.5,DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
			Score score6 = new Score(g3,p5,0,DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
			Score score7 = new Score(g3,p2,.5,DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));



		//	Repositories


			gameRepository.save(g1);
			gameRepository.save(g2);
			gameRepository.save(g3);

			gpReposityory.save(gp1);
			gpReposityory.save(gp2);
			gpReposityory.save(gp3);
			gpReposityory.save(gp4);
			gpReposityory.save(gp5);
			gpReposityory.save(gp6);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);


			salvoRepository.save(salvoes1);
			salvoRepository.save(salvoes2);
			salvoRepository.save(salvoes3);

			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);
			scoreRepository.save(score5);
			scoreRepository.save(score6);
			scoreRepository.save(score7);
		};
	}
}
@Configuration
@EnableWebSecurity
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName -> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user:" + inputName);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception{
				http
						.csrf()
						.disable()
						.authorizeRequests()
						.antMatchers("/api/score_board","/","/web/games.html","/web/games.css","/web/games.js","/api/games", "/h2-console/**","/api/**", "/api/players**", "/api/game_view/**", "/web/game.html**","/web/game.js","/web/game.css")
						.permitAll()
						.anyRequest()
						.hasRole("USER");


				http.formLogin()
						.usernameParameter("username")
						.passwordParameter("password")
						.loginPage("/api/login")
						.permitAll();

				http.logout().logoutUrl("/api/logout");

				http.headers().frameOptions().disable();

		//if user is not authenticated, just send an authentication failure response
		http.exceptionHandling()
				.authenticationEntryPoint(((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));

		// successful log in , remove flags auth requests.
		http.formLogin().successHandler((request, response, authentication) -> clearAuthenticationAttributes(request));

		// if login fails send a failed auth msg
		http.formLogin().failureHandler(((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));

		// on successful logout, succ msg
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}



