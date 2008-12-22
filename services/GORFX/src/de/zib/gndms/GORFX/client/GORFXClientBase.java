package de.zib.gndms.GORFX.client;

import de.zib.gndms.GORFX.stubs.GORFXPortType;
import de.zib.gndms.GORFX.stubs.service.GORFXServiceAddressingLocator;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.NotificationConsumerManager;

import java.io.InputStream;
import java.rmi.RemoteException;


/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public abstract class GORFXClientBase extends ServiceSecurityClient {	
	protected GORFXPortType portType;
	protected Object portTypeMutex;
    protected NotificationConsumerManager consumer = null;
    protected EndpointReferenceType consumerEPR = null;

	public GORFXClientBase(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	   	initialize();
	}
	
	public GORFXClientBase(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
		initialize();
	}
	
	private void initialize() throws RemoteException {
	    this.portTypeMutex = new Object();
		this.portType = createPortType();
	}

	private GORFXPortType createPortType() throws RemoteException {

		GORFXServiceAddressingLocator locator = new GORFXServiceAddressingLocator();
		// attempt to load our context sensitive wsdd file
		InputStream resourceAsStream = getClass().getResourceAsStream("client-config.wsdd");
		if (resourceAsStream != null) {
			// we found it, so tell axis to configure an engine to use it
			EngineConfiguration engineConfig = new FileProvider(resourceAsStream);
			// set the engine of the locator
			locator.setEngine(new AxisClient(engineConfig));
		}
		GORFXPortType port = null;
		try {
			port = locator.getGORFXPortTypePort(getEndpointReference());
		} catch (Exception e) {
			throw new RemoteException("Unable to locate portType:" + e.getMessage(), e);
		}

		return port;
	}
	
    

}