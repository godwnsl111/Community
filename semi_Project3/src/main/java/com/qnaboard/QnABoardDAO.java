package com.qnaboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.util.DBConn;

public class QnABoardDAO {
	private Connection conn = DBConn.getConnection();

	// 데이터 추가
	public void insertBoard(QnABoardDTO dto, String mode) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;

		try {
			sql = " SELECT QnAboard_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			seq = 0;
			if (rs.next()) {
				seq = rs.getInt(1);
			}
			dto.setBoardNum(seq);

			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;

			if (mode.equals("write")) {
				// 글쓰기일 때
				dto.setGroupNum(seq);
				dto.setOrderNo(0);
				dto.setDepth(0);
				dto.setParent(0);
			} else if (mode.equals("reply")) {
				// 답변일 때
				updateOrderNo(dto.getGroupNum(), dto.getOrderNo());

				dto.setDepth(dto.getDepth() + 1);
				dto.setOrderNo(dto.getOrderNo() + 1);
			}

			sql = " INSERT INTO QnAboard(boardNum, userId, categoryType, subject, content, "
					+ " groupNum, depth, orderNo, parent, hitCount, reg_date) "
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, SYSDATE) ";
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, dto.getBoardNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getCategoryType());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getContent());
			pstmt.setInt(6, dto.getGroupNum());
			pstmt.setInt(7, dto.getDepth());
			pstmt.setInt(8, dto.getOrderNo());
			pstmt.setInt(9, dto.getParent());

			pstmt.executeUpdate();

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

	// 답변일 경우 orderNo 변경
	public void updateOrderNo(int groupNum, int orderNo) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE QnAboard SET orderNo = orderNo+1 WHERE groupNum = ? AND orderNo > ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, groupNum);
			pstmt.setInt(2, orderNo);

			pstmt.executeUpdate();

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

			sql = "SELECT NVL(COUNT(*), 0) FROM QnAboard";
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

	// 검색에서의 데이터 개수
	public int dataCount(String condition, String keyword, String categoryType) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT NVL(COUNT(*), 0) FROM QnAboard b JOIN member1 m ON b.userId = m.userId ");
			if (keyword.length() != 0) {
				if (condition.equals("all")) {
					sb.append("     WHERE ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 )  ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("     WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
				} else {
					sb.append("     WHERE INSTR(" + condition + ", ?) >= 1 ");
				}
			}
			if (keyword.length() != 0 && !categoryType.equals("all")) {
				sb.append(" AND categoryType='" + categoryType + "' ");
			} else if (keyword.length() == 0 && !categoryType.equals("all")) {
				sb.append(" WHERE categoryType='" + categoryType + "' ");
			}

			pstmt = conn.prepareStatement(sb.toString());

			if (keyword.length() != 0) {
				pstmt.setString(1, keyword);
				if (condition.equals("all")) {
					pstmt.setString(2, keyword);
				}
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

	// 게시물 리스트
	public List<QnABoardDTO> listBoard(int start, int end) {
		List<QnABoardDTO> list = new ArrayList<QnABoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT boardNum, b.userId, userName, userNickName, categoryType, ");
			sb.append("               subject, groupNum, orderNo, depth, hitCount,");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("         FROM QnAboard b ");
			sb.append("         JOIN member1 m ON b.userId = m.userId ");
			sb.append("         ORDER BY groupNum DESC, orderNo ASC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				QnABoardDTO dto = new QnABoardDTO();

				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setCategoryType(rs.getString("categoryType"));
				dto.setSubject(rs.getString("subject"));
				dto.setGroupNum(rs.getInt("groupNum"));
				dto.setDepth(rs.getInt("depth"));
				dto.setOrderNo(rs.getInt("orderNo"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));

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

	// 검색시 게시물 리스트
	public List<QnABoardDTO> listBoard(int start, int end, String condition, String keyword, String categoryType) {
		List<QnABoardDTO> list = new ArrayList<QnABoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
				sb.append("         SELECT boardNum, b.userId, userName, userNickName, categoryType, ");
				sb.append("               subject, groupNum, orderNo, depth, hitCount, ");
				sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
				sb.append("         FROM QnAboard b ");
				sb.append("         JOIN member1 m ON b.userId = m.userId ");
				if (keyword.length() != 0) {
					if (condition.equals("all")) {
						sb.append("     WHERE ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
					} else if (condition.equals("reg_date")) {
						keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
						sb.append("     WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
					} else {
						sb.append("     WHERE INSTR(" + condition + ", ?) >= 1 ");
					}
				}
				if (keyword.length() != 0 && !categoryType.equals("all")) {
					sb.append(" AND categoryType='" + categoryType + "' ");
				} else if (keyword.length() == 0 && !categoryType.equals("all")) {
					sb.append(" WHERE categoryType='" + categoryType + "' ");
				}
				sb.append("         ORDER BY groupNum DESC, orderNo ASC ");
				sb.append("     ) tb WHERE ROWNUM <= ? ");
				sb.append(" ) WHERE rnum >= ? ");

				System.out.println(sb.toString());
				
				pstmt = conn.prepareStatement(sb.toString());
				
				if (keyword.length() != 0 && condition.equals("all")) {
					pstmt.setString(1, keyword);
					pstmt.setString(2, keyword);
					pstmt.setInt(3, end);
					pstmt.setInt(4, start);
				} else if (keyword.length() != 0 && !condition.equals("all")) {
					pstmt.setString(1, keyword);
					pstmt.setInt(2, end);
					pstmt.setInt(3, start);
				} else if (keyword.length() == 0) {
					pstmt.setInt(1, end);
					pstmt.setInt(2, start);
				}

				rs = pstmt.executeQuery();

				while (rs.next()) {
					QnABoardDTO dto = new QnABoardDTO();

					dto.setBoardNum(rs.getInt("boardNum"));
					dto.setUserId(rs.getString("userId"));
					dto.setUserName(rs.getString("userName"));
					dto.setUserNickName(rs.getString("userNickName"));
					dto.setCategoryType(rs.getString("categoryType"));
					dto.setSubject(rs.getString("subject"));
					dto.setGroupNum(rs.getInt("groupNum"));
					dto.setDepth(rs.getInt("depth"));
					dto.setOrderNo(rs.getInt("orderNo"));
					dto.setHitCount(rs.getInt("hitCount"));
					dto.setReg_date(rs.getString("reg_date"));

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
	public void updateHitCount(int boardNum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " UPDATE QnAboard SET hitCount=hitCount+1 WHERE boardNum=? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, boardNum);
			
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

	// 해당 게시물 보기
	public QnABoardDTO readBoard(int boardNum) {
		QnABoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT b.boardNum, b.userId, userName, userNickName, categoryType, subject, "
					+ " content, reg_date, hitCount, NVL(likeCount, 0) likeCount, "
					+ " groupNum, depth, orderNo, parent "
					+ " FROM QnAboard b "
					+ " JOIN member1 m ON b.userId=m.userId "
					+ " LEFT OUTER JOIN ( "
					+ "			SELECT boardNum, COUNT(*) likeCount FROM QnALike "
					+ "			GROUP BY boardNum "
					+ " ) qnal ON b.boardNum = qnal.boardNum" // 좋아요
					+ " WHERE b.boardNum = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, boardNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new QnABoardDTO();
				
				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setCategoryType(rs.getString("categoryType"));
				dto.setGroupNum(rs.getInt("groupNum"));
				dto.setDepth(rs.getInt("depth"));
				dto.setOrderNo(rs.getInt("orderNo"));
				dto.setParent(rs.getInt("parent"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setLikeCount(rs.getInt("likeCount"));
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
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

	// 이전글
	public QnABoardDTO preReadBoard(int groupNum, int orderNo, String condition, String keyword, String categoryType) {
		QnABoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT boardNum, subject ");
				sb.append("    FROM QnAboard b ");
				sb.append("    JOIN member1 m ON b.userId = m.userId ");
				sb.append("    WHERE ( (groupNum = ? AND orderNo < ?) OR (groupNum > ?) ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				if(!categoryType.equals("all")) {
					sb.append(" AND categoryType='" + categoryType + "' ");
				}
				sb.append("     ORDER BY groupNum ASC, orderNo DESC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());

				pstmt.setInt(1, groupNum);
				pstmt.setInt(2, orderNo);
				pstmt.setInt(3, groupNum);
				pstmt.setString(4, keyword);
				if (condition.equals("all")) {
					pstmt.setString(5, keyword);
				}
			} else {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT boardNum, subject FROM QnAboard ");
				sb.append("     WHERE (groupNum = ? AND orderNo < ?) OR (groupNum > ?) ");
				if(!categoryType.equals("all")) {
					sb.append(" AND categoryType='" + categoryType + "' ");
				}
				sb.append("     ORDER BY groupNum ASC, orderNo DESC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());

				pstmt.setInt(1, groupNum);
				pstmt.setInt(2, orderNo);
				pstmt.setInt(3, groupNum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new QnABoardDTO();

				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setSubject(rs.getString("subject"));
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

	// 다음글
	public QnABoardDTO nextReadBoard(int groupNum, int orderNo, String condition, String keyword, String categoryType) {
		QnABoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT boardNum, subject ");
				sb.append("    FROM QnAboard b ");
				sb.append("    JOIN member1 m ON b.userId = m.userId ");
				sb.append("    WHERE ( (groupNum = ? AND orderNo > ?) OR (groupNum < ?) ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				if(!categoryType.equals("all")) {
					sb.append(" AND categoryType='" + categoryType + "' ");
				}
				sb.append("     ORDER BY groupNum DESC, orderNo ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());

				pstmt.setInt(1, groupNum);
				pstmt.setInt(2, orderNo);
				pstmt.setInt(3, groupNum);
				pstmt.setString(4, keyword);
				if (condition.equals("all")) {
					pstmt.setString(5, keyword);
				}
			} else {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT boardNum, subject FROM QnAboard ");
				sb.append("     WHERE (groupNum = ? AND orderNo > ?) OR (groupNum < ?) ");
				if(!categoryType.equals("all")) {
					sb.append(" AND categoryType='" + categoryType + "' ");
				}
				sb.append("     ORDER BY groupNum DESC, orderNo ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());

				pstmt.setInt(1, groupNum);
				pstmt.setInt(2, orderNo);
				pstmt.setInt(3, groupNum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new QnABoardDTO();

				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setSubject(rs.getString("subject"));
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

	// 게시물 수정
	public void updateBoard(QnABoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE QnAboard SET subject =?, content=? WHERE boardNum =? AND userId = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getBoardNum());
			pstmt.setString(4, dto.getUserId());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
	}

	// 게시물 삭제
	public void deleteBoard(int boardNum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM QnAboard WHERE boardNum IN "
					+ " (SELECT boardNum FROM QnAboard "
					+ " START WITH boardNum = ? "
					+ " CONNECT BY PRIOR boardNum = parent) " ;
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, boardNum);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
	}
	
	// 로그인 유저의 게시글 공감 유무(좋아요를 했는지 안했는지)
		public boolean isUserQnALike(int boardNum, String userId) {
			boolean result = false;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT boardNum, userId FROM QnALike WHERE boardNum = ? AND userId = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, boardNum);
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
	// 게시물의 공감 추가
		public void insertQnALike(int boardNum, String userId) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "INSERT INTO QnALike(boardNum, userId) VALUES (?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, boardNum);
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
		public void deleteQnALike(int boardNum, String userId) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "DELETE FROM QnALike WHERE boardNum = ? AND userId = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, boardNum);
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
		public int countQnALike(int boardNum) {
			int result = 0;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT NVL(COUNT(*), 0) FROM QnALike WHERE boardNum=?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, boardNum);
				
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
		
		// 게시물의 댓글 추가
		public void insertReply(ReplyDTO dto) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "INSERT INTO QnAReply(replyNum, boardNum, userId, content, answer, reg_date) "
						+ " VALUES (QnAReply_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE)";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, dto.getBoardNum());
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
		public int dataCountReply(int boardNum) {
			int result = 0;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT NVL(COUNT(*), 0) FROM QnAReply WHERE boardNum=? AND answer=0";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, boardNum);
				
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
		public List<ReplyDTO> listReply(int boardNum, int start, int end) {
			List<ReplyDTO> list = new ArrayList<>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			StringBuilder sb = new StringBuilder();
			
			try {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
				sb.append("         SELECT r.replyNum, r.userId, userNickName, imagefilename, boardNum, content, r.reg_date, ");
				sb.append("                NVL(answerCount, 0) answerCount, ");
				sb.append("                NVL(likeCount, 0) likeCount, ");
				sb.append("                NVL(disLikeCount, 0) disLikeCount ");
				sb.append("         FROM QnAReply r ");
				sb.append("         JOIN member1 m ON r.userId = m.userId ");
				sb.append("	        LEFT OUTER  JOIN (");
				sb.append("	            SELECT answer, COUNT(*) answerCount ");
				sb.append("             FROM QnAReply  WHERE answer != 0 ");
				sb.append("             GROUP BY answer ");
				sb.append("         ) a ON r.replyNum = a.answer ");
				sb.append("         LEFT OUTER  JOIN ( ");
				sb.append("	            SELECT replyNum,  ");
				sb.append("                 COUNT(DECODE(replyLike, 1, 1)) likeCount, ");
				sb.append("                 COUNT(DECODE(replyLike, 0, 1)) disLikeCount ");
				sb.append("             FROM QnAReplyLike GROUP BY replyNum  ");
				sb.append("         ) b ON r.replyNum = b.replyNum  ");
				sb.append("	        WHERE boardNum = ? AND r.answer=0 ");
				sb.append("         ORDER BY r.replyNum DESC ");
				sb.append("     ) tb WHERE ROWNUM <= ? ");
				sb.append(" ) WHERE rnum >= ? ");
				
				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, boardNum);
				pstmt.setInt(2, end);
				pstmt.setInt(3, start);

				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					ReplyDTO dto = new ReplyDTO();
					
					dto.setReplyNum(rs.getInt("replyNum"));
					dto.setBoardNum(rs.getInt("boardNum"));
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
				sql = "SELECT replyNum, boardNum, r.userId, userNickName, content ,r.reg_date "
						+ "  FROM QnAReply r JOIN member1 m ON r.userId=m.userId  "
						+ "  WHERE replyNum = ? ";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, replyNum);

				rs=pstmt.executeQuery();
				
				if(rs.next()) {
					dto=new ReplyDTO();
					
					dto.setReplyNum(rs.getInt("replyNum"));
					dto.setBoardNum(rs.getInt("boardNum"));
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
				sql = "DELETE FROM QnAReply "
						+ "  WHERE replyNum IN  "
						+ "  (SELECT replyNum FROM QnAReply START WITH replyNum = ?"
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
		// 댓글의 좋아요 / 싫어요 추가
		public void insertReplyLike(ReplyDTO dto) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "INSERT INTO QnAReplyLike(replyNum, userId, replyLike) VALUES (?, ?, ?)";
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
					+ " FROM QnAReplyLike WHERE replyNum = ? ";
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
		// 댓글의 답글 리스트
		public List<ReplyDTO> listReplyAnswer(int answer) {
			List<ReplyDTO> list = new ArrayList<>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			StringBuilder sb=new StringBuilder();
			
			try {
				sb.append(" SELECT replyNum, boardNum, r.userId, userNickName, imagefilename, content, reg_date, answer ");
				sb.append(" FROM QnAReply r ");
				sb.append(" JOIN member1 m ON r.userId=m.userId ");
				sb.append(" WHERE answer=? ");
				sb.append(" ORDER BY replyNum DESC ");
				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, answer);

				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					ReplyDTO dto=new ReplyDTO();
					
					dto.setReplyNum(rs.getInt("replyNum"));
					dto.setBoardNum(rs.getInt("boardNum"));
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
				sql = "SELECT NVL(COUNT(*), 0) FROM QnaReply WHERE answer=?";
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
}
