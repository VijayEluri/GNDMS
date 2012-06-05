/*
 * Copyright 2008-2012 Zuse Institute Berlin (ZIB)
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
 *
 */

package de.zib.gndms.common.kit.security;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
* @date: 04.06.12
* @time: 12:09
* @author: Jörg Bachmann
* @email: bachmann@zib.de
*/
public class CustomSSLContextRequestFactory extends SimpleClientHttpRequestFactory {
    final private SSLContext sslContext;

    public CustomSSLContextRequestFactory( SSLContext sslContext ) {
        this.sslContext = sslContext;
    }

    protected void prepareConnection( HttpURLConnection connection, String httpMethod )
            throws IOException
    {
        super.prepareConnection( connection, httpMethod );

        if( connection instanceof HttpsURLConnection)
        {
            HttpsURLConnection httpscon = ( HttpsURLConnection )connection;

            httpscon.setSSLSocketFactory( sslContext.getSocketFactory() );
        }
    }
}
