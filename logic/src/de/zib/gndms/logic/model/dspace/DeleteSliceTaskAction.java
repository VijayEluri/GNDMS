package de.zib.gndms.logic.model.dspace;
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

import de.zib.gndms.logic.model.ModelIdHoldingOrder;
import de.zib.gndms.logic.model.ModelTaskAction;
import de.zib.gndms.model.dspace.Slice;
import de.zib.gndms.model.gorfx.types.TaskState;
import de.zib.gndms.neomodel.common.Session;
import de.zib.gndms.neomodel.gorfx.Task;
import org.jetbrains.annotations.NotNull;

/**
 * @author Maik Jorra
 * @email jorra@zib.de
 * @date 20.12.11  11:44
 * @brief
 */
public class DeleteSliceTaskAction extends ModelTaskAction<ModelIdHoldingOrder> {


    public DeleteSliceTaskAction() {

        super( ModelIdHoldingOrder.class );
    }


    @Override
    protected void onCreated( @NotNull final String wid, @NotNull final TaskState state,
                              final boolean isRestartedTask, final boolean altTaskState )
            throws Exception
    {

        if (! isRestartedTask ) {
            final Session session = getDao().beginSession();
            try {
                final Task task = getModel().getTask(session);
                task.setWID(wid);
                task.setMaxProgress( 1 );
                task.setProgress( 0 );
                session.success();
            }
            finally { session.finish(); }
        }
        super.onCreated( wid, state, isRestartedTask, altTaskState );
    }


    @Override
    protected void onInProgress( @NotNull final String wid, @NotNull final TaskState state,
                                 final boolean isRestartedTask, final boolean altTaskState )
            throws Exception
    {
        ensureOrder();

        Slice slice = getModelEntity( Slice.class );

        getDirectoryAux().deleteDirectory( slice.getOwner(),
                slice.getSubspace().getPathForSlice( slice ) );

        deleteModelEntity( Slice.class );

        autoTransitWithPayload( Boolean.TRUE );
    }


}