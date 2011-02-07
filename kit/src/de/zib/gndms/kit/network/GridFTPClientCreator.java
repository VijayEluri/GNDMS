package de.zib.gndms.kit.network;

/*
 * Copyright 2008-2010 Zuse Institute Berlin (ZIB)
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



import de.zib.gndms.kit.access.CredentialProvider;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;

import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.Callable;

/**
 * @author  try ma ik jo rr a zib
 * @version $Id$
 *          <p/>
 *          User: mjorra, Date: 20.02.2009, Time: 17:40:03
 */
public class GridFTPClientCreator implements Callable<GridFTPClient>  {
    private static final Logger log = Logger.getLogger( GridFTPClientCreator.class );

    private String host;
    private int port;
    private Stack ctx;
    private CredentialProvider credProvider;


    public GridFTPClientCreator() {
        ctx = NDC.cloneStack();
    }




    public GridFTPClientCreator( String host, int port, CredentialProvider cp ) {
        this.host = host;
        this.port = port;
        this.credProvider = cp;
        ctx = NDC.cloneStack();
    }


    public GridFTPClient call() throws ServerException, IOException, InterruptedException, ClientException {

        NDC.inherit( ctx );
        /*
        int stacksize = ctx.size();
        while (! ctx.empty() ) {
            NDC.push( (String) ctx.pop() );
        }
        */
        try {
            log.info( "creating client" );
            final GridFTPClient cnt = new GridFTPClient( host, port );
            credProvider.installCredentials( cnt );
            validateClient( cnt );
            return cnt;
        } finally {
            NDC.remove();
//            for( int i=0; i < stacksize; ++i )
//                ctx.push( NDC.pop() );
        }
    }


    private void validateClient( final GridFTPClient cnt ) throws ServerException, IOException, ClientException {
        boolean d = false;
        try {
            log.debug( "validating client " + host + ":" + port );
            //cnt.getFeatureList();
         //   cnt.list();
            cnt.changeDir( "/" );
            d = true;
            log.debug( "successful validated" );
        } finally {
            if( d == false ) {
                log.debug( "validation failed, discarding client" );
                cnt.close();
                log.debug( "done" ); 
            }
        }
    }


    public String getHost() {
        return host;
    }


    public void setHost( final String host ) {
        this.host = host;
    }


    public int getPort() {
        return port;
    }


    public void setPort( final int port ) {
        this.port = port;
    }
}