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


.img-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, 65px);
	grid-gap: 5px;
}

.img-grid .item {
    object-fit: cover; /* 가로세로 비율은 유지하면서 컨테이너에 꽉 차도록 설정 */
    width: 65px;
    height: 65px;
	cursor: pointer;
}


.img-box {
	max-width: 600px;
	padding: 5px;
	box-sizing: border-box;
	display: flex; /* 자손요소를 flexbox로 변경 */
	flex-direction: row; /* 정방향 수평나열 */
	flex-wrap: nowrap;
	overflow-x: auto;
}
.img-box img {
	width: 37px; height: 37px;
	margin-right: 5px;
	flex: 0 0 auto;
	cursor: pointer;
}

</style>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script type="text/javascript">
function sendOk() {
    const f = document.freeGalleryForm;
	let str;
	
	str = f.category.value.trim();
    if(!str) {
        alert("카테고리를 선택해주세요. ");
        f.category.focus();
        return;
    }
    
    
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
    
    
    let mode = "${mode}";
    if( (mode === "write") && (!f.selectFile.value) ) {
        alert("한 개 이상의 이미지를 등록해주세요.");
        f.selectFile.focus();
        return;
    }
    
    

    f.action = "${pageContext.request.contextPath}/freeGallery/${mode}_ok.do";
    f.submit();
}

// 업데이트 모드일 때 이미지 삭제
<c:if test="${mode=='update'}">
	function deleteFile(fileNum) {
		if(! confirm("이미지를 삭제 하시겠습니까 ?")) {
			return;
		}
		
		let query = "num=${dto.num}&fileNum=" + fileNum + "&page=${page}";
		let url = "${pageContext.request.contextPath}/freeGallery/deleteFile.do?" + query;
		location.href = url;
	}
</c:if>

// 이미지 파일 선택
$(function() {
	$("body").on("click", ".form .img-add", function() {
		$("form input[name=selectFile]").trigger("click");
	});
	
	// 파일 선택창에서 수정이 발생했을 때
	$("form input[name=selectFile]").change(function() {
		
		
		if(! this.files){
			return false;
		}
		
		const fileArr = Array.from(this.files); // 유사배열을 배열로 변환
		
		fileArr.forEach((file, index)=>{
			const reader = new FileReader();
			let $img = $("<img>", {class:"item img-item"});
			$img.attr("data-filename", file.name);
			reader.onload = e => {
				$img.attr("src", e.target.result);
			};
			reader.readAsDataURL(file);
			$(".img-grid").append($img);
		});
	});
	
	
	
	// 선택한 이미지 파일 취소
	$("body").on("click", ".form .img-item", function() {
		if(! confirm("선택한 파일을 취소하시겠습니까 ? ")){
			return false;
		}
		
		let selectFiles = document.freeGalleryForm.selectFile.files;
		const fileArr = Array.from(selectFiles);
		let filename = $(this).attr("data-filename");
		
		for(let i=0; i<fileArr.length; i++){
			if(filename === fileArr[i].name){
				fileArr.splice(i, 1); // 배열의 내용 삭제
				break;
			}
		}
		
		let dt = new DataTransfer(); // Drag&Drop 되는 대상 Data를 담는 역할을 한다.
		for(file of fileArr) {
			dt.items.add(file);
		}
		document.freeGalleryForm.selectFile.files = dt.files;
		
		$(this).remove();
		
	});
	
	
});
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="body-container" style="width: 800px;">
		<div class="body-title">
			<h3><i class="fas fa-clipboard-list"></i> 자유 갤러리 </h3>
		</div>
        
		<form name="freeGalleryForm" method="post" enctype="multipart/form-data">
			<table class="table table-border table-form">
				<tr>
					<td>카&nbsp;테&nbsp;고&nbsp;리</td>
					<td> 
						<select name="category" class="form-select">
							<option value="" >분류</option>
							<option value="daily" ${dto.category == "daily"?"selected='selected'":""}>일상</option>
							<option value="food" ${dto.category == "food"?"selected='selected'":""}>음식</option>
							<option value="family" ${dto.category == "family"?"selected='selected'":""}>아이/가족</option>
							<option value="animal" ${dto.category == "animal"?"selected='selected'":""}>동물</option>
							<option value="landscape" ${dto.category == "landscape"?"selected='selected'":""}>풍경</option>
							<option value="nightscape" ${dto.category == "nightscape"?"selected='selected'":""}>아경</option>
							<option value="travel" ${dto.category == "travel"?"selected='selected'":""}>여행</option>
							<option value="authentication" ${dto.category == "authentication"?"selected='selected'":""}>인증</option>
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
						<div class="form">
							<div class="img-grid"><img class="item img-add" src="${pageContext.request.contextPath}/resource/images/add_photo.png">
							<c:if test="${mode=='update'}">
								<c:forEach var="vo" items="${listFile}">
									<img class="item" src="${pageContext.request.contextPath}/uploads/freeGallery/${vo.imageFilename}"
										onclick="deleteFile('${vo.fileNum}');">
								</c:forEach>
							</c:if>
							</div>
							<input type="file" name="selectFile" accept="image/*" multiple="multiple" style="display: none;">
						</div>
					</td>
				</tr>

			</table>
				
			<table class="table">
				<tr> 
					<td align="center">
						<button type="button" class="btn" onclick="sendOk();">${mode=="update"?"수정완료":"등록하기"}</button>
						<button type="reset" class="btn">다시입력</button>
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/freeGallery/list.do';">${mode=="update"?"수정취소":"등록취소"}</button>
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