package de.zib.gndms.common.dspace.service;

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

import de.zib.gndms.common.dspace.SliceKindConfiguration;
import de.zib.gndms.common.logic.config.Configuration;
import de.zib.gndms.common.rest.Facets;
import de.zib.gndms.common.rest.Specifier;
import org.springframework.http.ResponseEntity;

/**
 * The interface of the SliceKind service.
 * 
 * Clients and resources which should provide the SliceKind service should
 * implement this interface.
 * 
 * Some general remarks on the parameters:
 * <ul>
 * <li>The <b>dn</b> parameter is a identifier for the user responsible for the
 * method invocation. It is required to check if the user has the necessary
 * permissions for the call. <br>
 * Using REST the attribute should be provided using a http-header property
 * called "DN".
 * </ul>
 * 
 * @author Ulrike Golas
 */
public interface SliceKindService {

	/**
	 * Lists the slice kind representation.
	 * 
	 * @param subspace
	 *            The subspace identifier.
	 * @param sliceKind
	 *            The slice kind identifier.
	 * @param dn
	 *            The dn of the user invoking the method.
	 * @return The representation of the slice kind.
	 */
	ResponseEntity<Configuration> getSliceKindInfo(String subspace,
			String sliceKind, String dn);


    /**
     * Same as the setter just the other way 'round.
     *
     * @param subspace What the name intends.
     * @param sliceKind What the name intends.
     * @param dn What the name ... ok it's the distinguished name.
     * @return The current config.
     */
    ResponseEntity< SliceKindConfiguration > getSliceKindConfig( String subspace, String sliceKind, String dn );

	/**
	 * Sets a slice kind configuration.
	 * 
	 * @param subspace
	 *            The subspace identifier.
	 * @param sliceKind
	 *            The slice kind identifier.
	 * @param config The configuration of the slice kind.
	 * @param dn
	 *            The dn of the user invoking the method.
	 * @return A confirmation.
	 */
	ResponseEntity< Integer > setSliceKindConfig(String subspace, String sliceKind,
			Configuration config, String dn);

	/**
	 * Deletes a slice kind.
	 * 
	 * @param subspace
	 *            The subspace identifier.
	 * @param sliceKind
	 *            The slice kind identifier.
	 * @param dn
	 *            The dn of the user invoking the method.
	 * @return A confirmation.
	 */
	ResponseEntity< Specifier< Facets > > deleteSliceKind(String subspace, String sliceKind,
			String dn);
}
