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
.body-title11 {
font-size: 24px;
}
.table-article tr > td {
	padding-left: 5px; padding-right: 5px;
}
.table-article .img {
	max-width:100%; height:auto; resize:both;
}
</style>
<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deletePhoto() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "tradeNum=${dto.tradeNum}&page=${page}";
		    let url = "${pageContext.request.contextPath}/sell/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="body-container" style="width: 1100px;">
		<div class="body-title11">
			<h3><i class="fa-solid fa-basket-shopping"></i> 팝니다 </h3>
		</div>
        
		<table class="table table-border table-article">
			<thead>
				<tr>
					<td colspan="2" align="center">
						${dto.subject}
					</td>
				</tr>
			</thead>
			
			<tbody>
				<tr>
					<td width="50%">
						작성자 : ${dto.userNickName}
					</td>
					<td align="right">
						${dto.reg_date}
					</td>
				</tr>
				<tr>
					<td width="50%">
						가격 : ${dto.price}
					</td>
					<td align="right">
						조회수 : ${dto.hitCount}
					</td>					
				</tr>
				
				<tr style="border-bottom: none;">
					<td colspan="2" style="padding-bottom: 0;">
						<img src="${pageContext.request.contextPath}/uploads/sell/${dto.imageFilename}" class="img">
					</td>
				</tr>
	
				<tr>
					<td colspan="2">
						${dto.content}
					</td>
				</tr>
			</tbody>
		</table>
		
		<table class="table">
			<tr>
				<td width="50%">
					<c:choose>
						<c:when test="${sessionScope.member.userId==dto.userId}">
							<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/sell/update.do?tradeNum=${dto.tradeNum}&page=${page}';">수정</button>
						</c:when>
						<c:otherwise>
							<button type="button" class="btn" disabled="disabled">수정</button>
						</c:otherwise>
					</c:choose>
			    	
					<c:choose>
			    		<c:when test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
			    			<button type="button" class="btn" onclick="deletePhoto();">삭제</button>
			    		</c:when>
			    		<c:otherwise>
			    			<button type="button" class="btn" disabled="disabled">삭제</button>
			    		</c:otherwise>
			    	</c:choose>
				</td>
				<td align="right">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/sell/list.do?page=${page}';">리스트</button>
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