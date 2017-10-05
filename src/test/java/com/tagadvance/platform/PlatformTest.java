package com.tagadvance.platform;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.google.common.testing.NullPointerTester;
import com.tagadvance.platform.predicates.Amd64Predicate;
import com.tagadvance.platform.predicates.LinuxPredicate;
import com.tagadvance.platform.predicates.MacPredicate;
import com.tagadvance.platform.predicates.WindowsPredicate;

/**
 * 
 * @see http://lopica.sourceforge.net/os.html
 */
public class PlatformTest {

	@Test
	public void testConstructor() {
		NullPointerTester nullPointerTester = new NullPointerTester();
		nullPointerTester.testAllPublicConstructors(Platform.class);
	}

	@Test
	public void testWindowsResolution() throws PlatformDependencyException {
		String name = "Windows 10", version = "10.0", architecture = "amd64";
		OperatingSystem os = new OperatingSystem(name, version, architecture);
		Platform platform = new Platform(os);
		assertFalse(platform.isResolvable(PlatformDependencyMarker.class));
		platform.register(WindowsPlatformDependency.class);
		assertTrue(platform.isResolvable(PlatformDependencyMarker.class));
		PlatformDependencyMarker marker = platform.resolve(PlatformDependencyMarker.class);
		assertNotNull(marker);
	}

	@Test
	public void testLinuxResolution() throws PlatformDependencyException {
		String name = "Linux", version = "4.4.0", architecture = "x86_64";
		OperatingSystem os = new OperatingSystem(name, version, architecture);
		Platform platform = new Platform(os);
		assertFalse(platform.isResolvable(PlatformDependencyMarker.class));
		platform.register(LinuxPlatformDependency.class);
		assertTrue(platform.isResolvable(PlatformDependencyMarker.class));
		PlatformDependencyMarker marker = platform.resolve(PlatformDependencyMarker.class);
		assertNotNull(marker);
	}

	@Test
	public void testMacResolution() throws PlatformDependencyException {
		String name = "Mac OS X", version = "4.4.0", architecture = "x86_64";
		OperatingSystem os = new OperatingSystem(name, version, architecture);
		Platform platform = new Platform(os);
		assertFalse(platform.isResolvable(PlatformDependencyMarker.class));
		platform.register(MacPlatformDependency.class);
		assertTrue(platform.isResolvable(PlatformDependencyMarker.class));
		PlatformDependencyMarker marker = platform.resolve(PlatformDependencyMarker.class);
		assertNotNull(marker);
	}

	@Test
	public void testWindowsAmd64Resolution() throws PlatformDependencyException {
		String name = "Windows 10", version = "10.0", architecture = "amd64";
		OperatingSystem os = new OperatingSystem(name, version, architecture);
		Platform platform = new Platform(os);
		assertFalse(platform.isResolvable(PlatformDependencyMarker.class));

		platform.register(LinuxPlatformDependency.class);
		assertFalse(platform.isResolvable(PlatformDependencyMarker.class));
		platform.register(MacPlatformDependency.class);
		assertFalse(platform.isResolvable(PlatformDependencyMarker.class));

		platform.register(WindowsAmd64PlatformDependency.class);
		assertTrue(platform.isResolvable(PlatformDependencyMarker.class));
		PlatformDependencyMarker marker = platform.resolve(PlatformDependencyMarker.class);
		assertNotNull(marker);
	}

	@Test
	public void testWindowsAmd64ResolutionWithX86Architecture() throws PlatformDependencyException {
		String name = "Windows 10", version = "10.0", architecture = "x86";
		OperatingSystem os = new OperatingSystem(name, version, architecture);
		Platform platform = new Platform(os);
		platform.register(WindowsAmd64PlatformDependency.class);
		assertFalse(platform.isResolvable(PlatformDependencyMarker.class));
		assertThrows(UnsupportedPlatformException.class, () -> {
			platform.resolve(PlatformDependencyMarker.class);
		});
	}

	@Test
	public void testWindowsResolutionViaSupplier() throws PlatformDependencyException {
		String name = "Windows 10", version = "10.0", architecture = "amd64";
		OperatingSystem os = new OperatingSystem(name, version, architecture);
		Platform platform = new Platform(os);
		assertFalse(platform.isResolvable(PlatformDependencyMarker.class));

		platform.registerFactory(new WindowsPlatformDependencySupplier());
		assertTrue(platform.isResolvable(PlatformDependencyMarker.class));
		PlatformDependencyMarker marker = platform.resolve(PlatformDependencyMarker.class);
		assertNotNull(marker);
	}

	@Test
	public void testWindowsResolutionViaStaticFactoryMethod() throws PlatformDependencyException {
		String name = "Windows 10", version = "10.0", architecture = "amd64";
		OperatingSystem os = new OperatingSystem(name, version, architecture);
		Platform platform = new Platform(os);
		assertFalse(platform.isResolvable(PlatformDependencyMarker.class));

		platform.registerStaticFactory(PlatformDependencyStaticFactory.class);
		assertTrue(platform.isResolvable(PlatformDependencyMarker.class));
		PlatformDependencyMarker marker = platform.resolve(PlatformDependencyMarker.class);
		assertTrue(marker instanceof WindowsPlatformDependency);
	}

	private static interface PlatformDependencyMarker {

	}

	private static class WindowsPlatformDependency implements PlatformDependencyMarker {

		@PlatformDependency(WindowsPredicate.class)
		public WindowsPlatformDependency() {

		}

	}

	private static class LinuxPlatformDependency implements PlatformDependencyMarker {

		@PlatformDependency(LinuxPredicate.class)
		public LinuxPlatformDependency() {

		}

	}

	private static class MacPlatformDependency implements PlatformDependencyMarker {

		@PlatformDependency(MacPredicate.class)
		public MacPlatformDependency() {

		}

	}

	private static class WindowsAmd64PlatformDependency implements PlatformDependencyMarker {

		@PlatformDependency({WindowsPredicate.class, Amd64Predicate.class})
		public WindowsAmd64PlatformDependency() {

		}

	}

	private static class WindowsPlatformDependencySupplier
			implements Supplier<PlatformDependencyMarker> {

		@Override
		@PlatformDependency(WindowsPredicate.class)
		public PlatformDependencyMarker get() {
			return new WindowsPlatformDependency();
		}

	}

	private static class PlatformDependencyStaticFactory {

		@PlatformDependency(WindowsPredicate.class)
		public static PlatformDependencyMarker create1() {
			return new WindowsPlatformDependency();
		}

		@PlatformDependency(LinuxPredicate.class)
		public static PlatformDependencyMarker create2() {
			return new LinuxPlatformDependency();
		}

		@PlatformDependency(LinuxPredicate.class)
		public static PlatformDependencyMarker create3() {
			return new MacPlatformDependency();
		}

	}

}
