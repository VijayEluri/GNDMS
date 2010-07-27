package de.zib.gndms.GORFX.action;

import de.zib.gndms.logic.model.gorfx.ORQTaskAction;
import de.zib.gndms.logic.model.gorfx.FileTransferTaskAction;
import de.zib.gndms.model.gorfx.types.InterSliceTransferORQ;
import de.zib.gndms.model.gorfx.types.TaskState;
import de.zib.gndms.model.gorfx.AbstractTask;
import de.zib.gndms.model.gorfx.SubTask;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityManager;

/**
 * @author: Maik Jorra <jorra@zib.de>
 * @version: $Id$
 * <p/>
 * User: mjorra, Date: 04.11.2008, Time: 17:45:47
 */
public class InterSliceTransferTaskAction extends ORQTaskAction<InterSliceTransferORQ> {


    public InterSliceTransferTaskAction() {
    }


    public InterSliceTransferTaskAction( @NotNull EntityManager em, @NotNull AbstractTask model ) {
        super( em, model );
    }


    public InterSliceTransferTaskAction( @NotNull EntityManager em, @NotNull String pk ) {
        super( em, pk );
    }


    @SuppressWarnings( { "ThrowableInstanceNeverThrown" } )
    protected void onInProgress( @NotNull AbstractTask model ) {

        try {
            InterSliceTransferORQCalculator.checkURIs( getOrq( ) );
        } catch ( Exception e ) {
            fail( new RuntimeException( e.getMessage( ), e ) );
        }

        SubTask st = new SubTask( model );
        try {
	        final EntityManager em = getEmf().createEntityManager();

            st.setId( getUUIDGen().nextUUID() );
            st.fromTask( em, model );
            st.setTerminationTime( model.getTerminationTime() );

	        FileTransferTaskAction fta = new FileTransferTaskAction(em, st );
            fta.setClosingEntityManagerOnCleanup( true );

            fta.setLog( getLog() );
            fta.call( );
            if( st.getState().equals( TaskState.FINISHED ) )
                finish( st.getData( ) );
            else
                fail( (RuntimeException) st.getData() );

        } catch ( RuntimeException e ) {
            honorOngoingTransit( e );
        } catch ( Exception e ) {
            /*
            if( isFinishedException( e ) )
                finish( st.getData( ) );
            else if( isFailedTransition( e ) )
                fail( (RuntimeException) st.getData() );
            else
            */
            fail( new RuntimeException( e ) );
        }
    }


    @NotNull
    protected Class<InterSliceTransferORQ> getOrqClass() {
        return InterSliceTransferORQ.class;
    }
}