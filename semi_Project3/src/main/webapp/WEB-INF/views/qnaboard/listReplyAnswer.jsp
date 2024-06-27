<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:forEach var="vo" items="${listReplyAnswer}">
	<li class="rp_general">
		<div class='answer-left'>└</div>
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
			               	<c:when test="${sessionScope.member.userId == vo.userId || sessionScope.member.userId == 'admin' }">
			                  	<span class="link_reply deleteReplyAnswer" data-replyNum='${vo.replyNum}' data-answer='${vo.answer}'>삭제</span>
			               	</c:when>
			               	<c:otherwise>
			                  	<span class="link_reply notifyReplyAnswer">신고</span>
			               	</c:otherwise>
		            	</c:choose>
					</div>
				</div>
			</div>
		</div>
	</li>
</c:forEach>
