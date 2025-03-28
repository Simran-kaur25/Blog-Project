package com.blog.demo.core.models.impl;

import com.adobe.granite.security.user.UserProperties;
import com.adobe.granite.security.user.UserPropertiesManager;
import com.adobe.granite.security.user.UserPropertiesService;

import com.blog.demo.core.models.HeadingModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.jcr.Session;
import java.text.SimpleDateFormat;
import java.util.Date;

@Model(adapters = HeadingModel.class,
        adaptables = {SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class HeadingModelImpl implements HeadingModel {


    // Injecting Page Manager to work with pages
    @ScriptVariable
    private PageManager pageManager;

    // Resource resolver to interact with the JCR
    @SlingObject
    private ResourceResolver resourceResolver;

    @Self
    private SlingHttpServletRequest request;

    private String title;
    private String author;
    private String date;

    @ValueMapValue(name ="jcr:createdBy")
    private String userId;

    @PostConstruct
    protected void init() {

        // Fetch the current page using the PageManager
        Page currentPage = pageManager.getContainingPage(request.getResource());
        if (currentPage != null) {
            this.title = currentPage.getTitle();

            // Fetch created date properly
            Date createdDate = currentPage.getProperties().get("jcr:created", Date.class);
            this.date = (createdDate != null) ? formatDate(createdDate) : "No Date Available";
        }

        // Fetch logged-in user author name
        this.author = getLoggedInUserName();
    }

    private String getLoggedInUserName() {
        try {
            UserPropertiesManager upm = resourceResolver.adaptTo(UserPropertiesManager.class);
            if (upm != null) {
                UserProperties userProperties = upm.getUserProperties(userId, UserPropertiesService.PROFILE_PATH);
                if (userProperties != null) {
                    // Fetch full name, if available; otherwise, fallback to userId
                    String fullName = userProperties.getDisplayName();
                    return (fullName != null && !fullName.isEmpty()) ? fullName : userId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown User";
    }
    public String formatDate(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy").format(date);
    }
    @Override
    public String getPageTitle() {
        return title;
    }
    @Override
    public String getAuthorName() {
        return author;
    }
    @Override
    public String getPublishDate() {
        return date;
    }
}