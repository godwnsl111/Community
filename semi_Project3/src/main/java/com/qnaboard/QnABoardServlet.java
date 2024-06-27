package com.qnaboard;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/qnaboard/*")
public class QnABoardServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();

		// 세션 정보 LoginFilter 에서 처리
		/*
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		if (info == null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}
		*/
		
		
		// uri에 따른 작업 구분
		if (uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if (uri.indexOf("write.do") != -1) {
			writeForm(req, resp);
		} else if (uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req, resp);
		} else if (uri.indexOf("article.do") != -1) {
			article(req, resp);
		} else if (uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		} else if (uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		} else if (uri.indexOf("reply.do") != -1) {
			replyForm(req, resp);
		} else if (uri.indexOf("reply_ok.do") != -1) {
			replySubmit(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if (uri.indexOf("insertQnALike.do") != -1) {
			// 게시물 공감 저장
			insertQnABoardLike(req, resp);
		} else if (uri.indexOf("insertReply.do") != -1) {
			// 댓글 추가
			insertReply(req, resp);
		} else if (uri.indexOf("listReply.do") != -1) {
			// 댓글 리스트
			listReply(req, resp);
		} else if (uri.indexOf("deleteReply.do") != -1) {
			// 댓글 삭제
			deleteReply(req, resp);
		} else if (uri.indexOf("insertReplyLike.do") != -1) {
			// 댓글 좋아요/싫어요 추가
			insertReplyLike(req, resp);
		} else if (uri.indexOf("countReplyLike.do") != -1) {
			// 댓글 좋아요/싫어요 개수
			countReplyLike(req, resp);
		} else if (uri.indexOf("insertReplyAnswer.do") != -1) {
			// 댓글의 답글 추가
			insertReplyAnswer(req, resp);
		} else if (uri.indexOf("listReplyAnswer.do") != -1) {
			// 댓글의 답글 리스트
			listReplyAnswer(req, resp);
		} else if (uri.indexOf("deleteReplyAnswer.do") != -1) {
			// 댓글의 답글 삭제
			deleteReplyAnswer(req, resp);
		} else if (uri.indexOf("countReplyAnswer.do") != -1) {
			// 댓글의 답글 개수
			countReplyAnswer(req, resp);
		}
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 리스트
		QnABoardDAO dao = new QnABoardDAO();
		MyUtil util = new MyUtil();

		String cp = req.getContextPath();

		try {
			String categoryType = req.getParameter("categoryType");
			if (categoryType == null) {
				categoryType = "all";
			}
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}

			// 검색
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if (condition == null) {
				condition = "all";
				keyword = "";
			}

			// GET 방식인 경우 디코딩
			if (req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}

			// 전체 데이터 개수
			int dataCount;
			if (keyword.length() == 0 && categoryType.equals("all")) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(condition, keyword, categoryType);
			}

			// 전체 페이지 수
			int rows = 10;
			int total_page = util.pageCount(rows, dataCount);
			if (current_page > total_page) {
				current_page = total_page;
			}

			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;

			// 게시물 가져오기
			List<QnABoardDTO> list = null;
			if (keyword.length() == 0 && categoryType.equals("all")) {
				list = dao.listBoard(start, end);
			} else {
				list = dao.listBoard(start, end, condition, keyword, categoryType);
			}

			// 리스트 글번호 만들기
			int listNum, n = 0;
			for (QnABoardDTO dto : list) {
				listNum = dataCount - (start + n - 1);
				dto.setListNum(listNum);
				n++;
			}

			String query = "categoryType=" + categoryType;
			if (keyword.length() != 0) {
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			}

			// 페이징 처리
			String listUrl = cp + "/qnaboard/list.do";
			String articleUrl = cp + "/qnaboard/article.do?page=" + current_page;

			listUrl += "?" + query;
			articleUrl += "&" + query;

			String paging = util.paging(current_page, total_page, listUrl);

			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			req.setAttribute("categoryType", categoryType);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// JSP로 포워딩
		forward(req, resp, "/WEB-INF/views/qnaboard/list.jsp");
	}

	private void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글쓰기 폼
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/qnaboard/write.jsp");
	}

	private void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 저장
		QnABoardDAO dao = new QnABoardDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/qnaboard/list.do");
			return;
		}

		try {
			QnABoardDTO dto = new QnABoardDTO();

			// userId는 세션에 저장된 정보
			dto.setUserId(info.getUserId());

			// 파라미터
			dto.setCategoryType(req.getParameter("categoryType"));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));

			dao.insertBoard(dto, "write");
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/qnaboard/list.do");
	}

	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글보기
		QnABoardDAO dao = new QnABoardDAO();
		MyUtil util = new MyUtil();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");

		String cp = req.getContextPath();
		String page = req.getParameter("page");

		String query = "page=" + page;

		try {
			int boardNum = Integer.parseInt(req.getParameter("boardNum"));
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String categoryType = req.getParameter("categoryType");

			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			if (categoryType == null) {
				categoryType = "all";
			}

			keyword = URLDecoder.decode(keyword, "utf-8");

			if (keyword.length() != 0 || !categoryType.equals("all")) {
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8")
						+ "&categoryType=" + categoryType;
			}

			// 조회수 증가
			dao.updateHitCount(boardNum);

			// 게시물 가져오기
			QnABoardDTO dto = dao.readBoard(boardNum);
			if (dto == null) {
				resp.sendRedirect(cp + "/qnaboard/list.do?" + query);
				return;
			}
			dto.setContent(util.htmlSymbols(dto.getContent()));
			
			// 로그인 유저의 게시글 공감 유무
			boolean isUserLike = dao.isUserQnALike(boardNum, info.getUserId()); // 좋아요가 된 상태면 true, 아니면 false

			// 이전글 다음글
			QnABoardDTO preReadDto = dao.preReadBoard(dto.getGroupNum(), dto.getOrderNo(), condition, keyword,
					categoryType);
			QnABoardDTO nextReadDto = dao.nextReadBoard(dto.getGroupNum(), dto.getOrderNo(), condition, keyword,
					categoryType);

			// JSP 로 전달할 속성
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("query", query);
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);
			req.setAttribute("isUserLike", isUserLike);

			// 포워딩
			forward(req, resp, "/WEB-INF/views/qnaboard/article.jsp");
			return;

		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/qnaboard/list.do?" + query);
	}

	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 폼
		QnABoardDAO dao = new QnABoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		String page = req.getParameter("page");
		String condition = req.getParameter("condition");
		String keyword = req.getParameter("keyword");
		String categoryType = req.getParameter("categoryType");
		if (categoryType == null) {
			categoryType = "all";
		}
		String query = "";
		if (keyword != null) {
			query = "page=" + page + "&condition=" + condition + "&keyword=" + keyword + "&categoryType="
					+ categoryType;
		} else {
			query = "page=" + page + "&condition=" + condition + "&keyword=" + "&categoryType=" + categoryType;
		}
		
		try {
			int boardNum = Integer.parseInt(req.getParameter("boardNum"));
			QnABoardDTO dto = dao.readBoard(boardNum);
			
			if(dto == null) {
				resp.sendRedirect(cp + "/qnaboard/list.do?"+query);
				return;
			}
			
			// 게시물을 올린 사용자가 아니면
			if(! dto.getUserId().equals(info.getUserId())) {
				resp.sendRedirect(cp + "/qnaboard/list.do?"+query);
				return;
			}
			
			req.setAttribute("mode", "update");
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("condition", condition);
			req.setAttribute("categoryType", categoryType);
			req.setAttribute("keyword", keyword);
			
			forward(req, resp, "/WEB-INF/views/qnaboard/write.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp + "/qnaboard/list.do?"+query);
		
	}

	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 완료
		QnABoardDAO dao = new QnABoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/qnaboard/list.do");
			return;
		}
		String page = req.getParameter("page");
		String condition = req.getParameter("condition");
		String categoryType = req.getParameter("categoryType");
		
		
		String keyword = req.getParameter("keyword");
		keyword = URLDecoder.decode(keyword, "utf-8");
		
		String query = "page="+page+"&condition="+condition+"&keyword="+URLEncoder.encode(keyword,"utf-8")+"&categoryType"+categoryType;
		
		try {
			QnABoardDTO dto = new QnABoardDTO();
			dto.setBoardNum(Integer.parseInt(req.getParameter("boardNum")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			dto.setUserId(info.getUserId());
			
			dao.updateBoard(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp + "/qnaboard/list.do?"+query);
	}

	private void replyForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 답변 폼
		QnABoardDAO dao = new QnABoardDAO();

		String cp = req.getContextPath();

		String page = req.getParameter("page");
		String condition = req.getParameter("condition");
		String keyword = req.getParameter("keyword");
		String categoryType = req.getParameter("categoryType");
		if (categoryType == null) {
			categoryType = "all";
		}
		String query = "";
		if (keyword != null) {
			query = "page=" + page + "&condition=" + condition + "&keyword=" + keyword + "&categoryType="
					+ categoryType;
		} else {
			query = "page=" + page + "&condition=" + condition + "&keyword=" + "&categoryType=" + categoryType;
		}

		try {
			int boardNum = Integer.parseInt(req.getParameter("boardNum"));

			QnABoardDTO dto = dao.readBoard(boardNum);
			if (dto == null) {
				resp.sendRedirect(cp + "/qnaboard/list.do?" + query);
				return;
			}

			String s = "[" + dto.getSubject() + "] 에 대한 답변입니다. \n";
			dto.setContent(s);

			req.setAttribute("mode", "reply");
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("condition", condition);
			req.setAttribute("categoryType", categoryType);
			req.setAttribute("keyword", keyword);

			forward(req, resp, "/WEB-INF/views/qnaboard/write.jsp");
			return;

		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp + "/qnaboard/list.do?" + query);
	}

	private void replySubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 답변 완료
		QnABoardDAO dao = new QnABoardDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/qnaboard/list.do");
			return;
		}

		String page = req.getParameter("page");
		String condition = req.getParameter("condition");
		String categoryType = req.getParameter("categoryType");
		
		
		String keyword = req.getParameter("keyword");
		keyword = URLDecoder.decode(keyword, "utf-8");
		
		String query = "page="+page+"&condition="+condition+"&keyword="+URLEncoder.encode(keyword,"utf-8")+"&categoryType"+categoryType;

		try {
			QnABoardDTO dto = new QnABoardDTO();

			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setCategoryType(req.getParameter("categoryType"));
			dto.setGroupNum(Integer.parseInt(req.getParameter("groupNum")));
			dto.setOrderNo(Integer.parseInt(req.getParameter("orderNo")));
			dto.setDepth(Integer.parseInt(req.getParameter("depth")));
			dto.setParent(Integer.parseInt(req.getParameter("parent")));

			dto.setUserId(info.getUserId());

			dao.insertBoard(dto, "reply");

		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp + "/qnaboard/list.do?" + query);
	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 삭제
		QnABoardDAO dao = new QnABoardDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();

		String page = req.getParameter("page");
		String categoryType = req.getParameter("categoryType");
		if(categoryType == null) {
			categoryType = "all";
		}
		String query = "page=" + page + "&categoryType="+categoryType;

		try {
			int boardNum = Integer.parseInt(req.getParameter("boardNum"));
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");

			if (keyword.length() != 0) {
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
			}

			QnABoardDTO dto = dao.readBoard(boardNum);

			if (dto == null) {
				resp.sendRedirect(cp + "/qnaboard/list.do?" + query);
				return;
			}

			// 게시물을 올린 사용자나 admin이 아니면
			if (!dto.getUserId().equals(info.getUserId()) && !info.getUserId().equals("admin")) {
				resp.sendRedirect(cp + "/qnaboard/list.do?" + query);
				return;
			}

			dao.deleteBoard(boardNum);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/qnaboard/list.do?" + query);
	}
	
	private void insertQnABoardLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 좋아요 저장 - AJAX:JSON
		QnABoardDAO dao = new QnABoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String state = "false";
		int QnALikeCount = 0;
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			String isLike = req.getParameter("isLike");
			if (isLike.equals("true")) {
				dao.deleteQnALike(num, info.getUserId()); // 좋아요 취소
			} else {
				dao.insertQnALike(num, info.getUserId()); // 좋아요
			}
			
			// 게시글 좋아요 개수
			QnALikeCount = dao.countQnALike(num);
			state = "true";
			
		} catch (SQLException e) {
			state = "liked";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		job.put("QnALikeCount", QnALikeCount);
		
		resp.setContentType("text/html;charset=utf-8");

		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}
	
	
	// 리플 리스트 - AJAX:TEXT
	private void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		QnABoardDAO dao = new QnABoardDAO();
		MyUtil util = new MyUtil();

		try {
			// 댓글을 리스트를 가져올 게시물 번호
			int boardNum = Integer.parseInt(req.getParameter("boardNum"));
			String pageNo = req.getParameter("pageNo");
			int current_page = 1;
			if (pageNo != null) {
				current_page = Integer.parseInt(pageNo);
			}

			int rows = 5;
			int total_page = 0;
			int replyCount = 0;

			replyCount = dao.dataCountReply(boardNum);
			total_page = util.pageCount(rows, replyCount);
			if (current_page > total_page) {
				current_page = total_page;
			}

			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;

			// 리스트에 출력할 댓글
			List<ReplyDTO> listReply = dao.listReply(boardNum, start, end);

			// 내용의 엔터를 <br>로
			for (ReplyDTO dto : listReply) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}

			// 페이징처리 : listPage - 자바스크립트 함수명
			String paging = util.pagingFunc(current_page, total_page, "listPage");

			req.setAttribute("listReply", listReply);
			req.setAttribute("pageNo", current_page);
			req.setAttribute("replyCount", replyCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);

			forward(req, resp, "/WEB-INF/views/qnaboard/listReply.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 문제가 있는 경우 에러 코드를 전송
		resp.sendError(400);

	}

	// 리플 또는 답글 저장 - AJAX:JSON
	private void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		QnABoardDAO dao = new QnABoardDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String state = "false";

		try {
			ReplyDTO dto = new ReplyDTO();

			dto.setBoardNum(Integer.parseInt(req.getParameter("boardNum"))); // 아버지 번호
			dto.setUserId(info.getUserId()); // 로그인한 사람이니까 session 넣어주면 됨
			dto.setContent(req.getParameter("content"));
			String answer = req.getParameter("answer");
			if (answer != null) {
				dto.setAnswer(Integer.parseInt(answer));
			}

			dao.insertReply(dto);

			state = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 처리 결과를 JSON으로 전송
		JSONObject job = new JSONObject();
		job.put("state", state);

		resp.setContentType("text/html;charset=utf-8");

		PrintWriter out = resp.getWriter();
		out.print(job.toString());

	}

	// 리플 또는 답글 삭제 - AJAX:JSON
	private void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		QnABoardDAO dao = new QnABoardDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		String state = "false";
		
		try {
			int replyNum = Integer.parseInt(req.getParameter("replyNum"));
			dao.deleteReply(replyNum, info.getUserId());
			
			state = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}
	
	// 댓글 좋아요 / 싫어요 저장 - AJAX:JSON
	private void insertReplyLike(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		QnABoardDAO dao = new QnABoardDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String state = "false";
		int likeCount = 0;
		int disLikeCount = 0;
		
		try {
			int replyNum = Integer.parseInt(req.getParameter("replyNum"));
			int replyLike = Integer.parseInt(req.getParameter("replyLike"));

			ReplyDTO dto = new ReplyDTO();

			dto.setReplyNum(replyNum);
			dto.setUserId(info.getUserId());
			dto.setReplyLike(replyLike);
			
			dao.insertReplyLike(dto);

			Map<String, Integer> map = dao.countReplyLike(replyNum);

			if (map.containsKey("likeCount")) {
				likeCount = map.get("likeCount");
			}

			if (map.containsKey("disLikeCount")) {
				disLikeCount = map.get("disLikeCount");
			}

			state = "true";
		} catch (SQLException e) {
			if(e.getErrorCode() == 1) {
				state = "liked";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject job = new JSONObject();
		job.put("state", state);
		job.put("likeCount", likeCount);
		job.put("disLikeCount", disLikeCount);

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}

	// 댓글 좋아요 / 싫어요 개수 - AJAX:JSON
	private void countReplyLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		QnABoardDAO dao = new QnABoardDAO();

		int likeCount = 0;
		int disLikeCount = 0;

		try {
			int replyNum = Integer.parseInt(req.getParameter("replyNum"));
			Map<String, Integer> map = dao.countReplyLike(replyNum);

			if (map.containsKey("likeCount")) {
				likeCount = map.get("likeCount");
			}

			if (map.containsKey("disLikeCount")) {
				disLikeCount = map.get("disLikeCount");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject job = new JSONObject();
		job.put("likeCount", likeCount);
		job.put("disLikeCount", disLikeCount);

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}

	// 답글 저장 - AJAX:JSON , 답글 주소를 insertReply.do로 설정 하면 필요 없음
	private void insertReplyAnswer(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		insertReply(req, resp);
	}

	// 리플의 답글 리스트 - AJAX:TEXT
	private void listReplyAnswer(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		QnABoardDAO dao = new QnABoardDAO();

		try {
			int answer = Integer.parseInt(req.getParameter("answer"));
			
			List<ReplyDTO> listReplyAnswer = dao.listReplyAnswer(answer);
			
			// 엔터를 <br>로
			for (ReplyDTO dto : listReplyAnswer) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}

			req.setAttribute("listReplyAnswer", listReplyAnswer);

			forward(req, resp, "/WEB-INF/views/qnaboard/listReplyAnswer.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendError(400);

	}

	// 리플 답글 삭제 - AJAX:JSON
	private void deleteReplyAnswer(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		deleteReply(req, resp);
	}

	// 리플의 답글 개수 - AJAX:JSON
	private void countReplyAnswer(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		QnABoardDAO dao = new QnABoardDAO();
		int count = 0;

		try {
			int answer = Integer.parseInt(req.getParameter("answer"));
			count = dao.dataCountReplyAnswer(answer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject job = new JSONObject();
		job.put("count", count);

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());

	}
}
