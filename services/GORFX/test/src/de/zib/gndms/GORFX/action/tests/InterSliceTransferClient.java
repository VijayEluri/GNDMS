package de.zib.gndms.GORFX.action.tests;

import de.zib.gndms.dspace.slice.client.SliceClient;
import de.zib.gndms.dspace.subspace.client.SubspaceClient;
import de.zib.gndms.dspace.client.DSpaceClient;
import de.zib.gndms.kit.application.AbstractApplication;
import de.zib.gndms.typecon.common.GORFXClientTools;
import de.zib.gndms.typecon.common.GORFXTools;
import org.apache.axis.message.MessageElement;
import org.kohsuke.args4j.Option;
import types.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author: Maik Jorra <jorra@zib.de>
 * @version: $Id$
 * <p/>
 * User: mjorra, Date: 04.11.2008, Time: 18:28:22
 */
public class InterSliceTransferClient extends AbstractApplication {

    public static int MILLIS = 500;
    
    // do provider stagin
    // create new slice on different host
    // transfer staged data to created slice

    @Option( name="-uri", required=true, usage="URI of the gorfx service" )
    public String uri;
    @Option( name="-duri", required=true, usage="Destination dspace uri (where TransferTest subspace resides)" )
    public String duri;
    @Option( name="-props", required=true, usage="properties for the staging request" )
    public String props;
    @Option( name="-sk", metaVar="URI", required=true, usage="slice kind uri for target slice" )
    public String skuri;


    final private ContextT xsdContext = new ContextT();

    public void run() throws Exception {

        // Create reusable context with pseudo DN
        final ContextT xsdContext = new ContextT();
        final ContextTEntry entry =
            GORFXTools.createContextEntry("Auth.DN", "any_dn" );
        xsdContext.setEntry(new ContextTEntry[] { entry });

        SliceReference sr =
            GORFXTestClientUtils.doStageIn( uri, props, xsdContext, GORFXTestClientUtils.newContract(), MILLIS );

        // create a new Slice in the target subspace
        DSpaceClient dcnt = new DSpaceClient( duri );
        SubspaceClient subc =
            new SubspaceClient(
                dcnt.getSubspace( new QName( "http://www.c3grid.de/G2/Subspace", "TransferSubspace" ) ).getEndpointReference() );

        Calendar tt = new GregorianCalendar( );
        tt.add( Calendar.YEAR, 20 );
        long ssize = (long) (20 * 1024 * Math.pow( 10, 3 ));
        SliceClient slice = subc.createSlice( skuri, tt, ssize );

        // prepare inter slice transfer
        InterSliceTransferORQT istorq =
            GORFXClientTools.createInterSliceTransferORQT( sr, new SliceReference( slice.getEndpointReference() ) );

        InterSliceTransferResultT isrt =
            GORFXTestClientUtils.commonTaskExecution( uri, istorq, xsdContext,
                GORFXTestClientUtils.newContract(), MILLIS, InterSliceTransferResultT.class );

        MessageElement[] mes = isrt.get_any( );
        ArrayList<String> al = new ArrayList( mes.length );
        for( int i=0; i < mes.length; ++i )
            al.add( (String) mes[i].getObjectValue( String.class ) );
        String[] fls = al.toArray( new String[al.size()] );
        System.out.println( "Copied files: " );
        for( String s: Arrays.asList( fls ) ) {
            System.out.println( "\t" + s );
        }
        System.exit( 0 );

    }


    public static void main( String[] args ) throws Exception {

        InterSliceTransferClient cnt = new InterSliceTransferClient();
        cnt.run( args );
    }
}