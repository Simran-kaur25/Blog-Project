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

import static com.blog.demo.core.util.Utils.getImagePath;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = PublishedBlogsModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class PublishedBlogsModelImpl implements PublishedBlogsModel {

    // Logger for logging information and errors
    private static final Logger LOG = LoggerFactory.getLogger(PublishedBlogsModelImpl.class);

    // Default image to show when no image is found
    private static final String DEFAULT_IMAGE = "/content/dam/default.jpg";

    // Date formats used to format the blog creation dates
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat REQUEST_DATE_FORMAT = new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);

    @ScriptVariable
    private PageManager pageManager;

    @Self
    private SlingHttpServletRequest request;

    // Injected property for fetching the text value
    @ValueMapValue
    private String text;

    // List to store fetched blog data
    private List<BlogData> blogs;

    // OSGi Service reference to the PublishedBlogsService to fetch configuration - max number of blogs
    @OSGiService
    private PublishedBlogsService publishedBlogsService;  // OSGi Service Reference

    @PostConstruct
    protected void init() {
        blogs = new ArrayList<>();

        // Fetch the current page using the reference text property
        Page currentPage = pageManager.getPage(text);

        if (currentPage == null) {
            LOG.error("Current page not found!");
            return;
        }

        // Get child pages (blogs) of the current page
        Iterator<Page> children = currentPage.listChildren();

        // Get the maximum number of blogs to display from the service
        int maxBlogs = publishedBlogsService.getMaxBlogs(); // Fetch max blogs from service
        int count = 0;

        // Check if the user has applied a filter for the month
        String paramDate = request.getParameter("month");
        boolean filteredDate = paramDate != null;

        // Iterate through the children (blog pages)
        while (children.hasNext() && count < maxBlogs) {
            Page blogPage = children.next();

            String title = blogPage.getTitle();
            String description = blogPage.getProperties().get("jcr:description", String.class);
            String image = getImagePath(request,blogPage); // used Utils class
            String link = blogPage.getPath() + ".html";
            String date = getFormattedDate(blogPage);

            // Skip the blog if the date doesn't match the filter
            if (filteredDate && !matchesRequestedDate(date, paramDate)) {
                continue;
            }

            // Add the blog data if it contains valid information
            if (title != null || description != null || image != null) {
                blogs.add(new BlogData(title, description, image, link, date));
                count++;
            }
        }

        LOG.info("Fetched {} blogs (Max allowed: {})", blogs.size(), maxBlogs);
    }

    // Method to check if the blog's month matches the requested filter month
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


    // Method to format the blog's creation date
    private String getFormattedDate(Page childPage) {
        Calendar createdDateCal = childPage.getProperties().get("jcr:created", Calendar.class);
        return (createdDateCal != null) ? DATE_FORMAT.format(createdDateCal.getTime()) : "N/A";
    }

    @Override
    public List<BlogData> getBlogs() {
        return blogs;
    }

    // Inner class to hold blog data
    public static class BlogData {
        private final String title;
        private final String description;
        private final String image;
        private final String link;
        private final String date;

        // Constructor to initialize blog data fields
        public BlogData(String title, String description, String image, String link, String date) {
            this.title = title != null ? title : "";
            this.description = description != null ? description : "";
            this.image = image != null ? image : "";
            this.link = link != null ? link : "";
            this.date = date != null ? date : "N/A";
        }

        // Getters for the blog data fields
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getImage() { return image; }
        public String getLink() { return link; }
        public String getDate() { return date; }
    }
}
