package com.kronos.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select TEMPUSER, PASSWORD, STATUS from T_USER where TEMPUSER=?")
				.authoritiesByUsernameQuery("SELECT USER_ID, T_ROLE.NAME AS ROLE FROM  T_USER_ROLE, T_ROLE WHERE "
						+ "T_USER_ROLE.ROLE_ID=T_ROLE.ID AND T_USER_ROLE.USER_ID= ?");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// Los recursos estáticos no requieren autenticación
				.antMatchers("/push/**","/bootstrap/**", "/sw.js","/js/**", "/css/**","/push/register").permitAll()
				// Las vistas públicas no requieren autenticación
				.antMatchers("/push/**","/","/push/register","/api/accords/**","/accords/list","/accords/edit/**").permitAll()
				// Todas las demás URLs de la Aplicación requieren autenticación
				
				.antMatchers("/accords/addAccord/**","/push/register").hasAnyAuthority("Concejo Municipal")
				
				.anyRequest().authenticated()
				
				// El formulario de Login no requiere autenticacion
				.and().formLogin().permitAll()
				.and()
				.exceptionHandling().accessDeniedPage("/forbidden");
			
		
	}
	
	

}