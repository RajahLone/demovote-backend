package fr.triplea.demovote.spring;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@ComponentScan(basePackages = { "fr.triplea.demovote.web" })
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer 
{

  public MvcConfig() { super(); }

  @Override
  public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) { configurer.enable(); }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) { registry.addResourceHandler("/resources/**").addResourceLocations("/", "/resources/"); }


  @Bean
  WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() { return (factory) -> factory.setRegisterDefaultServlet(true); }

}
