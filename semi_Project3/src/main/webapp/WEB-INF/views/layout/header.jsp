<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<div class="header-top">
    <div class="header-left">
        <h1 class="logo"><a href="${pageContext.request.contextPath}/">Triple</a></h1>
    </div>
    
    <div class="header-right">
        <div style="text-align: right;">
        	<c:if test="${empty sessionScope.member }">
				<a href="${pageContext.request.contextPath}/member/login.do">로그인</a>
				&nbsp;|&nbsp;
				<a href="${pageContext.request.contextPath}/member/member.do">회원가입</a>
			</c:if>
			<c:if test="${not empty sessionScope.member }">
        		<span style="color:blue;">${sessionScope.member.userNickName}</span>님
        		&nbsp;|&nbsp;
				<a href="${pageContext.request.contextPath}/member/logout.do">로그아웃</a>
				&nbsp;|&nbsp;
				<a href="${pageContext.request.contextPath}/member/pwd.do?mode=update">정보수정</a>
			</c:if>
        </div>
    </div>
</div>

<div class="wrap">
	<ul id="nav" class="first">
		<li><a href="${pageContext.request.contextPath}/">홈</a></li>
		<li><a href="#">정보</a>
			<ul>
				<li><a href="${pageContext.request.contextPath}/infoBoard/list.do">정보 게시판</a></li>
				<li><a href="#">코딩 게시판</a></li>
			</ul>
		</li>
		<li><a href="#">커뮤니티</a>
			<ul>
				<li><a href="${pageContext.request.contextPath}/freeBoard/list.do">소통공간</a></li>
				<li><a href="#">커뮤니티 갤러리</a></li>
			</ul>			
		</li>
		<li><a href="#">갤러리</a>
			<ul>
				<li><a href="${pageContext.request.contextPath}/freeGallery/list.do">자유 갤러리</a></li>
				<li><a href="#">코딩갤러리</a></li>
			</ul>
		</li>
		<li><a href="#">고객센터</a>
			<ul>
				<li><a href="${pageContext.request.contextPath}/notice/list.do">공지사항</a></li>
				<li><a href="${pageContext.request.contextPath }/qnaboard/list.do">질문답변</a></li>
				<li><a href="#">이벤트</a></li>
				<li><a href="#">일정</a></li>
			</ul>
		</li>
		
		<li><a href="#">장터</a>
			<ul>
				<li><a href="${pageContext.request.contextPath}/sell/list.do">팝니다</a></li>
				<li><a href="${pageContext.request.contextPath}/buy/list.do">삽니다</a></li>
			</ul>
		</li>
	</ul>
</div>


