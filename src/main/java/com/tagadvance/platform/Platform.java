package com.tagadvance.platform;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class Platform {

	private static final Logger log = LoggerFactory.getLogger(Platform.class);

	private final OperatingSystem os;

	private final List<Bucket> bucketList = new ArrayList<>();

	private final LoadingCache<Class<? extends Predicate<OperatingSystem>>, Predicate<OperatingSystem>> cache =
			CacheBuilder.newBuilder().build(
					new CacheLoader<Class<? extends Predicate<OperatingSystem>>, Predicate<OperatingSystem>>() {
						@Override
						public Predicate<OperatingSystem> load(
								Class<? extends Predicate<OperatingSystem>> c)
								throws InstantiationException, IllegalAccessException {
							return c.newInstance();
						}
					});

	/**
	 * 
	 * @param os
	 * @throws NullPointerException if <code>os</code> is null
	 */
	public Platform(OperatingSystem os) {
		super();
		this.os = checkNotNull(os);
	}

	/**
	 * 
	 * @param clazz
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 */
	public void register(Class<?> clazz) {
		try {
			Constructor<?> constructor = clazz.getConstructor();
			PlatformDependency annotation = constructor.getAnnotation(PlatformDependency.class);
			if (annotation == null) {
				String message = String.format("missing @%s on default constructor of %s",
						PlatformDependency.class.getSimpleName(), clazz.getName());
				throw new IllegalArgumentException(message);
			}
			Callable<?> callable = () -> {
				return constructor.newInstance();
			};
			Bucket bucket = new Bucket(clazz, annotation, callable);
			bucketList.add(bucket);
		} catch (NoSuchMethodException e) {
			String message = String.format("%s is missing a default constructor", clazz.getName());
			throw new IllegalArgumentException(message, e);
		}
	}

	/**
	 * 
	 * @param clazz
	 */
	public void registerStaticFactory(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		registerMethods(methods, null);
	}

	/**
	 * 
	 * @param o
	 */
	public void registerFactory(Object o) {
		Method[] methods = o.getClass().getMethods();
		registerMethods(methods, o);
	}

	private void registerMethods(Method[] methods, Object o) {
		boolean isNull = o == null;
		Predicate<Method> filter = method -> {
			int modifiers = method.getModifiers();
			boolean isStatic = Modifier.isStatic(modifiers);
			return isNull == isStatic;
		};
		Arrays.asList(methods).stream().filter(filter).forEach(method -> {
			PlatformDependency annotation = method.getAnnotation(PlatformDependency.class);
			if (annotation != null) {
				Class<?> returnType = method.getReturnType();
				Callable<?> callable = () -> {
					return method.invoke(o);
				};
				Bucket bucket = new Bucket(returnType, annotation, callable);
				bucketList.add(bucket);
			}
		});
	}

	public boolean isResolvable(Class<?> type) {
		checkNotNull(type);
		return findBucket(type).isPresent();
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T resolve(Class<T> type) throws PlatformDependencyException {
		checkNotNull(type);
		Optional<Bucket> optional = findBucket(type);
		if (optional.isPresent()) {
			Callable<?> callable = optional.get().getCallable();
			try {
				return (T) callable.call();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new PlatformDependencyException(e);
			} catch (Exception e) {
				/*
				 * I am keeping this block separate in case I want to handle the above exceptions
				 * individually in the future
				 */
				throw new PlatformDependencyException(e);
			}
		}

		String message = "missing platform dependency for type " + type;
		throw new UnsupportedPlatformException(message);
	}

	private Optional<Bucket> findBucket(Class<?> type) {
		Predicate<Bucket> providesClassPredicate =
				(bucket) -> type.isAssignableFrom(bucket.getProvidesClass());
		Predicate<Bucket> platformPredicate = (bucket) -> {
			for (Class<? extends Predicate<OperatingSystem>> c : bucket.getAnnotation().value()) {
				try {
					Predicate<OperatingSystem> predicate = cache.get(c);
					if (!predicate.test(os)) {
						return false;
					}
				} catch (ExecutionException e) {
					log.warn("", e);
				}
			}
			return true;
		};
		return bucketList.stream().filter(providesClassPredicate).filter(platformPredicate)
				.findFirst();
	}

	/**
	 * Internal value object.
	 */
	private static class Bucket {

		private final Class<?> providesClass;

		private final PlatformDependency annotation;

		private final Callable<?> callable;

		public Bucket(Class<?> providesClass, PlatformDependency annotation, Callable<?> callable) {
			super();
			this.providesClass = providesClass;
			this.annotation = annotation;
			this.callable = callable;
		}

		public Class<?> getProvidesClass() {
			return providesClass;
		}

		public PlatformDependency getAnnotation() {
			return annotation;
		}

		public Callable<?> getCallable() {
			return callable;
		}

	}

}
