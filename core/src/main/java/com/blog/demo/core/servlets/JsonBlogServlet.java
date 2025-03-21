package com.blog.demo.core.servlets;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Iterator;

@Component(service = Servlet.class, name = "Blog Servlet Get", property = {
        org.osgi.framework.Constants.SERVICE_DESCRIPTION + "=Blog Servlet GET for Bootcamp",
        "sling.servlet.resourceTypes=" + "blogproject/components/structure/page",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.extensions=" + "json"
})
public class JsonBlogServlet extends SlingSafeMethodsServlet {
    private static final String DEFAULT_IMAGE = "/content/dam/default.jpg";

    @Override
    protected void doGet(@Nonnull final SlingHttpServletRequest request, @Nonnull final SlingHttpServletResponse response) throws IOException {
        String requestPath = request.getParameter("page");
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource parentResource = resourceResolver.getResource(requestPath);

        JsonArray blogArray = new JsonArray();

        if (parentResource != null) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                Page parentPage = pageManager.getContainingPage(parentResource);
                Iterator<Page> childPages = parentPage.listChildren();

                while (childPages.hasNext()) {
                    Page childPage = childPages.next();
                    JsonObject blogObject = new JsonObject();

                    blogObject.addProperty("title", childPage.getTitle());
                    blogObject.addProperty("description", childPage.getProperties().get("jcr:description", "No Description"));
                    blogObject.addProperty("date", childPage.getProperties().get("jcr:created", String.class));
                    blogObject.addProperty("link", childPage.getPath() + ".html");
                    blogObject.addProperty("image", getImagePath(childPage));

                    blogArray.add(blogObject);
                }
            }
        }

        // Set response type and write the JSON output
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(blogArray.toString());
    }

    private String getImagePath(Page childPage) {
        String imagePath = DEFAULT_IMAGE;
        Resource imageResource = childPage.getContentResource("root/responsivegrid/image/file/jcr:content");

        if (imageResource != null) {
            ValueMap properties = imageResource.getValueMap();
            if (properties.containsKey("jcr:data")) { // Check if binary data exists
                imagePath = childPage.getPath() + "/jcr:content/root/responsivegrid/image/file";
            }
        }

        return imagePath;
    }
}