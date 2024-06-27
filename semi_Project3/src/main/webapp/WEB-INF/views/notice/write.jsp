<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>NOTICE</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">

.body-container {
    margin: 0 auto 15px;
    width: 700px;
    min-height: 450px;
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

.table-form textarea {
    display: block;
    width: 100%;
    height: 300px;
    padding: 15px;
    box-sizing: border-box;
    border: 0px;
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

.table-form tr > td:nth-child(2) {
	padding-left: 10px;
}

.table-form input[type=text], .table-form input[type=file] {
	width: 96%;
}

.table-form td:noticecontent {
	width: 100%;
	background-color : #fff;
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

    f.action = "${pageContext.request.contextPath}/notice/${mode}_ok.do";
    f.submit();
}

<c:if test="${mode=='update'}">
function deleteFile(fileNum){
	if(confirm('파일을 삭제하시겠습니까 ? ')){
		let query = "num=${dto.num}&page=${page}&numFile="+numFile;
		let url = "${pageContext.request.contextPath}/notice/deleteFile.do?"+query;
		location.href = url;
	}
}
</c:if>

</script>

<link rel="icon" href="data:;base64,iVBORw0KGgo=">
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="body-container" style="width: 700px;">
		<div class="body-title">
			<h1> 공지사항 </h1>
			<p> 관리자만 입력 가능합니다. </p>
		</div>
		
        
		<form name="boardForm" method="post" enctype="multipart/form-data">
			<table class="table table-border table-form">
				<tr> 
					<td style="width: 110px; text-align: center; background: #fff;">제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
					<td> 
						<input type="text" name="subject" maxlength="100" class="form-control" value="${dto.subject}">
					</td>
				</tr>
				
				<tr> 
					<td style="width: 110px; text-align: center; background: #fff;">글쓴이</td>
					<td> 
						<p>${sessionScope.member.userNickName}</p>
					</td>
				</tr>
				
				<tr> 
					<td class="noticecontent" colspan="2"> 
						<textarea name="content" class="form-control" placeholder="내용 입력" >${dto.content }</textarea>
					</td>
				</tr>
				
				<tr>
					<td style="width: 110px; text-align: center; background: #fff;">첨부 파일</td>
					<td>
						<input type="file" name="selectFile" class="form-control" multiple="multiple">
					</td>
				</tr>
				
				<c:if test="${mode=='update'}">
					<c:forEach var = "vo" items="${listFile}">
						<tr>
							<td>첨부된 파일</td>
							<td>
								<p>
									<a href="javascript:deleteFile('${vo.numFile }');"><i class="fa-solid fa-alien-8bit"></i></a>
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
						<button type="button" class="btn" onclick="sendBoard();">${mode == "update"?"수정완료":"등록하기"}</button>
						<button type="reset" class="btn">다시입력</button>
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/list.do';">${mode=="update"?"수정취소":"등록취소"}</button>
						<c:if test="${mode=='update' }">
							<input type="hidden" name="num" value="${dto.num }">
							<input type="hidden" name="page" value="${page }">
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