package de.zib.gndms.infra.db;

import de.zib.gndms.infra.GridConfig;
import de.zib.gndms.infra.monitor.GroovyBindingFactory;
import de.zib.gndms.infra.monitor.GroovyMoniServer;
import de.zib.gndms.model.common.VEPRef;
import de.zib.gndms.model.dspace.DSpaceVEPRef;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.axis.types.URI;
import org.apache.log4j.Logger;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.jndi.Initializable;
import org.globus.wsrf.utils.AddressingUtils;
import org.jetbrains.annotations.NotNull;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This sets up the configuration and database storage area shared between
 * GNDMS services.
 *
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 17.06.2008 Time: 23:09:00
 */
@SuppressWarnings({"OverloadedMethodsWithSameNumberOfParameters", "NestedAssignment"})
public final class GNDMSystem implements Initializable, SystemHolder {
	private static final Lock factoryLock = new ReentrantLock();

	private final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();

	private final Logger logger = Logger.getLogger(GNDMSystem.class);


	@NotNull
	private final Map<String, Object> instances;

	@NotNull
	private final GridConfig sharedConfig;

	@NotNull
	private File sharedDir;

	@NotNull
	private File logDir;

	@NotNull
	private File dbDir;

	@NotNull
	private File dbLogFile;

	@NotNull
	private EntityManagerFactory emf;

	@NotNull
	private File monitorConfig;

	@NotNull
	private GroovyMoniServer groovyMonitor;

	@NotNull
	private final ThreadLocal<EntityManagerGuard> emgs = new ThreadLocal<EntityManagerGuard>();

	private static final int INITIAL_CAPACITY = 32;

	/**
	 * Retrieves a GNDMSSystem using context.lookup(name).
	 *
	 * A lightweight factory facade is either atomically retrieved from context or bound under name
	 * iff name is unbound in context. The factory acts as an intermediary and ensures that at most
	 * one DbSetupFacade ever gets instantiated and initialized.
	 *
	 * This instance is returned by this call from the factory facade.
	 *
	 * @param sharedContext
	 * @param facadeName
	 * @return GNDMSSystem singleton
	 * @throws NamingException
	 */
	@NotNull
	public static GNDMSystem lookupSystem(@NotNull Context sharedContext,
	                                        @NotNull Name facadeName,
	                                        @NotNull GridConfig anySharedConfig)
		  throws NamingException {
		try {
			final SysFactory theFactory = new SysFactory(anySharedConfig);
			sharedContext.bind(facadeName, theFactory);
			return theFactory.getInstance();
		}
		catch (NameAlreadyBoundException n) {
			return ((SysFactory) sharedContext.lookup(facadeName)).getInstance();
		}
	}

	@SuppressWarnings({"StaticMethodOnlyUsedInOneClass"})
	@NotNull
	public static GNDMSystem lookupSystem(
		  @NotNull Context sharedContext, @NotNull String facadeName,
		  @NotNull GridConfig anySharedConfig)
		  throws NamingException {
		try {
			final SysFactory theFactory = new SysFactory(anySharedConfig);
			sharedContext.bind(facadeName, theFactory);
			return theFactory.getInstance();
		}
		catch (NameAlreadyBoundException n) {
			return ((SysFactory) sharedContext.lookup(facadeName)).getInstance();
		}
	}

	private GNDMSystem(@NotNull GridConfig anySharedConfig)  {
		sharedConfig = anySharedConfig;
		instances = new HashMap<String, Object>(INITIAL_CAPACITY);
		addInstance("gndms", this);
		// initialization intentionally deferred to initialize
	}

	public void initialize() throws RuntimeException {
		try {
			// Q: Think about how to correct UNIX directory/file permissions from java-land
			// A: External script during deployment
			sharedDir = new File(sharedConfig.getGridPath()).getCanonicalFile();
			final String gridName = sharedConfig.getGridName();

			logger.info("Initializing for grid: '" + gridName
				  + "' (shared dir: '" + sharedDir.getPath() + "')");

			createDirectories();
			emf = createEMF(gridName);
			tryTxExecution();

			setupShellService(gridName);
		}
		catch (Exception e) {
			logger.error("Initialization failed", e);
			throw new RuntimeException(e);
		}
	}

