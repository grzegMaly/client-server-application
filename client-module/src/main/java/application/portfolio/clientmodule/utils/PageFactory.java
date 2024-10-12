package application.portfolio.clientmodule.utils;

import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PageFactory {

    private static final Map<Class<? extends Page>, Page> classPageMap = new ConcurrentHashMap<>();

    public static <T extends Page> T getInstance(Class<T> classClass) {

        if (classPageMap.containsKey(classClass)) {
            return classClass.cast(classPageMap.get(classClass));
        }

        Page page = createPage(classClass);
        if (page != null) {
            classPageMap.put(classClass, page);
        }

        return classClass.cast(page);
    }

    private static Page createPage(Class<? extends Page> classClass) {
        try {
            var constructor = classClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            var tempPage = constructor.newInstance();


            if (tempPage.usedAsScene().get()) {
                if (!tempPage.createScene()) {
                    return null;
                }
            }

            CompletableFuture<Boolean> loadResult = tempPage.loadPage();
            return loadResult.join() ? tempPage : null;

        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to create page: " + classClass.getName(), e);
        }
    }
}