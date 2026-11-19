<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="isEdit" value="${video != null}"/>
<c:set var="hasErr" value="${not empty errors}"/>
<c:set var="useDb" value="${isEdit && !hasErr}"/>
<html><head><title>${isEdit ? 'Sửa video' : 'Thêm video'}</title></head>
<body>
<div class="card shadow-sm mx-auto" style="max-width:720px"><div class="card-body p-4">
    <h4 class="mb-3">${isEdit ? 'Sửa video' : 'Thêm video'}</h4>
    <c:if test="${alert != null}"><div class="alert alert-danger"><c:out value="${alert}"/></div></c:if>

    <%-- enctype="multipart/form-data": upload file bằng Servlet Multipart --%>
    <form action="${pageContext.request.contextPath}/video/${isEdit ? 'update' : 'insert'}" method="post"
          enctype="multipart/form-data" class="needs-validation" novalidate>
        <c:if test="${isEdit}"><input type="hidden" name="videoId" value="<c:out value='${video.videoId}'/>"></c:if>

        <t:field name="title" label="Tiêu đề" value="${useDb ? video.title : param.title}" required="true" maxlength="500" msg="Vui lòng nhập tiêu đề (tối đa 500 ký tự)"/>

        <div class="mb-3">
            <label class="form-label" for="f_description">Mô tả</label>
            <textarea class="form-control ${not empty errors.description ? 'is-invalid' : ''}" id="f_description" name="description" rows="3" maxlength="500"><c:out value="${useDb ? video.description : param.description}"/></textarea>
            <div class="invalid-feedback"><c:out value="${errors.description}"/></div>
        </div>
        <div class="mb-3">
            <label class="form-label" for="categoryId">Danh mục</label>
            <select class="form-select" id="categoryId" name="categoryId">
                <option value="0">-- Không chọn --</option>
                <c:forEach items="${categories}" var="cate">
                    <option value="${cate.categoryid}"
                        ${(useDb && video.category != null && video.category.categoryid == cate.categoryid) || (!useDb && param.categoryId == cate.categoryid) ? 'selected' : ''}>
                        <c:out value="${cate.categoryname}"/></option>
                </c:forEach>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Video</label>
            <input type="file" class="form-control mb-2" name="videoFile" accept="video/mp4,video/webm,video/ogg">
            <div class="form-text mb-2">Chọn file mp4 / webm / ogg (tối đa 100MB) hoặc nhập link bên dưới (mp4 hoặc YouTube).</div>
            <input type="text" class="form-control ${not empty errors.videoLink ? 'is-invalid' : ''}" name="videoLink" placeholder="https://..."
                   value="<c:out value='${useDb ? (fn:startsWith(video.videoUrl, "http") ? video.videoUrl : "") : param.videoLink}'/>">
            <div class="invalid-feedback"><c:out value="${errors.videoLink}"/></div>
            <c:if test="${isEdit && not empty video.videoUrl && !fn:startsWith(video.videoUrl, 'http')}">
                <div class="form-text">Đang dùng file đã upload. Bỏ trống để giữ nguyên.</div>
            </c:if>
        </div>

        <div class="mb-3">
            <label class="form-label">Poster (ảnh đại diện)</label>
            <c:if test="${isEdit && not empty video.poster}">
                <c:choose>
                    <c:when test="${fn:startsWith(video.poster, 'http')}"><c:set var="posterUrl" value="${video.poster}"/></c:when>
                    <c:otherwise><c:url var="posterUrl" value="/image"><c:param name="fname" value="${video.poster}"/></c:url></c:otherwise>
                </c:choose>
                <div class="mb-2"><img src="<c:out value='${posterUrl}'/>" height="120" class="rounded border" alt="poster"></div>
            </c:if>
            <input type="file" class="form-control mb-2" name="posterFile" accept="image/*">
            <input type="text" class="form-control ${not empty errors.posterLink ? 'is-invalid' : ''}" name="posterLink" placeholder="hoặc link ảnh https://..."
                   value="<c:out value='${useDb ? (fn:startsWith(video.poster, "http") ? video.poster : "") : param.posterLink}'/>">
            <div class="invalid-feedback"><c:out value="${errors.posterLink}"/></div>
        </div>

        <div class="mb-3">
            <label class="form-label d-block">Trạng thái</label>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="active" id="on" value="1" ${!isEdit || video.active == 1 ? 'checked' : ''}>
                <label class="form-check-label" for="on">Công khai</label>
            </div>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="active" id="off" value="0" ${isEdit && video.active != 1 ? 'checked' : ''}>
                <label class="form-check-label" for="off">Ẩn</label>
            </div>
        </div>

        <button type="submit" class="btn btn-primary">${isEdit ? 'Cập nhật' : 'Thêm video'}</button>
        <a class="btn btn-link" href="${pageContext.request.contextPath}/video">Quay lại</a>
    </form>
</div></div>
</body></html>
