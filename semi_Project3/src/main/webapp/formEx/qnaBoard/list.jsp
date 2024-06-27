<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp" />
<style type="text/css">
.table-list thead>tr:first-child {
	background: #fff;
}

.table-list th, .table-list td {
	text-align: center;
}

.table-list .left {
	text-align: left;
	padding-left: 5px;
}

.table-list .left .newImg {
	width: 15px;
	text-align: center;
	height: 15px;
	line-height: 15px;
}

.table-list .left .contentImg img{
	width: 40px;
	text-align: center;
	height: 30px;
	line-height: 15px;
}

.table-list .num {
	width: 60px;
	color: #787878;
}

.table-list .categoryType {
	width: 60px;
	color: #787878;
}

.table-list .subject {
	color: #787878;
}

.table-list .name {
	width: 100px;
	color: #787878;
}

.table-list .date {
	width: 100px;
	color: #787878;
}

.table-list .hit {
	width: 70px;
	color: #787878;
}

.table-list .contentLine:nth-child(2n){
	background: #f7f7f7
}

.table-list .contentLine:hover{
	background: #B2EBF4;
	transition: ease 1s;
}

.body-container {
	margin: 0 auto 15px;
	width: 1000px;
	min-height: 450px;
}

.body-container:before, .body-container:after {
	content: "";
	display: block;
	clear: both;
}

.body-title {
	color: #424951;
	padding-top: 25px;
	padding-bottom: 5px;
	margin: 0 0 25px 0;
	border-bottom: 1px solid #ddd;
}

.body-title h3 {
	font-size: 23px;
	min-width: 300px;
	font-family: "Malgun Gothic", "맑은 고딕", NanumGothic, 나눔고딕, 돋움, sans-serif;
	font-weight: bold;
	margin: 0 0 -5px 0;
	padding-bottom: 5px;
	display: inline-block;
	border-bottom: 3px solid #424951;
}

/* 카테고리 */
ul li, ol li {
	list-style: none
}

.category {
	border-left: 1px solid #e7e7e7;
	border-bottom: 1px solid
}

.category li {
	display: table-cell
}

.category li.on {
	position: relative
}

.category li.on:before {
	position: absolute;
	content: '';
	background-color: #fff;
	width: 00%;
	height: 1px;
	bottom: -1px;
	left: 0px
}

.category li a {
	text-decoration: none;
	cursor: pointer;
	color: #666;
	display: block;
	position: relative;
	border-top: 1px solid #e7e7e7;
	text-align: center;
	padding: 9px 20px
}

.category li a:after {
	content: '';
	display: block;
	position: absolute;
	top: 0;
	right: 0;
	bottom: 0;
	width: 1px;
	background: #e6e3df
}

.category li.on a, .category li:hover a {
	border-bottom-color: transparent;
	border-top: 1px solid #444
}

.category li.on a:after, .category li:hover a:after {
	content: '';
	display: block;
	position: absolute;
	top: 0;
	right: 0;
	bottom: -1px;
	width: 1px;
	background: #444
}

.category li.on a:before, .category li:hover a:before {
	content: '';
	display: block;
	position: absolute;
	top: 0;
	left: 0;
	bottom: -1px;
	width: 1px;
	background: #444
}
</style>
<script type="text/javascript">
	function searchList() {
		const f = document.searchForm;
		f.submit();
	}
