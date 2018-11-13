package top.crossoverjie.cicada.server.route;

import io.netty.handler.codec.http.QueryStringDecoder;
import top.crossoverjie.cicada.server.annotation.CicadaRoute;
import top.crossoverjie.cicada.server.enums.StatusEnum;
import top.crossoverjie.cicada.server.exception.CicadaException;
import top.crossoverjie.cicada.server.util.ClassScanner;
import top.crossoverjie.cicada.server.util.PathUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/11/13 01:48
 * @since JDK 1.8
 */
public class RouterScanner {

    private static Map<String,Method> routes = null ;

    private volatile static RouterScanner routerScanner ;

    /**
     * get single Instance
     * @return
     */
    public static RouterScanner getInstance(){
        if (routerScanner == null){
            synchronized (RouterScanner.class){
                if (routerScanner == null){
                    routerScanner = new RouterScanner() ;
                }
            }
        }
        return routerScanner ;
    }

    private RouterScanner(){}

    /**
     * get route method
     * @param queryStringDecoder
     * @param packageName
     * @return
     * @throws Exception
     */
    public Method routeMethod(QueryStringDecoder queryStringDecoder, String packageName) throws Exception {
        if (routes == null){
            routes = new HashMap<>(16) ;
            loadRouteMethods(packageName) ;
        }

        String url = PathUtil.getRoutePath(queryStringDecoder.path());

        Method method = routes.get(url);

        if (method == null){
            throw new CicadaException(StatusEnum.NOT_FOUND) ;
        }

        return method;

    }


    private void loadRouteMethods(String packageName) throws Exception {
        Set<Class<?>> classes = ClassScanner.getClasses(packageName) ;

        for (Class<?> aClass : classes) {
            Method[] declaredMethods = aClass.getMethods();

            for (Method method : declaredMethods) {
                CicadaRoute annotation = method.getAnnotation(CicadaRoute.class) ;
                if (annotation == null){
                    continue;
                }
                routes.put(annotation.value(),method) ;
            }
        }
    }
}