package de.zib.gndms.model.gorfx.types;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * A time constrain is a selection criteria for data stagin.
 * @author Maik Jorra <jorra@zib.de>
 * @verson \$id$
 * <p/>
 * User: bzcjorra Date: Sep 15, 2008 4:11:00 PM
 */
public class TimeConstraint {  
	
	private DateTime minTime;
    private DateTime maxTime;
    private DateTimeFormatter ISOFormatter;

    public TimeConstraint( ) {
        ISOFormatter = ISODateTimeFormat.dateTime( );
    }

    public boolean hasMinTime() {
        return minTime != null;
    }

    public DateTime getMinTime() {
        return minTime;
    }


    public String getMinTimeString() {
        return ISOFormatter.print( minTime );
    }


    public void setMinTime( String minTime ) {
        this.minTime = ISOFormatter.parseDateTime( minTime );
    }

    
    public void setMinTime( DateTime minTime ) {
        this.minTime = minTime;
    }


    public boolean hasMaxTime() {
        return maxTime != null;
    }


    public DateTime getMaxTime() {
        return maxTime;
    }


    public String getMaxTimeString() {
        return ISOFormatter.print( maxTime );
    }


    public void setMaxTime( String maxTime ) {
        this.maxTime = ISOFormatter.parseDateTime( maxTime );
    }


    public void setMaxTime( DateTime maxTime ) {
        this.maxTime = maxTime;
    }
}