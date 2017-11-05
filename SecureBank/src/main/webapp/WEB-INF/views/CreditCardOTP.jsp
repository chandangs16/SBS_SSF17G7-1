<%@page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous"/>



  <%@ include file="customerMenu.jsp" %>
	<div class="page-header">
		<h1>${fn:escapeXml(heading)}</h1>
	</div>

	<div id="add-withdraw">
		<form:form id="otp" method="POST" modelAttribute="transaction" action=${pageContext.request.contextPath}/customer/creditcard/process-otp htmlEscape="true">
			<div>
				<c:if test=${!empty successMsg}>
					<div class="alert alert-success">						
						${fn:escapeXml(successMsg)}
					</div>
				</c:if>
				<c:if test="${!empty failureMsg}">
					<div class="alert alert-danger">						
						${fn:escapeXml(failureMsg)}
					</div>
				</c:if>
				<div class="modal-header">
					<h4 class="modal-title">Please verify your transaction by using the one time password sent to you by email</h4>
				</div>
				<div class="modal-body">
					<p>
						<label>OTP:</label>
						<input name="otp" type="text" class="form-control" placeholder="8 digits">
					</p>
					<input name="otpId" type="hidden" class="form-control" value="${otpId}">
					<input name="transactionId" type="hidden" class="form-control" value="${transactionId}">
					<input name="type" type="hidden" class="form-control" value="${type}">
				</div>
				<div class="modal-footer"> 
					<button name="submit" value="confirm" type="submit" class="btn btn-success">Confirm Transaction</button>
					<button name="submit" value="cancel" type="submit" class="btn btn-danger">I want to cancel this transaction</button>
				</div>
			</div>
			<%--Cross site scripting protection --%>
			<spring:htmlEscape defaultHtmlEscape="true" /> 

		</form:form>
		<!-- /form -->
	</div>
	
	<div id="virtualKeyboard"></div>
	<!-- /#add-withdraw -->