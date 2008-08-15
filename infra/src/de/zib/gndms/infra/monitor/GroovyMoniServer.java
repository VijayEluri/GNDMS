package de.zib.gndms.infra.monitor;

import de.zib.gndms.infra.InfiniteEnumeration;
import de.zib.gndms.infra.PropertiesFromFile;
import de.zib.gndms.infra.util.LDPHolder;
import de.zib.gndms.infra.util.LoggingDecisionPoint;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.SessionIdManager;
import org.mortbay.jetty.SessionManager;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.servlet.*;
import org.mortbay.thread.BoundedThreadPool;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple management console that runs a groovy shells over http using
 * jetty.  Plain text authentication with a single username:password is used
 * to limit console access though extending this calls for SSL support should be pretty
 * straightforward.
 * <br />
 *
 * If an instance is executed in a thread, it tries to reread the configuration file
 * in regular intervals and restarts the server iff any change is found.
 * <br />
 *
 * The config file is a plain java properties file. All properties start with
 * "monitor.". Existing properies are: host (for selecting the socket interface),
 * port, enabled ("true" or "false"), user, password (cleartext), configRefreshCycle (ms),
 * maxScriptSizeInBytes, maxConnections, minConnections (must be >= 2!), sessionTimeout (ms),
 * roleName, defaultMode (GroovyMonitor.RunMode, should not be set to CLOSE).
 * <br />
 *
 * If no config file is found, a default file is created.  The console is always disabled
 * by default and configured for localhost:23232 with an unguessable default password and
 * a configuration refresh cycle of 30 seconds.
 * </br />
 *
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 17.07.2008 Time: 14:33:37
 */
@SuppressWarnings({"AccessToStaticFieldLockedOnInstance"})
public class GroovyMoniServer implements Runnable, LoggingDecisionPoint {
	// 1 sec min hopefully is a sensible lower bound
	private static final int MIN_REFRESH_CYCLE = 1000;

	@NotNull
	private static final Logger logger;

	@NotNull
	private static final Properties DEFAULT_PROPERTIES;

	@NotNull
	private static final String DEFAULT_COMMENT;

	@NonNls
	public static final String ATTR_ROLE_NAME = "roleName";

	@NonNls
	public static final String ATTR_MONITOR_SERVER = "monitorServer";

	static {
		logger = Logger.getLogger(GroovyMoniServer.class);

		DEFAULT_COMMENT = " Default properties for monitoring console. "
	   +"Set monitor.enabled=true to access via "
	   +"http://<host>:<port>/ using plain text authentication "
	   +"(all connections are *unencrpyted*)";

		Properties props = new Properties();
		props.put("monitor.enabled", "true");
		props.put("monitor.host", "localhost");
		props.put("monitor.port", "23232");
		props.put("monitor.user", "admin");
		// random, unguessable default password
		props.put("monitor.password", UUID.randomUUID().toString());
		props.put("monitor.configRefreshCycle", "30000");
		props.put("monitor.maxScriptSizeInBytes", "65535");
		props.put("monitor.maxConnections", "6");
		props.put("monitor.minConnections", "3");

		// for authorization in GroovyMonitorServlet
		props.put("monitor.roleName", "moniadmin");
		props.put("monitor.defaultMode", "SCRIPT");
		// session timeout after ca. twenty minutes
		props.put("monitor.sessionTimeout", "1337000");
		props.put("monitor.logged", "config,start,stop,reconfig,newdefaults,!load");

		DEFAULT_PROPERTIES = props;
	}


	/**
	 * Unique identifier for this monitor server; used in logs
	 */
	@NotNull
	final String unitName;

	/**
	 * Server configuration stream
	 */
	@NotNull
	private final InfiniteEnumeration<? extends Map<Object, Object>> configStream;

	/**
	 * Factory for creating bindings for newly created monitors
	 */
	@Nullable
	private GroovyBindingFactory bindingFactory;

