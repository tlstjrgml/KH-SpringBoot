package kh.springboot.board.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	private final BoardMapper mapper;

	public int getListCount(int i) {
		return mapper.getListCount(i);
	}

	public ArrayList<Board> selectBoardList(PageInfo pi, int i) {
		int offset = (pi.getCurrentPage()-1)* pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset,pi.getBoardLimit());
		return mapper.selectBoardList(i, rowBounds);
	}

	public int insertBoard(Board b) {
		// TODO Auto-generated method stub
		return mapper.insertBoard(b);
	}

//	public Board selectBoard(int bId, int page) {
//		// TODO Auto-generated method stub
//		Board b = mapper.selectBoard(bId);
//		if(b != null) {
//			if(id != null && !b.getBoardWriter().equals(id)) {
//				int result = mapper.updateCount(bId);
//				if(result>0) {
//					b.setBoardCount(b.getBoardCount()+1);
//				}
//			}
//		}
//	}

	public Board selectBoard(int bId , String id) { 
	    Board b = mapper.selectBoard(bId);
	    if (b != null) {
	        if (id != null && !b.getBoardWriter().equals(id)) {
	            int result = mapper.updateCount(bId);
	            if (result > 0) {
	                b.setBoardCount(b.getBoardCount() + 1);
	            }
	        }
	    }
	    return b; 
	}

	

	public int updateBoard(Board b) {
		return mapper.updateBoard(b);
	}

	public int deleteBoard(int bId) {
		// TODO Auto-generated method stub
		return mapper.deleteBoard(bId);
	}


	public ArrayList<Attachment> selectAttmBoardList(Integer bId) {
		return mapper.selectAttmBoardList(bId);
	}

	public int insertAttm(ArrayList<Attachment> list) {
		return mapper.insertAttm(list);
	}


	public ArrayList<Board> selectTop() {
		return mapper.selectTop();
	}

	public ArrayList<Reply> selectReplyList(int bId) {
		// TODO Auto-generated method stub
		return mapper.selectReplyList(bId);
	}

	public int insertReply(Reply r) {
		// TODO Auto-generated method stub
		return mapper.insertReply(r);
	}
	


	public int deleteReply(int rId) {
		// TODO Auto-generated method stub
		return mapper.deleteReply(rId);
	}

	public int rupdate(Reply r) {
		// TODO Auto-generated method stub
		return mapper.rupdate(r);
	}



//	public int statusNAttm(int bId) {
//		// TODO Auto-generated method stub
//		return mapper.statusNAttm(bId);
//	}




	
}


