package kh.springboot.member.model.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor //생성자 주입 방법
@SessionAttributes("loginUser")
@RequestMapping("/member")
public class MemberController {
	
	//DI 필드 주입
	//@Autowired 
	
	private final MemberService mService;
	private final BCryptPasswordEncoder bcrypt;
	
	@GetMapping("/signIn")
	public String signIn() {
		System.out.println(bcrypt.encode("1234"));
		System.out.println(bcrypt.encode("pass01"));
		System.out.println(bcrypt.encode("pass02"));
		return "logIn";
	}
	/***** 파라미터 받아오기 
	 * @return *****/
	//1. HttpServletRequest 사용(servelt방식)
//	@PostMapping("member/signIn")
//	public void login(HttpServletRequest request) {
//		String id = request.getParameter("id");
//		String pwd = request.getParameter("pwd");
//		System.out.println(id);
//		System.out.println(pwd);
//		//System.out.println("123");
//	}
	
//	//2.@RequestParam 사용
//	@PostMapping("member/signIn")
//	public void login(@RequestParam(value = "id", defaultValue = "hello") String id,
//					  @RequestParam(value = "pwd", defaultValue = "world") String pwd,
//					  @RequestParam(value = "test", required=false) String test) {
//		System.out.println(id);
//		System.out.println(pwd);
//		System.out.println(test);
//		
//	}
	
	//3.@RequestParam 생략 - 권장 x
//	@PostMapping("/member/signIn")
//	public void login(String id, String pwd) {
//		System.out.println(id);
//		System.out.println(pwd);
//	}
	
//	//4.@ModelAttribute사용(기본생성자를 만들고 setter메소드를 보고->커맨드 방식) -> 커맨드방식을 이용한 @ModelAttribute
//	@PostMapping("member/signIn")
//	public void login(@ModelAttribute Member m) {
//		System.out.println(mService); //주소값
//		mService.login(m);
//	}

	//5.@ModelAttribute 생략
//	@PostMapping("member/signIn")
//	public String login(Member m, HttpSession session) {
////		System.out.println(m);
//		Member loginUser = mService.login(m);
//		System.out.println(loginUser);
//		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
//			session.setAttribute("loginUser", loginUser);
////			return "views/home"; //forward방식(url이 유지되었음)
//			return "redirect:/home";
//		}else {
//			throw new MemberException("로그인을 실패했습니다."); //사용자정의 예외 발생
//		}
//	}
	
	
//	@GetMapping("/member/logout")
//	public String logout(HttpSession session) {
//		session.invalidate();
//		return "redirect:/home";
//	}
	
	@GetMapping("/enroll")
	public String enroll() {
		
		return "enroll";
	}
	
//	@PostMapping("/member/enroll")
//	public void enroll(@RequestParam("id")String id,
//					   @RequestParam("pwd")String pwd,
//					   @RequestParam("name")String name,
//					   @RequestParam("nickName")String nickName) -> 너무 복잡
	
	@PostMapping("/enroll")
	public String enroll(@ModelAttribute Member m, @RequestParam("emailId")String emailId, @RequestParam("emailDomain")String emailDomain) {
		String email = null;
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}		
		
		//bcypt 암호화 - spring security에서 제공하는 암호화
		m.setPwd(bcrypt.encode(m.getPwd()));
		
		int result = mService.insertMember(m);
		if(result > 0) {
			return"redirect:/home";
		}else {
			throw new MemberException("회원가입을 실패하였습니다");
		}
		
	}
	
	/*** view에 전달하고자 하는 데이터가 있을 때에 대한 방법 ***/
	//1.model(데이터) 사용
	//  model은 map형식으로 데이터를 담음(key:value형식), requestScope로 전달하게됨
//	@GetMapping("/member/myInfo")
//	public String myInfo(HttpSession session, Model model) {
//		Member loginUser = (Member)session.getAttribute("loginUser");
//		if(loginUser != null) {
//			String id = loginUser.getId();
//			ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
//			model.addAttribute("list", list);
//		}
//		return"views/member/myInfo"; //forward방식
//	}
	
	//2.ModelAndView를 이용하여 전달하기
	@GetMapping("/myInfo")
	public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null) {
			String id = loginUser.getId();
			ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
			
			mv.addObject("list", list);
			mv.setViewName("myInfo");
		}
		return mv; //forward방식
	}	
	
	
	//3.@SessionAttributes
	//Model에 attribute가 추가될 때 자동으로 키 값을 찾아 세션에 등록하는 기능 제공
	@PostMapping("/signIn")
	public String login(Member m, Model model) {
//		System.out.println(m);
		Member loginUser = mService.login(m);
		System.out.println(loginUser);
		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
			model.addAttribute("loginUser", loginUser);
//			return "views/home"; //forward방식(url이 유지되었음)
			return "redirect:/home";
		}else {
			throw new MemberException("로그인을 실패했습니다."); //사용자정의 예외 발생
		}
	}
	
	@GetMapping("/logout")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "redirect:/home";
	}
	
	@GetMapping("/edit")
	public String edit() {
		return "/edit";
	}
	
	@PostMapping("/edit")
	public String edit(@ModelAttribute Member m, Model model,
					   @RequestParam("emailId")String emailId, 
					   @RequestParam("emailDomain")String emailDomain) {
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		
		int result = mService.updateMember(m);
		if(result >0) {
			model.addAttribute("loginUser", mService.login(m));
			return "redirect:/member/myInfo";
		}else {
			throw new MemberException("회원 정보 수정을 실패했습니다");
		}
	}
	@PostMapping("updatePassword")
	public String updatePassword(@RequestParam("currentPwd")String pwd, @RequestParam("newPwd")String newPwd,
			/*HttpSession session*/ Model model) {
		//pwd랑 newPwd가 일치하는지 검사하기, 로그인한 비밀번호는 session에 있음
		//session과 model을 모두 사용가능
		/* Member m = (Member)session.getAttribute("loginUser"); */
		Member m = (Member)model.getAttribute("loginUser");
		
		if(bcrypt.matches(pwd, m.getPwd())) {
			m.setPwd(bcrypt.encode(newPwd));
			int result = mService.updatePassword(m);
			if(result>0) {
				model.addAttribute("loginUser",m);
				return "redirect:/home";
			}else {
				throw new MemberException("비밀번호 수정을 실패했습니다.");
			}
		}throw new MemberException("비밀번호 수정을 실패했습니다");
	}
	@GetMapping("delete")
	public String deleteMember(Model model) {
		int result = mService.deleteMember(((Member)model.getAttribute("loginUser")).getId());
		if(result>0) {
			return"redirect:/member/logout";
		}else {
			throw new MemberException("회원탈퇴를 실패했습니다");
		}
	}
}
