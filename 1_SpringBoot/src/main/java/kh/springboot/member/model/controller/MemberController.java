package kh.springboot.member.model.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@SessionAttributes("loginUser")
@RequestMapping("/member")
public class MemberController {

	private final MemberService mService;
	private final BCryptPasswordEncoder bcrypt;
	private final JavaMailSender mailSender;

	@GetMapping("/signIn")
	public String signIn() {
		System.out.println(bcrypt.encode("1234"));
		System.out.println(bcrypt.encode("pass01"));
		System.out.println(bcrypt.encode("pass02"));
		return "logIn";
	}

	@GetMapping("/enroll")
	public String enroll() {
		return "enroll";
	}

	@PostMapping("/enroll")
	public String enroll(@ModelAttribute Member m, @RequestParam("emailId") String emailId,
			@RequestParam("emailDomain") String emailDomain) {
		if (!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		m.setPwd(bcrypt.encode(m.getPwd()));
		int result = mService.insertMember(m);
		if (result > 0) {
			return "redirect:/home";
		} else {
			throw new MemberException("회원가입을 실패하였습니다");
		}
	}

	@GetMapping("/myInfo")
	public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		if (loginUser != null) {
			String id = loginUser.getId();
			ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
			
			ArrayList<TodoList> todoList = mService.selectTodoList(id);
			mv.addObject("todoList", todoList);
			
			mv.addObject("list", list);
			mv.setViewName("myInfo");
		}
		return mv;
	}

	@PostMapping("/signIn")
	public String login(Member m, Model model) {
		Member loginUser = mService.login(m);
		System.out.println(loginUser);
		if (loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
			model.addAttribute("loginUser", loginUser);
			return "redirect:/home";
		} else {
			throw new MemberException("로그인을 실패했습니다.");
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
			@RequestParam("emailId") String emailId,
			@RequestParam("emailDomain") String emailDomain) {
		if (!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		int result = mService.updateMember(m);
		if (result > 0) {
			model.addAttribute("loginUser", mService.login(m));
			return "redirect:/member/myInfo";
		} else {
			throw new MemberException("회원 정보 수정을 실패했습니다");
		}
	}

	@PostMapping("updatePassword")
	public String updatePassword(@RequestParam("currentPwd") String pwd,
			@RequestParam("newPwd") String newPwd, Model model) {
		Member m = (Member) model.getAttribute("loginUser");
		if (bcrypt.matches(pwd, m.getPwd())) {
			m.setPwd(bcrypt.encode(newPwd));
			int result = mService.updatePassword(m);
			if (result > 0) {
				model.addAttribute("loginUser", m);
				return "redirect:/home";
			} else {
				throw new MemberException("비밀번호 수정을 실패했습니다.");
			}
		}
		throw new MemberException("비밀번호 수정을 실패했습니다");
	}

	@GetMapping("delete")
	public String deleteMember(Model model) {
		int result = mService.deleteMember(((Member) model.getAttribute("loginUser")).getId());
		if (result > 0) {
			return "redirect:/member/logout";
		} else {
			throw new MemberException("회원탈퇴를 실패했습니다");
		}
	}

	@GetMapping("checkValue")
	@ResponseBody
	public int checkValue(@RequestParam("column") String col, @RequestParam("value") String val) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("col", col);
		map.put("val", val);
		int count = mService.checkValue(map);
		return count;
	}

	@GetMapping("echeck")
	@ResponseBody
	public String checkEmail(@RequestParam("email") String email) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();

		String subject = "[SpringBoot] 이메일 확인";
		String body = "<h1 align='center'>SpringBoot 이메일 확인</h1><br/>";
		body += "<div style='border: 3px solid skyblue; text-align:center; font-size: 15px;'>본 메일은 이메일을 확인하기 위하여 발송되었습니다.<br/>";
		body += "아래 숫자를 인증번호 확인란에 작성하여 확인해주시기 바랍니다.<br/><br/>";

		String random = "";
		for (int i = 0; i < 5; i++) {
			random += (int) (Math.random() * 10);
		}

		body += "<span style='font-size: 30px; text-decoration: underline;'><b>" + random + "</b></span><br/></div>";

		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
		mimeMessageHelper.setTo(email);
		mimeMessageHelper.setSubject(subject);
		mimeMessageHelper.setText(body, true);

		mailSender.send(mimeMessage);

		return random;
	}
	
	@PostMapping("profile")
	@ResponseBody
	public int updateProfile(@RequestParam(value = "profile", required=false) MultipartFile profile, Model model) {
		Member m = (Member)model.getAttribute("loginUser");
		
		String savePath = "c:\\profiles";
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		//기존 프로필에서 다른 프로필로 변경하는 경우에는 이전 프로필을 삭제
		if(m.getProfile() != null) {
			File f = new File(savePath + "\\" + m.getProfile());
			f.delete();
		}
		String renameFileName = null;
		if(profile != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			int ranNum = (int)(Math.random() * 100000);
			String originFileName = profile.getOriginalFilename();
			renameFileName = sdf.format(new Date()) + ranNum + originFileName.substring(originFileName.lastIndexOf("."));
			try {
				profile.transferTo(new File(folder + "\\" + renameFileName));
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		m.setProfile(renameFileName);
		int result = mService.updateProfile(m);
		if(result > 0) {
			model.addAttribute("loginUser", m);
		}
		return result;
	}
	
	@GetMapping("linsert")
	@ResponseBody
	public int linsert(@ModelAttribute TodoList todoList) {
		return mService.linsert(todoList);
	}
}