package com.freeGallery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.util.DBConn;

public class FreeGalleryDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertFreeGallery(FreeGalleryDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;
		
		try {
			sql = "SELECT freeGallery_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			seq = 0;
			if (rs.next()) {
				seq = rs.getInt(1);
			}
			dto.setNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			sql = "INSERT INTO freeGallery ( num, userId, category, subject, content, reg_date, hitCount) "
					+ " VALUES (?, ?, ?, ?, ?, SYSDATE, 0)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getCategory());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getContent());

			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO freeGalFile(fileNum, num, saveFilename) VALUES "
						+ " (freeGalFile_seq.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setInt(1, dto.getNum());
					pstmt.setString(2, dto.getImageFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}
			// freeGalFile 테이블에 파일 insert하기
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	
	// 데이터 개수
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freegallery";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}
	
	// 검색에서의 데이터 개수1(조건 x, 카테고리 o)
	public int dataCount(String category) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freegallery f JOIN member1 m ON f.userId = m.userId "
				+ "WHERE category = ?";
			

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, category);

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}
	
	// 검색에서의 데이터 개수2(조건 o, 카테고리 x)
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freegallery f JOIN member1 m ON f.userId = m.userId ";
			if (condition.equals("all")) {
				sql += "  WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  WHERE INSTR(" + condition + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if (condition.equals("all")) {
				pstmt.setString(2, keyword);
			}

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}
	
	
	// 검색에서의 데이터 개수3(조건 o, 카테고리 o)
	public int dataCount(String condition, String keyword, String category) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		System.out.println(condition);
		System.out.println(keyword);
		System.out.println(category);
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freegallery f JOIN member1 m ON f.userId = m.userId ";
			sql += "  WHERE category = ? ";
			if (condition.equals("all")) {
				sql += "  AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  AND INSTR(" + condition + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, category);
			pstmt.setString(2, keyword);
			if (condition.equals("all")) {
				pstmt.setString(3, keyword);
			}

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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
		System.out.println(result);
		return result;
	}
	
	
	// 게시물 리스트 (조건x, 카테고리 x)
	public List<FreeGalleryDTO> listFreeGallery(int start, int end) {
		List<FreeGalleryDTO> list = new ArrayList<FreeGalleryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT f.num, f.userId, userNickName, category, subject, savefilename, TO_CHAR(reg_date, 'YY/MM/DD') reg_date, "
					+ "NVL(likecount,0) likecount, NVL(replycount,0) replycount ");
			sb.append("         FROM freegallery f ");
			sb.append("         JOIN member1 m ON f.userId = m.userId ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             SELECT fileNum, num, savefilename FROM ( ");
			sb.append("                 SELECT fileNum, num, savefilename, ");
			sb.append("                       ROW_NUMBER() OVER(PARTITION BY num ORDER BY fileNum ASC) rank ");
			sb.append("                 FROM freegalfile");
			sb.append("             ) WHERE rank = 1 ");
			sb.append("         ) i ON f.num = i.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) likecount ");
			sb.append("             from freeGalLike ");
			sb.append("             group by num ");
			sb.append("         )fgl ON f.num = fgl.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) replycount ");
			sb.append("             from freeGalReply ");
			sb.append("             group by num ");
			sb.append("         )fgr ON f.num = fgr.num ");
			sb.append("         ORDER BY num DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				FreeGalleryDTO dto = new FreeGalleryDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setCategory(rs.getString("category"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("savefilename"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setLikeCount(rs.getInt("likecount"));
				dto.setReplyCount(rs.getInt("replycount"));
				
				list.add(dto);
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
		

		return list;
	}
	
	// 검색에서의 리스트1(조건x, 카테고리o)
	public List<FreeGalleryDTO> listFreeGallery(int start, int end, String category) {
		List<FreeGalleryDTO> list = new ArrayList<FreeGalleryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT f.num, f.userId, userNickName, category, subject, savefilename, TO_CHAR(reg_date, 'YY/MM/DD') reg_date, "
					+ "NVL(likecount,0) likecount, NVL(replycount,0) replycount ");
			sb.append("         FROM freegallery f ");
			sb.append("         JOIN member1 m ON f.userId = m.userId ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             SELECT fileNum, num, savefilename FROM ( ");
			sb.append("                 SELECT fileNum, num, savefilename, ");
			sb.append("                       ROW_NUMBER() OVER(PARTITION BY num ORDER BY fileNum ASC) rank ");
			sb.append("                 FROM freegalfile");
			sb.append("             ) WHERE rank = 1 ");
			sb.append("         ) i ON f.num = i.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) likecount ");
			sb.append("             from freeGalLike ");
			sb.append("             group by num ");
			sb.append("         )fgl ON f.num = fgl.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) replycount ");
			sb.append("             from freeGalReply ");
			sb.append("             group by num ");
			sb.append("         )fgr ON f.num = fgr.num ");
			sb.append("     WHERE category = ? ");
			sb.append("         ORDER BY num DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");


			pstmt = conn.prepareStatement(sb.toString());
				pstmt.setString(1, category);
				pstmt.setInt(2, end);
				pstmt.setInt(3, start);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				FreeGalleryDTO dto = new FreeGalleryDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setCategory(rs.getString("category"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("savefilename"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setLikeCount(rs.getInt("likecount"));
				dto.setReplyCount(rs.getInt("replycount"));
				
				list.add(dto);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

		return list;
	}
	
	
	// 검색에서의 게시물 리스트2(조건o, 카테고리 x)
	public List<FreeGalleryDTO> listFreeGallery(int start, int end, String condition, String keyword) {
		List<FreeGalleryDTO> list = new ArrayList<FreeGalleryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT f.num, f.userId, userNickName, category, subject, savefilename, TO_CHAR(reg_date, 'YY/MM/DD') reg_date, "
					+ "NVL(likecount,0) likecount, NVL(replycount,0) replycount ");
			sb.append("         FROM freegallery f ");
			sb.append("         JOIN member1 m ON f.userId = m.userId ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             SELECT fileNum, num, savefilename FROM ( ");
			sb.append("                 SELECT fileNum, num, savefilename, ");
			sb.append("                       ROW_NUMBER() OVER(PARTITION BY num ORDER BY fileNum ASC) rank ");
			sb.append("                 FROM freegalfile");
			sb.append("             ) WHERE rank = 1 ");
			sb.append("         ) i ON f.num = i.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) likecount ");
			sb.append("             from freeGalLike ");
			sb.append("             group by num ");
			sb.append("         )fgl ON f.num = fgl.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) replycount ");
			sb.append("             from freeGalReply ");
			sb.append("             group by num ");
			sb.append("         )fgr ON f.num = fgr.num ");
			if (condition.equals("all")) {
				sb.append("     WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append("     WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append("     WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			
			sb.append("         ORDER BY num DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");


			pstmt = conn.prepareStatement(sb.toString());
			
			if (condition.equals("all")) {
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
			
			while (rs.next()) {
				FreeGalleryDTO dto = new FreeGalleryDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setCategory(rs.getString("category"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("savefilename"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setLikeCount(rs.getInt("likecount"));
				dto.setReplyCount(rs.getInt("replycount"));
				
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

		return list;
	}
	
	
	
	// 검색에서의 게시물 리스트3(조건o, 카테고리 o)
	public List<FreeGalleryDTO> listFreeGallery(int start, int end, String condition, String keyword, String category) {
		List<FreeGalleryDTO> list = new ArrayList<FreeGalleryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT f.num, f.userId, userNickName, category, subject, savefilename, TO_CHAR(reg_date, 'YY/MM/DD') reg_date, "
					+ "NVL(likecount,0) likecount, NVL(replycount,0) replycount ");
			sb.append("         FROM freegallery f ");
			sb.append("         JOIN member1 m ON f.userId = m.userId ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             SELECT fileNum, num, savefilename FROM ( ");
			sb.append("                 SELECT fileNum, num, savefilename, ");
			sb.append("                       ROW_NUMBER() OVER(PARTITION BY num ORDER BY fileNum ASC) rank ");
			sb.append("                 FROM freegalfile");
			sb.append("             ) WHERE rank = 1 ");
			sb.append("         ) i ON f.num = i.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) likecount ");
			sb.append("             from freeGalLike ");
			sb.append("             group by num ");
			sb.append("         )fgl ON f.num = fgl.num ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select num, count(*) replycount ");
			sb.append("             from freeGalReply ");
			sb.append("             group by num ");
			sb.append("         )fgr ON f.num = fgr.num ");
			sb.append("     	WHERE category = ? ");
			if (condition.equals("all")) {
				sb.append("     AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append("     AND TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append("     AND INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append("         ORDER BY num DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");


			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, category);
			
			if (condition.equals("all")) {
				pstmt.setString(2, keyword);
				pstmt.setString(3, keyword);
				pstmt.setInt(4, end);
				pstmt.setInt(5, start);
			} else {
				pstmt.setString(2, keyword);
				pstmt.setInt(3, end);
				pstmt.setInt(4, start);
			}

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				FreeGalleryDTO dto = new FreeGalleryDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setCategory(rs.getString("category"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("savefilename"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setLikeCount(rs.getInt("likecount"));
				dto.setReplyCount(rs.getInt("replycount"));
				
				list.add(dto);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

		return list;
	}
	
	// 조회수 증가하기
	public void updateHitCount(int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE freeGallery SET hitCount=hitCount+1 WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

	}
	
	// 해당 게시물 보기
	public FreeGalleryDTO readFreeGal(int num) {
		FreeGalleryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT f.num, f.userId, userNickName, category, subject, content, "
					+ " reg_date, hitCount, NVL(likecount, 0) likecount "
					+ " FROM freegallery f "
					+ " JOIN member1 m ON f.userId=m.userId "
					+ " LEFT OUTER JOIN ("
					+ "      SELECT num, COUNT(*) likecount FROM freeGalLike" // 게시글의 좋아요
					+ "      GROUP BY num"
					+ " ) fgl ON f.num = fgl.num"
					+ " WHERE f.num = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				dto = new FreeGalleryDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setCategory(rs.getString("category"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setLikeCount(rs.getInt("likecount"));
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
	
	// 이전글
	public FreeGalleryDTO preReadGallery(int num, String condition, String keyword, String category) {
		FreeGalleryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT num, subject ");
				sb.append("    FROM FreeGallery f ");
				sb.append("    JOIN member1 m ON f.userId = f.userId ");
				sb.append("    WHERE ( num > ? ) AND category = ? ");
				sb.append("    ORDER BY num ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, num);
				pstmt.setString(2, category);
				

			} else if(keyword != null && keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT num, subject ");
				sb.append("    FROM FreeGallery f ");
				sb.append("    JOIN member1 m ON f.userId = f.userId ");
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
			} else if(keyword.length() != 0 && !category.equals("all")) { // 조건 o, 카테고리 o
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT num, subject ");
				sb.append("    FROM FreeGallery f ");
				sb.append("    JOIN member1 m ON f.userId = f.userId ");
				sb.append("    WHERE ( num > ? ) AND category = ? ");
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
				pstmt.setString(2, category);
				pstmt.setString(3, keyword);
				if (condition.equals("all")) {
					pstmt.setString(4, keyword);
				}
			} else { // 조건 x, 카테고리 x
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT num, subject FROM freeGallery ");
				sb.append("     WHERE num > ? ");
				sb.append("     ORDER BY num ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");
				
				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, num);
			}
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new FreeGalleryDTO();
				
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
	public FreeGalleryDTO nextReadGallery(int num, String condition, String keyword, String category) {
		FreeGalleryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT num, subject ");
				sb.append("    FROM FreeGallery f ");
				sb.append("    JOIN member1 m ON f.userId = f.userId ");
				sb.append("    WHERE ( num < ? ) AND category = ? ");
				sb.append("    ORDER BY num ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, num);
				pstmt.setString(2, category);
				

			} else if(keyword != null && keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT num, subject ");
				sb.append("    FROM FreeGallery f ");
				sb.append("    JOIN member1 m ON f.userId = f.userId ");
				sb.append("    WHERE ( num < ? ) ");
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
			} else if(keyword.length() != 0 && !category.equals("all")) { // 조건 o, 카테고리 o
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT num, subject ");
				sb.append("    FROM FreeGallery f ");
				sb.append("    JOIN member1 m ON f.userId = f.userId ");
				sb.append("    WHERE ( num < ? ) AND category = ? ");
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
				pstmt.setString(2, category);
				pstmt.setString(3, keyword);
				if (condition.equals("all")) {
					pstmt.setString(4, keyword);
				}
			} else { // 조건 x, 카테고리 x
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT num, subject FROM freeGallery ");
				sb.append("     WHERE num < ? ");
				sb.append("     ORDER BY num ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");
				
				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, num);
			}
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new FreeGalleryDTO();
				
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
	
	
	
	
	
	
	// 해당 게시물 파일 리스트 보기
	public List<FreeGalleryDTO> listPhotoFile(int num) {
		List<FreeGalleryDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, num, savefilename FROM freeGalFile WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				FreeGalleryDTO dto = new FreeGalleryDTO();

				dto.setFileNum(rs.getInt("fileNum"));
				dto.setNum(rs.getInt("num"));
				dto.setImageFilename(rs.getString("savefilename"));
				
				list.add(dto);
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

		return list;
	}
	
	// 로그인 유저의 게시글 공감 유무(좋아요를 했는지 안했는지)
	public boolean isUserFreeGalLike(int num, String userId) {
		boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, userId FROM freeGalLike WHERE num = ? AND userId = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = true; // 좋아요를 했으면 true, 아니면 false 값 반환
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
		
		return result;
	}

	// 게시물 삭제
	public void deleteFreeGal(int num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			if (userId.equals("admin")) {
				sql = "DELETE FROM freeGallery WHERE num=?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, num);
				
				pstmt.executeUpdate();
			} else {
				sql = "DELETE FROM freeGallery WHERE num=? AND userId=?";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, num);
				pstmt.setString(2, userId);
				
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	// 게시물 파일 리스트
	public List<FreeGalleryDTO> listFreeGalFile(int num) {
		List<FreeGalleryDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, num, savefilename FROM freeGalFile WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				FreeGalleryDTO dto = new FreeGalleryDTO();

				dto.setFileNum(rs.getInt("fileNum"));
				dto.setNum(rs.getInt("num"));
				dto.setImageFilename(rs.getString("savefilename"));
				
				list.add(dto);
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

		return list;
	}

	
	// freeGalFile 테이블에서 file 이름 지우기
	public void deleteFreeGalFile(String mode, int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			if (mode.equals("all")) {
				sql = "DELETE FROM freeGalFile WHERE num = ?";
			} else {
				sql = "DELETE FROM freeGalFile WHERE fileNum = ?";
			}
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
	}

	// 수정하기
	public void updateFreeGallery(FreeGalleryDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE freeGallery SET category=?, subject=?, content=? WHERE num=?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, dto.getCategory());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			pstmt.setInt(4, dto.getNum());

			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO freeGalFile(fileNum, num, savefilename) VALUES "
						+ " (freeGalFile_seq.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setInt(1, dto.getNum());
					pstmt.setString(2, dto.getImageFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}
	
	
	// 사진 파일 삭제를 위해 DB에서 읽어오기
	public FreeGalleryDTO readFreeGalFile(int fileNum) {
		FreeGalleryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, num, savefilename FROM freeGalFile WHERE fileNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, fileNum);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new FreeGalleryDTO();

				dto.setFileNum(rs.getInt("fileNum"));
				dto.setNum(rs.getInt("num"));
				dto.setImageFilename(rs.getString("savefilename"));
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
	
	// 게시물의 공감 추가
	public void insertFreeGalLike(int num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO freeGalLike(num, userId) VALUES (?, ?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			pstmt.setString(2, userId);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}
	
	// 게시글 공감 삭제
	public void deleteFreeGalLike(int num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM freeGalLike WHERE num = ? AND userId = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			pstmt.setString(2, userId);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
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
	
	// 게시물의 공감 개수
	public int countFreeGalLike(int num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freeGalLike WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
				
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return result;
	}
	
	// 게시물의 댓글 및 답글 추가
	public void insertReply(ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO freeGalReply(replyNum, num, userId, content, answer, reg_date) "
					+ " VALUES (freeGalReply_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getContent());
			pstmt.setInt(4, dto.getAnswer());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}

	// 게시물의 댓글 개수
	public int dataCountReply(int num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freeGalReply WHERE num=? AND answer=0";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
				
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return result;
	}

	// 게시물 댓글 리스트
	public List<ReplyDTO> listReply(int num, int start, int end) {
		List<ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT r.replyNum, r.userId, userNickName, imagefilename, num, content, r.reg_date, ");
			sb.append("                NVL(answerCount, 0) answerCount, ");
			sb.append("                NVL(likeCount, 0) likeCount, ");
			sb.append("                NVL(disLikeCount, 0) disLikeCount ");
			sb.append("         FROM freeGalReply r ");
			sb.append("         JOIN member1 m ON r.userId = m.userId ");
			sb.append("	        LEFT OUTER  JOIN (");
			sb.append("	            SELECT answer, COUNT(*) answerCount ");
			sb.append("             FROM freeGalReply  WHERE answer != 0 ");
			sb.append("             GROUP BY answer ");
			sb.append("         ) a ON r.replyNum = a.answer ");
			sb.append("         LEFT OUTER  JOIN ( ");
			sb.append("	            SELECT replyNum,  ");
			sb.append("                 COUNT(DECODE(replyLike, 1, 1)) likeCount, ");
			sb.append("                 COUNT(DECODE(replyLike, 0, 1)) disLikeCount ");
			sb.append("             FROM freeGalReplyLike GROUP BY replyNum  ");
			sb.append("         ) b ON r.replyNum = b.replyNum  ");
			sb.append("	        WHERE num = ? AND r.answer=0 ");
			sb.append("         ORDER BY r.replyNum DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, num);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ReplyDTO dto = new ReplyDTO();
				
				dto.setReplyNum(rs.getInt("replyNum"));
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setImageFileName(rs.getString("imagefilename"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswerCount(rs.getInt("answerCount"));
				dto.setLikeCount(rs.getInt("likeCount"));
				dto.setDisLikeCount(rs.getInt("disLikeCount"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return list;
	}
	
	// 댓글 읽어오기
	public ReplyDTO readReply(int replyNum) {
		ReplyDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT replyNum, num, r.userId, userNickName, content ,r.reg_date "
					+ "  FROM freeGalReply r JOIN member1 m ON r.userId=m.userId  "
					+ "  WHERE replyNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, replyNum);

			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new ReplyDTO();
				
				dto.setReplyNum(rs.getInt("replyNum"));
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
				
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return dto;
	}
	
	// 게시물의 댓글 삭제
	public void deleteReply(int replyNum, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		if(! userId.equals("admin")) {
			ReplyDTO dto = readReply(replyNum);
			if(dto == null || (! userId.equals(dto.getUserId()))) {
				return;
			}
		}
		
		try {
			sql = "DELETE FROM freeGalReply "
					+ "  WHERE replyNum IN  "
					+ "  (SELECT replyNum FROM freeGalReply START WITH replyNum = ?"
					+ "     CONNECT BY PRIOR replyNum = answer)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, replyNum);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}		
		
	}
	
	// 댓글의 답글 리스트
	public List<ReplyDTO> listReplyAnswer(int answer) {
		List<ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" SELECT replyNum, num, r.userId, userNickName, imagefilename, content, reg_date, answer ");
			sb.append(" FROM freeGalReply r ");
			sb.append(" JOIN member1 m ON r.userId=m.userId ");
			sb.append(" WHERE answer=? ");
			sb.append(" ORDER BY replyNum DESC ");
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, answer);

			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ReplyDTO dto=new ReplyDTO();
				
				dto.setReplyNum(rs.getInt("replyNum"));
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setImageFileName(rs.getString("imagefilename"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswer(rs.getInt("answer"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
				
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		return list;
	}
	
	// 댓글의 답글 개수
	public int dataCountReplyAnswer(int answer) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM freeGalReply WHERE answer=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, answer);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result=rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return result;
	}
	
	// 댓글의 좋아요 / 싫어요 추가
	public void insertReplyLike(ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO freeGalReplyLike(replyNum, userId, replyLike) VALUES (?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getReplyNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setInt(3, dto.getReplyLike());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}		

	}
	
	// 댓글의 좋아요 / 싫어요 개수
	public Map<String, Integer> countReplyLike(int replyNum) {
		Map<String, Integer> map = new HashMap<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT COUNT(DECODE(replyLike, 1, 1)) likeCount,  "
				+ "     COUNT(DECODE(replyLike, 0, 1)) disLikeCount  "
				+ " FROM freeGalReplyLike WHERE replyNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, replyNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				map.put("likeCount", rs.getInt("likeCount"));
				map.put("disLikeCount", rs.getInt("disLikeCount"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return map;
	}	
	
}
