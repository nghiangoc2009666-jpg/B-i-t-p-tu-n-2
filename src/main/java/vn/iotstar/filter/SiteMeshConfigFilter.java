package vn.iotstar.filter;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

import jakarta.servlet.annotation.WebFilter;

/** Cấu hình Sitemesh 3: chọn decorator theo đường dẫn. */
//@WebFilter("/*")
public class SiteMeshConfigFilter extends ConfigurableSiteMeshFilter {
    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        builder.addDecoratorPath("/*", "/views/decorators/web.jsp")
               .addDecoratorPath("/admin/*", "/views/decorators/admin.jsp")
               .addDecoratorPath("/manager/*", "/views/decorators/admin.jsp")
               .addExcludedPath("/image")
               .addExcludedPath("/media")
               .addExcludedPath("/static/*")
               .addExcludedPath("/views/decorators/*");
    }
}
