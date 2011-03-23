package de.zib.gndms.gndmc.offline;
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

import de.zib.gndms.logic.taskflow.tfmockup.DummyOrder;
import de.zib.gndms.model.gorfx.types.Order;
import de.zib.gndms.rest.Facet;
import de.zib.gndms.rest.Facets;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;

import javax.naming.spi.ObjectFactory;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * @author try ma ik jo rr a zib
 * @date 23.03.11  15:31
 * @brief
 */
public class JsonTest {

    public static void main( String[] args ) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping( ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY );

        DummyOrder dft = new DummyOrder();
        dft.setMessage( "Test task flow is flowing" );
        dft.setTimes( 20 );
        dft.setDelay( 1000 );
        dft.setFailIntentionally( false );

        final Facet f = new Facet( "foo", "http://fuuu" );
        final Facet f2 = new Facet( "bar", "http://barz.org" );
        Facets fs = new Facets();
        ArrayList<Facet> al =  new ArrayList<Facet>( 2 );
        al.add(f);
        al.add(f2);
        fs.setFacets( al );

        System.out.println( "writing json object" );
        ByteArrayOutputStream bo = new ByteArrayOutputStream( 1000*1024 );
        mapper.writeValue( bo, fs );
        bo.close();
        byte[] buf = bo.toByteArray();
        System.out.println( new String( buf ) );

        System.out.println( "reading back json object" );
        ByteArrayInputStream bi = new ByteArrayInputStream( buf );
        //DummyOrder do2 = (DummyOrder) mapper.readValue( bi, Order.class );
        Facets do2 = mapper.readValue( bi, Facets.class );
        System.out.println( do2.toString() );

        System.exit( 0 );
    }

}
