package com.notice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class NoticeDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertNotice(NoticeDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;
		
		try {
			sql = "SELECT notice_seq.NEXTVAL FROM dual";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			seq = 0;
			if(rs.next()) {
				seq = rs.getInt(1);
			}
			dto.setNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			sql = " INSERT INTO notice (num, notice, userId, subject, content, hitCount, reg_date) "
					+ "  VALUES(?, ?, ?, ?, ?, 0, SYSDATE)"; 
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNum());
			pstmt.setInt(2, dto.getNotice());
			pstmt.setString(3, dto.getUserId());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getContent());
			
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			
		
			if(dto.getSaveFiles() != null) {
				sql = "INSERT INTO noticeFile(numFile, num, saveFilename, originalFilename) "
						+ "VALUES(noticeFile_seq.NEXTVAL, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
				
				for(int i = 0; i<dto.getSaveFiles().length; i++) {
					pstmt.setInt(1, dto.getNum());
					pstmt.setString(2, dto.getSaveFiles()[i]);
					pstmt.setString(3, dto.getOriginalFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	// 전체 데이터 개수
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try { 
			sql = "SELECT COUNT(*) FROM notice";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
		
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
		}
		return result;
	}
	

	// 검색 데이터 개수
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM notice n";
			sql += "  JOIN member1 m ON n.userId = m.userId  " ; 
			if(condition.equals("all")) {
				sql += "  WHERE INSTR(subject, ? ) >= 1 OR INSTR(content, ? ) >= 1";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  WHERE TO_CHAR(reg_date, 'YYYYMMDD' )= ? "; 
			} else {
				sql += "  WHERE INSTR(" + condition + ", ?) >= 1";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if(condition.equals("all")) {
				pstmt.setString(2, keyword);
			}
			
			rs = pstmt.executeQuery();	
			
			if(rs.next()) {
				result = rs.getInt(1); 
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch(Exception e2) {	
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				}catch(Exception e2) {	
				}
			}
		}
		return result;
		
	}
	
	// 전체 리스트
	public List<NoticeDTO> listNotice(int start, int end){
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append( "SELECT * FROM ( "); 
			sb.append( "  SELECT ROWNUM rnum, tb.* FROM (");
			sb.append( "		SELECT num, notice, userName, subject, hitCount,");
			sb.append( "        TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date  ");
			sb.append( "		FROM notice n");
			sb.append( "		JOIN member1 m ON n.userId = m.userId  ");
			sb.append( "		ORDER BY num DESC ");
			sb.append( "		) tb WHERE ROWNUM <= ? ");
			sb.append( "	) WHERE rnum >= ? ");
					
			pstmt = conn.prepareStatement(sb.toString());
		
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				NoticeDTO dto = new NoticeDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setNotice(rs.getInt("notice"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch(Exception e2) {	
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				}catch(Exception e2) {	
				}
			}
		}
		
		
		return list;
	}
	
	
	public List<NoticeDTO> listNotice(int start, int end, String condition, String keyword){
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
		
		sb.append( "SELECT * FROM ( ");
		sb.append( "  SELECT ROWNUM rnum, tb.* FROM (");
		sb.append( "		SELECT num, notice, userName, subject, hitCount, reg_date");
		sb.append( "		FROM notice n");		
		sb.append( "		JOIN member1 m ON n.userId = m.userId  "); 
		if(condition.equals("all")) {
			sb.append("  WHERE INSTR(subject, ? ) >= 1 OR INSTR(content, ? ) >= 1");
		} else if (condition.equals("reg_date")) {
			sb.append("  WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
		} else {
			sb.append("  WHERE INSTR (" + condition + ", ?) >= 1 ");
		}
		sb.append( "		ORDER BY num DESC ");
		sb.append( "		) tb WHERE ROWNUM <= ? ");
		sb.append( "	) WHERE rnum >= ? ");
				
		pstmt = conn.prepareStatement(sb.toString());
	
		if(condition.equals("all")) {
			pstmt.setString(1, keyword);
			pstmt.setString(2, keyword);
			pstmt.setInt(3, end);
			pstmt.setInt(4, start);
		} else {
			pstmt.setString(1, keyword);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);
		}
	
		rs = pstmt.executeQuery();
		while(rs.next()) {
			NoticeDTO dto = new NoticeDTO();
			
			dto.setNum(rs.getInt("num"));
			dto.setNotice(rs.getInt("notice"));
			dto.setSubject(rs.getString("subject"));
			dto.setUserName(rs.getString("userName"));
			dto.setHitCount(rs.getInt("hitCount"));
			dto.setReg_date(rs.getString("reg_date"));	
			
			list.add(dto);
		}
			
	} catch (Exception e){
		e.printStackTrace();
	} finally {
		if(rs != null) {
			try {
				rs.close();
			}catch(Exception e2) {	
			}
		}
		if(pstmt != null) {
			try {
				pstmt.close();
			}catch(Exception e2) {	
			}
		}
	}
		return list;
	}
	
	public List<NoticeDTO> listNotice(){
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT num, notice, userName, subject, hitCount, "
					+ " TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date "
					+ "FROM notice n "
					+ "JOIN member1 m ON n.userId = m.userId "
					+ "WHERE notice =1 "
					+ "ORDER BY num DESC ";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				NoticeDTO dto = new NoticeDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setNotice(rs.getInt("notice"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
				
			}
	
	} catch (Exception e){
		e.printStackTrace();
	} finally {
		if(rs != null) {
			try {
				rs.close();
			}catch(Exception e2) {	
			}
		}
		if(pstmt != null) {
			try {
				pstmt.close();
			}catch(Exception e2) {	
			}
		}
	}
		return list;
	}
	

	
	public NoticeDTO readNotice(int num) {
		NoticeDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		
		try {
			sql = " SELECT num, n.userId, userName, notice, subject, content, "
					+ "  reg_date, hitCount "
					+ " FROM notice n "
					+ " JOIN member1 m ON n.userId = m.userId "
					+ " WHERE num = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				dto = new NoticeDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setNotice(rs.getInt("notice"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHitCount(rs.getInt("hitCount"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch(Exception e2) {	
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				}catch(Exception e2) {	
				}
			}
			
		}
	
		return dto;
	}
	

	public List<NoticeDTO> listNoticeFile(int num) {
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT numFile, saveFilename, originalFilename FROM noticeFile "
					+ " WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				NoticeDTO dto = new NoticeDTO();
				dto.setNumFile(rs.getInt("numFile"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch(Exception e2) {	
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				}catch(Exception e2) {	
				}
			}
		}
	
		return list;
	}
	
	// 이전글
		public NoticeDTO preReadNotice(int num, String condition, String keyword) {
			NoticeDTO dto = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			StringBuilder sb = new StringBuilder();

			try {
				if (keyword != null && keyword.length() != 0) {
					sb.append(" SELECT * FROM ( ");
					sb.append("    SELECT num, subject ");
					sb.append("    FROM notice n ");
					sb.append("    JOIN member1 m ON n.userId = m.userId ");
					sb.append("    WHERE ( num > ? ) ");
					if (condition.equals("all")) {
						sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
					} else if (condition.equals("reg_date")) {
						keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
						sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
					} else {
						sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
					}
					sb.append("     ORDER BY num ASC ");
					sb.append(" ) WHERE ROWNUM = 1 ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setInt(1, num);
					pstmt.setString(2, keyword);
					if (condition.equals("all")) {
						pstmt.setString(3, keyword);
					}
				} else {
					sb.append(" SELECT * FROM ( ");
					sb.append("     SELECT num, subject FROM notice ");
					sb.append("     WHERE num > ? ");
					sb.append("     ORDER BY num ASC ");
					sb.append(" ) WHERE ROWNUM = 1 ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setInt(1, num);
				}

				rs = pstmt.executeQuery();

				if (rs.next()) {
					dto = new NoticeDTO();
					dto.setNum(rs.getInt("num"));
					dto.setSubject(rs.getString("subject"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}

				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
					}
				}
			}

			return dto;
		}

		
		// 다음글
		public NoticeDTO nextReadNotice(int num, String condition, String keyword) {
			NoticeDTO dto = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			StringBuilder sb = new StringBuilder();

			try {
				if (keyword != null && keyword.length() != 0) {
					sb.append(" SELECT * FROM ( ");
					sb.append("    SELECT num, subject ");
					sb.append("    FROM notice n ");
					sb.append("    JOIN member1 m ON n.userId = m.userId ");
					sb.append("    WHERE ( num < ? ) ");
					if (condition.equals("all")) {
						sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
					} else if (condition.equals("reg_date")) {
						keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
						sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
					} else {
						sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
					}
					sb.append("     ORDER BY num DESC ");
					sb.append(" ) WHERE ROWNUM = 1 ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setInt(1, num);
					pstmt.setString(2, keyword);
					if (condition.equals("all")) {
						pstmt.setString(3, keyword);
					}
				} else {
					sb.append(" SELECT * FROM ( ");
					sb.append("     SELECT num, subject FROM notice ");
					sb.append("     WHERE num < ? ");
					sb.append("     ORDER BY num DESC ");
					sb.append(" ) WHERE ROWNUM = 1 ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setInt(1, num);
				}

				rs = pstmt.executeQuery();

				if (rs.next()) {
					dto = new NoticeDTO();
					dto.setNum(rs.getInt("num"));
					dto.setSubject(rs.getString("subject"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}

				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
					}
				}
			}

			return dto;
		}

		
		// 조회수
		public void updateHitCount(int num) throws Exception {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "UPDATE notice SET hitCount=hitCount+1 WHERE num=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, num);
			
				pstmt.executeUpdate();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					}catch(Exception e2) {	
					}
				}
			}
			
		}
		
		
		
		// 수정
		public void updateNotice(NoticeDTO dto) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "UPDATE notice SET notice=?, subject=?, content=? WHERE num=?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, dto.getNotice());
				pstmt.setString(2, dto.getSubject());
				pstmt.setString(3, dto.getContent());
				pstmt.setInt(4, dto.getNum());
				
				pstmt.executeUpdate();
				
				pstmt.close();
				pstmt.close();
				
				if(dto.getSaveFiles() != null) {
					sql = "INSERT INTO noticeFile(numFile, num, saveFilename, originalFilename)  "
							+ "VALUES (noticeFile_seq.NEXTVAL, ?, ?, ?)";
				
					pstmt = conn.prepareStatement(sql);
					
					for(int i = 0; i<dto.getSaveFiles().length; i++) {
						pstmt.setInt(1, dto.getNum());
						pstmt.setString(2, dto.getSaveFiles()[i]);
						pstmt.setString(3, dto.getOriginalFiles()[i]);
						
						pstmt.executeUpdate();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e2) {
					}
				}
			}
			
			
		}
		
		public void deleteNotice(int num) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "DELETE FROM notice WHERE num=?";
				
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				pstmt.executeUpdate();
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e2) {
					}
				}
			}
		}
		
		
		public NoticeDTO readNoticeFile (int numFile) {
			NoticeDTO dto = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT numFile, num, saveFilename, originalFilename FROM noticeFile "
						+ " WHERE numFile = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, numFile);
			
				rs = pstmt.executeQuery();
				if(rs.next()) {
					dto = new NoticeDTO();
					
					// 바로 밑의 문장이 궁금해요
					dto.setNumFile(rs.getInt("numFile"));
					dto.setNum(rs.getInt("Num"));
					dto.setSaveFilename(rs.getString("saveFilename"));
					dto.setOriginalFilename(rs.getString("originalFilename"));
					
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(rs != null) {
					try {
						rs.close();
					} catch (Exception e2) {
					}
				}
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e2) {
					}
				}
			}
		
			return dto;
		}
		
		
		
		
		public void deleteNoticeFile(String mode, int num) throws SQLException {
			// 파일 테이블 삭제
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				if(mode.equals("all")) {
					sql = "DELETE FROM noticeFile WHERE num=?";
				} else {
					sql = "DELETE FROM noticeFile WHERE numFile=?";
				}
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, num);
				pstmt.executeUpdate();
				
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e2) {
					}
				}
			}
		}
		
		
	
	
}