	private void createDirectories() throws IOException {
		doCheckOrCreateDir(sharedDir);

		logDir = new File(sharedDir, "log");
		doCheckOrCreateDir(logDir);

		prepareDbStorage();

		monitorConfig = new File(sharedDir, "monitor.properties");
	}

	private void prepareDbStorage() throws IOException {
		dbDir = new File(sharedDir, "db");
		doCheckOrCreateDir(dbDir);

		System.setProperty("derby.system.home", dbDir.getCanonicalPath());

		dbLogFile = new File(logDir, "derby.log");
		if (!dbLogFile.exists())
			dbLogFile.createNewFile();
		System.setProperty("derby.stream.error.file",
				  dbLogFile.getCanonicalPath());
	}

	@SuppressWarnings({"HardcodedFileSeparator"})
	public EntityManagerFactory createEMF(String gridName) throws Exception {

		Properties map = new Properties();

		map.put("openjpa.Id", gridName);
		map.put("openjpa.ConnectionURL", "jdbc:derby:" + gridName+";create=true");
		logger.info("Opening JPA Store: " + map.toString());

		return Persistence.createEntityManagerFactory(gridName, map);
	}

	public static void main(String[] args) {
		GNDMSystem sys = new GNDMSystem(new GridConfig() {
			@NotNull
			public String getGridJNDIEnvName() throws Exception {
						return null;
					}

			@NotNull
			public String getGridName() throws Exception {
						return "c3grid";

					}

			@NotNull
			public String getGridPath() throws Exception {
						return "/opt/gt-current/etc/c3grid_shared/";
					}
		});
		sys.initialize();
		sys.tryTxExecution();

	}

	@SuppressWarnings({"EmptyTryBlock"})
	private void tryTxExecution() {
		EntityManagerGuard emg;
		emg = currentEMG();
		boolean flag = emg.begin();
		try {
			// intended
		}
		finally {
			emg.commitAndClose(flag);
		}
	}

	private EntityManager createNewEntityManager() {
		final EntityManager em;
		em = emf.createEntityManager();
		return em;
	}

	/**
	 * Either retrieves the current or creates a new entity manager guard for a newly opened
	 * entity manager and ensures a proper uncaught exception handler is set up that
	 * dissociates the current guard from its thread in the case an uncaught exception is
	 * thrown by this thread.
	 *
	 * @see de.zib.gndms.infra.db.EntityManagerGuard
	 *
	 * @return the entity manager guard associated with this thread
	 */
	@NotNull
	public EntityManagerGuard currentEMG() {
		final EntityManagerGuard oldEMG = emgs.get();
		if (oldEMG != null) {
			if (oldEMG.getEM().isOpen())
				return oldEMG;
			else {
				logger.error("Closed EMG encountered where not allowed and fixed");
				emgs.remove();
			}
		}
		return setupNewThreadEMG();
	}

	@NotNull
	public final EntityManager currentEM() {
		return currentEMG().getEM();
	}

	private EntityManagerGuard setupNewThreadEMG() {
		installUEH();
		final EntityManagerGuard emg = new EntityManagerGuard(createNewEntityManager());
		emg.setEMThreadLocal(emgs);
		return emg;
	}

	private void installUEH() {
		final Thread thread = Thread.currentThread();
		Thread.UncaughtExceptionHandler ueh = thread.getUncaughtExceptionHandler();
		if (ueh == null || !(ueh instanceof SystemUEH))
			thread.setUncaughtExceptionHandler(new SystemUEH(ueh));
	}


