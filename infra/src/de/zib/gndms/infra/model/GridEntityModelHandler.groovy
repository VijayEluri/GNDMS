package de.zib.gndms.infra.model;

import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import de.zib.gndms.infra.wsrf.ReloadablePersistentResource
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Query
import javax.persistence.NoResultException
import javax.persistence.NonUniqueResultException
import de.zib.gndms.infra.service.GNDMServiceHome
import de.zib.gndms.infra.system.EMFactoryProvider
import de.zib.gndms.infra.system.EMTools
import de.zib.gndms.model.common.GridEntity
import de.zib.gndms.model.common.GridResource
import de.zib.gndms.model.common.SingletonGridResource
import de.zib.gndms.logic.model.BatchUpdateAction
import de.zib.gndms.logic.model.DefaultBatchUpdateAction
import de.zib.gndms.logic.model.EntityUpdateListener
import de.zib.gndms.logic.model.ModelAction
import org.globus.wsrf.NoSuchResourceException
import org.globus.wsrf.ResourceException
import org.globus.wsrf.ResourceIdentifier
import de.zib.gndms.logic.model.DelegatingEntityUpdateListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory
import de.zib.gndms.infra.system.EMFactoryProvider
import de.zib.gndms.infra.system.EMTools

/**
 * Helper class for managing persistent models of GNDMS resources 
 *
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 27.07.2008 Time: 19:41:17
 */
class GridEntityModelHandler<M extends GridEntity, H extends GNDMServiceHome, R extends ReloadablePersistentResource<M, H>> implements EMFactoryProvider {

	final Log logger = LogFactory.getLog(GridEntityModelHandler.class);

	final @NotNull Class<M> modelClass
	final @NotNull GNDMServiceHome home

	GridEntityModelHandler(final @NotNull Class<M> theClazz, final @NotNull H homeParam) {
		modelClass = theClazz;
		home = (GNDMServiceHome) homeParam;
	}


	final public @NotNull M createNewGridEntity() {
		return createNewEntity()
	}

	protected def @NotNull createNewEntity()
		{ getModelClass().newInstance() }


	/**
	 *  Calls the given model action after setting the corresponding parameters.
	 *
	 * There are several additional calls that just fix some of these parameters with sensible
	 * defaults: callResourceAction uses a resources' id to retrieve the model within the same
	 * transaction as the model action.  callNewResourceAction and callNewModelAction calls
	 * create a new EntityManager via this ModelHandlers' EntityManagerFactory. If no container
	 * for postponed actions is provided, a new DefaultBatchUpdateAction() is used.  If no
	 * EntityUpdateListener is provided, the one associated with this ModelHandler is used
	 * and set on postponedActions.
	 * 
	 */
	public <B> B callModelAction(
		final EntityManager emParam,
		final @NotNull BatchUpdateAction<GridResource, ?> postponedActions,
		final @NotNull EntityUpdateListener<GridResource> listener,
		final @NotNull de.zib.gndms.logic.model.ModelAction<M, B> theAction,
	    final @NotNull M theModel) {
		return (B) txRun(emParam, { EntityManager em ->
			 if (listener != null)
				 postponedActions.setListener(listener)
			theAction.setOwnPostponedActions(postponedActions)
			theAction.setModel(theModel)
			return theAction.call()
	    } ) 
	}

