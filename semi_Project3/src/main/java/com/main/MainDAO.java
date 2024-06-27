package com.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.buy.TradebuyDTO;
import com.freeBoard.FreeBoardDTO;
import com.freeGallery.FreeGalleryDTO;
import com.infoBoard.InfoBoardDTO;
import com.notice.NoticeDTO;
import com.qnaboard.QnABoardDTO;
import com.sell.TradeDTO;
import com.util.DBConn;

public class MainDAO {
	private Connection conn = DBConn.getConnection();
	
	// 자유갤러리 인기 이미지 리스트
		public List<FreeGalleryDTO> imageListFreeGallery() {
			List<FreeGalleryDTO> list = new ArrayList<FreeGalleryDTO>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			StringBuilder sb = new StringBuilder();

			try {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
				sb.append(
						"         SELECT f.num, f.userId, hitCount, userNickName, category, subject, savefilename, TO_CHAR(reg_date, 'YY/MM/DD') reg_date, "
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
				sb.append("         ORDER BY hitCount DESC ");
				sb.append("     ) tb WHERE ROWNUM <= 3 ");
				sb.append(" ) WHERE rnum >= 1 ");

				pstmt = conn.prepareStatement(sb.toString());

				rs = pstmt.executeQuery();

				while (rs.next()) {
					FreeGalleryDTO dto = new FreeGalleryDTO();

					dto.setNum(rs.getInt("num"));
					dto.setUserId(rs.getString("userId"));
					dto.setUserNickName(rs.getString("userNickName"));
					dto.setCategory(rs.getString("category"));
					dto.setSubject(rs.getString("subject"));
					dto.setHitCount(rs.getInt("hitCount"));
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
	

	// QnA 게시물 리스트
	public List<QnABoardDTO> QnAListBoard() {
		List<QnABoardDTO> list = new ArrayList<QnABoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT b.boardNum, b.userId, userName, userNickName, categoryType, ");
			sb.append("               subject, groupNum, orderNo, depth, hitCount,");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, likecount, NVL(replycount,0) replycount ");
			sb.append("         FROM QnAboard b ");
			sb.append("         JOIN member1 m ON b.userId = m.userId ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select boardNum, count(*) likecount ");
			sb.append("             from QnALike ");
			sb.append("             group by boardNum ");
			sb.append("         )qnal ON b.boardNum = qnal.boardNum ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             select boardNum, count(*) replycount ");
			sb.append("             FROM QnAReply ");
			sb.append("             group by boardNum ");
			sb.append("         )qnar ON b.boardNum = qnar.boardNum ");
			sb.append("         WHERE parent = 0 ");
			sb.append("         ORDER BY reg_date DESC");
			sb.append("     ) tb WHERE ROWNUM <= 10 ");
			sb.append(" ) WHERE rnum >= 1 ");

			pstmt = conn.prepareStatement(sb.toString());

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

	// 자유갤러리 리스트
	public List<FreeGalleryDTO> listFreeGallery() {
		List<FreeGalleryDTO> list = new ArrayList<FreeGalleryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append(
					"         SELECT f.num, f.userId, hitCount, userNickName, category, subject, savefilename, TO_CHAR(reg_date, 'YY/MM/DD') reg_date, "
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
			sb.append("         ORDER BY reg_date DESC ");
			sb.append("     ) tb WHERE ROWNUM <= 10 ");
			sb.append(" ) WHERE rnum >= 1 ");

			pstmt = conn.prepareStatement(sb.toString());

			rs = pstmt.executeQuery();

			while (rs.next()) {
				FreeGalleryDTO dto = new FreeGalleryDTO();

				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserNickName(rs.getString("userNickName"));
				dto.setCategory(rs.getString("category"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
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

	// 공지사항 리스트
	public List<NoticeDTO> noticeListBoard() {
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("  SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("		SELECT num, notice, userName, subject, hitCount,");
			sb.append("        TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date  ");
			sb.append("		FROM notice n");
			sb.append("		JOIN member1 m ON n.userId = m.userId  ");
			sb.append("		ORDER BY reg_date DESC ");
			sb.append("		) tb WHERE ROWNUM <= 10 ");
			sb.append("	) WHERE rnum >= 1 ");

			pstmt = conn.prepareStatement(sb.toString());

			rs = pstmt.executeQuery();
			while (rs.next()) {
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
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}

		return list;
	}

	// 소통공간 리스트
	public List<FreeBoardDTO> freeBoardListBoard() {
		List<FreeBoardDTO> list = new ArrayList<FreeBoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append("select * from ( ");
			sb.append(" select rownum rnum, tb.* FROM ( ");
			sb.append("  select f.num, f.userId, title, hitCount, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, ");
			sb.append("   NVL(replyCount, 0) replyCount FROM freeBoard f");
			sb.append("    join member1 m ON f.userId = m.userId ");
			sb.append("     left outer join ( ");
			sb.append("      select num, COUNT(*) replyCount FROM freeBoardReply ");
			sb.append("      GROUP BY num");
			sb.append("   ) fbr ON f.num = fbr.num");
			sb.append("   ORDER BY reg_date DESC ");
			sb.append("  ) tb WHERE ROWNUM <= 10 ");
			sb.append(" ) WHERE rnum >= 1 ");

			pstmt = conn.prepareStatement(sb.toString());

			rs = pstmt.executeQuery();

			while (rs.next()) {
				FreeBoardDTO dto = new FreeBoardDTO();

				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setTitle(rs.getString("title"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setReplyCount(rs.getInt("replyCount"));

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

	// 정보게시판 리스트
	public List<InfoBoardDTO> infoBoardListInfoBoard() {
		List<InfoBoardDTO> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("       SELECT num, subject, userName, hitCount, ");
			sb.append("          reg_date FROM infoboard n ");
			sb.append("          JOIN member1 m ON n.userId = m.userId ");
			sb.append("          ORDER BY reg_date DESC ");
			sb.append("       )tb WHERE ROWNUM <= 10 ");
			sb.append("   )WHERE rnum >= 1 ");

			pstmt = conn.prepareStatement(sb.toString());

			rs = pstmt.executeQuery();
			while (rs.next()) {
				InfoBoardDTO dto = new InfoBoardDTO();
				dto.setNum(rs.getInt("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));

				list.add(dto);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}

		return list;
	}
	
	// 팝니다 이미지 리스트
	public List<TradeDTO> listTradeImage() {
		List<TradeDTO> list = new ArrayList<TradeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT tradeNum, p.userId, subject, price, p.imageFilename, hitCount, ");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("         FROM trade p ");
			sb.append("         JOIN member1 m ON p.userId = m.userId ");
			sb.append("         ORDER BY hitCount DESC ");
			sb.append("     ) tb WHERE ROWNUM <= 4 ");
			sb.append(" ) WHERE rnum >= 1 ");	
			//
			pstmt = conn.prepareStatement(sb.toString());
			
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				TradeDTO dto = new TradeDTO();
				
				dto.setTradeNum(rs.getInt("tradeNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setPrice(rs.getString("price"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setReg_date(rs.getString("reg_date"));
				
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
	
	// 팝니다 리스트
	public List<TradeDTO> listTrade() {
		List<TradeDTO> list = new ArrayList<TradeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT tradeNum, p.userId, subject, price, p.imageFilename, hitCount, ");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("         FROM trade p ");
			sb.append("         JOIN member1 m ON p.userId = m.userId ");
			sb.append("         ORDER BY reg_date DESC ");
			sb.append("     ) tb WHERE ROWNUM <= 10 ");
			sb.append(" ) WHERE rnum >= 1 ");	
			//
			pstmt = conn.prepareStatement(sb.toString());
			
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				TradeDTO dto = new TradeDTO();
				
				dto.setTradeNum(rs.getInt("tradeNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setSubject(rs.getString("subject"));
				dto.setPrice(rs.getString("price"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setReg_date(rs.getString("reg_date"));
				
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
	
	// 삽니다 리스트
	public List<TradebuyDTO> listTradebuy() {
		List<TradebuyDTO> list = new ArrayList<TradebuyDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT buyNum, p.userId, subject, price, p.imageFilename, hitCount, ");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("         FROM tradebuy p ");
			sb.append("         JOIN member1 m ON p.userId = m.userId ");
			sb.append("         ORDER BY reg_date DESC ");
			sb.append("     ) tb WHERE ROWNUM <= 10 ");
			sb.append(" ) WHERE rnum >= 1 ");	
			//
			pstmt = conn.prepareStatement(sb.toString());
			
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				TradebuyDTO dto = new TradebuyDTO();
				
				dto.setBuyNum(rs.getInt("buyNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setPrice(rs.getString("price"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setReg_date(rs.getString("reg_date"));
				
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
}