	private void setupShellService(String gridName) throws Exception {
		groovyMonitor = new GroovyMoniServer(gridName,  monitorConfig, new GNDMSBindingFactory());
		groovyMonitor.startConfigRefreshThread(true);
	}

	public synchronized void addInstance(@NotNull String name, @NotNull Object obj) {
		if ("out".equals(name) || "err".equals(name) || "args".equals(name) || "em".equals(name)
			|| "emg".equals(name))
			throw new IllegalArgumentException("Reserved instance name");

		if (instances.containsKey(name))
			throw new IllegalStateException("Name clash in instance registration");
		else
			instances.put(name, obj);

		logger.debug(getSystemName() + " addInstance: '" + name + '\'');
	}
	
	@NotNull
	public synchronized <T> T getInstance(@NotNull String name, @NotNull Class<? extends T> clazz) {
		final Object obj = instances.get(name);
		if (obj == null)
			throw new
				  IllegalStateException("Null instance retrieved or invalid or unregistered name");
		return clazz.cast(obj);
	}

	public synchronized ServiceInfo lookupServiceInfo(@NotNull String instancePrefix) {
		return getInstance(instancePrefix+"Home", ServiceInfo.class);
	}


	private void doCheckOrCreateDir(File mainDir) {
		if (!mainDir.exists()) {
			logger.info("Creating " + mainDir.getPath());
			mainDir.mkdir();
		}
		if (!mainDir.isDirectory() || !mainDir.canRead())
			throw new IllegalStateException(mainDir + " is not accessible");
	}

