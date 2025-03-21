package com.blog.demo.core.models.impl;

import com.blog.demo.core.models.PublishedBlogsModel;
import com.blog.demo.core.services.PublishedBlogsService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = PublishedBlogsModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class PublishedBlogsModelImpl implements PublishedBlogsModel {

    private static final Logger LOG = LoggerFactory.getLogger(PublishedBlogsModelImpl.class);
    private static final String DEFAULT_IMAGE = "/content/dam/default.jpg";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat REQUEST_DATE_FORMAT = new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);

    @ScriptVariable
    private PageManager pageManager;

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    private String text;

    private List<BlogData> blogs;

    @OSGiService
    private PublishedBlogsService publishedBlogsService;  // OSGi Service Reference

    @PostConstruct
    protected void init() {
        blogs = new ArrayList<>();
        Page currentPage = pageManager.getPage(text);

        if (currentPage == null) {
            LOG.error("Current page not found!");
            return;
        }

        Iterator<Page> children = currentPage.listChildren();
        int maxBlogs = publishedBlogsService.getMaxBlogs(); // Fetch max blogs from service
        int count = 0;
        String paramDate = request.getParameter("month");
        boolean filteredDate = paramDate != null;

        while (children.hasNext() && count < maxBlogs) {
            Page blogPage = children.next();

            String title = blogPage.getTitle();
            String description = blogPage.getProperties().get("jcr:description", String.class);
            String image = getImagePath(blogPage);
            String link = blogPage.getPath() + ".html";
            String date = getFormattedDate(blogPage);

            if (filteredDate && !matchesRequestedDate(date, paramDate)) {
                continue;
            }

            if (title != null || description != null || image != null) {
                blogs.add(new BlogData(title, description, image, link, date));
                count++;
            }
        }

        LOG.info("Fetched {} blogs (Max allowed: {})", blogs.size(), maxBlogs);
    }

    private boolean matchesRequestedDate(String formattedDate, String requestDate) {
        try {
            Date blogDate = DATE_FORMAT.parse(formattedDate); // Convert blog date to Date object
            String blogMonthYear = REQUEST_DATE_FORMAT.format(blogDate); // Convert to "MM-yyyy" format
            return blogMonthYear.equals(requestDate);
        } catch (ParseException e) {
            LOG.error("Error parsing blog date: {}", formattedDate, e);
            return false;
        }
    }

    private String getImagePath(Page childPage) {
        String imagePath = DEFAULT_IMAGE;
        Resource imageResource = childPage.getContentResource("root/responsivegrid/image/file/jcr:content");

        if (imageResource != null) {
            ValueMap properties = imageResource.getValueMap();
            if (properties.containsKey("jcr:data")) {
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

    private String getFormattedDate(Page childPage) {
        Calendar createdDateCal = childPage.getProperties().get("jcr:created", Calendar.class);
        return (createdDateCal != null) ? DATE_FORMAT.format(createdDateCal.getTime()) : "N/A";
    }

    @Override
    public List<BlogData> getBlogs() {
        return blogs;
    }

    public static class BlogData {
        private final String title;
        private final String description;
        private final String image;
        private final String link;
        private final String date;

        public BlogData(String title, String description, String image, String link, String date) {
            this.title = title != null ? title : "";
            this.description = description != null ? description : "";
            this.image = image != null ? image : "";
            this.link = link != null ? link : "";
            this.date = date != null ? date : "N/A";
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getImage() { return image; }
        public String getLink() { return link; }
        public String getDate() { return date; }
    }
}
