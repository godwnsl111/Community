package com.buy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.member.SessionInfo;
import com.util.FileManager;
import com.util.MyUploadServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/buy/*")
public class TradebuyServlet extends MyUploadServlet{
	private static final long serialVersionUID = 1L;
	
	private String pathname;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String cp = req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if(info == null) {
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "buy";
		
		String uri = req.getRequestURI();
		
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
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		}
	}
	
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		TradebuyDAO dao = new TradebuyDAO();
		MyUtil util = new MyUtil();
		
		String cp = req.getContextPath();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			int dataCount = dao.dataCount();
			
			int rows = 15;
			int total_page = util.pageCount(rows, dataCount);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;
			
			List<TradebuyDTO> list = dao.listTradebuy(start, end);
			
			String listUrl = cp + "/buy/list.do";
			String articleUrl = cp + "/buy/article.do?page=" + current_page;
			String paging = util.paging(current_page, total_page, listUrl);
			
			req.setAttribute("list", list);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		forward(req, resp, "/WEB-INF/views/buy/list.jsp");
	}
		
		protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			// 
			req.setAttribute("mode", "write");
			forward(req, resp, "/WEB-INF/views/buy/write.jsp");
		}
		protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		TradebuyDAO dao = new TradebuyDAO();
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/buy/list.do");
			return;
		}
		try {
			TradebuyDTO dto = new TradebuyDTO();
			
			dto.setUserId(info.getUserId());
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setPrice(req.getParameter("price"));
			
			String filename = null;
			Part p = req.getPart("selectFile");
			Map<String, String> map = doFileUpload(p, pathname);
			if(map != null) {
				filename = map.get("saveFilename");
			}
			if(filename != null) {
				dto.setImageFilename(filename);
				dao.insertBuy(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp + "/buy/list.do");
	}
		protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			TradebuyDAO dao = new TradebuyDAO();
			
			String cp = req.getContextPath();
			String page = req.getParameter("page");
			
			try {
				int buyNum = Integer.parseInt(req.getParameter("buyNum"));
				
				dao.updateHitCount(buyNum);

				TradebuyDTO dto = dao.readTradebuy(buyNum);
				if (dto == null) {
					resp.sendRedirect(cp + "/buy/list.do?page=" + page);
					return;
				}

				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

				req.setAttribute("dto", dto);
				req.setAttribute("page", page);

				forward(req, resp, "/WEB-INF/views/buy/article.jsp");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}

			resp.sendRedirect(cp + "/buy/list.do?page=" + page);
		}
		

		protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			TradebuyDAO dao = new TradebuyDAO();
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");

			String cp = req.getContextPath();
			String page = req.getParameter("page");
			
			try {
				int buyNum = Integer.parseInt(req.getParameter("buyNum"));
				TradebuyDTO dto = dao.readTradebuy(buyNum);

				if (dto == null) {
					resp.sendRedirect(cp + "/buy/list.do?page=" + page);
					return;
				}

				// 게시물을 올린 사용자가 아니면
				if (!dto.getUserId().equals(info.getUserId())) {
					resp.sendRedirect(cp + "/buy/list.do?page=" + page);
					return;
				}

				req.setAttribute("dto", dto);
				req.setAttribute("page", page);

				req.setAttribute("mode", "update");

				forward(req, resp, "/WEB-INF/views/buy/write.jsp");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}

			resp.sendRedirect(cp + "/buy/list.do?page=" + page);
		}

		protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			TradebuyDAO dao = new TradebuyDAO();
			
			String cp = req.getContextPath();
			if (req.getMethod().equalsIgnoreCase("GET")) {
				resp.sendRedirect(cp + "/buy/list.do");
				return;
			}

			String page = req.getParameter("page");

			try {
				TradebuyDTO dto = new TradebuyDTO();
				
				dto.setBuyNum(Integer.parseInt(req.getParameter("buyNum")));
				dto.setSubject(req.getParameter("subject"));
				dto.setPrice(req.getParameter("price"));
				dto.setContent(req.getParameter("content"));

				String imageFilename = req.getParameter("imageFilename");
				dto.setImageFilename(imageFilename);

				Part p = req.getPart("selectFile");
				Map<String, String> map = doFileUpload(p, pathname);
				if (map != null) { // 이미지 파일을 업로드 한 경우
					String filename = map.get("saveFilename");
					// 기존 이미지 파일 지우기
					FileManager.doFiledelete(pathname, imageFilename);
					dto.setImageFilename(filename);
				}

				dao.updatetrade(dto);
				
				resp.sendRedirect(cp + "/buy/article.do?buyNum="+dto.getBuyNum()+"&page=" + page);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
			resp.sendRedirect(cp + "/buy/list.do?page=" + page);

		}

		protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			TradebuyDAO dao = new TradebuyDAO();
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");

			String cp = req.getContextPath();
			String page = req.getParameter("page");
			
			try {
				int buyNum = Integer.parseInt(req.getParameter("buyNum"));
				
				TradebuyDTO dto = dao.readTradebuy(buyNum);
				if (dto == null) {
					resp.sendRedirect(cp + "/buy/list.do?page=" + page);
					return;
				}
				
				if (!dto.getUserId().equals(info.getUserId()) && !info.getUserId().equals("admin")) {
					resp.sendRedirect(cp + "/buy/list.do?page=" + page);
					return;
				}
				
				FileManager.doFiledelete(pathname, dto.getImageFilename());
				
				dao.deleteTradebuy(buyNum);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			resp.sendRedirect(cp + "/buy/list.do?page=" + page);
		
		}
	

}
