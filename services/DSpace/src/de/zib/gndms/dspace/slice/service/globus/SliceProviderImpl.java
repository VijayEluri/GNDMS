package de.zib.gndms.dspace.slice.service.globus;

import de.zib.gndms.dspace.slice.service.SliceImpl;

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
public class SliceProviderImpl{
	
	SliceImpl impl;
	
	public SliceProviderImpl() throws RemoteException {
		impl = new SliceImpl();
	}
	

    public de.zib.gndms.dspace.slice.stubs.TransformSliceResponse transformSlice(de.zib.gndms.dspace.slice.stubs.TransformSliceRequest params) throws RemoteException, de.zib.gndms.dspace.slice.stubs.types.UnsupportedOrInvalidSliceKind, de.zib.gndms.dspace.slice.stubs.types.OutOfSpace {
    de.zib.gndms.dspace.slice.stubs.TransformSliceResponse boxedResult = new de.zib.gndms.dspace.slice.stubs.TransformSliceResponse();
    boxedResult.setSliceReference(impl.transformSlice(params.getSliceKind().getSliceKind()));
    return boxedResult;
  }

    public de.zib.gndms.dspace.slice.stubs.TransformSliceToResponse transformSliceTo(de.zib.gndms.dspace.slice.stubs.TransformSliceToRequest params) throws RemoteException, de.zib.gndms.dspace.slice.stubs.types.UnsupportedOrInvalidSliceKind, de.zib.gndms.dspace.slice.stubs.types.OutOfSpace, de.zib.gndms.dspace.slice.stubs.types.UnknownSubspace {
    de.zib.gndms.dspace.slice.stubs.TransformSliceToResponse boxedResult = new de.zib.gndms.dspace.slice.stubs.TransformSliceToResponse();
    boxedResult.setSliceReference(impl.transformSliceTo(params.getSubspaceSpecifier().getSubspaceSpecifier(),params.getSliceKind().getSliceKind()));
    return boxedResult;
  }

}
