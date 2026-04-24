package kh.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller //해당 클래스에 대해서 컨트롤러 역할을 하는 Bean(객체) 생성
public class HomeController {
	@GetMapping("/home") //HandlerMapping의 한 종류(get방식으로 들어왔을 때 GepMapping 사용함) + 어떤 주소로 갈지 모르니까 적어주기("/home")
	public String homeMain() { //원래는 void였지만 view에 연결하기 위해서 String으로 바꿈
		//ModelAndView : 데이터와 화면을 담는 객체 
		//Model: 데이터를 담는 객체 ex) request.setAttribute()
		//springBoot 에서 View만 연결할 때는  String 반환을 이용  
		
		//springboot의 viewResolver 기본설정
		//prefix: classpath:templates/ (resources)
		//suffix: .html
		return "views/home"; //home으로 url을 보내는 방법
	}
}
