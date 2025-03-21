// Not used
package com.blog.demo.core.models.impl;

        import com.blog.demo.core.models.PagePropertiesModel;
        import com.day.cq.wcm.api.Page;
        import org.apache.sling.api.SlingHttpServletRequest;
        import org.apache.sling.api.resource.Resource;
        import org.apache.sling.models.annotations.DefaultInjectionStrategy;
        import org.apache.sling.models.annotations.Model;
        import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
        import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

        import javax.annotation.PostConstruct;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;

@Model(adaptables = SlingHttpServletRequest.class, adapters = PagePropertiesModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PagePropertiesModelImpl implements PagePropertiesModel {


    @ScriptVariable
    private Page currentPage;

    private String formattedDate;

    @PostConstruct
    protected void init() {
        Calendar lastModified = currentPage.getProperties().get("cq:lastModified", Calendar.class);
        if (lastModified != null) {
            formattedDate = new SimpleDateFormat("MMMM dd, yyyy").format(lastModified.getTime());
        } else {
            formattedDate = "No Date Available";
        }
    }

    @Override
    public String getPageTitle() {
        return currentPage != null ? currentPage.getTitle() : "No Title";
    }

    @Override
    public String getPublishDate() {
        return formattedDate;
    }

    @Override
    public String getAuthorName() {
        return currentPage != null ? currentPage.getProperties().get("cq:lastModifiedBy", String.class) : "No Author";
    }

    @Override
    public String getDescription() {
        return currentPage.getProperties().get("jcr:description", String.class);
    }
}