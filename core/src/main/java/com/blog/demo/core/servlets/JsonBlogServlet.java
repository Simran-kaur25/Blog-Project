package com.blog.demo.core.servlets;


import com.blog.demo.core.util.Utils;
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

//import static com.blog.demo.core.util.Utils.getImagePath;

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

        // Extract the 'page' parameter from the request
        String requestPath = request.getParameter("page");

        // Get the resource resolver from the request
        ResourceResolver resourceResolver = request.getResourceResolver();

        // Get the resource based on the provided page path
        Resource parentResource = resourceResolver.getResource(requestPath);

        // Create a JSON array to hold the blog data
        JsonArray blogArray = new JsonArray();

        if (parentResource != null) {
            // Retrieve the page manager to access pages
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            if (pageManager != null) {
                // Get the parent page
                Page parentPage = pageManager.getContainingPage(parentResource);

                // Iterate over the child pages (blog pages) of the parent page
                Iterator<Page> childPages = parentPage.listChildren();

                while (childPages.hasNext()) {
                    Page childPage = childPages.next();

                    // Create a new JSON object to store the blog's data
                    JsonObject blogObject = new JsonObject();

                    blogObject.addProperty("title", childPage.getTitle());
                    blogObject.addProperty("description", childPage.getProperties().get("jcr:description", "No Description"));
                    blogObject.addProperty("date", childPage.getProperties().get("jcr:created", String.class));
                    blogObject.addProperty("link", childPage.getPath() + ".html");
                    blogObject.addProperty("image", Utils.getImagePath(childPage)); // used Utils class

                    // Add the blog object to the JSON array
                    blogArray.add(blogObject);
                }
            }
        }

        // Set response type and write the JSON output
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(blogArray.toString());
    }
}