package com.unibz.hikinghelper;

import com.unibz.hikinghelper.dao.UserRepository;
import com.unibz.hikinghelper.services.HikingUserDetailsServiceImpl;
import com.vaadin.annotations.Theme;
import com.vaadin.tapio.googlemaps.client.LatLon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@SpringBootApplication
@Configuration
@EnableWebMvc
@ComponentScan({ "com.unibz.hikinghelper" })
public class Application  implements WebMvcConfigurer {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

    @Autowired
    HikingUserDetailsServiceImpl userDetailsService;


	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/js/");
	}

	@Bean
	public ViewResolver viewResolver() {
		UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
		viewResolver.setViewClass(InternalResourceView.class);
		return viewResolver;
	}

	@Bean
	public CommandLineRunner loadData(LocationRepository repository) {
		return (args) -> {
			// save a couple of customers
            repository.deleteAll();
			repository.save(new Location("Trichtingen", new LatLon(48.2751726, 8.6505911)));
            repository.save(new Location("Bonn", new LatLon(50.73743, 7.0982068)));

            userDetailsService.saveUser("fabi", "test", "ADMIN");


		};
	}

}
