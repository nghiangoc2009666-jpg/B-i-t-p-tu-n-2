<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="type" %>
<%@ attribute name="value" %>
<%@ attribute name="icon" %>
<%@ attribute name="pattern" %>
<%@ attribute name="minlength" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="required" %>
<%@ attribute name="match" %>
<%@ attribute name="msg" %>
<%@ attribute name="help" %>
<c:set var="err" value="${errors[name]}"/>
<div class="mb-3">
    <label for="f_${name}" class="form-label">${label}<c:if test="${required == 'true'}"> <span class="text-danger">*</span></c:if></label>
    <div class="input-group has-validation">
        <c:if test="${not empty icon}"><span class="input-group-text"><i class="bi bi-${icon}"></i></span></c:if>
        <input id="f_${name}" name="${name}" type="${empty type ? 'text' : type}"
               class="form-control ${not empty err ? 'is-invalid' : ''}"
               <c:if test="${type != 'password'}">value="<c:out value='${value}'/>"</c:if>
               <c:if test="${not empty pattern}">pattern="${pattern}"</c:if>
               <c:if test="${not empty minlength}">minlength="${minlength}"</c:if>
               <c:if test="${not empty maxlength}">maxlength="${maxlength}"</c:if>
               <c:if test="${not empty match}">data-match="${match}"</c:if>
               ${required == 'true' ? 'required' : ''}>
        <div class="invalid-feedback"><c:out value="${not empty err ? err : msg}"/></div>
    </div>
    <c:if test="${not empty help}"><div class="form-text">${help}</div></c:if>
</div>
