package com.unibz.hikinghelper;

import com.unibz.hikinghelper.dao.LocationRepository;
import com.unibz.hikinghelper.model.Difficulty;
import com.unibz.hikinghelper.model.Location;
import com.unibz.hikinghelper.services.HikingUserDetailsServiceImpl;
import com.vaadin.tapio.googlemaps.client.LatLon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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

            repository.save(new Location("Bonn", new LatLon(50.73743, 7.0982068), Difficulty.MEDIUM, Duration.ofHours(1), new ArrayList<LatLon>(), new ArrayList<Double>()));

            ArrayList<LatLon> points = new ArrayList<LatLon>();
            points.add(new LatLon(48.2751726, 8.6505911));
            points.add(new LatLon(48.275, 8.655));
            points.add(new LatLon(48.270, 8.651));
            points.add(new LatLon(48.2751726, 8.6505911));
            repository.save(new Location("Trichtingen, DE", new LatLon(48.2751726, 8.6505911), Difficulty.EASY, Duration.ofHours(10), points, new ArrayList<Double>()));

			ArrayList<LatLon> pointsBolzano = new ArrayList<LatLon>();
            pointsBolzano.add(new LatLon(46.498295, 11.354758));
            pointsBolzano.add(new LatLon(46.499249, 11.352369));
            pointsBolzano.add(new LatLon(46.497802, 11.352455));
            pointsBolzano.add(new LatLon(46.498295, 11.354758));
            repository.save(new Location("Bolzano, IT", new LatLon(46.498295, 11.354758), Difficulty.EASY, Duration.ofHours(1), pointsBolzano, new ArrayList<Double>()));

            ArrayList<LatLon> pointsNagpur = new ArrayList<LatLon>();
            pointsNagpur.add(new LatLon(21.147305, 79.081145));
            pointsNagpur.add(new LatLon(21.152668, 79.081038));
            pointsNagpur.add(new LatLon(21.153540, 79.087261));
            pointsNagpur.add(new LatLon(21.145803, 79.088259));
            pointsNagpur.add(new LatLon(21.147305, 79.081145));
            repository.save(new Location("Nagpur, IN", new LatLon(21.145800, 79.088155), Difficulty.MEDIUM, Duration.ofHours(1), pointsNagpur, new ArrayList<Double>()));

            ArrayList<LatLon> pointsEverest = new ArrayList<LatLon>();
            pointsEverest.add(new LatLon(28.066057, 86.865008));
            pointsEverest.add(new LatLon(28.045202, 86.884805));
            pointsEverest.add(new LatLon(28.005198, 86.879312));
            pointsEverest.add(new LatLon(27.985190, 86.921884));
            pointsEverest.add(new LatLon(27.955474, 86.957590));
            pointsEverest.add(new LatLon(27.968564, 87.094003));
            pointsEverest.add(new LatLon(27.920038, 87.072030));
            pointsEverest.add(new LatLon(27.920038, 87.072030));
            repository.save(new Location("Mt. Everest", new LatLon(27.988121, 86.924975), Difficulty.HIGH, Duration.ofHours(12), pointsEverest, new ArrayList<Double>()));




            userDetailsService.saveUser("admin", "admin", "ADMIN");
			userDetailsService.saveUser("user", "pw", "USER");
/*
            String elevationCall = "https://maps.googleapis.com/maps/api/elevation/json?locations=39.7391536,-104.9847034&key=AIzaSyAdXfqEgqkjkDBBFC2dRoWU_-dST-S34dk";
            RestTemplate restTemplate = new RestTemplate();

            Elevation elevation = restTemplate.getForObject(elevationCall, Elevation.class);
            log.info(elevation.getResults().get(0).getElevation()); */

		};
	}

}
