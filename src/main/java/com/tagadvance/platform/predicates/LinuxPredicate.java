package com.tagadvance.platform.predicates;

import java.util.function.Predicate;

import com.tagadvance.platform.OperatingSystem;

public class LinuxPredicate implements Predicate<OperatingSystem> {

	@Override
	public boolean test(OperatingSystem os) {
		return os.getName().startsWith("Linux");
	}

}
