package com.blog.demo.core.models.impl;
import com.blog.demo.core.models.AboutModel;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = AboutModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AboutModelImpl implements AboutModel{
    @ScriptVariable
    private Page currentPage;

    @Override
    public String getPageDescription() {
        return (currentPage != null) ? currentPage.getDescription() : "No description available.";
    }
}