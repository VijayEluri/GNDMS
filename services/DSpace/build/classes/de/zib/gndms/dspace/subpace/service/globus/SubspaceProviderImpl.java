package de.zib.gndms.dspace.subpace.service.globus;

import de.zib.gndms.dspace.subpace.service.SubspaceImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the DSpaceImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class SubspaceProviderImpl{
	
	SubspaceImpl impl;
	
	public SubspaceProviderImpl() throws RemoteException {
		impl = new SubspaceImpl();
	}
	

    public de.zib.gndms.dspace.subpace.stubs.CreateSliceResponse createSlice(de.zib.gndms.dspace.subpace.stubs.CreateSliceRequest params) throws RemoteException, de.zib.gndms.dspace.subpace.stubs.types.UnsupportedOrInvalidSliceKind, de.zib.gndms.dspace.subpace.stubs.types.OutOfSpace {
    de.zib.gndms.dspace.subpace.stubs.CreateSliceResponse boxedResult = new de.zib.gndms.dspace.subpace.stubs.CreateSliceResponse();
    boxedResult.setSliceReference(impl.createSlice(params.getSliceKind().getSliceKind(),params.getSliceSize().getTotalStorageSize(),params.getTerminationTime().getTerminationTime()));
    return boxedResult;
  }

    public de.zib.gndms.dspace.subpace.stubs.GetSliceByIdResponse getSliceById(de.zib.gndms.dspace.subpace.stubs.GetSliceByIdRequest params) throws RemoteException, de.zib.gndms.dspace.subpace.stubs.types.UnknownSliceId {
    de.zib.gndms.dspace.subpace.stubs.GetSliceByIdResponse boxedResult = new de.zib.gndms.dspace.subpace.stubs.GetSliceByIdResponse();
    boxedResult.setSliceReference(impl.getSliceById(params.getSliceId().getSliceId()));
    return boxedResult;
  }

    public de.zib.gndms.dspace.subpace.stubs.ListCreatableSliceKindsResponse listCreatableSliceKinds(de.zib.gndms.dspace.subpace.stubs.ListCreatableSliceKindsRequest params) throws RemoteException {
    de.zib.gndms.dspace.subpace.stubs.ListCreatableSliceKindsResponse boxedResult = new de.zib.gndms.dspace.subpace.stubs.ListCreatableSliceKindsResponse();
    impl.listCreatableSliceKinds();
    return boxedResult;
  }

}
