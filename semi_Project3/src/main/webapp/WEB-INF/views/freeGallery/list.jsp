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

.body-container {
    margin: 0 auto 15px;
    width: 900px;
    min-height: 450px;
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

.grid-box {
	margin-top: 3px; margin-bottom: 5px;
	display: grid;
	/* auto-fill :  남는 공간(빈 트랙)을 그대로 유지, minmax : '최소, 최대 크기'를 정의 */
	grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
	grid-column-gap: 10px;
	grid-row-gap: 10px;
}

.grid-box .gallery_list {
	width: 224px;
    height: 289px;
	background: #f7f7f7;
	padding: 9px 9px 0 9px;
	border: 3px solid #fff; 
	cursor: pointer;
}

.gallery_list .gallery_img {width:200px; height:200px; background:#fff;}
.gallery_list .gallery_img img {width: 100%; height: 100%; cursor: pointer;}


.gallery_list .gallery_info>span {display: inline-block; margin-top: 4px;}


.gallery_list .gallery_info>span:first-child { font-size: 12px; padding-right: 1px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;}

.gallery_list .gallery_info {text-align: left; clear: left; line-height: 18px; min-height:22px; }
.gallery_list .gallery_info .gallery_title {line-height: 140%; letter-spacing: -0.5px; margin-top:7px; word-break: break-all; max-width:100%; max-height:30px; float:left; font-weight:bold; white-space:pre-line; overflow:hidden; }
.gallery_list .gallery_info .gallery_title>input {vertical-align:text-bottom; margin-right:2px;}
.gallery_list .gallery_info .gallery_info {float:left; width:10%; }
.gallery_list .gallery_info .list_name {display:inline-block; max-width:150px; overflow:hidden; text-overflow:ellipsis; color:#374273; font-size: 12px; text-decoration: none; font-family: '굴림';}

.gallery_list .gallery_data {text-align: right; font-size: 11px; float: right; color: #676767; padding-top: 1px;}
.gallery_list .gallery_vote_data {text-align: right;margin-right:4px;font-size: 9pt;float: left !important;font-family: dotum;vertical-align: middle;}
.gallery_list .gallery_vote_data img {width:17px;}
.gallery_list .gallery_icon {color:#676767;clear: both;}
.gallery_list .gallery_icon i {font-style:normal; vertical-align: super;}
.gallery_list .gallery_icon img {margin-right:3px;}
.gallery_list .gallery_like {font-size:9pt;float: right;font-family: dotum;}
.gallery_list .gallery_like img {width: 16.5px; height:14px;margin-bottom: 2px;}


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
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script type="text/javascript">
function searchList() {
	const f = document.searchForm;
	f.submit();
}
</script>
<script type="text/javascript">
$(function() {
	$("ul.category li").removeClass("on");
	switch($('input[name=category]').val()) {
		case "all" : $("ul.category li:nth-child(1)").addClass("on"); break;
		case "daily" :  $("ul.category li:nth-child(2)").addClass("on"); break;
		case "food" : $("ul.category li:nth-child(3)").addClass("on"); break;
		case "family" : $("ul.category li:nth-child(4)").addClass("on"); break;
		case "animal" : $("ul.category li:nth-child(5)").addClass("on"); break;
		case "landscape" : $("ul.category li:nth-child(6)").addClass("on"); break;
		case "nightscape" : $("ul.category li:nth-child(7)").addClass("on"); break;
		case "travel" : $("ul.category li:nth-child(8)").addClass("on"); break;
		case "authentication" : $("ul.category li:nth-child(9)").addClass("on"); break;
	}
	
	// li 태그는 value 값이 int 형만 가능
	$("ul.category li").click(function() {
		$("ul.category li").removeClass("on");
		$(this).addClass("on");
		var category = "";
		switch($(this).val()) {
			case 0 : category = "all"; break;
			case 1 : category = "daily"; break;
			case 2 : category = "food"; break;
			case 3 : category = "family"; break;
			case 4 : category = "animal"; break;
			case 5 : category = "landscape"; break;
			case 6 : category = "nightscape"; break;
			case 7 : category = "travel"; break;
			case 8 : category = "authentication"; break;
		}
		$('input[name=category]').val(category);
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
	<div class="body-container" style="width: 900px;">
		<div class="body-title">
			<h3><i class="far fa-image"></i> 자유 갤러리 </h3>
		</div>
        
		<ul class="category">
			<li value="0"><span>전체</span></li>
			<li value="1"><span>일상</span></li>
			<li value="2"><span>음식</span></li>
			<li value="3"><span>아이/가족</span></li>
			<li value="4"><span>동물</span></li>
			<li value="5"><span>풍경</span></li>
			<li value="6"><span>야경</span></li>
			<li value="7"><span>여행</span></li>
			<li value="8"><span>인증</span></li>
		</ul>
        <table class="table">
			<tr>
				<td width="50%">
					${dataCount}개 (${page}/${total_page} 페이지)
				</td>
				<td align="right">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/freeGallery/write.do';">사진올리기</button>
				</td>
			</tr>
		</table>
		
		
		<div class="grid-box">
			<c:forEach var="dto" items="${list}" varStatus="status">
				<div class="gallery_list" onclick="">
					<ul>
	            		<li class="gallery_img">
	            			<a href="${articleUrl}&num=${dto.num}">
	            				<img src="${pageContext.request.contextPath}/uploads/freeGallery/${dto.imageFilename}" onfocus="blur()" border="0">
	            			</a>        	
	            		</li>
	            		
	            		<li class="gallery_info">
		                	<span class="gallery_title">
		                		<a href=""><font class="thumb_list_title">${dto.subject}</font></a>
		                	</span>
	            		</li>
	            		
			            <li class="gallery_info">
			            	<span>
			            		<a href="#" onclick="return false">
			            			<font class="list_name">${dto.userNickName}</font></a>
			            	</span>
			                <span class="gallery_data ">${dto.reg_date}</span>
			            </li>
			            
			            
			            <li class="gallery_icon">
			            	<span class="gallery_vote_data">
			            		<img class="imgcss" src="${pageContext.request.contextPath}/resource/images/answer.png" alt="comment">
			            		<i>${dto.replyCount}</i></span>
			            	<span class="gallery_like">
			            		<img src="${pageContext.request.contextPath}/resource/images/list_heart.png" alt="추천" title="추천">
			            		<i>${dto.likeCount}</i>
			            	</span>
			            </li>
	        		</ul>			
				</div>
			</c:forEach>
		</div>
		
		
		<div class="page-box">
			${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
		</div>
		
		<table class="table">
			<tr>
				<td width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/freeGallery/list.do';">새로고침</button>
				</td>
				<td align="center">
					<form name="searchForm" action="${pageContext.request.contextPath}/freeGallery/list.do" method="post">
						<select name="condition" class="form-select">
							<option value="all" ${condition=="all" ? "selected='selected'":""}>제목+내용</option>
							<option value="userNickName" ${condition=="userNickName" ? "selected='selected'":""}>작성자</option>
							<option value="reg_date" ${condition=="reg_date" ? "selected='selected'":""}>등록일</option>
							<option value="subject" ${condition=="subject" ? "selected='selected'":""}>제목</option>
							<option value="content" ${condition=="content" ? "selected='selected'":""}>내용</option>
						</select>
						<input type="hidden" name ="category" value="${category}" >
						<input type="text" name="keyword" value="${keyword}" class="form-control">
						<button type="button" class="btn" onclick="searchList();">검색</button>
					</form>
				</td>
				<td align="right" width="100">
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