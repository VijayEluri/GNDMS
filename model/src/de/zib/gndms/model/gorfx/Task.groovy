package de.zib.gndms.model.gorfx

import javax.persistence.Embeddable
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Basic
import de.zib.gndms.model.common.TimedGridResource
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import de.zib.gndms.model.gorfx.types.TaskState
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Enumerated
import javax.persistence.EnumType


/**
 * ThingAMagic.
 * 
 * @author Stefan Plantikow<plantikow@zib.de>
 * @version $Id$ 
 *
 * User: stepn Date: 05.09.2008 Time: 13:41:58
 */
@Entity(name="Tasks")
@Table(name="tasks", schema="gorfx")
class Task extends TimedGridResource {
    @ManyToOne @JoinColumn(name="key", nullable=false, updatable=false, columnDefinition="LONG VARCHAR")
    OfferType type
    
    @Column(name="descr", nullable=false, updatable=false, columnDefinition="LONG VARCHAR")
    String description

    /* always this.tod >= this.validity */
    @Embedded
    @AttributeOverrides([
        @AttributeOverride(name="accepted", column=@Column(name="accepted", nullable=false, updatable=false)),
        @AttributeOverride(name="deadline", column=@Column(name="deadline", nullable=true, updatable=false)),
        @AttributeOverride(name="resultValidity", column=@Column(name="validity", nullable=true, updatable=false)),
    ])
    Contract contract

    @Column(name="broken")
    boolean broken = false

    @Enumerated(EnumType.STRING)
    @Column(name="state", nullable=false, updatable=true)
    TaskState state = TaskState.CREATED

    @Column(name="progress", nullable=false, updatable=true)
    float progress = 0.0f

    @Column(name="fault", nullable=true, updatable=true, columnDefinition="LONG VARCHAR")
    @Basic String faultString

    /**
     * Payload depending on state, either task results or a detailed task failure or task arguments 
     **/
    @Column(name="data", nullable=true, updatable=true)
    @Basic Serializable data
}

@Embeddable
class Contract {
    @Temporal(value = TemporalType.TIMESTAMP)
    Calendar accepted

    @Temporal(value = TemporalType.TIMESTAMP)
    Calendar deadline

    @Temporal(value = TemporalType.TIMESTAMP)
    Calendar resultValidity
}

