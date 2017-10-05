package com.tagadvance.platform.predicates;

import java.util.function.Predicate;

import com.tagadvance.platform.OperatingSystem;

public class Amd64Predicate implements Predicate<OperatingSystem> {

	private static final String[] architectures = {"amd64", "x86_64"};

	@Override
	public boolean test(OperatingSystem os) {
		for (String architecture : architectures) {
			if (architecture.equals(os.getArchitecture())) {
				return true;
			}
		}
		return false;
	}

}
