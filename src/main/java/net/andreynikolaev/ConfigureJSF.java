package net.andreynikolaev;

import com.sun.faces.config.FacesInitializer;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import net.andreynikolaev.api.RemoteServiceTest;
import org.apache.catalina.Context;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.SimpleRemoteStatelessSessionProxyFactoryBean;
import org.springframework.jndi.JndiTemplate;

/**
 *
 * @author Andrey Nikolaev
 */
@Configuration
@EnableConfigurationProperties
public class ConfigureJSF {

    @Bean
    public ServletRegistrationBean facesServletRegistration() {
        FacesServlet servlet = new FacesServlet();
        ServletRegistrationBean servletRegistrationBean = new JsfServletRegistrationBean(servlet, "/*");
        return servletRegistrationBean;
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        TomcatContextCustomizer contextCustomizer = (Context context) -> {
            context.addWelcomeFile("index.xhtml");
        };
        factory.addContextCustomizers(contextCustomizer);
        return factory;
    }

    @Bean
    public SimpleRemoteStatelessSessionProxyFactoryBean sessionDataFromPictureSchooter() {
        SimpleRemoteStatelessSessionProxyFactoryBean simpleRemoteStatelessSessionProxyFactoryBean = new SimpleRemoteStatelessSessionProxyFactoryBean();
        simpleRemoteStatelessSessionProxyFactoryBean.setJndiTemplate(jndiTemplate());
        simpleRemoteStatelessSessionProxyFactoryBean.setJndiName("ServiceTestGradle/CalculatorBean/remote");
        simpleRemoteStatelessSessionProxyFactoryBean.setBusinessInterface(RemoteServiceTest.class);
        simpleRemoteStatelessSessionProxyFactoryBean.setLookupHomeOnStartup(false);
        return simpleRemoteStatelessSessionProxyFactoryBean;

    }
    
    @Bean
    public JndiTemplate jndiTemplate() {
        JndiTemplate jndiTemplate = new JndiTemplate();
        org.jnp.interfaces.NamingContext ff;
        Properties properties = new Properties();
        properties.put(javax.naming.Context.URL_PKG_PREFIXES, "jboss.naming:org.jnp.interfaces");
        properties.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        properties.put(javax.naming.Context.PROVIDER_URL, "jnp://localhost:1099");
        jndiTemplate.setEnvironment(properties);

        return jndiTemplate;
    }

    public class JsfServletRegistrationBean extends ServletRegistrationBean {

        private JsfServletRegistrationBean(FacesServlet servlet, String xhtml) {
            super(servlet, xhtml);
        }

        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
            FacesInitializer facesInitializer = new FacesInitializer();
            Set<Class<?>> clazz = new HashSet<>();
            clazz.add(ConfigureJSF.class);
            facesInitializer.onStartup(clazz, servletContext);
        }
    }
}
