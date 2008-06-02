package de.zib.gndms.lofis.lofiset.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public interface LofiSetI {

  /**
   * Provides (empty) slices for the storage of replicas to this LofiSet
   *
   * @param sliceReference
   * @throws UnsupportedOrInvalidSlice
   *	
   */
  public void grantReplicaSlices(types.SliceReference[] sliceReference) throws RemoteException, de.zib.gndms.lofis.stubs.types.UnsupportedOrInvalidSlice ;

  /**
   * Add Slices to LofiSet
   *
   * @param replicaSlices
   * @throws ConflictResolutionFailed
   *	
   * @throws ConflictingDestinationsInMap
   *	
   * @throws MissingSourceFiles
   *	
   * @throws UnsupportedOrInvalidSlice
   *	
   */
  public void registerReplicaSlices(types.ReplicaSlicesT[] replicaSlices) throws RemoteException, de.zib.gndms.lofis.stubs.types.ConflictResolutionFailed, de.zib.gndms.lofis.stubs.types.ConflictingDestinationsInMap, de.zib.gndms.lofis.stubs.types.MissingSourceFiles, de.zib.gndms.lofis.stubs.types.UnsupportedOrInvalidSlice ;

  /**
   * Remove slices from this LofiSet
   *
   * @param sliceReference
   * @throws UnsupportedOrInvalidSlice
   *	
   * @throws UnavailableSlice
   *	
   */
  public void reclaimReplicaSlices(types.SliceReference[] sliceReference) throws RemoteException, de.zib.gndms.lofis.stubs.types.UnsupportedOrInvalidSlice, de.zib.gndms.lofis.lofiset.stubs.types.UnavailableSlice ;

  /**
   * Return replica slices matching any of the provided filters (first wins)
   *
   * @param replicaSliceFilter
   */
  public types.ReplicaSlicesT[] getReplicaSlices(types.ReplicaSliceFilterT[] replicaSliceFilter) throws RemoteException ;

  public org.oasis.wsrf.lifetime.DestroyResponse destroy(org.oasis.wsrf.lifetime.Destroy params) throws RemoteException ;

  public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(org.oasis.wsrf.lifetime.SetTerminationTime params) throws RemoteException ;

  public org.oasis.wsn.SubscribeResponse subscribe(org.oasis.wsn.Subscribe params) throws RemoteException ;

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException ;

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException ;

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException ;

  /**
   * Create new LofiSet containing a mapped subset of this lofi's content.
   *
   * @param lofiMap
   * @throws MissingSourceFiles
   *	
   * @throws ConflictingDestinationsInMap
   *	
   */
  public de.zib.gndms.lofis.lofiset.client.LofiSetClient getSubLofiSet(types.FileMappingSeqT lofiMap) throws RemoteException, org.apache.axis.types.URI.MalformedURIException, de.zib.gndms.lofis.stubs.types.MissingSourceFiles, de.zib.gndms.lofis.stubs.types.ConflictingDestinationsInMap ;

}

