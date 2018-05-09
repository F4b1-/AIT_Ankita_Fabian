package com.unibz.hikinghelper;

import com.google.gwt.maps.client.services.ElevationResult;
import com.unibz.hikinghelper.dao.UserRepository;
import com.unibz.hikinghelper.model.Difficulty;
import com.unibz.hikinghelper.model.Elevation;
import com.unibz.hikinghelper.services.HikingUserDetailsServiceImpl;
import com.vaadin.annotations.Theme;
import com.vaadin.tapio.googlemaps.client.LatLon;
import net.sf.json.JSONObject;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.time.Duration;
import java.util.ArrayList;

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

            ArrayList<LatLon> points = new ArrayList<LatLon>();


            points.add(new LatLon(48.2751726, 8.6505911));
            points.add(new LatLon(48.275, 8.655));
            points.add(new LatLon(48.270, 8.651));
            points.add(new LatLon(48.2751726, 8.6505911));

            repository.save(new Location("Bonn", new LatLon(50.73743, 7.0982068)));
            repository.save(new Location("Trichtingen", new LatLon(48.2751726, 8.6505911), Difficulty.EASY, Duration.ofHours(1), points));


            userDetailsService.saveUser("fabi", "test", "ADMIN");

            String elevationCall = "https://maps.googleapis.com/maps/api/elevation/json?locations=39.7391536,-104.9847034&key=AIzaSyAdXfqEgqkjkDBBFC2dRoWU_-dST-S34dk";
            RestTemplate restTemplate = new RestTemplate();

            Elevation elevation = restTemplate.getForObject(elevationCall, Elevation.class);
            log.info(elevation.getResults().get(0).getElevation());

		};
	}

}