	@NotNull
	public String getSystemName() {
		try {
			return '\'' + sharedConfig.getGridName() + "' system";
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	public GridConfig getSharedDir() {
		return sharedConfig;
	}

	@NotNull
	public File getLogDir() {
		return logDir;
	}

	@NotNull
	public File getDbDir() {
		return dbDir;
	}

	@NotNull
	public File getDbLogFile() {
		return dbLogFile;
	}

	@NotNull
	public EntityManagerFactory getEMF() {
		return emf;
	}

	@NotNull
	public GroovyMoniServer getMonitor() {
		return groovyMonitor;
	}

	@SuppressWarnings({"ReturnOfThis"})
	@NotNull
	public GNDMSystem getSystem() {
		return this;
	}

	public void setSystem(@NotNull GNDMSystem system) throws IllegalStateException {
		throw new IllegalStateException("Cant set this system");
	}
	public static EntityManager currentEM(@NotNull SystemHolder holder) {
			return holder.getSystem().currentEM();
	}

	public static EntityManagerGuard currentEMG(@NotNull SystemHolder holder) {
			return holder.getSystem().currentEMG();
	}

	@NotNull
	public String nextUUID() {
		return uuidGen.nextUUID();
	}

	@NotNull
	public EndpointReferenceType serviceEPRType(@NotNull URI defAddr, @NotNull VEPRef dSpaceRef)
		  throws URI.MalformedURIException {
		if (dSpaceRef.getGridSite() != null)
			throw new IllegalArgumentException("Non-local EPRTs currently unsupported");

		try {
			return AddressingUtils.createEndpointReference(
				  defAddr.toString(), (ResourceKey) null);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	public EndpointReferenceType serviceEPRType(@NotNull String instPrefix,
	                                           @NotNull VEPRef dSpaceRef)
		  throws URI.MalformedURIException {
		return serviceEPRType(lookupServiceInfo(instPrefix).getServiceAddress(), dSpaceRef);
	}

	@NotNull
	public VEPRef modelEPRT(@NotNull QName keyTypeName, @NotNull EndpointReferenceType epr) {
		@NotNull ReferencePropertiesType props = epr.getProperties();
		@NotNull MessageElement msgElem = props.get(keyTypeName);
		SimpleResourceKey key = new SimpleResourceKey(keyTypeName, msgElem.getObjectValue());

		VEPRef theVEPREF = new DSpaceVEPRef();
		// theVEPREF.setSite("");
		// theVEPREF.setRk(key);
		return theVEPREF;
	}

	@NotNull
	public VEPRef modelEPRT(@NotNull String instPrefix, @NotNull EndpointReferenceType epr) {
		return modelEPRT(lookupServiceInfo(instPrefix).getResourceKeyTypeName(), epr);
	}

	private static final class SysFactory {
		private GNDMSystem instance;
		private RuntimeException exception;
		private GridConfig sharedConfig;

		SysFactory(GridConfig anySharedConfig) {
			sharedConfig = anySharedConfig;
		}

		private GNDMSystem getInstance() {
			factoryLock.lock();
			try {
				if (exception != null)
					throw exception;
				if (instance == null) {
					try {
						GNDMSystem newInstance = new GNDMSystem(sharedConfig);
						newInstance.initialize();
						instance = newInstance;
					}
					catch (RuntimeException e) {
						exception = e;
					}
					finally {
						// not required after this point
						sharedConfig = null;
					}
				}
				return instance;
			}
			finally {
				factoryLock.unlock();
			}
		}
	}

	private final class GNDMSBindingFactory implements GroovyBindingFactory {
		private static final int BINDING_MAP_INITIAL_CAPACITY = 32;

		private final class BindingState {
			private final EntityManagerGuard emg = new EntityManagerGuard(createNewEntityManager());

			public void destroy() {
				if (emg != null)
					if (! emg.getEM().isOpen())
						emg.close();
			}
		}

		private final Map<Binding, BindingState> stateMap =
			  Collections.synchronizedMap(new HashMap<Binding, BindingState>(
				    BINDING_MAP_INITIAL_CAPACITY));

		@NotNull
			public Binding createBinding(
			  @NotNull GroovyMoniServer moniServer, @NotNull Principal principal,
			  String args) {
			final Binding binding = new Binding();
			for (Map.Entry<String, Object> entry : instances.entrySet())
				binding.setProperty(entry.getKey(), entry.getValue());
			BindingState state = new BindingState();
			binding.setProperty("emg", state.emg);
			stateMap.put(binding, state);
			return binding;
		}

		@SuppressWarnings({"StringBufferWithoutInitialCapacity"})
		public void initShell(@NotNull GroovyShell shell, @NotNull Binding binding) {
			StringBuilder builder = new StringBuilder();
			for (Map.Entry<String, Object> entry : instances.entrySet()) {
				final String key = entry.getKey();
				builder.append("Object.metaClass.");
				builder.append(key);
				builder.append('=');
				builder.append(key);
				builder.append(';');
			}
			builder.append("Object.metaClass.emg=emg;");
			shell.evaluate(builder.toString());
		}

		public void destroyBinding(@NotNull GroovyMoniServer moniServer,
		                           @NotNull Binding binding) {
			BindingState state = stateMap.remove(binding);
			if (state != null)
			   state.destroy();
		 }
	}

	/**
	 * Ensures proper removal of the currentEMG() in the presence of uncaught exceptions
	 */
	private final class SystemUEH implements Thread.UncaughtExceptionHandler {
		private final Thread.UncaughtExceptionHandler ueh;

		SystemUEH(final Thread.UncaughtExceptionHandler theOldUEH) {
			ueh = theOldUEH;
		}

		public void uncaughtException(Thread t, Throwable e) {
			emgs.remove();
			if (ueh == null)
				t.setUncaughtExceptionHandler(null);
			else {
				// This is a bit fragile and may break if the defaultUEH is exchanged
				// Cant be done much better; this appears to be one of java's dark corners...
				if (ueh instanceof ThreadGroup)
					t.setUncaughtExceptionHandler(null);
				else {
					if (ueh == Thread.getDefaultUncaughtExceptionHandler())
						t.setUncaughtExceptionHandler(null);
					else
						t.setUncaughtExceptionHandler(ueh);
				}
			}
			t.getUncaughtExceptionHandler().uncaughtException(t, e);
		}
	}

}
