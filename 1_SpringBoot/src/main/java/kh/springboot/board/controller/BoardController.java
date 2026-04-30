package kh.springboot.board.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import templates.views.common.Pagination;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService bService;

    @GetMapping("list")
    public ModelAndView selectList(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            ModelAndView mv, HttpServletRequest request) { 

        int listCount = bService.getListCount(1);
        PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
        ArrayList<Board> list = bService.selectBoardList(pi, 1);

        mv.addObject("list", list);
        mv.addObject("pi", pi);
        mv.setViewName("board/list");
        mv.addObject("loc", request.getRequestURI());	
        return mv;
    }
    
    @GetMapping("write")
    public String writeBoard() {
    	return "board/write";
    }
    
    @PostMapping("insert")
    public String insertBoard(@ModelAttribute Board b, HttpSession session) {
    	String boardWriter = ((Member)session.getAttribute("loginUser")).getId();
    	b.setBoardWriter(boardWriter);
    	b.setBoardType(1);
    	
    	int result = bService.insertBoard(b);
    	if(result > 0) {
    		return "redirect:/board/list";
    	}else {
    		throw new BoardException("게시글 작성을 실패했습니다");
    	}
    }

//	/* 게시판 상세보기 */
//    //board/?/?형태
//    @GetMapping("/{id}/{page}") //계속 바뀌는 값이기 때문에 구성만 해줌
//    public String selectBoard(@PathVariable("id")int bId, @PathVariable("page")int page, HttpSession session, Model model) {
//    	Member loginUser = (Member)session.getAttribute("loginUser");
//    	String id = null;
//    	if(loginUser != null) {
//    		id=loginUser.getId();
//    	}
//    	
//    	if(b != null) {
//    		model.addAttribute("b",b).addAttribute("page",page);
//    		return "detail"; //write.html을 복붙하여 detail.html로 변경
//    	}else {
//    		throw new BoardException("게시글 상세보기를 실패했습니다");
//    	}
//    	System.out.println(bId + ',' + page);
//    	return null;
//    }
    
    @GetMapping("/{id}/{page}")
    public String selectBoard(@PathVariable("id") int bId, @PathVariable("page") int page,
                              HttpSession session, Model model) {

        Member loginUser = (Member) session.getAttribute("loginUser");
        String id = null; 
        if (loginUser != null) {
            id = loginUser.getId();
        }

        Board b = bService.selectBoard(bId, id); 
        ArrayList<Reply> list = bService.selectReplyList(bId);
        if (b != null) {
            model.addAttribute("b", b).addAttribute("page", page).addAttribute("list",list);
            return "board/detail";
        } else {
            throw new BoardException("게시글 상세보기를 실패했습니다");
        }
    }
    
    @PostMapping("updForm")
    public String updatForm(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
    	Board b = bService.selectBoard(bId, null);
    	model.addAttribute("b",b).addAttribute("page",page);
    	return"views/board/edit";
    }
    @PostMapping("update")
    public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page) {
    	int result = bService.updateBoard(b);
    	
    	if(result>0) {
    		//return "redirect:/board/" + b.getBoardId() + "/"+ page;
    		return String.format("redirect:/board/%d/%d", b.getBoardId(), page);
    	}else {
    		throw new BoardException("게시글 수정을 실패했습니다");
    	}
    }
    @PostMapping("delete")
    public String deleteBoard(@RequestParam("boardId") int bId, HttpServletRequest request) {
    	int result = bService.deleteBoard(bId);
    	
    	if(result>0) {
    		return "redirect:/" +(request.getHeader("referer").contains("board") ? "board" : "attm") +"/list";
    	}else {
    		throw new BoardException("게시글 삭제를 실패했습니다");
    	}
    	
    }
    
    @GetMapping("top")
    @ResponseBody
    public ArrayList<Board> selecTop() {
    	ArrayList<Board> list = bService.selectTop();
    	return list;
    }
    
    @GetMapping("rinsert")
    @ResponseBody
    public ArrayList<Reply> insertReply(@ModelAttribute Reply r) {
    	int result = bService.insertReply(r);
    	ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
    	return list;
    }
    
    @GetMapping("rdelete")
    @ResponseBody
    public int deleteReply(@RequestParam("rId")int rId) {
    	return bService.deleteReply(rId);
    	
    }
    
    @GetMapping("rupdate")
    @ResponseBody
    public int rUPdate(@ModelAttribute Reply r) {
    	return bService.rupdate(r);
    }
    
    
}