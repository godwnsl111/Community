package com.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buy.TradebuyDTO;
import com.freeBoard.FreeBoardDTO;
import com.freeGallery.FreeGalleryDTO;
import com.infoBoard.InfoBoardDTO;
import com.notice.NoticeDTO;
import com.qnaboard.QnABoardDTO;
import com.sell.TradeDTO;
import com.util.MyServlet;

@WebServlet("/main.do")
public class MainServlet extends MyServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String uri=req.getRequestURI();
		
		if(uri.indexOf("main.do") != -1) {
			main(req, resp);
		}
	}
	
	private void main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 리스트
		MainDAO dao = new MainDAO();


		try {
			
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());

			// 현재 날짜 전달
			req.setAttribute("date", date);
			
			
			
			
			// 자유갤러리 인기 게시물 사진 가져오기
			List<FreeGalleryDTO> imageListFreeGallery = null;
			imageListFreeGallery = dao.imageListFreeGallery();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("imageListFreeGallery", imageListFreeGallery);
			
			// QnA 게시물 가져오기
			List<QnABoardDTO> qnalist = null;
			qnalist = dao.QnAListBoard();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("qnalist", qnalist);
			
			// 자유갤러리 게시물 가져오기
			List<FreeGalleryDTO> freeGalleryList = null;
			freeGalleryList = dao.listFreeGallery();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("freeGalleryList", freeGalleryList);
			
			// 공지사항 게시물 가져오기
			List<NoticeDTO> noticeList = null;
			noticeList = dao.noticeListBoard();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("noticeList", noticeList);
			
			// 소통공간 게시물 가져오기
			List<FreeBoardDTO> freeBoardList = null;
			freeBoardList = dao.freeBoardListBoard();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("freeBoardList", freeBoardList);
			
			// 정보게시판 게시물 가져오기
			List<InfoBoardDTO> infoBoardList = null;
			infoBoardList = dao.infoBoardListInfoBoard();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("infoBoardList", infoBoardList);
			
			// 삽니다 게시물 가져오기
			List<TradebuyDTO> listTradebuy = null;
			listTradebuy = dao.listTradebuy();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("listTradebuy", listTradebuy);
			
			// 팝니다 이미지 가져오기
			List<TradeDTO> listTradeImage = null;
			listTradeImage = dao.listTradeImage();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("listTradeImage", listTradeImage);
			
			// 팝니다 게시물 가져오기
			List<TradeDTO> listTrade = null;
			listTrade = dao.listTrade();
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("listTrade", listTrade);

			

		} catch (Exception e) {
			e.printStackTrace();
		}

		// JSP로 포워딩
		forward(req, resp, "/WEB-INF/views/main/main.jsp");
	}

}
