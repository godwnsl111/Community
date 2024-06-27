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
.table-form td {
	padding: 7px 0;
}
.table-form p {
	line-height: 200%;
}
.table-form tr:first-child {
	border-top: 2px solid #212529; 
}
.table-form tr > td:first-child {
	width: 110px; text-align: center; background: #f8f8f8;
}
.table-form tr > td:nth-child(2) {
	padding-left: 10px;
}

.table-form tr:nth-child(4) > td {
	background: #fff;
}
.table-form input[type=text], .table-form input[type=file], .table-form textarea {
	width: 96%;
}

.table-form input[type=file], .table-form textarea {
	border: none;
}

</style>

<script type="text/javascript">
function sendBoard() {
    const f = document.boardForm;
	let str;
	
    str = f.subject.value.trim();
    if(!str) {
        alert("제목을 입력하세요. ");
        f.subject.focus();
        return;
    }

    str = f.content.value.trim();
    if(!str) {
        alert("내용을 입력하세요. ");
        f.content.focus();
        return;
    }

    f.action = "${pageContext.request.contextPath}/qnaboard/${mode}_ok.do";
    f.submit();
}
<c:if test="${mode=='update'}">
	function deleteFile(fileNum) {
		if(confirm('파일을 삭제하시겠습니까 ? ')) {
			let query = "num=${dto.num}&page=${page}&fileNum="+fileNum;
			let url = "${pageContext.request.contextPath}/notice/deleteFile.do?"+query;
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
	<div class="body-container" style="width: 800px;">
		<div class="body-title">
			<h3><i class="fas fa-clipboard-list"></i> QnA </h3>
		</div>
        
		<form name="boardForm" method="post" enctype="multipart/form-data">
			<table class="table table-border table-form">
				<tr>
					<td>카&nbsp;테&nbsp;고&nbsp;리</td>
					<td> 
						<select name="categoryType" class="form-select">
							<option value="">분류</option>
							<option value="all">전체</option>
							<option value="normal">일반</option>
							<option value="board">게시판</option>
							<option value="gallery">갤러리</option>
							<option value="payment">주문/결제</option>
							<option value="return">반품/교환/환불</option>
							<option value="delivery">배송문의</option>
							<option value="servie">회원서비스</option>
						</select>
					</td>
				</tr>
			
				<tr> 
					<td>작성자</td>
					<td> 
						<p>${sessionScope.member.userNickName}</p>
					</td>
				</tr>
				
				<tr> 
					<td>제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
					<td> 
						<input type="text" name="subject" maxlength="100" placeholder="제목을 입력하세요"
							class="form-control" value="${dto.subject}">
					</td>
				</tr>
				
				<tr> 
					<td colspan="2"> 
						<textarea name="content" class="form-control" placeholder="내용을 입력하세요">${dto.content}</textarea>
					</td>
				</tr>
				
				<tr>
					<td>파&nbsp;&nbsp;&nbsp;&nbsp;일</td>
					<td> 
						<input type="file" accept="image/*" name="selectFile" class="form-control" multiple="multiple">
					</td>
				</tr>
				
				<c:if test="${mode=='update'}">
					<c:forEach var="vo" items="${listFile}">
						<tr>
							<td>첨부된 파일</td>
							<td>
								<p>
									<a href="javascript:deleteFile('${vo.fileNum}');"><i class="fa fa-trash-alt"></i></a>
									${vo.originalFilename}
								</p>
							</td>
						</tr>
					</c:forEach>
				</c:if>
			</table>
				
			<table class="table">
				<tr> 
					<td align="center">
						<button type="button" class="btn" onclick="sendBoard();">${mode=="update"?"수정완료":"등록하기"}</button>
						<button type="reset" class="btn">다시입력</button>
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}';">${mode=="update"?"수정취소":"등록취소"}</button>
						<c:if test="${mode=='update'}">
							<input type="hidden" name="num" value="${dto.num}">
							<input type="hidden" name="page" value="${page}">
						</c:if>
					</td>
				</tr>
			</table>
	
		</form>

        
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>