package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //설정과 관련되어있다는 것을 알려주는 어노테이션 + Bean(객체)를 생성해주는 어노테이션
@EnableWebSecurity //웹의 시큐리티를 활성화시키는 어노테이션(웹 보안 설정) - 없어도됨


public class SecurityConfig {
	@Bean //리턴값을 Bean에 등록하기만 하는 역할(객체 생성만 함)
	
	public SecurityFilterChain filterChain(HttpSecurity http) {
		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) //익명함수 표현식(람다)
			.csrf(csrf -> csrf.disable());
		return http.build();
	}
	
	//암호화
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
