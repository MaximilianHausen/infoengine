package net.totodev.infoengine.ecs;

import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.lang.invoke.*;
import java.util.Arrays;

public abstract class BaseSystem {
    private Scene scene;
    protected final Scene getScene() {
        return scene;
    }

    @MustBeInvokedByOverriders
    public void added(Scene scene) {
        this.scene = scene;
    }
    @MustBeInvokedByOverriders
    public void start(Scene scene) {
        getEventSubscribers().forEach(p -> scene.events.subscribe(p.getOne(), p.getTwo()));
    }

    @MustBeInvokedByOverriders
    public void stop(Scene scene) {
        getEventSubscribers().forEach(p -> scene.events.unsubscribe(p.getOne(), p.getTwo()));
    }
    @MustBeInvokedByOverriders
    public void removed(Scene scene) {
        this.scene = null;
    }

    private MutableList<Pair<String, MethodHandle>> eventSubscribers;

    public final MutableList<Pair<String, MethodHandle>> getEventSubscribers() {
        if (eventSubscribers != null) return eventSubscribers;

        eventSubscribers = Lists.mutable.empty();

        Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(EventSubscriber.class))
                .forEach(m -> {
                    m.setAccessible(true);
                    try {
                        eventSubscribers.add(Tuples.pair(
                                m.getAnnotation(EventSubscriber.class).value(),
                                MethodHandles.lookup().unreflect(m).bindTo(this)
                        ));
                    } catch (IllegalAccessException e) {
                        Logger.log(LogLevel.Error, "System", "Could not access method " + m.getName() + " when reading event subscribers.");
                    }
                });

        return eventSubscribers;
    }
}
