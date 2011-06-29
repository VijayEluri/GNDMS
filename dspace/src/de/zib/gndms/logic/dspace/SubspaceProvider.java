package de.zib.gndms.logic.dspace;
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

import java.util.List;

import de.zib.gndms.model.dspace.Subspace;

/**
 * Provides a mapping of subspace ids and subspaces.
 * 
 * @author Ulrike Golas
 */

// TODO implementation

public interface SubspaceProvider {
    
	/**
	 * Checks whether a given subspace id exists.
	 * @param subspace The subspace id.
	 * @return true, if this subspace exists, otherwise false.
	 */
	boolean exists(String subspace);

	/**
	 * Returns a list containing all existing subspace ids.
	 * @return The list.
	 */
	List<String> listSubspaces();

    /**
     * Returns the subspace for a given subspace id.
     * @param sliceKind The requested subspace id.
     * @return The corresponding subspace.
     */
	Subspace getSubspace(String subspace);
}