	private boolean enabled; // = false initially

	@NotNull
	private String host;

	private int port;

	@NotNull
	private String user;

	@NotNull
	private String password;

	@NotNull
	private GroovyMonitor.RunMode defaultMode;

	private long configRefreshCycle;

	private int sessionTimeout;

	private int maxScriptSizeInBytes;

	private int minConnections;

	private int maxConnections;


	@Nullable
	private Set<String> logged;

	@NotNull
	private String roleName;

	@Nullable
	private Server server;

	// Used for config file reloading

	private ReentrantLock lock = new ReentrantLock(true);
	private Thread thread;

	// avoids double restarts if restart is triggered from servlet and thread at the same time
	private boolean skipThreadBasedRefresh;

    // for supporting action runnning
    private ActionRunner actionRunner;


    /**
	 * Creates a facade class for running a monitor server based on
	 * groovy and jetty.
	 *
	 * @param theUnitName descriptive name for this server (used in logs)
	 * @param theCfg continuous stream of properties for this server
	 * @param theBindingFactory for creating groovy biding objects per console instance/connection
	 */
	public GroovyMoniServer(final @NotNull String theUnitName,
	                        final @NotNull InfiniteEnumeration<? extends Map<Object,Object>> theCfg,
	                        final @NotNull GroovyBindingFactory theBindingFactory,
                            final @NotNull ActionRunner runner) {

		if (theCfg instanceof LDPHolder)
			((LDPHolder)theCfg).setLDP(this);
		unitName = theUnitName;
		bindingFactory = theBindingFactory;
		configStream = theCfg;
        actionRunner = runner;
		setupState(configStream.nextElement());
	}

	/**
	 * Creates a facade class for running a monitor server based on
	 * groovy and jetty.
	 *
	 * @param theUnitName descriptive name for this server (used in logs)
	 * @param theConfigFile used to load server configuration
	 * @param theBindingFactory for creating groovy biding objects per console instance/connection
	 * @param runner
     * @throws Exception
	 */
	public GroovyMoniServer(
            final @NotNull String theUnitName, final @NotNull File theConfigFile,
            final @NotNull GroovyBindingFactory theBindingFactory, final ActionRunner runner)
            throws Exception
	{
		this(theUnitName,
			 new PropertiesFromFile(theConfigFile, theUnitName + " monitor config",
			    DEFAULT_PROPERTIES, DEFAULT_COMMENT, logger), theBindingFactory, runner);
		if (shouldLog("config"))
			logger.info(theUnitName + " GroovyMoniServer config is "
				  + theConfigFile.getCanonicalPath());
	}

