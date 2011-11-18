package de.zib.gndms.kit.network;

/*
 * Copyright 2008-2011 Zuse Institute Berlin (ZIB)
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



import de.zib.gndms.model.gorfx.FTPTransferState;
import de.zib.gndms.model.util.TxFrame;
import de.zib.gndms.neomodel.common.NeoDao;
import de.zib.gndms.neomodel.common.NeoSession;
import de.zib.gndms.neomodel.gorfx.Taskling;
import org.globus.ftp.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;

/**
 * A persistent marker listener for grid ftp file transfers.
 *
 * It keeps track of the restart marks and stores them into a
 * data base.
 *
 * Note even after a successful transfer the data base entry isn't deleted
 * you have to do this manually using derby's ij tool.
 * 
 * @author  try ma ik jo rr a zib
 * @version  $Id$
 * <p/>
 * User: mjorra, Date: 01.10.2008, Time: 13:27:12
 */
public class PersistentMarkerListener implements MarkerListener {
    private static Logger logger = Logger.getLogger( PersistentMarkerListener.class );
    private ByteRangeList byteRanges;
    private NeoDao dao;
    private FTPTransferState transferState;
    private Taskling taskling;


    public PersistentMarkerListener( ) {
        byteRanges = new ByteRangeList();
    }


    public void markerArrived( Marker marker ) {

        if( marker instanceof GridFTPRestartMarker )
            gridFTPRestartMarkerArrived( ( GridFTPRestartMarker) marker );
        else if( marker instanceof PerfMarker )  {
            logger.debug( "Transfer " + transferState.getTransferId() + ": PerfMarker arrived at " +
                ( (PerfMarker) marker).getTimeStamp() );
        } else
            logger.error( "Transfer " + transferState.getTransferId() + ": Unsupported marker received." );
    }


    private void gridFTPRestartMarkerArrived( GridFTPRestartMarker marker ) {

        byteRanges.merge( marker.toVector() );
        String args = byteRanges.toFtpCmdArgument();
        logger.debug( "Transfer " + transferState.getTransferId() + " markers: " + args );
        final NeoSession session = dao.beginSession();
        try {
            transferState.setFtpArgs( args );
            taskling.getTask(session).setPayload(transferState);
            session.success();
        }
        finally { session.finish(); }
    }

    
    public FTPTransferState getTransferState() {
        return transferState;
    }


    /**
     * Sets the transferStat object for this marker listener.
     *
     * If the object isn't persisted yet, this method persist it.
     * For this reason the state object needs it transferId to be set.
     */
    public void setTransferState( FTPTransferState transferState ) {

        this.transferState = transferState;
        byteRanges = new ByteRangeList();

        final NeoSession session = dao.beginSession();
        try {
            taskling.getTask(session).setPayload(transferState);
            if (transferState.getFtpArgs() != null) {
                GridFTPRestartMarker rm = new GridFTPRestartMarker(( transferState.getFtpArgsString()) );
                byteRanges.merge(rm.toVector());
            }

            session.success();
        }
        finally { session.finish(); }
    }


    public boolean hasCurrentFile( )  {
        return getCurrentFile() != null;
    }
    
    
    public String getCurrentFile() {
        return transferState.getCurrentFile();
    }


    /**
     * Sets file of the transfar state to currentFile.
     *
     * Additionally the current ftp args of the state are resetted.
     * The new state is written to the database immediately.
     */
    public void setCurrentFile( String currentFile ) {
        final NeoSession session = dao.beginSession();
        try {
            transferState.setCurrentFile( currentFile );
            transferState.setFtpArgs( "0-0" );
            taskling.getTask(session).setPayload(transferState);
            session.success();
        }
        finally { session.finish(); }
    }

    public Taskling getTaskling() {
        return taskling;
    }

    public void setTaskling(Taskling taskling) {
        this.taskling = taskling;
    }

    public NeoDao getDao() {
        return dao;
    }

    public void setDao(NeoDao dao) {
        this.dao = dao;
    }
}
