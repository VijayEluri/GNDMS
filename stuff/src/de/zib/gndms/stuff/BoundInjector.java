package de.zib.gndms.stuff;

import com.google.inject.Injector;
import org.jetbrains.annotations.NotNull;


/**
 * ThingAMagic.
 *
 * @author Stefan Plantikow<plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 10.12.2008 Time: 12:37:41
 */
public final class BoundInjector {
	private Injector injector;


	public synchronized Injector optionallyGetInjector() {
		return injector;
	}

	public synchronized @NotNull Injector getInjector() {
		if (injector == null)
			throw new IllegalStateException("Injector not yet set");
		else
			return injector;
	}


	public synchronized void setInjector(final @NotNull Injector injectorParam) {
		if (injector == null)
			injector = injectorParam;
		else
			if (injector != injectorParam)
				throw new IllegalStateException("Attempt to overwrite injector");
	}


	public void injectMembers(Object obj) {
		if (obj == null)
			return;
		getInjector().injectMembers(obj);
	}
}
