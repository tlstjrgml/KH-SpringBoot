package kh.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {  // 이 클래스가 main 클래스임을 springboot에 알려주는 어노테이션 
		SecurityAutoConfiguration.class, //security 기본설정
		UserDetailsServiceAutoConfiguration.class //비밀번호 설정 등
}) 
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
