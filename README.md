# java元注解和反射实现简单MVC框架
> Servlet 3.0 之前使用web.xml文件进行配置，例如：

    <servlet>
    
            <serlvet-name>myServlet</servlet-name>
    
            <servlet-calss>MyServlet的类路径</servlet-class>
    
    </servlet>
    
    <servlet-mapping>
    
    <serlvet-name>myServlet</servlet-name>
    
    <url-pattern>/servlet/myServlet</url-pattern>
    
    </servlet-mapping>
    
> Servlet 3.0 后出现了基于注解的 Servlet 。

    @WebServlet(name = "dispatcherServlet", urlPatterns = "/*", loadOnStartup = 1,
            initParams = {@WebInitParam(name = "base-package", value = "com.kinson.myspring")})
            
> java元注解介绍
[java元注解介绍](https://www.cnblogs.com/kingsonfu/p/10634174.html)
[java反射](https://www.cnblogs.com/kingsonfu/p/10486709.html)