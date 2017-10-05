# Java Cross-Platform Magic

Java Cross-Platform Magic uses the service locator anti-pattern to register and resolve cross-platform dependencies at runtime.

## Install
Gradle:
```groovy
repositories {
    jcenter()
    maven {
        url  "https://dl.bintray.com/tagadvance/GitHub" 
    }
}

dependencies {
	compile group: 'com.tagadvance', name: 'java-cross-platform-magic', version: 'alpha'
}
```

## Examples
```java
public interface Foo {

}

public class Foozle implements Foo {

	@PlatformDependency({WindowsPredicate.class})
	public Foozle() {

	}

}

{
	OperatingSystem os = OperatingSystem.getInstance();
	Platform platform = new Platform(os);
	platform.register(Foozle.class);
	try {
		Foo foo = platform.resolve(Foo.class);
	} catch (PlatformDependencyException e) {
		e.printStackTrace();
	}
}
```

```java
public class FoozleFactory {

	public FoozleFactory() {}

	@PlatformDependency({WindowsPredicate.class})
	public Foo create() {
		return new Foozle();
	}

}

{
	OperatingSystem os = OperatingSystem.getInstance();
	Platform platform = new Platform(os);
	platform.registerFactory(new FoozleFactory());
	try {
		Foo foo = platform.resolve(Foo.class);
	} catch (PlatformDependencyException e) {
		e.printStackTrace();
	}
}
```

```java
public class FoozleStaticFactory {

	private FoozleStaticFactory() {}
	
	@PlatformDependency({WindowsPredicate.class})
	public static Foo create() {
		return new Foozle();
	}

}

{
	OperatingSystem os = OperatingSystem.getInstance();
	Platform platform = new Platform(os);
	platform.registerStaticFactory(FoozleStaticFactory.class);
	try {
		Foo foo = platform.resolve(Foo.class);
	} catch (PlatformDependencyException e) {
		e.printStackTrace();
	}
}
```

## Legal Mumbo Jumbo
Oracle and Java are registered trademarks of Oracle and/or its affiliates. Other names may be trademarks of their respective owners.