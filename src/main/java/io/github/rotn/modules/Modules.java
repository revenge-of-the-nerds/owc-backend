package io.github.rotn.modules;

import com.google.common.reflect.ClassPath;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.rotn.annot.Module;
import io.github.rotn.annot.ModulesPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public final class Modules{
    private static final Logger logger = LoggerFactory.getLogger(Modules.class);
    private static final Map<String, AbstractModule> modules = new HashMap<>();
    private static Injector injector = null;

    public static Injector getInjector(){
        return injector;
    }

    public static void install(Class<?> entryType) throws Exception{
        if(!entryType.isAnnotationPresent(ModulesPath.class)){
            throw new RuntimeException(entryType + " needs @ModulesPath above it to load modules");
        }

        ClassPath classpath = ClassPath.from(ClassLoader.getSystemClassLoader());

        ModulesPath modulePaths = entryType.getDeclaredAnnotation(ModulesPath.class);
        for(String path : modulePaths.value()){
            logger.info("Loading modules from {}", path);
            for(ClassPath.ClassInfo info : classpath.getTopLevelClassesRecursive(path)){
                Class<?> infoClass = info.load();
                if(infoClass.isAnnotationPresent(Module.class) && AbstractModule.class.isAssignableFrom(infoClass)){
                    logger.info("Loading module {}", infoClass.getSimpleName());
                    modules.put(infoClass.getDeclaredAnnotation(Module.class).value(), newInstance(infoClass));
                }
            }
        }

        injector = Guice.createInjector(modules.values());
    }

    private static <T> T newInstance(Class tClass){
        try{
            Constructor<T> tConstructor = tClass.getDeclaredConstructor();
            tConstructor.setAccessible(true);
            return tConstructor.newInstance();
        } catch(Exception e){
            logger.error("Failed to create new instance of {}", tClass, e);
            System.exit(-1);
            return null;
        }
    }
}