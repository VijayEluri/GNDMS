package de.zib.gndms.taskflows.filetransfer.server;

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



import de.zib.gndms.common.model.gorfx.types.Quote;
import de.zib.gndms.kit.config.MandatoryOptionMissingException;
import de.zib.gndms.logic.model.TaskAction;
import de.zib.gndms.logic.model.gorfx.taskflow.DefaultTaskFlowFactory;
import de.zib.gndms.neomodel.gorfx.TaskFlow;
import de.zib.gndms.taskflows.filetransfer.client.FileTransferMeta;
import de.zib.gndms.taskflows.filetransfer.client.model.FileTransferOrder;
import de.zib.gndms.taskflows.filetransfer.server.logic.FileTransferQuoteCalculator;
import de.zib.gndms.taskflows.filetransfer.server.logic.FileTransferTaskAction;

import java.util.HashMap;
import java.util.Map;


/**
 * ThingAMagic.
 *
 * @author  try ste fan pla nti kow zib
 * @version $Id$
 *
 *          User: stepn Date: 09.10.2008 Time: 12:49:54
 */
public class FileTransferTaskFlowFactory
    extends DefaultTaskFlowFactory<FileTransferOrder, FileTransferQuoteCalculator> {


    public FileTransferTaskFlowFactory( ) {

        super( FileTransferMeta.FILE_TRANSFER_TYPE_KEY, FileTransferQuoteCalculator.class,
                FileTransferOrder.class );
    }


    @Override
    public FileTransferQuoteCalculator getQuoteCalculator() throws MandatoryOptionMissingException {
        final FileTransferQuoteCalculator quoteCalculator = super.getQuoteCalculator();
        injectMembers( quoteCalculator );
        return quoteCalculator;
    }


    @Override
    protected TaskFlow<FileTransferOrder> prepare( TaskFlow<FileTransferOrder>
                                                               fileTransferOrderTaskFlow ) {
        return fileTransferOrderTaskFlow;
    }


    @Override
    protected Map<String, String> getDefaultConfig() {

        final HashMap<String, String> config = new HashMap<String, String>( 1 );
        // todo add default values for gridftp-client factory
        return config;
    }


    @Override
    public TaskAction createAction( final Quote quote ) {
        FileTransferTaskAction action = new FileTransferTaskAction();
        getInjector().injectMembers( action );
        return action;
    }
}
