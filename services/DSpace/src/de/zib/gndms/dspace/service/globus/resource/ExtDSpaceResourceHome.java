package de.zib.gndms.dspace.service.globus.resource;

import de.zib.gndms.dspace.service.DSpaceConfiguration;
import de.zib.gndms.infra.GNDMSTools;
import de.zib.gndms.infra.GridConfig;
import de.zib.gndms.infra.service.GNDMServiceHome;
import de.zib.gndms.infra.system.GNDMSystem;
import de.zib.gndms.model.dspace.DSpace;
import de.zib.gndms.model.common.GridResource;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.*;
import org.jetbrains.annotations.NotNull;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.xml.namespace.QName;


/**
 * This class overrides the ResourceHome that is automatically generated by introduce for Globus
 * Toolkit. In GNDMS this is mainly necessary to provide RDBMS/JPA-based resource persistence.
 * In order to use the extended resource home they have to be configured in jndi-config.xml.
 * If this has been done properly, you should see an info-level log message during the start up
 * of the web service container that notifies succesfull initialization of the extended resource
 * home.
 *
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 16.07.2008 Time: 12:35:27
 */
public final class ExtDSpaceResourceHome  extends DSpaceResourceHome
	  implements GNDMServiceHome<DSpace> {

	// logger can be an instance field since resource home classes are instantiated at most once
	@NotNull @SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	private final Log logger = LogFactory.getLog(ExtDSpaceResourceHome.class);

	// grid config for gndmsystem initialization
	@SuppressWarnings({"StaticVariableOfConcreteClass"})
	private static final GridConfig SHARED_CONFIG = new GridConfig() {
		@Override @NotNull
		public String getGridJNDIEnvName() throws Exception
			{ return DSpaceConfiguration.getConfiguration().getGridJNDIEnv(); }

		@Override @NotNull
		public String getGridName() throws Exception
			{ return DSpaceConfiguration.getConfiguration().getGridName(); }

		@Override @NotNull
		public String getGridPath() throws Exception
			{ return DSpaceConfiguration.getConfiguration().getGridPath(); }

	};

	public static GridConfig getGridConfig() {
		return SHARED_CONFIG;
	}



	private boolean initialized;

	// System: Set during initialization
	@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
	@NotNull
	private GNDMSystem system;

	// Serbice Address: set during initialization
	@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
	private AttributedURI serviceAddress;

	@Override
	public synchronized void initialize() throws Exception {
		if (! initialized) {
			logger.info("DSpace home extension initializing");
			try {
				system = getGridConfig().retrieveSystemReference();
				serviceAddress = GNDMSTools.getServiceAddressFromContext();

				initialized = true;

				try {
					super.initialize();    // Overridden method
				}
				catch (RuntimeException e) {
					initialized = false;
					logger.error(e);
					throw e;
				}
			}
			catch (NamingException e) {
				logger.error("Initialization failed");
				throw new RuntimeException(e);
			}
		}
	}


	private void ensureInitialized() {
		try
			{ initialize();	}
		catch (Exception e) {
			logger.error("Unexpected initialization error", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Resource find(ResourceKey key) throws ResourceException {
		PersistentResource resource = (PersistentResource) super.find(null);
		if (key == null)
			return resource;
		else
			if (resource.getID().equals(key.getValue()))
				return resource;
			else
				throw new InvalidResourceKeyException("Invalid singleton key");
	}

	@Override
	public Resource createSingleton() {
		try	{
			final DSpaceResource resource = new DSpaceResource();
			resource.setResourceHome(this);
			resource.load(null);
			return resource;
		}
		catch (ResourceException e) {
			logger.error(e);
			return null;
		}
	}

	@NotNull
	public GNDMSystem getSystem() throws IllegalStateException {
		ensureInitialized();
		return system;
	}

	public void setSystem(@NotNull GNDMSystem newSystem) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cant overwrite system");
	}

	@NotNull
	public EntityManagerFactory getEntityManagerFactory() {
		return getSystem().getEntityManagerFactory();
	}

	@NotNull
	public AttributedURI getServiceAddress() {
		ensureInitialized();
		return serviceAddress;
	}

	@NotNull
	public final QName getResourceKeyTypeName() {
		return getKeyTypeName();
	}


    public void refresh(final @NotNull GridResource resource) {
        // TODO implement
    }


    @NotNull
    public String getNickName() {
        return "dspace";
    }


    @NotNull
    public Class<DSpace> getModelClass() {
        return DSpace.class;
    }
}
