package net.andreynikolaev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WebApplication extends SpringBootServletInitializer {

    static WebApplication webApplication;
    ConfigurableApplicationContext configurableApplicationContext;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebApplication.class);
    }

    public WebApplication() {

    }

    public static void main(String[] args) {
        webApplication = new WebApplication();
        webApplication.configurableApplicationContext = SpringApplication.run(WebApplication.class, args);

    }

    public WebApplication getInstance() {
        return this;
    }

    public static void stopApp() {
        webApplication.configurableApplicationContext.close();
    }

}
