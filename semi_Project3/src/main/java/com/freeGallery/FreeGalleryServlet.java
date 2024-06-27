package com.freeGallery;

import java.io.File;
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
import com.util.FileManager;
import com.util.MyUploadServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/freeGallery/*")
public class FreeGalleryServlet extends MyUploadServlet {
	private static final long serialVersionUID = 1L;
	private String pathname;
	
	
	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();
		
		HttpSession session = req.getSession();
		// 이미지를 저장할 경로(pathname)
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "freeGallery";
		
		
		// uri에 따른 작업 분류
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
		} else if (uri.indexOf("deleteFile.do") != -1) {
			deleteFile(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if (uri.indexOf("insertFreeGalLike.do") != -1) {
			// 게시물 공감 저장
			insertFreeGalLike(req, resp);
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
		// 자유 갤러리 게시물 리스트
		FreeGalleryDAO dao = new FreeGalleryDAO();
		MyUtil util = new MyUtil();

		String cp = req.getContextPath();
		
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}
			
			// 검색
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String category = req.getParameter("category");
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			
			if (category == null) {
				category = "all";
			}

			// GET 방식인 경우 디코딩
			if (req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}
			
			
			// 전체데이터 개수
			int dataCount;
			if (keyword.length() == 0 && category.equals("all")) { // 조건 x, 카테고리 x
				dataCount = dao.dataCount();
			} else if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				dataCount = dao.dataCount(category);
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				dataCount = dao.dataCount(condition, keyword);
			} else { // 조건 o, 카테고리 o
				dataCount = dao.dataCount(condition, keyword, category);
			}

			// 전체페이지수
			int rows = 12;
			int total_page = util.pageCount(rows, dataCount);
			if (current_page > total_page) {
				current_page = total_page;
			}
			
			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;
			
			// 게시물 가져오기
			List<FreeGalleryDTO> list = null;
			if (keyword.length() == 0 && category.equals("all")) { // 조건 x, 카테고리 설정 x
				list = dao.listFreeGallery(start, end);
			} else if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				list = dao.listFreeGallery(start, end, category);
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				list = dao.listFreeGallery(start, end, condition, keyword);
			} else { // 조건 o, 카테고리 o
				list = dao.listFreeGallery(start, end, condition, keyword, category);
			}
			
			String query = "";
			
			if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				query += "category=" + category;
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				query += "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			} else if(keyword.length() != 0 && !category.equals("all")) { // 조건 o, 카테고리 o
				query += "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8") + "&category=" + category;
			}
			
			
			
			// 페이징 처리
			String listUrl = cp + "/freeGallery/list.do";
			String articleUrl = cp + "/freeGallery/article.do?page=" + current_page;
			if (query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = util.paging(current_page, total_page, listUrl);
			
			// 포워딩할 list.jsp에 넘길 값
			req.setAttribute("list", list);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			req.setAttribute("category", category);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		forward(req, resp, "/WEB-INF/views/freeGallery/list.jsp");
	}
	
	private void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 이미지 올리기 폼(글 쓰기 폼)
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/freeGallery/write.jsp");
	}
	
	private void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 글 저장(완료)
		FreeGalleryDAO dao = new FreeGalleryDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/freeGallery/list.do");
			return;
		}
		
		try {
			FreeGalleryDTO dto = new FreeGalleryDTO();
			
			dto.setUserId(info.getUserId());
			dto.setCategory(req.getParameter("category"));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			// 파일 insert
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if (map != null) {
				String[] saveFiles = map.get("saveFilenames");
				dto.setImageFiles(saveFiles);
			}
			
			
			dao.insertFreeGallery(dto);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp + "/freeGallery/list.do");
	}
	
	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 글 보기
		FreeGalleryDAO dao = new FreeGalleryDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String category = req.getParameter("category");
			
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
			
			if (category == null) {
				category = "all";
			}
			
			if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				query += "&category=" + category;
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
			} else if(keyword.length() != 0 && !category.equals("all")) { // 조건 o, 카테고리 o
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8") + "&category=" + category;
			}
			
			// 조회수 증가
			dao.updateHitCount(num);
			
			// 게시물 가져오기
			FreeGalleryDTO dto = dao.readFreeGal(num);
			if (dto == null) { // 게시물이 없으면 다시 리스트로
				resp.sendRedirect(cp + "/freeGallery/list.do?" + query);
				return;
			}
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			// 로그인 유저의 게시글 공감 유무
			boolean isUserLike = dao.isUserFreeGalLike(num, info.getUserId()); // 좋아요가 된 상태면 true, 아니면 false
			
			
			// 이전글 다음글
			FreeGalleryDTO preReadDto = dao.preReadGallery(dto.getNum(), condition, keyword, category);
			FreeGalleryDTO nextReadDto = dao.nextReadGallery(dto.getNum(), condition, keyword, category);
			
			// 파일 가져오기
			List<FreeGalleryDTO> listFile = dao.listFreeGalFile(num);
			
			// JSP로 전달할 속성
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("query", query);
			req.setAttribute("listFile", listFile);
			
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);
			
			req.setAttribute("isUserLike", isUserLike);
			
			// 포워딩
			forward(req, resp, "/WEB-INF/views/freeGallery/article.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp + "/freeGallery/list.do?" + query);
	}
	
	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 글 수정 폼
		FreeGalleryDAO dao = new FreeGalleryDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();

		String page = req.getParameter("page");
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			FreeGalleryDTO dto = dao.readFreeGal(num);
			
			if (dto == null) {
				resp.sendRedirect(cp + "/freeGallery/list.do?page=" + page);
				return;
			}
			
			// 게시물을 올린 사용자가 아니면
			if (!dto.getUserId().equals(info.getUserId())) {
				resp.sendRedirect(cp + "/freeGallery/list.do?page=" + page);
				return;
			}
			
			List<FreeGalleryDTO> listFile = dao.listFreeGalFile(num);
			
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("listFile", listFile);

			req.setAttribute("mode", "update");
			
			forward(req, resp, "/WEB-INF/views/freeGallery/write.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp + "/freeGallery/list.do?page=" + page);
	}
	
	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 게시글 수정 완료
		FreeGalleryDAO dao = new FreeGalleryDAO();
		
		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/freeGallery/list.do");
			return;
		}
		
		String page = req.getParameter("page");
		
		try {
			FreeGalleryDTO dto = new FreeGalleryDTO();
			
			dto.setNum(Integer.parseInt(req.getParameter("num")));
			dto.setCategory(req.getParameter("category"));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if (map != null) {
				String[] saveFiles = map.get("saveFilenames");
				dto.setImageFiles(saveFiles);
			}
			
			dao.updateFreeGallery(dto);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp + "/freeGallery/list.do?page=" + page);

	}
	
	private void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 파일 삭제(수정에서만)
		FreeGalleryDAO dao = new FreeGalleryDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();

		String page = req.getParameter("page");
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			int fileNum = Integer.parseInt(req.getParameter("fileNum"));
			
			FreeGalleryDTO dto = dao.readFreeGal(num);
			
			if(dto == null) {
				resp.sendRedirect(cp + "/freeGallery/list.do?page=" + page);
				return;
			}
			
			if (!info.getUserId().equals(dto.getUserId())) {
				resp.sendRedirect(cp + "/freeGallery/list.do?page=" + page);
				return;
			}
			
			FreeGalleryDTO vo = dao.readFreeGalFile(fileNum);
			if(vo != null) {
				FileManager.doFiledelete(pathname, vo.getImageFilename());
				
				dao.deleteFreeGalFile("one", fileNum);
			}
			
			resp.sendRedirect(cp + "/freeGallery/update.do?num=" + num + "&page=" + page);
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/freeGallery/list.do?page=" + page);
		
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 자유 갤러리 게시글 삭제
		FreeGalleryDAO dao = new FreeGalleryDAO();
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			

			int num = Integer.parseInt(req.getParameter("num"));
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String category = req.getParameter("category");
			
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
			
			if (category == null) {
				category = "all";
			}
			
			if(keyword.length() == 0 && !category.equals("all")) { // 조건 x, 카테고리 설정 o
				query += "&category=" + category;
			} else if(keyword.length() != 0 && category.equals("all")) { // 조건 o, 카테고리 x
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			} else if(keyword.length() != 0 && !category.equals("all")) { // 조건 o, 카테고리 o
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8") + "&category=" + category;
			}
			
			FreeGalleryDTO dto = dao.readFreeGal(num);
			if (dto == null) {
				resp.sendRedirect(cp + "/freeGallery/list.do?" + query);
				return;
			}
			
			// 서버에서 이미지 파일 지우기
			List<FreeGalleryDTO> listFile = dao.listFreeGalFile(num);
			for(FreeGalleryDTO vo : listFile) {
				FileManager.doFiledelete(pathname, vo.getImageFilename());
			}
			
			// freeGalFile 테이블에서 해당 게시글 번호의 모든 행 지우기
			dao.deleteFreeGalFile("all", num);
			
			// freeGallery 테이블에서 게시글 지우기
			dao.deleteFreeGal(num, info.getUserId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/freeGallery/list.do?" + query);
	}
	
	private void insertFreeGalLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 좋아요 저장 - AJAX:JSON
		FreeGalleryDAO dao = new FreeGalleryDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String state = "false";
		int FreeGalLikeCount = 0;
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			String isLike = req.getParameter("isLike");
			if (isLike.equals("true")) {
				dao.deleteFreeGalLike(num, info.getUserId()); // 좋아요 취소
			} else {
				dao.insertFreeGalLike(num, info.getUserId()); // 좋아요
				 
			}
			
			// 게시글 좋아요 개수
			FreeGalLikeCount = dao.countFreeGalLike(num);
			state = "true";
			
		} catch (SQLException e) {
			state = "liked";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		job.put("FreeGalLikeCount", FreeGalLikeCount);
		
		resp.setContentType("text/html;charset=utf-8");

		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}
	
	
	// 리플 리스트 - AJAX:TEXT
	private void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FreeGalleryDAO dao = new FreeGalleryDAO();
		MyUtil util = new MyUtil();

		try {
			// 댓글을 리스트를 가져올 게시물 번호
			int num = Integer.parseInt(req.getParameter("num"));
			String pageNo = req.getParameter("pageNo");
			int current_page = 1;
			if (pageNo != null) {
				current_page = Integer.parseInt(pageNo);
			}

			int rows = 5;
			int total_page = 0;
			int replyCount = 0;

			replyCount = dao.dataCountReply(num);
			total_page = util.pageCount(rows, replyCount);
			if (current_page > total_page) {
				current_page = total_page;
			}

			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;

			// 리스트에 출력할 댓글
			List<ReplyDTO> listReply = dao.listReply(num, start, end);

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

			forward(req, resp, "/WEB-INF/views/freeGallery/listReply.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 문제가 있는 경우 에러 코드를 전송
		resp.sendError(400);

	}

	// 리플 또는 답글 저장 - AJAX:JSON
	private void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FreeGalleryDAO dao = new FreeGalleryDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String state = "false";

		try {
			ReplyDTO dto = new ReplyDTO();

			dto.setNum(Integer.parseInt(req.getParameter("num"))); // 아버지 번호
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
		FreeGalleryDAO dao = new FreeGalleryDAO();

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
		FreeGalleryDAO dao = new FreeGalleryDAO();

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
		FreeGalleryDAO dao = new FreeGalleryDAO();

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
		FreeGalleryDAO dao = new FreeGalleryDAO();

		try {
			int answer = Integer.parseInt(req.getParameter("answer"));
			
			List<ReplyDTO> listReplyAnswer = dao.listReplyAnswer(answer);
			
			// 엔터를 <br>로
			for (ReplyDTO dto : listReplyAnswer) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}

			req.setAttribute("listReplyAnswer", listReplyAnswer);

			forward(req, resp, "/WEB-INF/views/freeGallery/listReplyAnswer.jsp");
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
		FreeGalleryDAO dao = new FreeGalleryDAO();
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