	/**
	 *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
	 */
	public final <B> B callModelAction(
		final EntityManager emParam,
		final @NotNull EntityUpdateListener<GridResource> listener,
		final @NotNull ModelAction<M, B> theAction,
	    final @NotNull M theModel) {
		return callModelAction(emParam, new DefaultBatchUpdateAction<GridResource>(), listener, theAction,
		                       theModel)
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callModelAction(
		final EntityManager emParam,
		final @NotNull ModelAction<M, B> theAction,
	    final @NotNull M theModel) {
		return (R) callModelAction(emParam, getEntityUpdateListener(), theAction, theModel)
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callNewModelAction(
		final @NotNull EntityUpdateListener<GridResource> listener,
		final @NotNull ModelAction<M, B> theAction,
	    final @NotNull M theModel) {
		return callModelAction(null, listener, theAction, theModel)
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callNewModelAction(
		final @NotNull ModelAction<M, B> theAction,
	    final @NotNull M theModel) {
		return callModelAction(null, theAction, theModel)
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callNewModelAction(
		final @NotNull BatchUpdateAction<GridResource, ?> postponedActions,
		final @NotNull EntityUpdateListener<GridResource> listener,
		final @NotNull ModelAction<M, B> theAction,
	    final @NotNull M theModel) {
		return callModelAction(null, postponedActions, listener, theAction, theModel)
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callResourceAction(
			final EntityManager emParam,
			final @NotNull BatchUpdateAction<GridResource, ?> postponedActions,
			final @NotNull EntityUpdateListener<GridResource> listener,
			final @NotNull ModelAction<M, B> theAction,
	        final @NotNull R resource) {
		return (R) txRun(emParam, { EntityManager em ->
			return callResourceAction(emParam, postponedActions, listener, theAction,
			                  loadModel(em, resource))
		})
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callResourceAction(
		final EntityManager emParam,
		final EntityUpdateListener listener,
		final @NotNull ModelAction<M, B> theAction,
		final @NotNull R resource) {
		return callResourceAction(emParam, new DefaultBatchUpdateAction(), listener,
		                          theAction, resource)
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callResourceAction(
		final EntityManager emParam,
		final @NotNull ModelAction<M, B> theAction,
		final @NotNull R resource) {
		return (R) callResourceAction(emParam, getEntityUpdateListener(), theAction, resource)
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callNewResourceAction(
		final @NotNull EntityUpdateListener listener,
		final @NotNull ModelAction<M, B> theAction,
	    final @NotNull R resource) {
		return callResourceAction(null, listener, theAction, resource)
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callNewResourceAction(
		final @NotNull ModelAction<M, B> theAction,
	    final @NotNull R resource) {
		return callResourceAction(null, theAction, resource)
	}

    /**
     *  @see #callModelAction(EntityManager, de.zib.gndms.logic.model.BatchUpdateAction, de.zib.gndms.logic.model.EntityUpdateListener, de.zib.gndms.logic.model.ModelAction, M)
     */
	public final <B> B callNewResourceAction(
		final @NotNull BatchUpdateAction<?> postponedActions,
		final @NotNull EntityUpdateListener listener,
		final @NotNull ModelAction<M, B> theAction,
	    final @NotNull R resource) {
		return callResourceAction(null, postponedActions, listener, theAction, resource)
	}


	public @NotNull EntityUpdateListener getEntityUpdateListener() {
		return DelegatingEntityUpdateListener.getInstance(sys);
	}

    protected <B> B txRun(final EntityManager emParam, final @NotNull Closure block) {
        if (emParam == null)
            return EMTools.txRun(getEntityManagerFactory().createEntityManager(), true, block)
        else
            return EMTools.txRun(emParam, false, block)
    }

	/**
	 * @param emParam the EntityManager to be used or null for an EM from this handler's system
	 * @param resource a resource
	 * @return model for resource if included in database, null otherwise
	 */
	final @Nullable M tryLoadModel(final EntityManager emParam, final @NotNull R resource) {
		return (M) txRun(emParam,
			  { EntityManager em ->
				  return tryLoadModelById(em, (String) ((ResourceIdentifier)resource).getID())
			  })
	}


	/**
	 * @param emParam the EntityManager to be used or null for an EM from this handler's system
	 * @param id a resource id
	 * @return model with id id if included in database, null otherwise
	 */
	final @Nullable M tryLoadModelById(final EntityManager emParam, final @NotNull String id)
		{
            final Class<M> curModelClass = getModelClass()
            return (M) txRun(emParam, { EntityManager em ->
                        return em.find(curModelClass, id)
            })
        }


	/**
	 * @param emParam the EntityManager to be used or null for an EM from this handler's system
	 * @param resource a resource
	 * @return model for resource
	 * @throws NoSuchResourceException if no model exists
	 */
	final @NotNull M loadModel(final EntityManager emParam, final @NotNull R resource)
		  throws ResourceException {
		return (M) txRun(emParam,
			  { EntityManager em ->
					M model = tryLoadModel(em, resource)
					if (model == null)
						throw new NoSuchResourceException("Could not load model from database")
					else
                        return model
			  })
	}


	/**
	 * @param emParam the EntityManager to be used or null for an EM from this handler's system
	 * @param id a resource id
	 * @return model for resource id
	 * @throws NoSuchResourceException if no model exists
	 */
	final @NotNull M loadModelById(final EntityManager emParam, final @NotNull String id)
		  throws ResourceException {
		return (M) txRun(emParam,
			  { EntityManager em ->
              logger.info("home.load: " + id);


					M model = tryLoadModelById(em, id)
					if (model == null)
						throw new NoSuchResourceException("Could not load model from database")
					else
                        return model
			  })
	}


	final def refreshModel(final EntityManager emParam, final @NotNull M model)
		{ return (M) txRun(emParam) { EntityManager em -> em.refresh(model) } }


	/**
	 * Removes a resource's model from the persistent store
	 *
	 * @param emParam the EntityManager to be used or null for an EM from this handler's system
	 * @param resource
	 * @throws NoSuchResourceException if no model exists
	 */
	final def removeModel(final EntityManager emParam, final @NotNull R resource)
		{ return (M) txRun(emParam) { EntityManager em -> em.remove(loadModel(em, resource)) } }


	/**
	 * Stores a new model in the persistent store
	 *
	 * @param emParam the EntityManager to be used or null for an EM from this handler's system
	 * @param model
	 */
	final @NotNull M persistModel(final EntityManager emParam, final @NotNull M model)
		{ return (M) txRun(emParam,
			  { EntityManager em ->
				em.persist(model)
				return model
			  })
		}


	/**
	 * Merges a detached model into the persistent store
	 *
	 * @param emParam the EntityManager to be used or null for an EM from this handler's system
	 * @param model
	 */
	final @NotNull M mergeModel(final EntityManager emParam, final @NotNull M model)
	{ return (M) txRun(emParam,
		  { EntityManager em ->
			em.merge(model)
			return model
		  })
	}


	final @NotNull String getGridName()
		{ return home.getSystem().getGridName() }

	final @NotNull String nextUUID()
		{ return home.getSystem().nextUUID() }


	final @NotNull EntityManagerFactory getEntityManagerFactory()
		{ return home.getEntityManagerFactory() }
}


/**
 * Specializing ModelHandler for GridResources
 *
 * @see GridResource
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 09.08.2008 Time: 12:21:32
 */
class GridResourceModelHandler<M extends GridResource, H extends GNDMServiceHome, R extends ReloadablePersistentResource<M, H>> extends GridEntityModelHandler<M, H, R> {

	GridResourceModelHandler(final Class<M> theClazz, final H homeParam) {
		super(theClazz, homeParam);    // Overridden method
	}

	@Override
	protected def @NotNull createNewEntity() {
		final @NotNull Object model = super.createNewEntity();    // Overridden method
		((GridResource)model).setId(nextUUID());
		return (M) model;
	}

}

/**
 * ModelHandler specializing for SingletonGridResources
 *
 * @author Stefan Plantikow <plantikow@zib.de>
 * @version $Id$
 *
 *          User: stepn Date: 09.08.2008 Time: 12:29:43
 */
final class SingletonGridResourceModelHandler<M extends SingletonGridResource, H extends GNDMServiceHome, R extends ReloadablePersistentResource<M, H>> extends GridResourceModelHandler<M, H, R> {

	SingletonGridResourceModelHandler(final Class<M> theClazz, final H homeParam) {
		super(theClazz, homeParam);
	}

	@NotNull M getSingleModel(final EntityManager emParam,
	                          final @NotNull String queryName, final ModelInitializer<M> creator)
		  throws ResourceException {
		return (M) txRun(emParam, { EntityManager em ->
			try {
				final Query query = em.createNamedQuery(queryName)
				return (M) query.getSingleResult()
			}
			catch (NoResultException e) {
				final @NotNull M model = (M) createNewGridEntity()
				if (creator != null)
					creator.initializeModel(model)
				return persistModel(em, (GridEntity)model)
			}
			catch (NonUniqueResultException e)
				{ throw new ResourceException(e); }
		})
	}


	@Override
	protected def @NotNull createNewEntity() {
		final Object model = super.createNewEntity()
		return (M) model
	}
}