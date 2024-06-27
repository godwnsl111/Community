<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"> 
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Notice</title>
<link rel="icon" href="data:;base64,iVBORw0KGgo=">
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">

.page-box {
	clear: both;
	padding: 20px 0;
	text-align: center;
}
.paginate {
	clear: both;
	text-align: center;
	white-space: nowrap;
	font-size: 14px;	
}
.paginate a {
	border: 1px solid #ccc;
	color: #000;
	font-weight: 600;
	text-decoration: none;
	padding: 3px 7px;
	margin-left: 3px;
	vertical-align: middle;
}
.paginate a:hover, .paginate a:active {
	color: #FF5E00;
}
.paginate span {
	border: 1px solid #FFBB00;
	color: #FF5E00;
	font-weight: 600;
	padding: 3px 7px;
	margin-left: 3px;
	vertical-align: middle;
}
.paginate :first-child {
	margin-left: 0;
}


.body-container {
    margin: 0 auto 15px;
    width: 700px;
    min-height: 450px;
    position: relative;
}

.body-title {
    color: #424951;
    padding-top: 25px;
    padding-bottom: 5px;
    margin: 0 0 25px 0;
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

.board_write .cont textarea {
    display: block;
    width: 100%;
    height: 300px;
    padding: 15px;
    box-sizing: border-box;
    border: 0;
    resize: vertical;
}

.table-list thead > tr:first-child{ 
	background: #fff;
}
.table-list th, .table-list td {
	text-align: center;
}
.table-list .left {
	text-align: left; padding-left: 5px; 
}

.table-list .num {
	width: 60px; color: #787878;
}
.table-list .subject {
	color: #787878;
}
.table-list .name {
	width: 100px; color: #787878;
}
.table-list .date {
	width: 100px; color: #787878;
}
.table-list .hit {
	width: 70px; color: #787878;
}
.table-list .file {
	width: 50px; color: #787878;
}
.table-list .notice {
	display: inline-block; padding: 1px 3px; color: #fff;
}

.btn {
    text-align: center;
    display: inline-block;
    min-width: 50px;
    padding: 5px;
    border: 1px solid #000;
    border-radius: 2px;
    font-size: inherit;
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
		<div class="body-container" style="width: 700px;" >
		
		<div class="body-title">
			<h1> 공지사항 </h1>
			<br>
			<p> 필독을 권유합니다.</p>
		</div>
      
		<table class="table table-border table-list">
			<thead>
				<tr>
					<th class="num">번호</th>
					<th class="subject">제목</th>
					<th class="name">글쓴이</th>
					<th class="date">작성일</th>
					<th class="hit">조회</th>
					
				</tr>
			</thead>
			
			<tbody>
			
			<!-- 공지 빨간색으로 따로 뜨는 거
				<c:forEach var="dto" items="${listNotice }">
				<tr>
					<td><span class="notice">공지</span></td>
					<td class="left"> 
						<a href="${articleUrl }&num=${dto.num }">${dto.subject }</a>
					</td>
					<td>${dto.userName }</td>
					<td>${dto.reg_date }</td>
					<td>${dto.hitCount }</td>
				</tr>
				</c:forEach>
			 -->
			 
				<c:forEach var="dto" items="${list}">
				<tr>
					<td>${dto.listNum }</td>
					<td class="left"> 
						<a href="${articleUrl }&num=${dto.num }">${dto.subject }</a>
					</td>
					<td>${dto.userName }</td>
					<td>${dto.reg_date }</td>
					<td>${dto.hitCount }</td>
				</tr>
				</c:forEach>
				
			</tbody>
			
		</table>
		
		<div class="page-box">
			${dataCount == 0? "등록된 게시물이 없습니다." : paging }
		</div>
		<table class="table">
			<!-- 페이징 : 나중에 1 2 3 4 5 이런 식으로 사각형에 숫자 담긴 것으로 변경. << >> 있음
			<tr>				
				<td width="50%">게시글 총 ${dataCount }개(${page }/${total_page }페이지)</td>
				<td align="right">&nbsp;</td>
			</tr>
			 -->
		</table>
		
		<table class="table">
			<tr>
				<td width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/list.do';">새로고침</button>
				</td>
				<td align="center">
					<form name="searchForm">
						<select name="condition" class="form-select">
							<option value="all" ${condition == "all" ? "selected='selected'":"" } >제목+내용</option>
							<option value="userName" ${condition == "userName" ? "selected='selected'":"" }>작성자</option>
							<option value="reg_date" ${condition == "reg_date" ? "selected='selected'":"" }>등록일</option>
							<option value="subject" ${condition == "subject" ? "selected='selected'":"" }>제목</option>
							<option value="content" ${condition == "content" ? "selected='selected'":"" }>내용</option>
						</select>
						<input type="text" name="keyword" value="${keyword }" class="form-control">
						<button type="button" class="btn" onclick="searchList();">검색</button>
					</form>
				</td>
				<td align="right" width="100">
					<c:if test="${sessionScope.member.userId == 'admin'}"> 
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/write.do';">글올리기</button>
					</c:if>
				</td>
			</tr>
		</table>	
		

	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>

</body>
</html>