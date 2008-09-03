package de.zib.gndms.service.globus;

import de.zib.gndms.service.WHORFXImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the WHORFXImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WHORFXProviderImpl{
	
	WHORFXImpl impl;
	
	public WHORFXProviderImpl() throws RemoteException {
		impl = new WHORFXImpl();
	}
	

    public de.zib.gndms.stubs.CallMaintenanceActionResponse callMaintenanceAction(de.zib.gndms.stubs.CallMaintenanceActionRequest params) throws RemoteException {
    de.zib.gndms.stubs.CallMaintenanceActionResponse boxedResult = new de.zib.gndms.stubs.CallMaintenanceActionResponse();
    boxedResult.setResponse(impl.callMaintenanceAction(params.getAction(),params.getOptions().getContext()));
    return boxedResult;
  }

    public de.zib.gndms.stubs.LookupORFResponse lookupORF(de.zib.gndms.stubs.LookupORFRequest params) throws RemoteException, de.zib.gndms.stubs.types.UnknownORFType {
    de.zib.gndms.stubs.LookupORFResponse boxedResult = new de.zib.gndms.stubs.LookupORFResponse();
    boxedResult.setResponse(impl.lookupORF(params.getOrfType(),params.getContext().getContext()));
    return boxedResult;
  }

    public de.zib.gndms.stubs.UpdateMappingsResponse updateMappings(de.zib.gndms.stubs.UpdateMappingsRequest params) throws RemoteException {
    de.zib.gndms.stubs.UpdateMappingsResponse boxedResult = new de.zib.gndms.stubs.UpdateMappingsResponse();
    impl.updateMappings();
    return boxedResult;
  }

}
