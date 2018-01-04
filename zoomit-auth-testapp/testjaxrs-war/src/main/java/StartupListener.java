import be.zoomit.auth.ZoomitAuthContextProvider;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.HashMap;
import java.util.Map;

@WebListener
public class StartupListener implements ServletContextListener {

    public static final String HTTP_SERVLET_LAYER_NAME = "HttpServlet";
    public static final String ZOOMIT_AUTH_MODULE_DESCRIPTION = "Zoomit Authentication Module";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        Map<String, String> authContextProperties = new HashMap<>();
        ZoomitAuthContextProvider zoomitAuthContextProvider = new ZoomitAuthContextProvider(authContextProperties, null);
        String appContext = getAppContext(servletContext);
        AuthConfigFactory authConfigFactory = AuthConfigFactory.getFactory();

        authConfigFactory.registerConfigProvider(
                zoomitAuthContextProvider,
                HTTP_SERVLET_LAYER_NAME,
                appContext,
                ZOOMIT_AUTH_MODULE_DESCRIPTION
        );
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    public String getAppContext(ServletContext servletContext) {
        return servletContext.getVirtualServerName() + " " + servletContext.getContextPath();
    }
}