package de.zib.gndms.dspace.subpace.service;

import de.zib.gndms.dspace.subpace.service.globus.resource.SubspaceResource;
import  de.zib.gndms.dspace.service.DSpaceConfiguration;

import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertySet;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * Provides some simple accessors for the Impl.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public abstract class SubspaceImplBase {
	
	public SubspaceImplBase() throws RemoteException {
	
	}
	
	public DSpaceConfiguration getConfiguration() throws Exception {
		return DSpaceConfiguration.getConfiguration();
	}
	
	
	public de.zib.gndms.dspace.subpace.service.globus.resource.SubspaceResourceHome getResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("home");
		return (de.zib.gndms.dspace.subpace.service.globus.resource.SubspaceResourceHome)resource;
	}

	
	
	
	public de.zib.gndms.dspace.service.globus.resource.DSpaceResourceHome getDSpaceResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("dSpaceHome");
		return (de.zib.gndms.dspace.service.globus.resource.DSpaceResourceHome)resource;
	}
	
	public de.zib.gndms.dspace.slice.service.globus.resource.SliceResourceHome getSliceResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("sliceHome");
		return (de.zib.gndms.dspace.slice.service.globus.resource.SliceResourceHome)resource;
	}
	
	
	protected ResourceHome getResourceHome(String resourceKey) throws Exception {
		MessageContext ctx = MessageContext.getCurrentContext();

		ResourceHome resourceHome = null;
		
		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + resourceKey;
		try {
			javax.naming.Context initialContext = new InitialContext();
			resourceHome = (ResourceHome) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate resource home. : " + resourceKey, e);
		}

		return resourceHome;
	}


}