	/**
	 * Will run a monitor server using $HOME/monitor.properties
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		GroovyMoniServer theMoniServer;
		File configFile;
		boolean killConfig = false;
		if (args.length > 0)
			configFile = new File(args[0]);
		else {
			configFile = new File(System.getProperty("user.home")
				  + File.separatorChar
				  + "monitor.properties");
		}
		theMoniServer = new GroovyMoniServer("plain", configFile, new EmptyBindingFactory(),
                                             new ActionRunner() {
                                                 public Object runAction(
                                                         final @NotNull String className,
                                                         final @NotNull String opts,
                                                         final @NotNull PrintWriter writer) {
                                                     // intended
                                                     return null;
                                                 }
                                             });
		theMoniServer.startConfigRefreshThread(false);
	}

	@SuppressWarnings({"InfiniteLoopStatement", "ConstantConditions"})
	public void run() {
		boolean skipOnNextIteration = false;

		try {
			setupConfigRefreshThread();

			boolean finished = false;
			do {
				try {
					if (skipOnNextIteration)
						skipOnNextIteration = false;
					else
						refresh();

					Thread.sleep(getConfigRefreshCycle());
				}
				catch (InterruptedException e) {
					lock.lock();
					try {
						if (thread == null)
							finished = true;
						skipOnNextIteration = skipThreadBasedRefresh;
						skipThreadBasedRefresh = false;
					}
					finally { lock.unlock(); }
				}
				catch (Exception e) {
					logger.warn("Could not refresh() " + unitName + " monitor: "
						  + e.toString());
				}
			}  while (! finished);
		}
		finally {
			try {
				// in case of an unexpected exception, thread also needs to be set to null
				lock.lock();
				thread = null;
			}
			finally { lock.unlock(); }
		}
	}

	private void setupConfigRefreshThread() {
		try {
			lock.lock();
			if (thread != null)
				throw new RuntimeException("Attempt to run multiple refresh threads");
			thread = Thread.currentThread();
		}
		finally { lock.unlock(); }
	}

	public void startConfigRefreshThread(boolean isDaemon) {
		final Thread theThread = new Thread(this);
		theThread.setDaemon(isDaemon);
		theThread.start();
	}

	public void stopConfigRefreshThread() {
		lock.lock();
		try {
			final Thread theThread = thread;
			if (thread != null) {
				thread = null;
				theThread.interrupt();
			}
		}
		finally { lock.unlock(); }
	}
	
	@SuppressWarnings({"BooleanMethodNameMustStartWithQuestion"})
	public boolean restart() throws Exception {
		return refresh(true);
	}

	@SuppressWarnings({"BooleanMethodNameMustStartWithQuestion"})
	public boolean refresh() throws Exception {
		return refresh(false);
	}

	/**
	 * Start server if it is not running, otherwise,
	 * restart server if a new configuration is available (kills current connections)
	 *
	 * @param evenIfNoNewConfig if true, will restart in any case
	 * @return true if the server is running after this call
	 * @throws Exception
	 */
	@SuppressWarnings(
		  {"BooleanMethodNameMustStartWithQuestion", "StatementWithEmptyBody", "MethodWithMoreThanThreeNegations"})
	public boolean refresh(boolean evenIfNoNewConfig) throws Exception {
		lock.lock();
		try {
			if (skipThreadBasedRefresh)
				return isServerRunning();

			synchronized(this) {
				if (server == null && enabled)
						;
				else {
					Map<Object, Object> next = configStream.nextElementIfDifferent();
					if (next == null && !evenIfNoNewConfig)
						return server != null;
					else {
						stopServer();
						if (next != null)
							setupState(next);
					}
				}
			}

			if (thread != null) {
				skipThreadBasedRefresh = true;
				thread.interrupt();
			}
			return startServer();
		}
		finally { lock.unlock(); }
	}

	/**
	 * Start the servlet server for serving monitoring consoles
	 *
	 * @return true if the server was started succesfully
	 * @throws Exception
	 */
	@SuppressWarnings(
		  {"HardcodedFileSeparator", "AccessToStaticFieldLockedOnInstance",
			    "BooleanMethodNameMustStartWithQuestion"})
	public synchronized boolean startServer() throws Exception {
		if (enabled && server == null) {
			if (shouldLog("start"))
				logger.info("Starting monitor server for '" +
					  unitName + "' on " + host + ':' + port);
			try {
					Server aServer = createServerObject();

					Connector connector = createConnector();

					SecurityHandler secH = createSecurityHandler();
					ContextHandler conH = new ContextHandler();
					conH.setAttribute(ATTR_ROLE_NAME, roleName);
					conH.setAttribute(ATTR_MONITOR_SERVER, this);

					SessionHandler sesH = createSessionHandler();
					ServletHandler srvH = createServletHandler();

					aServer.setConnectors(new Connector[] { connector });
					conH.setHandler(secH);
					secH.setHandler(sesH);
					sesH.setHandler(srvH);
					aServer.setHandler(conH);
					aServer.setStopAtShutdown(true);

					aServer.start();
					server = aServer;
					return true;
			}
			catch (Exception e) {
				logger.warn("Failed starting monitor server for '" + unitName + '\'');
				throw e;
			}
		}
		return false;
	}

