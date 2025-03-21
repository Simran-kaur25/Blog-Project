package com.blog.demo.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Published Blogs Configuration", description = "Configuration for displaying published blogs")
public @interface PublishedBlogsConfig {

    @AttributeDefinition(
            name = "Number of Blogs to Display",
            description = "Define how many blogs should be displayed"
    )
    int maxBlogs() default 1;
}

