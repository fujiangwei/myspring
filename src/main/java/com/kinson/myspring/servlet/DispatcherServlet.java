package com.kinson.myspring.servlet;

import com.kinson.myspring.annotation.*;
import com.kinson.myspring.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * descripiton:
 *
 * @author: kinson(2219945910@qq.com)
 * @date: 2019/4/1
 * @time: 0:36
 * @modifier:
 * @since:
 */
// @WebServlet 以前我们定义一个 Servlet ，需要在 web.xml 中去配置，不过在 Servlet 3.0 后出现了基于注解的 Servlet 。
@WebServlet(name = "dispatcherServlet", urlPatterns = "/*", loadOnStartup = 1,
        initParams = {@WebInitParam(name = "base-package", value = "com.kinson.myspring")})
public class DispatcherServlet extends HttpServlet {
    /**
     * 扫描的包
     */
    private String basePackage = "";

    /**
     * 基包下面所有的带包路径权限定类名
     */
    private List<String> packageNames = new ArrayList<String>();

    /**
     * 注解实例化 格式为注解上的名称：注解实例化对象
     */
    private Map<String, Object> instanceMap = new HashMap<String, Object>();

    /**
     * 包路径权限定类名称：注解上的名称
     */
    private Map<String, String> nameMap = new HashMap<String, String>();

    /**
     * Url地址和方法的映射关系：注解上的名称
     */
    private Map<String, Method> urlMethodMap = new HashMap<String, Method>();

