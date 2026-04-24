package kh.springboot.common.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import kh.springboot.board.model.exception.BoardException;
import kh.springboot.member.model.exception.MemberException;
@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler({MemberException.class, BoardException.class})
	public String handlerException(RuntimeException e, Model model) {
		model.addAttribute("message", e.getMessage());
		return "error/500";
	}

}
