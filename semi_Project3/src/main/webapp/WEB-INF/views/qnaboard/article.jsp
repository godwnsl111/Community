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

.table-list .contentLine:nth-child(2n) {
	background: #f7f7f7
}

.table-list .contentLine:hover {
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

.table-article tr>td {
	padding-left: 5px;
	padding-right: 5px;
}


/* 리플 */
.ico_skin{display:block;overflow:hidden;font-size:0;line-height:0;background:url(images/ico_skin.png) 0 0 no-repeat;text-indent:-9999px}
.area-reply strong {font-size: 16px;}
.area_reply{margin-top:51px; margin-bottom: 40px;}
.area_reply .tit_reply{font-weight:normal;font-size:20px;color:#222;display:block;margin-bottom:14px}
.reply .fld_reply{border:none; padding:20px;border-top:1px solid #e8e8e8;background-color:#fafafa;position:relative}
.reply .reply_write{position:relative;padding:7px 12px;border:1px solid #e8e8e8;background-color:#fff}
.reply .tf_reply{width:100%;height:70px;border:1px solid #fff;font-size:13px;resize:none;box-sizing:border-box;color:#5c5c5c}
.reply .writer_btn{float:right;margin-top:10px}
.reply .writer_btn .btn_enter{border: 0 none; background-color: transparent; cursor: pointer; float:left;width:71px;height:36px;border-radius:18px;font-size:13px;line-height:38px;background-color:#6bacce;color:#fff}
.list_reply .thumb_profile{float:left;width:48px;height:48px;margin-right:20px;border-radius:48px;background-position:-140px -20px;margin-top:2px;}
.list_reply .img_profile{display:block;width:100%;height:100%;border-radius:48px;}
.area_reply .list_reply li{position:relative;padding:23px 0 22px 0;border-bottom:1px solid #efefef; min-height:53px}

.area_reply .list_reply li:first-child{border-top:0 none}
#tt-body-page .list_reply li:first-child{padding-top:0}
.area_reply .reply_content{overflow:hidden;display:block;}
.area_reply .tit_nickname{float:left;overflow:hidden;max-width:200px;margin-right:10px;color:#222;white-space:nowrap;text-overflow:ellipsis;font-size:16px;}
.area_reply .tit_nickname a:hover{text-decoration:none}
.area_reply .txt_date{float:left;margin-top:5px;font-size:12px;color:#a7a7a7}
.area_reply .txt_reply{display:block;padding-top:4px;font-size:13px;line-height:21px;color:#5c5c5c;clear:both}
.area_reply .list_reply li:hover .area_more,
.area_reply .list_reply li:hover .area_more .reply_layer { display:block }
.area_more .reply_layer { position:absolute; right:0px; top:25px }
.area_more .reply_layer .link_reply { margin:0 2px;font-size:12px;color:#6bacce }
.area_more .reply_layer .link_reply:hover { text-decoration: none}
.area_more .reply_layer { position: absolute; right: 0px; top: 25px;}
li {list-style: none;}

.pre {
	border-top: 1px solid lightgray;
}
/* 리플 끝 */




/* 좋아요 기능*/
@import url('https://fonts.googleapis.com/css?family=Montserrat:600&display=swap');
.heart-btn{
   	margin-top: 30px;
	position: relative;
	width: 100px;
}
.content{
  padding: 4px 6px;
  display: flex;
  border: 2px solid #eae2e1;
  border-radius: 5px;
  cursor: pointer;
}

.heart{
  position: absolute;
  background: url("${pageContext.request.contextPath}/resource/images/heart.png") no-repeat;
  background-position: left;
  background-size: 2900%;
  height: 50px;
  width: 50px;
  top: 50%;
  left: 15%;
  transform: translate(-50%,-50%);
}

.text{
  font-size: 17px;
  margin-left: 20px;
  color: grey;
  font-family: 'Montserrat',sans-serif;
}
.numb{
  font-size: 15px;
  margin-left: 5px;
  font-weight: 600;
  color: #9C9496;
  font-family: sans-serif;
}

.content.heart-active{
  border-color: #f9b9c4;
}
.numb.heart-active{
  color: #000;
}
.text.heart-active{
  color: #000;
}
.heart.heart-active{
  animation: animate .8s steps(28) 1;
  background-position: right;
}

@keyframes animate {
  0%{
    background-position: left;
  }
  100%{
    background-position: right;
  }

/* 좋아요 끝 */



</style>
<script type="text/javascript">
	<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
		if (confirm("게시글을 삭제 하시 겠습니까 ? ")) {
			let query = "boardNum=${dto.boardNum}&${query}";
			let url = "${pageContext.request.contextPath}/qnaboard/delete.do?"
					+ query;
			location.href = url;
		}
	}
	</c:if>
	
	
</script>

<script type="text/javascript">
function login() {
	location.href="${pageContext.request.contextPath}/member/login.do";
}

function ajaxFun(url, method, query, dataType, fn) {
	$.ajax({
		type:method,
		url:url,
		data:query,
		dataType:dataType,
		success:function(data){
			fn(data);
		},
		beforeSend:function(jqXHR){
			jqXHR.setRequestHeader("AJAX", true); // header에 AJAX라는 속성명에 true를 설정
		},
		error:function(jqXHR) {
			if(jqXHR.status === 403){
				login();
				return false;
			} else if(jqXHR.status === 400){
				alert("요청처리를 실패했습니다.");
				return false;
			}
			
			console.log(jqXHR.responseTet);
		}
	});
}

// 게시글 공감 여부
$(function() {
   $(".content").click(function() {
      let isLike = $(".content").hasClass("heart-active");
      let msg = isLike ? '게시글 공감을 취소 하시겠습니까 ? ' : '게시글에 공감하십니까 ? ';
      
      if(! confirm(msg)) {
         return false;
      }
      
      let url = "${pageContext.request.contextPath}/qnaboard/insertQnALike.do";
      let query = "num=${dto.boardNum}&isLike="+isLike;
      const fn = function(data) {
         let state = data.state;
         
         if(state === "true") {
        	$('.content').toggleClass("heart-active")
           	$('.text').toggleClass("heart-active")
          	$('.numb').toggleClass("heart-active")
          	$('.heart').toggleClass("heart-active")
            
            let count = data.QnALikeCount;
            $("#like_num").text(count);
         }
      };
      
      ajaxFun(url, "post", query, "json", fn);
      
   });
});

//글보기 화면이 보임과 동시에 AJAX로 1페이지 리플 리스트를 가져오기
$(function() {
   listPage(1);
});


// 페이징 처리에서 호출하는 자바스크립트 함수
function listPage(page) {
   	let url = "${pageContext.request.contextPath}/qnaboard/listReply.do";
   	let query = "boardNum=${dto.boardNum}&pageNo="+page;
   	let selector = "#listReply";
   
   	const fn = function(data) {
      	$(selector).html(data);
   	};
   
   	// AJAX-Text
   	ajaxFun(url, "get", query, "html", fn);
}



//댓글(리플) 등록
$(function() {
   	// send 버튼 클릭 시
	$(".btn_enter").click(function() {
    	let boardNum = "${dto.boardNum}"; // 리플을 등록할 게시물번호
      	let content = $(".tf_reply").val().trim();
      	if(! content) {
      		$(".tf_reply").focus();
         	return false;
      	}
      	content = encodeURIComponent(content);
      
      	let url = "${pageContext.request.contextPath}/qnaboard/insertReply.do";
      	let query = "boardNum="+boardNum+"&content="+content+"&answer=0";
      
      	const fn = function(data) {
      		$(".tf_reply").val("");
         
         	let state = data.state;
         	if(state === "true") {
            	// 등록 완료 후 1페이지의 댓글 다시 불러오기
            	listPage(1);
         	} else {
            	alert("댓글을 추가하지 못했습니다.");
         	}
      	};
      	ajaxFun(url, "post", query, "json", fn);      
   	});
});

// 댓글 삭제
$(function() {
	$("body").on("click", ".deleteReply", function() {
		if(! confirm('댓글을 삭제하시겠습니까 ? ')) {
			return false;
		}
		
		let replyNum = $(this).attr('data-replyNum');
		let page = $(this).attr('data-pageNo');
		
		let url =  '${pageContext.request.contextPath}/qnaboard/deleteReply.do';
		let query = 'replyNum=' + replyNum;
		
		const fn = function(data) {
			listPage(page);
		};
		
		ajaxFun(url, "post", query, "html", fn);
	});
});

// 댓글 좋아요 / 싫어요
$(function(){
	// 댓글 좋아요 / 싫어요 등록
	$("body").on("click", ".btnSendReplyLike", function(){
		let replyNum = $(this).attr("data-replyNum");
		let replyLike = $(this).attr("data-replyLike");
		const $btn = $(this);
		
		let msg = "해당 댓글이 마음에 들지 않으십니까 ?";
		if(replyLike === "1") {
			msg="해당 댓글에 공감하십니까 ?";
		}
		
		if(! confirm(msg)) {
			return false;
		}
		
		let url = "${pageContext.request.contextPath}/qnaboard/insertReplyLike.do";
		let query = "replyNum=" + replyNum + "&replyLike=" + replyLike;
		
		const fn = function(data){
			let state = data.state;
			if(state === "true") {
				let likeCount = data.likeCount;
				let disLikeCount = data.disLikeCount;
				
				$btn.parent("span").children().eq(0).find("span").html(likeCount);
				$btn.parent("span").children().eq(1).find("span").html(disLikeCount);
			} else if(state === "liked") {
				alert("댓글 공감/비공감은 한 번만 가능합니다.");
			} else {
				alert("게시물 공감 여부 처리가 실패했습니다.");
			}
		};
		
		ajaxFun(url, "post", query, "json", fn);
	});
});



// 댓글별 답글 리스트
function listReplyAnswer(answer) {
   let url="${pageContext.request.contextPath}/qnaboard/listReplyAnswer.do";
   let query = "answer="+answer;
   let selector = "#listReplyAnswer" + answer;
   
   const fn = function(data) {
      $(selector).html(data);
   };
   ajaxFun(url, "get", query, "html", fn);
}


// 댓글별 답글 개수
function countReplyAnswer(answer) {
	let url = "${pageContext.request.contextPath}/qnaboard/countReplyAnswer.do";
	let query = "answer="+answer;
	
	const fn = function(data) {
		let count = data.count;
		let selector = "#answerCount" + answer;
		 $(selector).html(count);
	};
	ajaxFun(url, "get", query, "json", fn);
}

// 답글 버튼(댓글별 답글 등록폼 및 답글리스트)
$(function() {
   $("body").on("click", ".btnReplyAnswerLayout", function() {
      const $div = $(this).closest("div").next();
      
      let isVisible = $div.is(':visible');
      let replyNum = $(this).attr("data-replyNum");
      
      if( isVisible ){
         $div.hide();
      } else {
         $div.show();
         
         // 답글 리스트
         listReplyAnswer(replyNum);
         
         // 답글 개수
         countReplyAnswer(replyNum)
         
         
      }
   });
});

// 댓글별 답글 등록
$(function() {
   $("body").on("click", ".btnSendReplyAnswer", function() {
      let boardNum = "${dto.boardNum}";
      let replyNum = $(this).attr("data-replyNum");
      const $div = $(this).parent().prev("div");
      
      let content = $div.find("textarea").val().trim();
      if( ! content){
         $div.find("textarea").focus();
         return false;
      }
      content = encodeURIComponent(content);
      
      let url = "${pageContext.request.contextPath}/qnaboard/insertReply.do";
      let query = "boardNum="+boardNum+"&content="+content+"&answer="+replyNum;
   
      const fn = function(data) {
         $div.find("textarea").val("");
         
         let state = data.state;
         if(state === "true") {
            // 답글 리스트
            listReplyAnswer(replyNum);
            
            // 답글 개수
            countReplyAnswer(replyNum)
         }
      };
      
      ajaxFun(url, "post", query, "json", fn);
   });
});


// 댓글별 답글 삭제
$(function(){
	$("body").on("click", ".deleteReplyAnswer", function(){
		if(! confirm("댓글을 삭제하시겠습니까 ? ")) {
		    return false;
		}
		
		let replyNum = $(this).attr("data-replyNum");
		let answer = $(this).attr("data-answer");
		
		let url = "${pageContext.request.contextPath}/qnaboard/deleteReply.do";
		let query = "replyNum=" + replyNum;
		
		const fn = function(data){
			listReplyAnswer(answer);
			countReplyAnswer(answer);
		};
		
		ajaxFun(url, "post", query, "json", fn);
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

			<table class="table table-border table-article">
				<thead>
					<tr>
						<td colspan="2" align="center">
						<h2>
						<c:if test="${dto.depth!=0 }">[Re] </c:if>
							${dto.subject}
						</h2>
						</td>
					</tr>
				</thead>

				<tbody>
					<tr>
						<td width="50%" style="font-size: 15px;"><span style="font-weight: bold;">작성자</span> ${dto.userNickName}</td>
						<td align="right">${dto.reg_date} | 조회 ${dto.hitCount}</td>
					</tr>

					<tr>
						<td colspan="2" valign="top" height="200">${dto.content}</td>
					</tr>
				</tbody>
			</table>
			
			<table class="table table-border table-article">
				<tbody>
					<tr>
						<td colspan="2">이전글 : <c:if test="${not empty preReadDto}">
								<a
									href="${pageContext.request.contextPath}/qnaboard/article.do?${query}&boardNum=${preReadDto.boardNum}">${preReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">다음글 : <c:if test="${not empty nextReadDto}">
								<a
									href="${pageContext.request.contextPath}/qnaboard/article.do?${query}&boardNum=${nextReadDto.boardNum}">${nextReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
				</tbody>
			</table>


			<table class="table">
				<tr>
					<td width="50%">
						<button type="button" class="btn"
							onclick="location.href='${pageContext.request.contextPath}/qnaboard/reply.do?boardNum=${dto.boardNum}&${query}';">답변</button>
						<c:choose>
							<c:when test="${sessionScope.member.userId==dto.userId}">
								<button type="button" class="btn"
									onclick="location.href='${pageContext.request.contextPath}/qnaboard/update.do?boardNum=${dto.boardNum}&${query}';">수정</button>
							</c:when>
							<c:otherwise>
								<button type="button" class="btn" disabled="disabled">수정</button>
							</c:otherwise>
						</c:choose> <c:choose>
							<c:when
								test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
								<button type="button" class="btn" onclick="deleteBoard();">삭제</button>
							</c:when>
							<c:otherwise>
								<button type="button" class="btn" disabled="disabled">삭제</button>
							</c:otherwise>
						</c:choose>
					</td>
					<td align="right">
						<button type="button" class="btn"
							onclick="location.href='${pageContext.request.contextPath}/qnaboard/list.do?${query}';">리스트</button>
					</td>
				</tr>
			</table>
			
			<div class="heart-btn">
      			<div class="content ${isUserLike?'heart-active':''}">
        			<span class="heart ${isUserLike?'heart-active':''}"></span>
        			<span class="text ${isUserLike?'heart-active':''}">LIKE</span>
        			<span class="numb ${isUserLike?'heart-active':''}" id="like_num">${dto.likeCount}</span>
      			</div>
    		</div>


			<div class="reply">
				<form action="" onsubmit="return false" method="post">
					<fieldset class="fld_reply">
						<div class="reply_write">
							<textarea name="comment" id="comment" class="tf_reply"
								placeholder="댓글을 입력해주세요" tabindex="3"></textarea>
						</div>
						<div class="writer_btn">
							<button type="submit" class="btn_enter" tabindex="5">Send</button>
						</div>
					</fieldset>
				</form>
			</div>
			<div id="listReply" class="area_reply"></div>
			

			

		</div>
	</main>

	<footer>
		<jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
	</footer>

	<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp" />
</body>
</html>