package com.blog.demo.core.models.impl;

import com.blog.demo.core.models.ArchiveModel;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.text.SimpleDateFormat;
import java.util.*;

@Model(adaptables = Resource.class, adapters = ArchiveModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArchiveModelImpl implements ArchiveModel {

    @ValueMapValue
    private String blogPagePath; // Path to published blogs

    @SlingObject
    private Resource componentResource;

//    private static final String PUBLISHED_BLOG_PAGE_URL = "/content/blogproject/published-blog-page.html";

    @Override
    public List<Map<String, String>> getArchivedBlogs() {
        List<Map<String, String>> blogList = new ArrayList<>();
        Set<String> uniqueMonths = new HashSet<>(); // To store unique month-year entries

        if (blogPagePath == null) {
            return blogList; // Return empty list if path is not set
        }

        Resource blogPageResource = componentResource.getResourceResolver().getResource(blogPagePath);

        if (blogPageResource != null) {
            SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM yyyy"); // e.g., "March 2025"
            SimpleDateFormat urlFormatter = new SimpleDateFormat("MM-yyyy"); // e.g., "03-2025"

            for (Resource blog : blogPageResource.getChildren()) {
                ValueMap blogProperties = blog.getValueMap();
                Calendar createdDate = blogProperties.get("jcr:created", Calendar.class);

                if (createdDate != null) {
                    String monthDisplay = monthFormatter.format(createdDate.getTime()); // "March 2025"
                    String monthUrl = urlFormatter.format(createdDate.getTime()); // "03-2025"

                    // Construct Archive Page Link (Filtering by month)
                    String archivePageLink = /* PUBLISHED_BLOG_PAGE_URL */ blogPagePath+ "?month=" + monthUrl;

                    // Store unique month entries
                    if (uniqueMonths.add(monthUrl)) {
                        Map<String, String> blogData = new HashMap<>();
                        blogData.put("Month", monthDisplay);
                        blogData.put("ArchiveLink", archivePageLink);
                        blogList.add(blogData);
                    }
                }
            }
        }
        return blogList;
    }
}