	/***
	 * Stop the monitorserver
	 *
	 * @throws Exception
	 */
	@SuppressWarnings({"AccessToStaticFieldLockedOnInstance"})
	public synchronized void stopServer() throws Exception {
		try {
			final Server aServer = server;
			if (aServer != null) {
				if (shouldLog("stop"))
					logger.info("Stopping monitor server for '" + unitName + '\'');
				aServer.stop();
			}
		}
		finally {
			server = null;
		}
	}

	/**
	 * Setup this object's state based on a newly retrieved configuration
	 * @param props
	 */
	@SuppressWarnings({"MagicNumber"})
	private synchronized void setupState(@NotNull Map<Object, Object> props) {
		logProps(props);
		host = getProperty(props, "monitor.host").trim();
		user = getProperty(props, "monitor.user").trim();
		defaultMode = GroovyMonitor.RunMode.valueOf(
			  getProperty(props, "monitor.defaultMode").trim().toUpperCase());
		password = getProperty(props, "monitor.password").trim();
		setupNumbers(props);
		roleName = getProperty(props, "monitor.roleName").trim();
		enabled = "true".equals(getProperty(props, "monitor.enabled").trim());
		final String loggedStr = getProperty(props, "monitor.logged").trim();
		enabled &= isSaneSetup(LoggingDecisionPoint.Parser.parseTokenSet(loggedStr));
	}

	/**
	 * Log props but avoid logging the password
	 * 
	 * @param props
	 */
	private void logProps(Map<Object, Object> props) {
		if (shouldLog("reconfig")) {
			// Admittedly crazy way of doing thing but props may be read-only therefore we cant
			// remove and reput
			Properties maskedProps = new Properties();
			for (Object key : props.keySet()) {
				if (! "monitor.password".equals(key))
					maskedProps.put(key, props.get(key));
			}
			logger.debug("Reconfiguration of " + unitName + " monitor using: " + maskedProps.toString());
		}
	}

	private void setupNumbers(Map<Object, Object> props) {
		try {
			port = parseInt(props, "monitor.port");
			minConnections = Math.max(2, parseInt(props, "monitor.minConnections"));
			maxConnections = Math.max(minConnections, parseInt(props, "monitor.maxConnections"));
			configRefreshCycle = parseLong(props, "monitor.configRefreshCycle");
			if (configRefreshCycle < MIN_REFRESH_CYCLE)
				configRefreshCycle = MIN_REFRESH_CYCLE;
			maxScriptSizeInBytes = parseInt(props, "monitor.maxScriptSizeInBytes");
			sessionTimeout  = parseInt(props, "monitor.sessionTimeout");
		}
		catch (NumberFormatException n) {
			enabled = false;
			logger.warn(n.getMessage());
			throw n;
		}
	}

	@SuppressWarnings({"MagicNumber"})
	private boolean isSaneSetup(Set<String> strings) {
		if (strings == null) {
			logger.warn("Could not parse monitor.logged (format: [[!]token] [,[!]token]*); "
				  + " monitor disabled");
		}
		else if (port < 0 || port > 65535) {
			logger.warn("Invalid port number; monitor disabled");
		}
		else if (host.length() == 0) {
			logger.warn("Invalid (empty) host name; monitor disabled");
		}
		else if (user.length() == 0) {
			logger.warn("Invalid (empty) user name; monitor disabled");
		}
		else if (password.length() == 0) {
			logger.warn("Invalid (empty) password; monitor disabled");
		}
		else {
			logged = strings;
			return true;
		}
		return false;
	}

	private Connector createConnector() {
		Connector connector = new SelectChannelConnector();
		connector.setHost(host);
		connector.setPort(port);
		return connector;
	}

