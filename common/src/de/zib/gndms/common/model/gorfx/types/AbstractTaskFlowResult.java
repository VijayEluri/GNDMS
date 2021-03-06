package de.zib.gndms.common.model.gorfx.types;

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



import java.io.Serializable;

/**
 * @author  try ma ik jo rr a zib
 * @version  $Id$
 * <p/>
 * User: mjorra, Date: 13.10.2008, Time: 12:35:03
 */
public abstract class AbstractTaskFlowResult<R> implements TaskResult<R> {

    private String taskFlowType;
    
    private static final long serialVersionUID = 8410587166706272881L;

    
    protected AbstractTaskFlowResult() {
    }


    protected AbstractTaskFlowResult( String taskFlowType ) {
        this.taskFlowType = taskFlowType;
    }


    public String getTaskFlowType() {
        return taskFlowType;
    }


    protected void setTaskFlowType( String uri ) {
        this.taskFlowType = uri;
    }

 }
