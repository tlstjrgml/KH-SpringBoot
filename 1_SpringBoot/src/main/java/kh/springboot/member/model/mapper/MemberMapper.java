package kh.springboot.member.model.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import kh.springboot.member.model.vo.Member;
 
@Mapper // 해당 인터페이스를 myBatis Mapper로 등록 = xml메퍼파일 sql과 연결
public interface MemberMapper { //츄상메소드이기 때문에 내용을 작성할 수 없음
	
	Member login(Member m);

	int insertMember(Member m);

	ArrayList<HashMap<String, Object>> selectMyList(String id);

	int updateMember(Member m);

	int updatePassword(Member m);



	int deleteMember(String id);

//	int checkId(String id);
//
//	int checkNickName(String nickName);

	int checkValue(HashMap<String, String> map);




		
	 
}
