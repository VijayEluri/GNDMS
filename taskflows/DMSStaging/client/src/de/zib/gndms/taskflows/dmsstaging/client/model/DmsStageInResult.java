package de.zib.gndms.taskflows.dmsstaging.client.model;

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


import de.zib.gndms.common.rest.Specifier;
import de.zib.gndms.common.model.gorfx.types.SliceResultImpl;

/**
 * @author  try ma ik jo rr a zib
 * @version  $Id$
 * <p/>
 * User: mjorra, Date: 14.11.2008, Time: 15:32:37
 */
public class DmsStageInResult extends SliceResultImpl {

    private static final long serialVersionUID = 1563758463267173946L;


    public DmsStageInResult( ) {

        super( DmsStageInMeta.DMS_STAGE_IN_KEY );
    }


    public DmsStageInResult( final Specifier<Void> sliceSpecifier ) {

        super( DmsStageInMeta.DMS_STAGE_IN_KEY, sliceSpecifier );
    }
}
