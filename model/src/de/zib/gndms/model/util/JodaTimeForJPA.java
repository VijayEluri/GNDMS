package de.zib.gndms.model.util;
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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;

/**
 * @author Maik Jorra
 * @email jorra@zib.de
 * @date 26.01.12  11:50
 * @brief
 *
 */
@SuppressWarnings( "UnusedDeclaration" )
public final class JodaTimeForJPA {

    private static final DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTime();

    //public static String fromDateTime( final DateTime dateTime ) {
    //    return FORMATTER.print( dateTime );
    //}

    public static Calendar fromDateTime( final DateTime dateTime ) {
        return dateTime.toGregorianCalendar();
    }

    public static DateTime toDateTime( final Calendar dateTime ) {
        return new DateTime( dateTime );
    }


    // do not instantiate
    private JodaTimeForJPA() {}
}
