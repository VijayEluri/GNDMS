package de.zib.gndms.gndms.security;
/*
 * Copyright 2008-2011 Zuse Institute Berlin (ZIB)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maik Jorra
 * @email jorra@zib.de
 * @date 29.02.12  17:17
 * @brief The UserDetails provider for gndms.
 *
 * This is part of the service authentication module.
 *
 * It checks the role of an pre-authenticated user. Role-checking is done by look-ups in
 * grid-mapfiles. To possible files can be registered on for admins an one for standard user
 * depending on which file contains the requested DN the users Role is set.
 *
 * If a DN doesn't occur in on of the grid-mapfiles the user isn't allowed to access the service.
 */
public class GridMapUserDetailsService implements UserDetailsService {

    private String gridMapfileName;
    private String adminGridMapfileName;


    public GridMapUserDetailsService() {
    }


    @Override
    public UserDetails loadUserByUsername( final String dn ) throws UsernameNotFoundException {

        List<GrantedAuthority> authorityList =  new ArrayList<GrantedAuthority>( 1 );
        // search admin
        boolean isUser = true;
        String localUser = null;
        try {
            localUser = searchInGridMapfile( adminGridMapfileName, dn );
        	if( localUser != null) {
                authorityList.add( adminRole() );
                
            } else {
            	localUser = searchInGridMapfile( gridMapfileName, dn );
            	if( localUser != null )
                authorityList.add( userRole() );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        if ( isUser && authorityList.size() == 0 )
            throw new UsernameNotFoundException( "DN not permitted: " + dn );

        GNDMSUserDetails userDetails = new GNDMSUserDetails( );
        userDetails.setAuthorities( authorityList );
        userDetails.setDn( dn );
        userDetails.setIsUser( isUser );
        userDetails.setLocalUser(localUser);

        return userDetails;
    }


    private SimpleGrantedAuthority userRole() {

        return new SimpleGrantedAuthority( "ROLE_USER" );
    }


    private SimpleGrantedAuthority adminRole() {

        return new SimpleGrantedAuthority( "ROLE_ADMIN" );
    }


    public static String searchInGridMapfile( final String fileName,
                                           final String dn ) throws IOException {
        
        BufferedReader reader = new BufferedReader( new FileReader( fileName ) );

        try{
            String line;
            while ( ( line = reader.readLine() ) != null ) {

                if( line.startsWith( "\""+ dn + "\"" ) )
                    return line.substring(dn.length()+2).trim();

            }
        } finally {  reader.close(); }

        return null;
    }


    public String getGridMapfileName() {

        return gridMapfileName;
    }


    public void setGridMapfileName( final String gridMapfileName ) {

        this.gridMapfileName = gridMapfileName;
    }


    public String getAdminGridMapfileName() {

        return adminGridMapfileName;
    }


    public void setAdminGridMapfileName( final String adminGridMapfileName ) {

        this.adminGridMapfileName = adminGridMapfileName;
    }



}