</script>
</head>
<body>

	<header>
		<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
	</header>

	<main>
		<div class="body-container" style="width: 1000px;">
			<div class="body-title">
				<h3>
					<i class="fas fa-chalkboard-teacher"></i> 질문과 답변
				</h3>
			</div>


			<ul class="category">
				<li class="on"><a href="#">전체</a></li>
				<li><a href="#">일반</a></li>
				<li><a href="#">게시판</a></li>
				<li><a href="#">갤러리</a></li>
				<li><a href="#">주문/결제</a></li>
				<li><a href="#">반품/교환/환불</a></li>
				<li><a href="#">배송문의</a></li>
				<li><a href="#">회원서비스</a></li>
			</ul>

			<table class="table">
				<tr>
					<td width="50%">${dataCount}개(${page}/${total_page} 페이지)</td>
					<td align="right">&nbsp;</td>
				</tr>
			</table>

			<table class="table table-border table-list">
				<thead>
					<tr>
						<th class="num">번호</th>
						<th class="categoryType">카테고리</th>
						<th class="subject">제목</th>
						<th class="name">작성자</th>
						<th class="date">작성일</th>
						<th class="hit">조회수</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="dto" items="${list}">
						<tr class="contentLine">
							<td>${dto.listNum}</td>
							<td>${dto.categoryType}</td>
							<td class="left">
							<span class="contentImg"><img src="${pageContext.request.contextPath }/resource/images/image.png"></span>
							<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach> 
							<c:if test="${dto.depth!=0}">└&nbsp;</c:if> 
							<a href="${articleUrl}&boardNum=${dto.boardNum}">${dto.subject}</a>
								 <c:if test="${dto.gap<1 }">
									<img src="${pageContext.request.contextPath }/resource/images/list_heart.png">
								</c:if>
							</td>
							<td>${dto.userName}</td>
							<td>${dto.reg_date}</td>
							<td>${dto.hitCount}</td>
						</tr>
					</c:forEach>

					<tr class="contentLine">
						<td>1</td>
						<td>일반</td>
						<td class="left">
						<span class="contentImg"><img src="${pageContext.request.contextPath }/resource/images/image.png"></span>
						<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach> 
								<c:if test="${dto.depth!=0}">└&nbsp;</c:if> 
								<a href="${articleUrl}&boardNum=${dto.boardNum}">제목입니당</a> 
								<c:if test="${dto.gap<1 }">
								<img class="newImg" src="${pageContext.request.contextPath }/resource/images/list_heart.png">
								</c:if></td>
						<td>짱구</td>
						<td>2022-04-26</td>
						<td>23</td>
					</tr>
					<tr class="contentLine">
						<td>2</td>
						<td>풍경</td>
						<td class="left">
						<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach> 
						<c:if test="${dto.depth!=0}">└&nbsp;</c:if> 
							<a href="${articleUrl}&boardNum=${dto.boardNum}">짱구네집</a> 
							<img class="newImg" src="${pageContext.request.contextPath }/resource/images/list_heart.png">
						</td>
						<td>짱구아빠</td>
						<td>2022-04-26</td>
						<td>83</td>
					</tr>
					<tr class="contentLine">
						<td>3</td>
						<td>풍경</td>
						<td class="left">
						<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach> 
						<c:if test="${dto.depth!=0}">└&nbsp;</c:if> 
							<a href="${articleUrl}&boardNum=${dto.boardNum}">짱구네집</a> 
							<img class="newImg" src="${pageContext.request.contextPath }/resource/images/list_heart.png">
						</td>
						<td>짱구아빠</td>
						<td>2022-04-26</td>
						<td>83</td>
					</tr>
					<tr class="contentLine">
						<td>4</td>
						<td>풍경</td>
						<td class="left">
						<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach> 
						<c:if test="${dto.depth!=0}">└&nbsp;</c:if> 
							<a href="${articleUrl}&boardNum=${dto.boardNum}">짱구네집</a> 
							<img class="newImg" src="${pageContext.request.contextPath }/resource/images/list_heart.png">
						</td>
						<td>짱구아빠</td>
						<td>2022-04-26</td>
						<td>83</td>
					</tr>
					<tr class="contentLine">
						<td>5</td>
						<td>풍경</td>
						<td class="left">
						<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach> 
						<c:if test="${dto.depth!=0}">└&nbsp;</c:if> 
							<a href="${articleUrl}&boardNum=${dto.boardNum}">짱구네집</a> 
							<img class="newImg" src="${pageContext.request.contextPath }/resource/images/list_heart.png">
						</td>
						<td>짱구아빠</td>
						<td>2022-04-26</td>
						<td>83</td>
					</tr>
					<tr class="contentLine">
						<td>6</td>
						<td>풍경</td>
						<td class="left">
						<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach> 
						<c:if test="${dto.depth!=0}">└&nbsp;</c:if> 
							<a href="${articleUrl}&boardNum=${dto.boardNum}">짱구네집</a> 
							<img class="newImg" src="${pageContext.request.contextPath }/resource/images/list_heart.png">
						</td>
						<td>짱구아빠</td>
						<td>2022-04-26</td>
						<td>83</td>
					</tr>
					<tr class="contentLine">
						<td>7</td>
						<td>풍경</td>
						<td class="left">
						<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach> 
						<c:if test="${dto.depth!=0}">└&nbsp;</c:if> 
							<a href="${articleUrl}&boardNum=${dto.boardNum}">짱구네집</a> 
							<img class="newImg" src="${pageContext.request.contextPath }/resource/images/list_heart.png">
						</td>
						<td>짱구아빠</td>
						<td>2022-04-26</td>
						<td>83</td>
					</tr>
					

				</tbody>
			</table>

			<div class="page-box">${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
			</div>

			<table class="table">
				<tr>
					<td width="100">
						<button type="button" class="btn"
							onclick="location.href='${pageContext.request.contextPath}/board/list.do';"
							title="새로고침">
							새로고침<i class="fa-solid fa-arrow-rotate-right"></i>
						</button>
					</td>
					<td align="center">
						<form name="searchForm"
							action="${pageContext.request.contextPath}/board/list.do"
							method="post">
							<select name="condition" class="form-select">
								<option value="all"
									${condition=="all"?"selected='selected'":"" }>제목+내용</option>
								<option value="userName"
									${condition=="userName"?"selected='selected'":"" }>작성자</option>
								<option value="reg_date"
									${condition=="reg_date"?"selected='selected'":"" }>등록일</option>
								<option value="subject"
									${condition=="subject"?"selected='selected'":"" }>제목</option>
								<option value="content"
									${condition=="content"?"selected='selected'":"" }>내용</option>
							</select> <input type="text" name="keyword" value="${keyword}"
								class="form-control">
							<button type="button" class="btn" onclick="searchList();">검색</button>
						</form>
					</td>
					<td align="right" width="100">
						<button type="button" class="btn"
							onclick="location.href='${pageContext.request.contextPath}/board/write.do';">글올리기</button>
					</td>
				</tr>
			</table>

		</div>
	</main>

	<footer>
		<jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
	</footer>

	<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp" />
</body>
</html>