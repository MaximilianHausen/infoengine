package net.totodev.infoengine.ecs;

import net.totodev.infoengine.core.CoreEvents;
import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.*;
import org.eclipse.collections.impl.tuple.Tuples;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.lang.invoke.*;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class BaseSystem {
    private Scene scene;
    private MutableList<Pair<String, MethodHandle>> eventSubscribers;
    private MutableList<Triple<Boolean, Class<?>, MethodHandle>> cachedComponentSetters;

    protected final Scene getScene() {
        return scene;
    }

    @MustBeInvokedByOverriders
    public void added(Scene scene) {
        this.scene = scene;
    }
    @MustBeInvokedByOverriders
    @SuppressWarnings("unchecked")
    public void start(Scene scene) {
        getCachedComponentSetters().forEach(t -> {
            try {
                if (t.getOne())
                    t.getThree().invoke(this, scene.getGlobalComponent((Class<? extends IGlobalComponent>) t.getTwo()));
                else
                    t.getThree().invoke(this, scene.getComponent((Class<? extends IComponent>) t.getTwo()));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        getEventSubscribers().forEach(p -> scene.events.subscribe(p.getOne(), p.getTwo()));
    }

    @MustBeInvokedByOverriders
    public void stop(Scene scene) {
        getEventSubscribers().forEach(p -> scene.events.unsubscribe(p.getOne(), p.getTwo()));

        getCachedComponentSetters().forEach(t -> {
            try {
                t.getThree().invoke(this, null);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
    @MustBeInvokedByOverriders
    public void removed(Scene scene) {
        this.scene = null;
    }

    public final MutableList<Pair<String, MethodHandle>> getEventSubscribers() {
        if (eventSubscribers != null) return eventSubscribers;

        eventSubscribers = Lists.mutable.empty();

        Stream.concat(Arrays.stream(this.getClass().getDeclaredMethods()), Arrays.stream(BaseSystem.class.getDeclaredMethods()))
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

    public final MutableList<Triple<Boolean, Class<?>, MethodHandle>> getCachedComponentSetters() {
        if (cachedComponentSetters != null) return cachedComponentSetters;

        cachedComponentSetters = Lists.mutable.empty();

        Arrays.stream(this.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(CachedComponent.class))
                .forEach(f -> {
                    Class<?> fieldType = f.getType();
                    try {
                        f.setAccessible(true);
                        if (IComponent.class.isAssignableFrom(fieldType))
                            cachedComponentSetters.add(Tuples.triple(false, fieldType, MethodHandles.lookup().unreflectSetter(f)));
                        else if (IGlobalComponent.class.isAssignableFrom(fieldType))
                            cachedComponentSetters.add(Tuples.triple(true, fieldType, MethodHandles.lookup().unreflectSetter(f)));
                        else
                            Logger.log(LogLevel.Error, "System", "Skipped cached component because the type " + fieldType.getName() + " of field " + f.getName() + " does not implement IComponent or IGlobalComponent.");
                    } catch (InaccessibleObjectException | SecurityException e) {
                        Logger.log(LogLevel.Error, "System", "Could not set field " + f.getName() + " as accessible when reading cached components.");
                    } catch (IllegalAccessException e) {
                        Logger.log(LogLevel.Error, "System", "Could not access field " + f.getName() + " when reading cached components.");
                    }
                });

        return cachedComponentSetters;
    }

    @EventSubscriber(CoreEvents.ComponentAdded)
    public void componentAdded(IComponent component) {
        Class<? extends IComponent> componentType = component.getClass();
        getCachedComponentSetters().forEach(t -> {
            if (!t.getOne() && t.getTwo() == componentType) {
                try {
                    t.getThree().invoke(this, component);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    @EventSubscriber(CoreEvents.GlobalComponentAdded)
    public void globalComponentAdded(IGlobalComponent component) {
        Class<? extends IGlobalComponent> componentType = component.getClass();
        getCachedComponentSetters().forEach(t -> {
            if (t.getOne() && t.getTwo() == componentType) {
                try {
                    t.getThree().invoke(this, component);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @EventSubscriber(CoreEvents.ComponentRemoved)
    public void componentRemoved(IComponent component) {
        Class<? extends IComponent> componentType = component.getClass();
        getCachedComponentSetters().forEach(t -> {
            if (!t.getOne() && t.getTwo() == componentType) {
                try {
                    t.getThree().invoke(this, null);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    @EventSubscriber(CoreEvents.GlobalComponentRemoved)
    public void globalComponentRemoved(IGlobalComponent component) {
        Class<? extends IGlobalComponent> componentType = component.getClass();
        getCachedComponentSetters().forEach(t -> {
            if (t.getOne() && t.getTwo() == componentType) {
                try {
                    t.getThree().invoke(this, null);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
