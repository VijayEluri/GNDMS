package de.zib.gndms.model.common;

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



import javax.persistence.MappedSuperclass;
import javax.persistence.Column;
import javax.persistence.Id;

/**
 *
 * UUID-as-36-char-String identified grid resource
 *
 **/
@MappedSuperclass
public abstract class GridResource extends GridEntity implements GridResourceItf{
    
    private String id;

    @Id @Column(name="id", nullable=false, length=36, columnDefinition="CHAR", updatable=false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
