package kh.springboot.board.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.Reply;

@Mapper
public interface BoardMapper {

	int getListCount(int i);
	
	ArrayList<Board> selectBoardList(int i, RowBounds rowBounds);



	int insertBoard(Board b);

	Board selectBoard(int bId);

	int updateCount(int bId);


	int updateBoard(Board b);

	int deleteBoard(int bId);

	ArrayList<Attachment> selectAttmBoardList(Integer bId);

	int insertAttm(ArrayList<Attachment> list);

	int deleteBoard();

	ArrayList<Board> selectTop();

	ArrayList<Reply> selectReplyList(int bId);

	int insertReply(Reply r);

	int rdelete(Reply r);

	int deleteReply(int rId);

	int rupdate(Reply r);

//	int statusNAttm(int bId);	
	
	
}
	