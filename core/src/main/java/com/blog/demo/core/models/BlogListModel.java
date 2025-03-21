package com.blog.demo.core.models;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BlogListModel {

    private static final Logger LOG = LoggerFactory.getLogger(BlogListModel.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private static final String DEFAULT_IMAGE = "/content/dam/default.jpg";
    private static final SimpleDateFormat REQUEST_DATE_FORMAT = new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);

    @ScriptVariable
    private PageManager pageManager;

    @ValueMapValue
    private String parentPath; // Fetching path from dialog
@Self
SlingHttpServletRequest request;
    public List<BlogItem> getBlogItems() {
        List<BlogItem> blogs = new ArrayList<>();

        if (parentPath == null || parentPath.isEmpty()) {
            LOG.warn("Parent path is not configured in the dialog.");
            return blogs;
        }

        LOG.debug("Fetching blog list from: {}", parentPath);
        Page parentPage = pageManager.getPage(parentPath);
        String paramDate=request.getParameter("month");
        boolean filteredDate=paramDate !=null;

        if (parentPage != null) {
            Iterator<Page> childPages = parentPage.listChildren();

            while (childPages.hasNext()) {
                Page childPage = childPages.next();
                LOG.debug("Processing child page: {}", childPage.getPath());
                String title = Optional.ofNullable(childPage.getTitle())
                        .orElse(childPage.getProperties().get("jcr:title", "No Title"));
                String formattedDate = getFormattedDate(childPage);
                String description = childPage.getProperties().get("jcr:description", "No Description");
                String link = childPage.getPath() + ".html";
                String imagePath = getImagePath(childPage);
                if(filteredDate && !matchesRequestedDate(formattedDate,paramDate)){
                    continue;
                }

                blogs.add(new BlogItem(title, formattedDate, description, link, imagePath));
            }
        }

        return blogs;
    }

    private String getFormattedDate(Page childPage) {
        Calendar createdDateCal = childPage.getProperties().get("jcr:created", Calendar.class);
        return (createdDateCal != null) ? DATE_FORMAT.format(createdDateCal.getTime()) : "N/A";
    }
    private boolean matchesRequestedDate(String formattedDate, String requestDate) {
        try {
            Date blogDate = DATE_FORMAT.parse(formattedDate); // Convert blog date to Date object
            String blogMonthYear = REQUEST_DATE_FORMAT.format(blogDate); // Convert to "MMMM yyyy" format
            return blogMonthYear.equals(requestDate);
        } catch (Exception e) {
            LOG.error("Error parsing blog date: {}", formattedDate, e);
            return false;
        }
    }
    /**
     * Fetches the image path from JCR structure.
     */
    private String getImagePath(Page childPage) {
        String imagePath = DEFAULT_IMAGE;
        Resource imageResource = childPage.getContentResource("root/responsivegrid/image/file/jcr:content");

        if (imageResource != null) {
            ValueMap properties = imageResource.getValueMap();
            if (properties.containsKey("jcr:data")) {  // Check if binary data exists
                imagePath = childPage.getPath() + "/jcr:content/root/responsivegrid/image/file";
                LOG.debug("Image found at: {}", imagePath);
            } else {
                LOG.warn("No image found under root/responsivegrid/image/file/jcr:content for page: {}", childPage.getPath());
            }
        } else {
            LOG.warn("Image resource not found for page: {}", childPage.getPath());
        }

        return imagePath;
    }

    public static class BlogItem {
        private final String title;
        private final String date;
        private final String description;
        private final String link;
        private final String image;

        public BlogItem(String title, String date, String description, String link, String image) {
            this.title = title != null ? title : "";
            this.date = date != null ? date : "";
            this.description = description != null ? description : "";
            this.link = link != null ? link : "";
            this.image = image != null ? image : "";
        }

        public String getTitle() { return title; }
        public String getDate() { return date; }
        public String getDescription() { return description; }
        public String getLink() { return link; }
        public String getImage() { return image; }
    }
}