    /**
     * Method和权限定类名的映射关系，用于通过Method找到该方法的对象利用反射执行
     */
    private Map<Method, String> methodPackageMap = new HashMap<Method, String>();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {

        //获取请求URI,eg：/user/hello
        final String uri = req.getRequestURI();
        final String contextPath = req.getContextPath();
        final String path = uri.replaceAll(contextPath, "");

        //提取出 URL，通过 URL 映射到Method 上，然后通过反射的方式进行调用即可。
        Method method = urlMethodMap.get(path);
        if (null != method) {
            //通过方法获取方法所在的包路径
            String packageName = methodPackageMap.get(method);
            //通过包路径获取注解上的名称
            String controllerName = nameMap.get(packageName);
            //通过注解名称获取对应的实例对象
            UserController userController = (UserController) instanceMap.get(controllerName);

            try {
                //设置方法可访问
                method.setAccessible(Boolean.TRUE);
                //领用反射进行方法调用
                method.invoke(userController);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化
     * 1、扫描基包下的类，得到信息 A。
     * 2、对于 @Controller/@Service/@Repository 注解而言，我们需要拿到对应的名称，并初始化它们修饰的类，形成映射关系 B。
     * 3、扫描类中的字段，如果发现有 @Qualifier 的话，我们需要完成注入。
     * 4、扫描 @RequestMapping，完成 URL 到某一个 Controller 的某一个方法上的映射关系 C。
     *
     * @param config
     */
    @Override
    public void init(ServletConfig config) {
        System.out.println("开始初始化。。。。。。");

        //通过初始化参数直接将需要扫描的基包路径传入
        basePackage = config.getInitParameter("base-package");

        try {
            //扫描基包得到全部的带包路径权限定类名
            scanBasePackage(basePackage);
            //把代用注解的类实例化方如Map中，key为注解上的名称
            instance(packageNames);
            //IOC注入
            springIOC();
            //完成Url地址与方法的映射关系
            handleUrlMethodMap();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        System.out.println("初始化结束。。。。。。");
    }

    /**
     * 通过初始化参数直接将需要扫描的基包路径传入
     *
     * @param basePackage 基包路径
     */
    private void scanBasePackage(String basePackage) {
        //加载类资源路径
        URL url = this.getClass().getClassLoader()
                .getResource(basePackage.replaceAll("\\.", "/"));

        File basePackageFile = new File(url.getPath());
        System.out.println("scan:" + basePackageFile);
        File[] childFiles = basePackageFile.listFiles();
        for (File file : childFiles) {
            //目录递归扫描
            if (file.isDirectory()) {
                scanBasePackage(basePackage + "." + file.getName());
            } else if (file.isFile()) {
                //Controller.class====Controller，即去掉.class
                System.out.println(">>>>>>>>>>> " + file.getName() + "====" + file.getName().split("\\.")[0]);
                packageNames.add(basePackage + "." + file.getName().split("\\.")[0]);
            }
        }
    }

    /**
     * 把代用注解的类实例化方如Map中，key为注解上的名称
     *
     * @param packageNames 包路径名集合
     */
    private void instance(List<String> packageNames) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (packageNames.size() < 1) {
            return;
        }

        for (String packageName : packageNames) {
            //根据包路径获取Class对象
            Class<?> clazz = Class.forName(packageName);
            //Controller注解处理
            if (clazz.isAnnotationPresent(Controller.class)) {
                Controller controller = (Controller) clazz.getAnnotation(Controller.class);
                String controllerName = controller.value();

                instanceMap.put(controllerName, clazz.newInstance());
                nameMap.put(packageName, controllerName);

                System.out.println("Controller :" + packageName + ", value :" + controllerName);
            } else if (clazz.isAnnotationPresent(Service.class)) {
                //Service注解处理
                Service service = (Service) clazz.getAnnotation(Service.class);
                String serviceName = service.value();

                instanceMap.put(serviceName, clazz.newInstance());
                nameMap.put(packageName, serviceName);

                System.out.println("Service :" + packageName + ", value :" + serviceName);
            } else if (clazz.isAnnotationPresent(Repository.class)) {
                //Repository注解处理
                Repository repository = clazz.getAnnotation(Repository.class);
                String repositoryName = repository.value();

                instanceMap.put(repositoryName, clazz.newInstance());
                nameMap.put(packageName, repositoryName);
                System.out.println("Repository :" + packageName + ", value :" + repositoryName);
            }
        }
    }

    /**
     * IOC注入
     */
    private void springIOC() throws IllegalAccessException {
        for (Map.Entry<String, Object> instanceEntry : instanceMap.entrySet()) {
            //获取当前对象的所有字段
            Field[] declaredFields = instanceEntry.getValue().getClass().getDeclaredFields();

            for (Field field : declaredFields) {
                //字段上是否有Qualifier注解
                if (field.isAnnotationPresent(Qualifier.class)) {
                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    String qualifierName = qualifier.value();

                    //设置当前的字段为可访问
                    field.setAccessible(Boolean.TRUE);
                    //设置当前字段
                    field.set(instanceEntry.getValue(), instanceMap.get(qualifierName));

                    System.out.println("==========" + field);
                }
            }
        }
    }

    /**
     * Url地址与方法的映射关系
     *
     * @throws ClassNotFoundException
     */
    private void handleUrlMethodMap() throws ClassNotFoundException {
        if (packageNames.size() < 1) {
            return;
        }

        for (String packageName : packageNames) {
            //根据包路径获取Class对象
            Class clazz = Class.forName(packageName);

            //当前类是否有Controller注解
            if (clazz.isAnnotationPresent(Controller.class)) {
                //获取当前Controller类的所有方法
                Method[] methods = clazz.getMethods();
                //拼接访问URI
                StringBuffer baseUrl = new StringBuffer();

                //当前Controller是否有RequestMapping注解
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                    //XxxController类上的requestMapping值
                    baseUrl.append(requestMapping.value());
                }

                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        //XxxController类上的响应方法上的requestMapping值
                        baseUrl.append(requestMapping.value());

                        //URL 提取出来，映射到 Controller 的 Method 上。
                        System.out.println("baseUrl : " + baseUrl.toString());
                        urlMethodMap.put(baseUrl.toString(), method);
                        methodPackageMap.put(method, packageName);
                    }
                }
            }
        }
    }
}
