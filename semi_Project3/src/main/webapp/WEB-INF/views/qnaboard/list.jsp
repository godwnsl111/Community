<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
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

.table-list .left img {
	width: 15px;
	text-align: center;
	height: 15px;
	line-height: 15px;
}

.table-list .num {
	width: 60px;
	color: #787878;
}

.table-list .categoryType {
	width: 160px;
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

ul li, ol li {list-style: none}
.category {border-left: 1px solid #e7e7e7;border-bottom: 1px solid}
.category li {display: table-cell}
.category li.on {position:relative}
.category li.on:before {
	position: absolute;
	content: '';
	background-color: #fff;
	width: 00%;
	height: 1px;
	bottom: -1px;
	left: 0px
}
.category li span {
	text-decoration: none;
    cursor: pointer;
    color: #666;
    
	display: block;
	position: relative;
	border-top: 1px solid #e7e7e7;
	text-align: center;
	padding: 9px 20px
	
}
.category li span:after { 
	content:''; 
	display:block; 
	position:absolute; 
	top:0; 
	right:0; 
	bottom:0; 
	width:1px; 
	background:#e6e3df
}
.category li.on span, .category li:hover span {
	border-bottom-color:transparent; 
	border-top:1px solid #444;
}
.category li.on span:after, .category li:hover span:after { 
	content:''; 
	display:block; 
	position:absolute; 
	top:0; 
	right:0; 
	bottom:-1px; 
	width:1px; 
	background:#444
}
.category li.on span:before, .category li:hover span:before { 
	content:''; 
	display:block; 
	position:absolute; 
	top:0; 
	left:0;
	bottom:-1px; 
	width:1px; 
	background:#444
}
</style>
<script type="text/javascript">
function searchList() {
	const f = document.searchForm;
	f.submit();
}
</script>

<script type="text/javascript">
$(function() {
	$("ul.category li").removeClass("on");
	switch($('input[name=categoryType]').val()) {  
		case "all" : $("ul.category li:nth-child(1)").addClass("on"); break;
		case "일반" :  $("ul.category li:nth-child(2)").addClass("on"); break;
		case "게시판" : $("ul.category li:nth-child(3)").addClass("on"); break;
		case "갤러리" : $("ul.category li:nth-child(4)").addClass("on"); break;
		case "주문/결제" : $("ul.category li:nth-child(5)").addClass("on"); break;
		case "반품/교환/환불" : $("ul.category li:nth-child(6)").addClass("on"); break;
		case "배송문의" : $("ul.category li:nth-child(7)").addClass("on"); break;
		case "회원서비스" : $("ul.category li:nth-child(8)").addClass("on"); break;
	}
	
	
	// li 태그는 value 값이 int 형만 가능
	$("ul.category li").click(function() {
		$("ul.category li").removeClass("on");
		$(this).addClass("on");
		var category = "";
		switch($(this).val()) {
			case 0 : category = "all"; break;
			case 1 : category = "일반"; break;
			case 2 : category = "게시판"; break;
			case 3 : category = "갤러리"; break;
			case 4 : category = "주문/결제"; break;
			case 5 : category = "반품/교환/환불"; break;
			case 6 : category = "배송문의"; break;
			case 7 : category = "회원서비스"; break;
		}
		$('input[name=categoryType]').val(category);
		$('form[name=searchForm] .btn').trigger("click");
	});
	
	
	
});

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
				<li value="0"><span >전체</span></li>
				<li value="1"><span >일반</span></li>
				<li value="2"><span >게시판</span></li>
				<li value="3"><span >갤러리</span></li>
				<li value="4"><span >주문/결제</span></li>
				<li value="5"><span>반품/교환/환불</span></li>
				<li value="6"><span>배송문의</span></li>
				<li value="7"><span>회원서비스</span></li>
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
							<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach> 
							<c:if test="${dto.depth!=0}">└&nbsp;</c:if> 
							<a href="${articleUrl}&boardNum=${dto.boardNum}">${dto.subject}</a>
								 <c:if test="{dto.gap<1 }">
									<img src="${pageContext.request.contextPath }/resource/images/list_heart.png">
								</c:if>
							</td>
							<td>${dto.userNickName}</td>
							<td>${dto.reg_date}</td>
							<td>${dto.hitCount}</td>
						</tr>
					</c:forEach>

				</tbody>
			</table>

			<div class="page-box">${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
			</div>

			<table class="table">
				<tr>
					<td width="100">
						<button type="button" class="btn"
							onclick="location.href='${pageContext.request.contextPath}/qnaboard/list.do';"
							title="새로고침">
							새로고침<i class="fa-solid fa-arrow-rotate-right"></i>
						</button>
					</td>
					<td align="center">
						<form name="searchForm"
							action="${pageContext.request.contextPath}/qnaboard/list.do"
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
							</select> 
							<input type="text" name="keyword" value="${keyword}" class="form-control">
							<input type="hidden" name="categoryType" value="${categoryType}">
							<button type="button" class="btn" onclick="searchList();">검색</button>
						</form>
					</td>
					<td align="right" width="100">
						<button type="button" class="btn"
							onclick="location.href='${pageContext.request.contextPath}/qnaboard/write.do';">글올리기</button>
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