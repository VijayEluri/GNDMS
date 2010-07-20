package de.zib.gndms.infra.system;

import com.google.common.base.Function;
import org.globus.wsrf.ResourceException;
import org.jetbrains.annotations.NotNull;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import de.zib.gndms.infra.system.TxSafeRuntimeException;

/**
 * Helper code for executing jpa transactions in groovy
 * 
 * @author Stefan Plantikow<plantikow@zib.de>
 * @version $Id$ 
 *
 * User: stepn Date: 13.08.2008 Time: 15:24:02
 */
final public class EMTools {
    private EMTools() { throw new UnsupportedOperationException("Don't"); }


    public static void txBegin(final @NotNull EntityManager em) {
            em.getTransaction().begin();
    }


    public static void txRollback(final @NotNull EntityManager em) {
            em.getTransaction().rollback();
    }


    public static void txCommit(final @NotNull EntityManager em) {
            em.getTransaction().commit();
    }


    public static boolean txIsActive(final @NotNull EntityManager em) {
            return em.getTransaction().isActive();
    }

    /**
     * Runs block either with the given EntityManager or if none was provided a newly
     * created one.  Upon completion of block, the associated transaction is commited.
     * If a TxSafeRuntimeException is caught, it's cause is rethrown.
     * If a RuntimeException is caught, it is rethrown after transaction rollback.
     * If a new EntityManager was created by txRun, it is finally closed before returning
     * the result of block.
     *
     * @param em EntityManager em the entity manager to be used
     * @param closeEM wether em should be closed finally
     * @param block the Closure to be executed
     */
    public static <T> T txRun(final @NotNull EntityManager em, boolean closeEM,
                              final @NotNull Function<EntityManager, T> block) {
            final EntityTransaction tx = em.getTransaction();
            final boolean isNewTx = ! tx.isActive();

            final T result;
            try {
                    if (isNewTx) {
                            tx.begin();
                            result = block.apply(em);
                            tx.commit();
                    }
                    else
                            result = block.apply(em);
                    return result;
            }
            catch (TxSafeRuntimeException re) {
                    throw new RuntimeException(re.getCause());
            }
            catch (RuntimeException re) {
                    if (tx.isActive()) tx.rollback();
                    throw re;                         
            }
            finally {
                    if (closeEM && em.isOpen()) em.close();
            }
    }

    // TODO: Integrate this with GridEntityModelHandler, requires better exception handling in AbstractEntityModelAction
    public static <T> T txResRun(final @NotNull EntityManager em, boolean closeEM,
                              final @NotNull Function<EntityManager, T> block) throws ResourceException {
        try {
            return txRun(em, closeEM, block);
        }
        catch (RuntimeException e) {
            final Throwable cause = e.getCause();
            if (e == null || !(cause instanceof ResourceException))
                throw e;
            else
                throw (ResourceException) cause;
        }
    }
}