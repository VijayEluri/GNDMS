package de.zib.gndms.dspace.service;

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

import de.zib.gndms.common.dspace.SliceConfiguration;
import de.zib.gndms.common.dspace.service.SliceInformation;
import de.zib.gndms.common.dspace.service.SliceService;
import de.zib.gndms.common.model.FileStats;
import de.zib.gndms.common.rest.*;
import de.zib.gndms.dspace.service.utils.UnauthorizedException;
import de.zib.gndms.gndmc.gorfx.TaskClient;
import de.zib.gndms.infra.system.GNDMSystem;
import de.zib.gndms.kit.util.DirectoryAux;
import de.zib.gndms.logic.model.dspace.*;
import de.zib.gndms.model.common.NoSuchResourceException;
import de.zib.gndms.model.dspace.Slice;
import de.zib.gndms.model.dspace.SliceKind;
import de.zib.gndms.model.dspace.Subspace;
import de.zib.gndms.model.util.TxFrame;
import de.zib.gndms.neomodel.gorfx.Taskling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

// import de.zib.gndms.neomodel.gorfx.Taskling;

/**
 * The sliceId service implementation.
 * 
 * @author Ulrike Golas
 */

@Controller
@RequestMapping(value = "/dspace")
public class SliceServiceImpl implements SliceService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private EntityManagerFactory emf;
    private String localBaseUrl;
    private String baseUrl;
	private SubspaceProvider subspaceProvider;
	private SliceKindProvider sliceKindProvider;
	private SliceProvider sliceProvider;
	private List< String > sliceFacetNames;
	private UriFactory uriFactory;

    public static final Pattern SLICE_CONFIG_NAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9-_]*");

    private DirectoryAux directoryAux;

    private GNDMSystem system;

    private RestTemplate restTemplate;

    @Inject
    public void setSliceKindProvider( SliceKindProvider sliceKindProvider ) {
        this.sliceKindProvider = sliceKindProvider;
    }

    @Inject
    public void setSliceProvider(SliceProvider sliceProvider) {
        this.sliceProvider = sliceProvider;
    }

    public void setUriFactory(UriFactory uriFactory) {
        this.uriFactory = uriFactory;
    }

    /**
	 * Initialization of the sliceId service.
	 */
	@PostConstruct
	public final void init() {
        setUriFactory( new UriFactory() );
	}

	@Override
	@RequestMapping( value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}", method = RequestMethod.GET )
    @Secured( "ROLE_USER" )
	public ResponseEntity< Facets > listSliceFacets(
            @PathVariable final String subspaceId,
            @PathVariable final String sliceKindId,
			@PathVariable final String sliceId,
			@RequestHeader( "DN" ) final String dn ) {
		GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKindId, sliceId, dn );

        try {
            // check for the existence of that slice
            findSliceOfKind(subspaceId, sliceKindId, sliceId);

            return new ResponseEntity< Facets >( new Facets( listFacetsOfSlice( subspaceId, sliceKindId, sliceId ) ), headers, HttpStatus.OK );
        } catch ( NoSuchElementException ne ) {
            logger.warn( "The sliceId " + sliceId + " of sliceId kind " + sliceKindId
                    + "does not exist within the subspace " + subspaceId + "." );
            return new ResponseEntity< Facets >( null,
                    headers, HttpStatus.NOT_FOUND );
        }
	}

    @Override
    @RequestMapping( value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}/config", method = RequestMethod.PUT )
    @Secured( "ROLE_USER" )
    public ResponseEntity< Integer > setSliceConfiguration(
            @PathVariable final String subspaceId,
            @PathVariable final String sliceKindId,
            @PathVariable final String sliceId,
            @RequestBody final SliceConfiguration config,
            @RequestHeader( "DN" ) final String dn ) {
        GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKindId, sliceId, dn );

        // TODO: put that to sliceProvider
        EntityManager entityManager = emf.createEntityManager();
        final TxFrame txf = new TxFrame( entityManager );
        try {
            final Slice slice = entityManager.find( Slice.class, sliceId );
            if( config.getSize() != null ) {
                final Subspace subspace = entityManager.find( Subspace.class, subspaceId );
                
                subspace.releaseSpace( slice.getTotalStorageSize() );
                subspace.requestSpace( config.getSize() );
            }
            slice.setConfiguration( config );
            txf.commit();
        }
        finally { txf.finish();  }
        sliceProvider.invalidate( sliceId );
        subspaceProvider.invalidate( subspaceId );

        return new ResponseEntity< Integer >( 0, headers, HttpStatus.OK );
    }

    @Override
    @RequestMapping( value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}/config", method = RequestMethod.GET )
    @Secured( "ROLE_USER" )
    public ResponseEntity< SliceInformation > getSliceInformation(
            @PathVariable final String subspaceId,
            @PathVariable final String sliceKindId,
            @PathVariable final String sliceId,
            @RequestHeader("DN") final String dn ) {
        GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKindId, sliceId, dn);

        try {
            Slice sliceModel = findSliceOfKind( subspaceId, sliceKindId, sliceId );
            de.zib.gndms.infra.dspace.Slice slice = new de.zib.gndms.infra.dspace.Slice( sliceModel );

            return new ResponseEntity< SliceInformation >( slice.getSliceInformation(), headers, HttpStatus.OK );
        } catch( NoSuchElementException e) {
            logger.warn( e.getMessage() );
            return new ResponseEntity< SliceInformation >( new SliceInformation(), headers, HttpStatus.NOT_FOUND );
        }
    }

	@Override
	@RequestMapping(value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}", method = RequestMethod.POST)
    @Secured( "ROLE_USER" )
	public ResponseEntity<Specifier<Void>> transformSlice(
			@PathVariable final String subspaceId,
			@PathVariable final String sliceKindId,
			@PathVariable final String sliceId,
            @RequestBody final Specifier<Void> newSliceKind,
            @RequestHeader("DN") final String dn) {
		GNDMSResponseHeader headers = setHeaders(subspaceId, sliceKindId, sliceId, dn);

		try {
			Slice slice = findSliceOfKind( subspaceId, sliceKindId, sliceId );

			SliceKind newSliceK = sliceKindProvider.get( subspaceId,
                    newSliceKind.getUrl());
			Subspace space = subspaceProvider.get( subspaceId );

            EntityManager em = emf.createEntityManager();
			TxFrame tx = new TxFrame( em );
			try {
				// TODO is this right? what is this uuid generator (last entry)?
				TransformSliceAction action = new TransformSliceAction(
						dn, slice.getTerminationTime(),
						newSliceK, space, slice.getTotalStorageSize(), null);
				action.setOwnEntityManager( em );
				logger.info("Calling action for transforming sliceId " + sliceId
						+ ".");
				action.call();
				tx.commit();
			} finally {
				tx.finish();
				if ( em != null && em.isOpen()) {
					em.close();
				}
			}

			Specifier<Void> spec = new Specifier<Void>();

			HashMap<String, String> urimap = new HashMap<String, String>(2);
			urimap.put("service", "dspace");
			urimap.put(UriFactory.SUBSPACE, subspaceId );
			urimap.put(UriFactory.SLICE_KIND, sliceKindId );
			urimap.put(UriFactory.SLICE, sliceId );
			spec.setUriMap(new HashMap<String, String>(urimap));
			spec.setUrl(uriFactory.quoteUri(urimap));

			return new ResponseEntity<Specifier<Void>>(spec, headers,
					HttpStatus.OK);
		} catch (NoSuchElementException ne) {
			logger.warn(ne.getMessage());
			return new ResponseEntity<Specifier<Void>>(null, headers,
					HttpStatus.NOT_FOUND);
		}
	}

	@Override
	@RequestMapping( value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}", method = RequestMethod.DELETE )
    @Secured( "ROLE_USER" )
	public ResponseEntity<Specifier<Facets>> deleteSlice(
            @PathVariable final String subspaceId,
            @PathVariable final String sliceKindId,
            @PathVariable final String sliceId,
            @RequestHeader("DN") final String dn) {
		GNDMSResponseHeader headers = setHeaders(subspaceId, sliceKindId, sliceId, dn);

		try {
            // submit action
            final Taskling ling = sliceProvider.deleteSlice(subspaceId, sliceId);

            // get service facets of task
            final TaskClient client = new TaskClient( localBaseUrl );
            client.setRestTemplate( restTemplate );
            final Specifier< Facets > spec =
                    TaskClient.TaskServiceAux.getTaskSpecifier( client, ling.getId(), uriFactory, null, dn );

            // return specifier for service facets
            return new ResponseEntity< Specifier< Facets > >( spec, headers, HttpStatus.OK );
		} catch (NoSuchElementException ne) {
			logger.warn(ne.getMessage());
			return new ResponseEntity<Specifier<Facets>>(null, headers,
					HttpStatus.NOT_FOUND);
		}
	}

	@Override
	@RequestMapping(value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}/files", method = RequestMethod.GET)
    @Secured( "ROLE_USER" )
	public ResponseEntity< List<FileStats> > listFiles(
			@PathVariable final String subspaceId,
			@PathVariable final String sliceKindId,
			@PathVariable final String sliceId,
			@RequestHeader( "DN" ) final String dn ) {
		final GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKindId, sliceId, dn );

		try {
			final Subspace space = subspaceProvider.get( subspaceId );
			final Slice slice = findSliceOfKind( subspaceId, sliceKindId, sliceId );
			final String path = space.getPathForSlice( slice );
            
			File dir = new File( path );
			if( dir.exists() && dir.canRead() && dir.isDirectory() ) {
                List<FileStats> files = new LinkedList<FileStats>();
                recursiveListFiles( path, "", files );
                
                headers.add( "DiskUsage", String.valueOf( sliceProvider.getDiskUsage(subspaceId, sliceId) ) );
                
				return new ResponseEntity< List<FileStats> >( files, headers, HttpStatus.OK );
			} else {
				return new ResponseEntity< List<FileStats> >( null, headers, HttpStatus.FORBIDDEN );
			}
		} catch( NoSuchElementException ne ) {
			logger.warn( ne.getMessage() );
			return new ResponseEntity< List<FileStats> >( null, headers, HttpStatus.NOT_FOUND );
		}
	}

    @RequestMapping(value = "/_{subspace}/_{sliceKind}/_{sliceId}/files", method = RequestMethod.POST)
    @Secured( "ROLE_USER" )
    public ResponseEntity<Integer> setFileContents(
            @PathVariable final String subspaceId,
            @PathVariable final String sliceKind,
            @PathVariable final String sliceId,
            @RequestParam( "files" ) final List< MultipartFile > files,
            @RequestHeader("DN") final String dn) {
        GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKind, sliceId, dn);

        try {
            Subspace space = subspaceProvider.get( subspaceId );
            Slice slice = findSliceOfKind( subspaceId, sliceKind, sliceId );
            String path = space.getPathForSlice(slice);
            
            final long sliceMaxSize = slice.getTotalStorageSize();
            
            for( MultipartFile file: files ) {
                long sliceSize = sliceProvider.getDiskUsage(subspaceId, sliceId);
                if( sliceSize >= sliceMaxSize )
                    throw new IOException( "Slice " + sliceId + " has reached maximum size of " + sliceMaxSize + " Bytes" );
                
                File newFile = new File( path + File.separatorChar + file.getOriginalFilename() );

                if( newFile.exists() ) {
                    logger.warn( "File " + newFile + "will be overwritten." );
                }

                file.transferTo( newFile );
            }
            return new ResponseEntity<Integer>(0, headers, HttpStatus.OK);
        } catch (NoSuchElementException ne) {
            logger.warn(ne.getMessage(), ne);
            return new ResponseEntity<Integer>(0, headers, HttpStatus.NOT_FOUND);
        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage(), e);
            return new ResponseEntity<Integer>(0, headers, HttpStatus.FORBIDDEN);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            return new ResponseEntity<Integer>(0, headers, HttpStatus.FORBIDDEN);
        }
    }

	@Override
	@RequestMapping(value = "/_{subspace}/_{sliceKind}/_{slice}/files", method = RequestMethod.DELETE)
    @Secured( "ROLE_USER" )
	public ResponseEntity<Integer> deleteFiles(
			@PathVariable final String subspaceId,
			@PathVariable final String sliceKind,
			@PathVariable final String sliceId,
			@RequestHeader("DN") final String dn) {
		GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKind, sliceId, dn);

		try {
			Subspace space = subspaceProvider.get( subspaceId );
			Slice slice = findSliceOfKind( subspaceId, sliceKind, sliceId );
			String path = space.getPathForSlice( slice );

            File f = new File( path );
            String[] fl = f.list( );
            for( String s: fl ) {
                String p = path + File.separatorChar + s;
                if ( !directoryAux.deleteDirectory( dn, p ) ) {
                    logger.warn("Some file in directory " + p + " could not be deleted.");
                    return new ResponseEntity<Integer>(0, headers, HttpStatus.CONFLICT);
                }
            }
		} catch (NoSuchElementException ne) {
			logger.warn(ne.getMessage());
			return new ResponseEntity<Integer>(0, headers, HttpStatus.NOT_FOUND);
		}
        return new ResponseEntity<Integer>(0, headers, HttpStatus.OK);
	}

    @Override
    @RequestMapping(value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}/gsiftp", method = RequestMethod.GET)
    @Secured( "ROLE_USER" )
    public ResponseEntity<String> getGridFtpUrl(
            @PathVariable final String subspaceId,
            @PathVariable final String sliceKindId,
            @PathVariable final String sliceId,
            @RequestHeader("DN") final String dn) {
        GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKindId, sliceId, dn );

        try {
            Subspace space = subspaceProvider.get( subspaceId );
            Slice slice = findSliceOfKind( subspaceId, sliceKindId, sliceId );
            return new ResponseEntity<String>(
                    space.getGsiFtpPathForSlice( slice ), headers, HttpStatus.OK);
        } catch (NoSuchElementException ne) {
            logger.warn(ne.getMessage());
            return new ResponseEntity<String>(null, headers,
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @RequestMapping(value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}/directory", method = RequestMethod.GET)
    @Secured( "ROLE_USER" )
    public ResponseEntity<String> getRelativeDirectoryPath(
            @PathVariable final String subspaceId,
            @PathVariable final String sliceKindId,
            @PathVariable final String sliceId,
            @RequestHeader("DN") final String dn) {
        GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKindId, sliceId, dn );

        try {
            Slice slice = findSliceOfKind( subspaceId, sliceKindId, sliceId );

            return new ResponseEntity<String>(
                    slice.getKind().getSliceDirectory() + File.separatorChar + slice.getDirectoryId(), headers, HttpStatus.OK);
        } catch (NoSuchElementException ne) {
            logger.warn(ne.getMessage());
            return new ResponseEntity<String>(null, headers,
                    HttpStatus.NOT_FOUND);
        }
    }

	@Override
	@RequestMapping(value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}/_{fileName:.*}",
            method = RequestMethod.GET)
    @Secured( "ROLE_USER" )
	public ResponseEntity<Integer> listFileContent(
            @PathVariable final String subspaceId,
            @PathVariable final String sliceKindId,
            @PathVariable final String sliceId,
            @PathVariable final String fileName,
            @RequestParam( value="attrs", required = false) final List<String> attrs,
            @RequestHeader("DN") final String dn,
            final OutputStream out) {
		GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKindId, sliceId, dn );

		try {
			Subspace space = subspaceProvider.get( subspaceId );
			Slice slice = findSliceOfKind( subspaceId, sliceKindId, sliceId);
			String path = space.getPathForSlice(slice);
			File file = new File(path + File.separatorChar + fileName);

			if (out == null) {
                final IllegalStateException illegalStateException =
                        new IllegalStateException( "OutputStream not defined." );
                logger.warn( illegalStateException.getMessage() );
                throw illegalStateException;
			}

			if (file.exists() && file.canRead() && file.isFile()) {
				// TODO get requested file attributes

				if ( attrs == null || attrs.contains("contents")) {
                    FileCopyUtils.copy( new FileInputStream( file ), out );
				}
                
				return new ResponseEntity<Integer>(0, headers,
						HttpStatus.OK);
			} else {
				logger.warn("File " + file + " cannot be read or is no file.");
				return new ResponseEntity<Integer>(0, headers,
						HttpStatus.FORBIDDEN);
			}

		} catch (NoSuchElementException ne) {
			logger.warn(ne.getMessage());
			return new ResponseEntity<Integer>(0, headers,
					HttpStatus.NOT_FOUND);
		} catch (FileNotFoundException e) {
			logger.warn(e.getMessage());
			return new ResponseEntity<Integer>(0, headers,
					HttpStatus.FORBIDDEN);
		} catch (IOException e) {
			logger.warn(e.getMessage());
			return new ResponseEntity<Integer>(0, headers,
					HttpStatus.FORBIDDEN);
		}
	}

	@Override
	@RequestMapping(value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}/_{fileName:.*}", method = RequestMethod.POST)
    @Secured( "ROLE_USER" )
	public ResponseEntity<Integer> setFileContent(
			@PathVariable final String subspaceId,
			@PathVariable final String sliceKindId,
			@PathVariable final String sliceId,
			@PathVariable final String fileName,
			@RequestParam( "file" ) final MultipartFile file,
            @RequestHeader("DN") final String dn) {
		GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKindId, sliceId, dn );

		try {
			Subspace space = subspaceProvider.get( subspaceId );
			Slice slice = findSliceOfKind( subspaceId, sliceKindId, sliceId );
			String path = space.getPathForSlice(slice);
			File newFile = new File(path + File.separatorChar + fileName);

			if (newFile.exists()) {
				logger.warn("File " + newFile + "will be overwritten. ");			
			}

            final long sliceMaxSize = slice.getTotalStorageSize();
            long sliceSize = sliceProvider.getDiskUsage(subspaceId, sliceId);
            if( sliceSize >= sliceMaxSize )
                throw new IOException( "Slice " + sliceId + " has reached maximum size of " + sliceMaxSize + " Bytes" );

            
            file.transferTo( newFile );
			
			//DataOutputStream dos = new DataOutputStream(new FileOutputStream(newFile));

			//dos.write(file.getBytes());
			//dos.close();
			return new ResponseEntity<Integer>(0, headers, HttpStatus.OK);
		} catch (NoSuchElementException ne) {
			logger.warn(ne.getMessage(), ne);
			return new ResponseEntity<Integer>(0, headers, HttpStatus.NOT_FOUND);
		} catch (FileNotFoundException e) {
			logger.warn(e.getMessage(), e);
			return new ResponseEntity<Integer>(0, headers, HttpStatus.FORBIDDEN);
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
			return new ResponseEntity<Integer>(0, headers, HttpStatus.FORBIDDEN);
		}
	}

	@Override
	@RequestMapping(value = "/_{subspaceId}/_{sliceKindId}/_{sliceId}/_{fileName:.*}", method = RequestMethod.DELETE)
    @Secured( "ROLE_USER" )
	public ResponseEntity<Integer> deleteFile(
			@PathVariable final String subspaceId,
			@PathVariable final String sliceKindId,
			@PathVariable final String sliceId,
            @PathVariable final String fileName,
            @RequestHeader("DN") final String dn) {
		GNDMSResponseHeader headers = setHeaders( subspaceId, sliceKindId, sliceId, dn );

		try {
			Subspace space = subspaceProvider.get( subspaceId );
			Slice slice = findSliceOfKind( subspaceId, sliceKindId, sliceId );
			String path = space.getPathForSlice( slice );

            if( directoryAux.deleteDirectory( dn, path + File.separator + fileName ) ) {
                return new ResponseEntity< Integer >( 0, headers, HttpStatus.OK );
            } else {
                logger.warn( "File " + path + " could not be deleted." );
                return new ResponseEntity< Integer >( 0, headers, HttpStatus.FORBIDDEN );
            }
		} catch (NoSuchElementException ne) {
			logger.warn(ne.getMessage(), ne);
			return new ResponseEntity<Integer>(0, headers, HttpStatus.NOT_FOUND);
		}
	}

    private List< Facet > listFacetsOfSlice( String subspaceId, String sliceKindId, String sliceId ) {
        Map< String, String > vars = new HashMap< String, String >( );
        vars.put( "service", "dspace" );
        vars.put( "subspace", subspaceId );
        vars.put( "sliceKind", sliceKindId );
        vars.put( "sliceId", sliceId );

        List< Facet > facets = new LinkedList< Facet >( );

        for( String facetName: sliceFacetNames ) {
            Facet facet = new Facet( facetName, uriFactory.sliceUri(vars, facetName) );
            facets.add( facet );
        }
        return facets;
    }

	/**
	 * Sets the GNDMS response header for a given subspace, sliceId kind, sliceId
	 * and dn using the base URL.
	 * 
	 * @param subspace
	 *            The subspace id.
	 * @param sliceKind
	 *            The sliceId kind id.
	 * @param slice
	 *            The sliceId id.
	 * @param dn
	 *            The dn.
	 * @return The response header for this subspace.
	 */
	private GNDMSResponseHeader setHeaders(final String subspace,
			final String sliceKind, final String slice, final String dn) {
		GNDMSResponseHeader headers = new GNDMSResponseHeader();
		headers.setResourceURL(baseUrl + "/dspace/_" + subspace + "/_"
				+ sliceKind + "/_" + slice);
		headers.setParentURL(baseUrl + "/dspace/_" + subspace + "/_"
				+ sliceKind);
		if (dn != null) {
			headers.setDN(dn);
		}
		return headers;
	}

	/**
	 * Returns a specific sliceId of a given sliceId kind id, if it exists in the
	 * subspace.
	 * 
	 * @param subspaceId
	 *            The subspace id.
	 * @param sliceKindId
	 *            The sliceId kind id.
	 * @param sliceId
	 *            The sliceId id.
	 * @return The sliceId.
	 * @throws NoSuchElementException
	 *             If no such sliceId exists.
	 */
	private Slice findSliceOfKind( final String subspaceId,
			final String sliceKindId, final String sliceId )
			throws NoSuchElementException {
		Slice slice = sliceProvider.getSlice( subspaceId, sliceId );
		SliceKind sliceK = sliceKindProvider.get( subspaceId, sliceKindId );

		if( !slice.getKind().equals( sliceK ) ) {
            logger.error( "Slice " + sliceId + " is of sliceKind " +
                    slice.getKind().getId() + " instead of " + sliceKindId );
			throw new NoSuchElementException();
		}
		return slice;
	}

    void recursiveListFiles( String path, String prefix, List<FileStats> list ) {
        List< String > flatContents = directoryAux.listContent( path );
        
        for( String c: flatContents ) {
            File f = new File( path + File.separatorChar + c );
            
            if( f.isDirectory() ) {
                try {
                    recursiveListFiles( f.getCanonicalPath(), prefix + File.separatorChar + c, list );
                } catch (IOException e) {
                    logger.error( "Could not get canonical path of " + f );
                }
            }
            else {
                FileStats stats = directoryAux.stat( f );
                stats.path = prefix + File.separatorChar + c;
                list.add( stats );
            }
        }
    }

	/**
	 * Returns the base url of this sliceId service.
	 * 
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Sets the base url of this sliceId service.
	 * 
	 * @param baseUrl
	 *            the baseUrl to set
	 */
	public void setBaseUrl(final String baseUrl) {
		this.baseUrl = baseUrl;
	}


    /**
     * Sets the local base url of this sliceId service.
     *
     * @param localBaseUrl
     *            the localBaseUrl to set
     */
    public void setLocalBaseUrl( String localBaseUrl ) {
        this.localBaseUrl = localBaseUrl;
    }


    /**
	 * Returns the facets of this sliceId service.
	 * 
	 * @return the sliceFacets
	 */
	public List< String > getSliceFacetNames() {
		return sliceFacetNames;
	}

	/**
	 * Sets the facets of this sliceId service.
	 * 
	 * @param sliceFacetNames
	 *            the sliceFacets to set
	 */
	public void setSliceFacetNames(final List< String > sliceFacetNames ) {
		this.sliceFacetNames = sliceFacetNames;
	}

    @Inject
    public void setSubspaceProvider( SubspaceProvider subspaceProvider )
    {
        this.subspaceProvider = subspaceProvider;
    }

    public GNDMSystem getSystem() {
        return system;
    }

    @Inject
    public void setSystem( GNDMSystem system ) {
        this.system = system;
    }

    @Inject
    public void setRestTemplate( RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }

    @Inject
    public void setDirectoryAux(DirectoryAux directoryAux) {
        this.directoryAux = directoryAux;
    }

    @ExceptionHandler( NoSuchResourceException.class )
    public ResponseEntity<Void> handleNoSuchResourceException(
            NoSuchResourceException ex,
            HttpServletResponse response )
            throws IOException
    {
        logger.debug("handling exception for: " + ex.getMessage());
        response.setStatus( HttpStatus.NOT_FOUND.value() );
        response.sendError( HttpStatus.NOT_FOUND.value() );
        return new ResponseEntity<Void>( null, setHeaders(ex.getMessage(), null, null, null), HttpStatus.NOT_FOUND );
    }

    @ExceptionHandler( UnauthorizedException.class )
    public ResponseEntity<Void> handleUnAuthorizedException(
            UnauthorizedException ex,
            HttpServletResponse response )
            throws IOException
    {
        logger.debug( "handling exception for: " + ex.getMessage() );
        response.setStatus( HttpStatus.UNAUTHORIZED.value() );
        response.sendError(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<Void>( null, setHeaders(ex.getMessage(), null, null, null), HttpStatus.UNAUTHORIZED );
    }


    /**
     * Sets the entity manager factory.
     * @param emf the factory to set.
     */
    @PersistenceUnit
    public void setEmf(final EntityManagerFactory emf) {
        this.emf = emf;
    }
}
