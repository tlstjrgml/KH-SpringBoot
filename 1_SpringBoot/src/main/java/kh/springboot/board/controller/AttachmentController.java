package kh.springboot.board.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import templates.views.common.Pagination;

@Controller
@RequestMapping("/attm")
@RequiredArgsConstructor
public class AttachmentController {
	private final BoardService bService;
	
	
	
	@GetMapping("list")
	public String selectList(@RequestParam(value="page", defaultValue="1") int currentPage,
			Model model, HttpServletRequest request) {
			
		int listCount = bService.getListCount(2);
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 9);
		ArrayList<Board> bList = bService.selectBoardList(pi, 2);
		ArrayList<Attachment> aList = bService.selectAttmBoardList(null);

		if(bList != null) {
			model.addAttribute("loc", request.getRequestURI());
			model.addAttribute("bList", bList).addAttribute("pi", pi).addAttribute("aList", aList); /* bList - board pi-paging aList-thumnail*/
			return "views/attm/list";
		}else {
			throw new BoardException("첨부파일 게시글 조회를 실패했습니다");
		}
	}
	
	@GetMapping("write")
	public String writeAttm() {
		return "views/attm/write";
	}
	
	@PostMapping("insert")
	@Transactional //트랜잭션 담당 처리
	public String insertAttmBoard(@ModelAttribute Board b,@RequestParam("file")ArrayList<MultipartFile> files, HttpSession session ) {
		//System.out.println(b);
		//System.out.println(files);
		b.setBoardWriter(((Member)session.getAttribute("loginUser")).getId());
		
		ArrayList<Attachment> list = new ArrayList<Attachment>(); //Attachment VO는 필요한 정보만 담겨있음
		for(int i = 0; i<files.size(); i++) {
			MultipartFile upload = files.get(i);
//			if(upload != null && !upload.isEmpty()) {
			if(!upload.getOriginalFilename().equals("")) {
				//System.out.println("files[" + i + "]: " + upload.getOriginalFilename()); //파일이 비어있으면 안들어오게
				String[] returnArr = saveFile(upload); //upload=multipartfile
				if(returnArr[1] != null) {
					Attachment a = new Attachment(); //
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					
					list.add(a);
				}
			}
		}
		//레벨설정 - list에는 파일에 대한 정보만 들어있으니까 files를 볼 필요가 없음
		//list의 첫번째를 썸네일로
		for(int i = 0; i < list.size(); i++) {
			Attachment a = list.get(i);
			if(i == 0) { //0번째에 있으면 썸네일로 하고
				a.setAttmLevel(0);
			}else { //아니면 다 level을 1로 지정함
				a.setAttmLevel(1);
			}
		}
		
		//insert
		int result1 = 0; //결과값 담는 변수
		int result2 = 0; //동일
		if(list.isEmpty()) {
			b.setBoardType(1);
			result1 = bService.insertBoard(b);
		}else {
			b.setBoardType(2);
			result1 = bService.insertBoard(b);
//			System.out.println(b);
			for(Attachment a:list) {
				a.setRefBoardId(b.getBoardId());
			}
			result2 = bService.insertAttm(list);
		}
		
		if(result1 + result2 == list.size() + 1) { //list안에 들어있는 Attachment와 board가 다 잘 들어갔을 떄
			if(result2 == 0) { //첨부파일 없음
				return "redirect:/board/list";
			}else {
				return "redirect:/attm/list";
			}
		}else { //DB에 삽입실패 = 파일저장소에는 저장이 되었지만 DB에는 안들어간 상태
			for(Attachment a: list) {
				deleteFile(a.getRenameName());
			}
			throw new BoardException("첨부파일 게시글 등록을 실패했습니다");
			
		}
		
	}
	//사용자 지정 메소드
	public String[] saveFile(MultipartFile upload) {
		String savePath = "c:\\uploadFiles";
		
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdir();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		int ranNum = (int)(Math.random()*100000);
		String originFileName = upload.getOriginalFilename(); //rename.확장자를 위하여 사용함
		String renameFileName = sdf.format(new Date()) + ranNum + originFileName.substring(originFileName.lastIndexOf("."));
		
		String renamePath = folder + "\\" + renameFileName;
		
		try {
			upload.transferTo(new File(renamePath));
		} catch (Exception e) {
			System.out.println("파일 전송 에러: " + e.getMessage());
		} 
		
		String[] returnArr = new String[2];
		returnArr[0] = savePath;
		returnArr[1] = renameFileName;
		
		return returnArr;
		
	}
	//사용자 지정 메소드
	public void deleteFile(String renameName) {
		String savePath = "c:\\uploadFiles";
		File f = new File(savePath + "\\" + renameName);
		if(f.exists()) f.delete(); //if나 for문 안에 한줄만 있으면 중괄호 뺄 수 있음
		
	}
	
	@GetMapping("/{id}/{page}")
	public String selectAttm(@PathVariable("id") int bId, @PathVariable("page") int page, HttpSession session, Model model) {
		//조회수 올리기도 같이해야함
		Member loginUser = (Member)session.getAttribute("loginUser");
		String id = null;
		if(loginUser != null) {
			id = loginUser.getId();
		}
		Board b = bService.selectBoard(bId, id); //bId는 데이터베이스에서, id는 
		ArrayList<Attachment> list = bService.selectAttmBoardList((Integer)bId);
		//쿼리문: select * from attachment where attm_status = 'Y' and ref_board_id = #{bId}
		
		if(b != null) {
			model.addAttribute("b",b).addAttribute("page", page).addAttribute("list", list);
			return "views/attm/detail";
		}else {
			throw new BoardException("첨부파일 게시글 상세보기를 실패했습니다");
		}
	}
		
		
		
}

