package de.zib.adis;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;


public class ABI
{
        @Parameter( names = "--usage", hidden=true )
        private boolean usage;

        @Parameter( names={ "--baseurl", "-b" }, description="Base URL of VolD database", required=true )
        private String voldURL;

        @Parameter( names = { "--grid", "-g" }, description="Grid name" )
        private String grid = "c3grid";

        @Parameter( names = { "--enc", "-e" }, description="Encoding" )
        private String enc = "utf-8";

        @Parameter( description="Commands" )
        private List< String > commands = new ArrayList< String>();

        public static void main( String[] args )
        {
                ABI abi = new ABI();

                JCommander jc = new JCommander( abi, args );

                if( abi.usage )
                {
                        jc.usage();
                        System.exit( 0 );
                }

                Adis adis = new Adis( );
                adis.setVoldURL( abi.voldURL );
                adis.setEnc( abi.enc );
                adis.setGrid( abi.grid );

                // we need at least a methods name
                if( abi.commands.size() < 1 )
                {
                        System.exit( 0 );
                }

                String cmd = abi.commands.remove( 0 );

                // use reflect to call adis method
                {
                        Class c = Adis.class;
                        Method[] methods = c.getDeclaredMethods( );

                        // search the right method
                        for( Method m: methods )
                        {
                                // search the right method
                                if( Modifier.PUBLIC != m.getModifiers() )
                                        continue;
                                if( ! m.getName().equals( cmd ) )
                                        continue;

                                Class[] paramtypes = m.getParameterTypes();

                                // get parameters
                                Object[] params = new Object[ paramtypes.length ];
                                for( int i = 0; i < paramtypes.length; ++i )
                                {
                                        //if( paramtypes[i].isInstance( String.class ) )
                                        if( paramtypes[i].isAssignableFrom( String.class ) )
                                        {
                                                if( 0 == abi.commands.size() )
                                                {
                                                        throw new IllegalArgumentException( "The command " + cmd + " excepts at least one more argument on method " + m.toGenericString() + "." );
                                                }

                                                params[ i ] = abi.commands.remove( 0 );
                                        }
                                        //else if( paramtypes[i].isInstance( Collection.class ) )
                                        else if( paramtypes[i].isAssignableFrom( Collection.class ) )
                                        {
                                                Set< String > s = new HashSet< String >();

                                                while( 0 != abi.commands.size() )
                                                {
                                                        s.add( abi.commands.remove( 0 ) );
                                                }

                                                params[i] = s;
                                        }
                                        else
                                        {
                                                throw new IllegalStateException( "Internal Error: don't understand the parameter type of the API." );
                                        }
                                }

                                // call and handle return value
                                Object result;
                                try
                                {
                                        result = m.invoke( adis, params );
                                }
                                catch( Exception e )
                                {
                                        e.printStackTrace();
                                        return;
                                }

                                // handle results
                                {
                                        if( result instanceof String )
                                        {
                                                String r = ( String )result;
                                                System.out.println( r );
                                        }
                                        else if ( result instanceof Collection< ? > )
                                        {
                                                Collection< String > r = ( Collection< String > )result;
                                                printCollection( r );
                                        }
                                        else if( result instanceof Map< ?, ? > )
                                        {
                                                Map< String, String > r = ( Map< String, String > )result;
                                                printMap( r );
                                        }
                                        else if( Boolean.TYPE.isInstance( result ) )
                                        {
                                        }
                                        else if( Character.TYPE.isInstance( result ) )
                                        {
                                        }
                                        else if( Byte.TYPE.isInstance( result ) )
                                        {
                                        }
                                        else if( Short.TYPE.isInstance( result ) )
                                        {
                                        }
                                        else if( Integer.TYPE.isInstance( result ) )
                                        {
                                        }
                                        else if( Long.TYPE.isInstance( result ) )
                                        {
                                        }
                                        else if( Float.TYPE.isInstance( result ) )
                                        {
                                        }
                                        else if( Double.TYPE.isInstance( result ) )
                                        {
                                        }
                                        else if( Void.TYPE.isInstance( result ) )
                                        {
                                        }
                                }

                                return;
                        }

                        // did not find it - put list of possible methods
                        {
                                System.out.println( "List of possible commands: " );
                                for( Method m: methods )
                                {
                                        System.out.println( m.getName() + " |--> " + m.toGenericString() );
                                }
                        }
                }

        }

        private static void printCollection( Collection< String > col )
        {
                for( String s: col )
                {
                        System.out.println( s );
                }
        }

        private static void printMap( Map< String, String > map )
        {
                for( Map.Entry< String, String > entry: map.entrySet() )
                {
                        System.out.println( entry.getKey() + ": " + entry.getValue() );
                }
        }

        private static void commands( )
        {
                System.out.println( "valid commands are:" );
                System.out.println( "   getwss" );
                System.out.println( "   getdms" );
                System.out.println( "   listoais" );
                System.out.println( "   listimportsites" );
                System.out.println( "   listexportsites" );
                System.out.println( "   listworkfows" );
                System.out.println( "   listpublisher" );
                System.out.println( "   listgorfxbyoid OIDPREFIX" );
                System.out.println( "   getepbyworkflow WORKFLOW" );
        }
}