	@SuppressWarnings({"HardcodedFileSeparator"})
	private SecurityHandler createSecurityHandler() {
		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__DIGEST_AUTH);
		constraint.setRoles(new String[] { roleName });
		constraint.setAuthenticate(true);

		ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec("/*");

		HashUserRealm ur = new HashUserRealm(unitName);
		ur.put(user, password);
		ur.addUserToRole(user, roleName);

		SecurityHandler secH = new SecurityHandler();
		secH.setConstraintMappings(new ConstraintMapping[] { cm });
		secH.setUserRealm(ur);
		return secH;
	}

	private SessionHandler createSessionHandler() {
		SessionManager sesM = new HashSessionManager();
		SessionIdManager sidM = new HashSessionIdManager();

		sesM.setMaxInactiveInterval(sessionTimeout);
		sesM.setIdManager(sidM);

		SessionHandler sesH = new SessionHandler();
		sesH.setSessionManager(sesM);
		sesH.setServer(server);
		return sesH;
	}

	@SuppressWarnings({"HardcodedFileSeparator"})
	private static ServletHandler createServletHandler() {
		ServletHandler srvH = new ServletHandler();
		ServletHolder holder = new ServletHolder(GroovyMoniServlet.class);
		srvH.addServletWithMapping(holder, "/");
		return srvH;
	}

	private synchronized Server createServerObject() {
		Server aServer = new Server();
		BoundedThreadPool pool = new BoundedThreadPool();
		aServer.setThreadPool(pool);
		pool.setMinThreads(minConnections);
		pool.setMaxThreads(maxConnections);
		return aServer;
	}

	private static int parseInt(Map<Object, Object> props, String propName) {
		try {
			return Integer.parseInt(getProperty(props, propName).trim());
		}
		catch (NumberFormatException nfe) {
			throw new RuntimeException("Could not parse parameter " + propName, nfe);
		}
	}

	private static long parseLong(Map<Object, Object> props, String propName) {
		try{
			return Long.parseLong(getProperty(props, propName).trim());
		}
		catch (NumberFormatException nfe) {
			throw new RuntimeException("Could not parse parameter " + propName, nfe);
		}
	}
	/**
	 *
	 * @param map
	 * @param key
	 * @return value for key in map or DEFAULT_PROPERTIES
	 */
	@NotNull
	private static String getProperty(@NotNull Map<Object, Object> map, @NotNull String key)
	{
		final Object candidate = map.get(key);
		final String ret =  (String)
			  (candidate instanceof String ? candidate : DEFAULT_PROPERTIES.get(key));
		return ret == null ? "" : ret;
	}

	@NotNull
	public GroovyBindingFactory getBindingFactory() {
		if (bindingFactory == null)
			throw new IllegalStateException();
		return bindingFactory;
	}

	public void setBindingFactory(@Nullable GroovyBindingFactory newBindingFactory) {
		bindingFactory = newBindingFactory;
	}


	public synchronized boolean isServerEnabled() {
		return enabled;
	}

	public synchronized boolean isServerRunning() {
		return server != null;
	}

	public synchronized int getMinConnections() {
		return minConnections;
	}

	public synchronized int getMaxConnections() {
		return maxConnections;
	}

	public synchronized long getConfigRefreshCycle() {
		return configRefreshCycle;
	}

	public synchronized int getMaxScriptSizeInBytes() {
		return maxScriptSizeInBytes;
	}

	public synchronized int getSessionTimeout() {
		return sessionTimeout;
	}

	@NotNull
	synchronized GroovyMonitor.RunMode getDefaultMode() {
		return defaultMode;
	}

	public synchronized boolean shouldLog(@NotNull String token) {
		return logged == null || logged.contains(token);
	}


    public void runAction(
            final @NotNull String classNameParam,
            final @NotNull String argsParam,
            final @NotNull PrintWriter writerParam) {
        actionRunner.runAction(classNameParam, argsParam, writerParam);
    }
}
