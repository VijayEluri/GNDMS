package de.zib.gndms.GORFX.service;

/*
 * Copyright 2008-2010 Zuse Institut Berlin (ZIB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import de.zib.gndms.GORFX.service.globus.resource.GORFXResource;
import  de.zib.gndms.GORFX.service.GORFXConfiguration;

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
public abstract class GORFXImplBase {
	
	public GORFXImplBase() throws RemoteException {
	
	}
	
	public GORFXConfiguration getConfiguration() throws Exception {
		return GORFXConfiguration.getConfiguration();
	}
	
	
	public de.zib.gndms.GORFX.service.globus.resource.GORFXResourceHome getResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("home");
		return (de.zib.gndms.GORFX.service.globus.resource.GORFXResourceHome)resource;
	}

	
	
	
	public de.zib.gndms.GORFX.ORQ.service.globus.resource.ORQResourceHome getORQResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("oRQHome");
		return (de.zib.gndms.GORFX.ORQ.service.globus.resource.ORQResourceHome)resource;
	}
	
	public de.zib.gndms.GORFX.offer.service.globus.resource.OfferResourceHome getOfferResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("offerHome");
		return (de.zib.gndms.GORFX.offer.service.globus.resource.OfferResourceHome)resource;
	}
	
	public de.zib.gndms.GORFX.context.service.globus.resource.TaskResourceHome getTaskResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("taskHome");
		return (de.zib.gndms.GORFX.context.service.globus.resource.TaskResourceHome)resource;
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

