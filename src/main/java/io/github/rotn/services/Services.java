package io.github.rotn.services;

import com.google.common.reflect.ClassPath;
import io.github.rotn.annot.RequestHandler;
import io.github.rotn.annot.Service;
import io.github.rotn.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.lang.reflect.Method;

public final class Services{
    private static final Logger logger = LoggerFactory.getLogger(Services.class);

    public static void loadAll() throws Exception{
        ClassPath classpath = ClassPath.from(ClassLoader.getSystemClassLoader());
        for(ClassPath.ClassInfo info : classpath.getTopLevelClassesRecursive("io.github.rotn.services")){
            Class<?> infoClass = info.load();
            if(infoClass.isAnnotationPresent(Service.class)){
                logger.info("Loading service {}", infoClass.getDeclaredAnnotation(Service.class));
                Object serviceInstance = Modules.getInjector().getInstance(infoClass);
                for(Method method : infoClass.getDeclaredMethods()){
                    if(method.isAnnotationPresent(RequestHandler.class)){
                        RequestHandler handler = method.getDeclaredAnnotation(RequestHandler.class);
                        switch(handler.method()){
                            case get:{
                                Spark.get(handler.path(), handler.produces(), new RequestMappedProxy(serviceInstance, method));
                                break;
                            }
                            case delete:{
                                Spark.delete(handler.path(), handler.produces(), new RequestMappedProxy(serviceInstance, method));
                                break;
                            }
                            case post:{
                                Spark.delete(handler.path(), handler.produces(), new RequestMappedProxy(serviceInstance, method));
                                break;
                            }
                            default: throw new RuntimeException("Unhandled request handler type: " + handler.method());
                        }
                    }
                }
            }
        }
    }

    private static final class RequestMappedProxy
    implements Route{
        private final Object instance;
        private final Method invoke;

        private RequestMappedProxy(Object instance, Method invoke){
            this.instance = instance;
            this.invoke = invoke;
            try{
                this.invoke.setAccessible(true);
            } catch(Exception e){
                throw new RuntimeException("Unable to obtain public access to a private function: " + e);
            }
        }

        @Override
        public Object handle(Request request, Response response) throws Exception {
            return this.invoke.invoke(this.instance, request, response);
        }
    }
}