package de.zib.gndms.comserv.delegation;

import de.zib.gndms.kit.application.AbstractApplication;
import org.kohsuke.args4j.Option;

import org.globus.wsrf.impl.security.descriptor.ClientSecurityDescriptor;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.globus.wsrf.impl.security.authorization.HostAuthorization;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.globus.wsrf.utils.AddressingUtils;

import org.globus.axis.util.Util;
import org.apache.axis.message.addressing.EndpointReferenceType;

import org.globus.delegation.DelegationUtil;
import org.globus.delegation.DelegationConstants;


import org.globus.wsrf.encoding.ObjectSerializer;

import org.globus.wsrf.impl.security.util.AuthUtil;

import java.security.cert.X509Certificate;

import org.globus.gsi.GlobusCredential;

import javax.xml.namespace.QName;

import java.io.FileWriter;

/**
 * @author Maik Jorra <jorra@zib.de>
 * @version $Id$
 *          <p/>
 *          User: mjorra, Date: 28.01.2009, Time: 12:16:24
 */
public class DelClient extends AbstractApplication {


    @Option( name="-uri", required=true, usage="URL of GORFX-Endpoint", metaVar="URI" )
    protected String uri;

    @Option( name="-proxyfile", usage="grid-proxy-file to lead", metaVar="proxy-file" )
    protected String proxyFile;


    public static void main(String[] args) throws Exception {
        
        DelClient cnt = new DelClient();
        cnt.run( args );
    }


    public void run() throws Exception {

        // aquire cert chain
        ClientSecurityDescriptor desc = new ClientSecurityDescriptor();

//        desc.setGSITransport( (Integer) Constants.ENCRYPTION );

        System.out.println( "connecting to service: " + uri );
        EndpointReferenceType delegEpr = AddressingUtils.createEndpointReference( uri, null);
        System.out.println( "epr: " + delegEpr );

        desc.setGSITransport( (Integer) Constants.SIGNATURE );
        Util.registerTransport();
        desc.setAuthz( NoAuthorization.getInstance() );
        
        X509Certificate[] certs = DelegationUtil.getCertificateChainRP( delegEpr, desc );

        if( certs == null  )
            throw new Exception( "No Certs received" );

        int len = certs.length;
        System.out.println( "Cert cnt: " + len );
        if( len > 0 )
        System.out.println( certs[0] );


        //  load global cert
        GlobusCredential credential = null;
        if (proxyFile == null) {
            credential = GlobusCredential.getDefaultCredential();
        } else {
            credential = new GlobusCredential(proxyFile);
        }
        System.out.println( credential );

        // get delegate:
        int tt = 600;
        EndpointReferenceType delegatedCredEPR = DelegationUtil.delegate( uri, credential, certs[0], tt, true, desc);

        System.out.println("Delegated credential EPR:\n" + delegatedCredEPR);

        FileWriter writer = null;
        try {
            writer = new FileWriter( "/home/mjorra/tmp/cred_out");
            QName qName = new QName("", "DelegatedEPR");
            writer.write(ObjectSerializer.toString(delegatedCredEPR,
                qName));
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}