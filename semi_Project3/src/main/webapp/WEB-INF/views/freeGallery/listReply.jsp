<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<strong class="tit_reply"><span>${replyCount}</span> Comments</strong>
<div class="entry_comment">
	<ul class="list_reply">
		<c:forEach var="vo" items="${listReply}">
			<li class="rp_general">
				<span class="ico_skin thumb_profile">
					<c:choose>
						<c:when test="${vo.getImageFileName() == null}">
							<img src="${pageContext.request.contextPath}/resource/images/basic_profile.png" width="48" height="48" class="img_profile" alt="프로필사진">
					</c:when>
					<c:otherwise>
						<img src="${pageContext.request.contextPath}/uploads/profile/${vo.imageFileName}" width="48" height="48" class="img_profile" alt="프로필사진">
					</c:otherwise>
			</c:choose>
				</span>
				<span class="reply_content">
					<span class="tit_nickname">${vo.userNickName}</span>
					<span class="txt_date">${vo.reg_date}</span>
					<span class="txt_reply">
						${vo.content}
					</span>
				</span>
				<div class="area_more">
					<div class="reply_layer">
						<div class="inner_reply_layer">
							<div class="layer_body">
								<c:choose>
					               	<c:when test="${sessionScope.member.userId==vo.userId||sessionScope.member.userId=='admin'}">
					                  	<span class="link_reply deleteReply" data-replyNum='${vo.replyNum}' data-pageNo='${pageNo}'>삭제</span>
					               	</c:when>
					               	<c:otherwise>
					                  	<span class="link_reply notifyReply">신고</span>
					               	</c:otherwise>
				            	</c:choose>
							</div>
						</div>
					</div>
				</div>
				<div style="margin: 10px 0px;">
					<button type='button' class='btn btnReplyAnswerLayout' data-replyNum='${vo.replyNum}'>답글 <span id="answerCount${vo.replyNum}">${vo.answerCount}</span></button>
					<span style="float: right;">
						<button type='button' class='none_border btnSendReplyLike' data-replyNum='${vo.replyNum}' data-replyLike='1' title="좋아요"><i class="far fa-hand-point-up"></i> <span>${vo.likeCount}</span></button>
            			<button type='button' class='none_border btnSendReplyLike' data-replyNum='${vo.replyNum}' data-replyLike='0' title="싫어요"><i class="far fa-hand-point-down"></i> <span>${vo.disLikeCount}</span></button>           
					</span>
				</div>
				
				<div class="reply-answer">
					<div id='listReplyAnswer${vo.replyNum}' class='answer-list'></div>
					<div class="answer-form">
                   		<div class='answer-left' style="color: '#ccc'">└</div>
                   		<div class='answer-right'><textarea class='form-control' placeholder="답글을 입력해주세요"></textarea></div>
                   		<div class='answer-footer writer_btn'>
                   			<button type='button' class='btn_enter btnSendReplyAnswer' data-replyNum='${vo.replyNum}'>답글 등록</button>
               			</div>
               		</div>
				</div>
			</li>
		</c:forEach>
	</ul>
</div>
<div class="page-box">
   ${paging}
</div>  	
