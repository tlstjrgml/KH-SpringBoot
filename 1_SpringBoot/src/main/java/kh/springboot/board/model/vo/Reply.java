package kh.springboot.board.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Reply {
	private int replyId;
	private String replyContent;
	private int refBoardId;
	private String replyWriter;
	private String nickName;
	private Date replyCreateDate;
	private Date replyModifyDate;
	private String replySatus;
}
