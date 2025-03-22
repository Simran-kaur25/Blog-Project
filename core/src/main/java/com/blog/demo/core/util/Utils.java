package com.blog.demo.core.util;

import com.blog.demo.core.models.impl.PublishedBlogsModelImpl;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(PublishedBlogsModelImpl.class);
    private static final String DEFAULT_IMAGE = "/content/dam/default.jpg";

    public static String getImagePath(Page childPage) {
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
}
