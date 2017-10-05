package com.tagadvance.platform;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.StandardSystemProperty;

public class OperatingSystem {

	private final String name, version, architecture;

	public static OperatingSystem getInstance() {
		return new OperatingSystem(StandardSystemProperty.OS_NAME.value(),
				StandardSystemProperty.OS_VERSION.value(), StandardSystemProperty.OS_ARCH.value());
	}

	/**
	 * 
	 * @param name
	 * @param version
	 * @param architecture
	 */
	OperatingSystem(String name, String version, String architecture) {
		super();
		this.name = checkNotNull(name, "name must not be null");
		this.version = checkNotNull(version, "version must not be null");
		this.architecture = checkNotNull(architecture, "architecture must not be null");
	}

	public String getName() {
		return name;
	}

	public String getArchitecture() {
		return architecture;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass()).add("name", name).add("version", version)
				.add("architecture", architecture).toString();
	}

	public static void main(String[] args) {
		OperatingSystem os = OperatingSystem.getInstance();
		System.out.println(os);
	}

}
