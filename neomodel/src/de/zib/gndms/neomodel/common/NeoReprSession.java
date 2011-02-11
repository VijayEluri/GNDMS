package de.zib.gndms.neomodel.common;

import de.zib.gndms.neomodel.common.NeoSession;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: stepn
 * Date: 04.02.11
 * Time: 18:28
 * To change this template use File | Settings | File Templates.
 */
public class NeoReprSession {
    private final @NotNull NeoSession session;
    private final @NotNull NeoDao dao;

    NeoReprSession(@NotNull NeoDao dao, @NotNull NeoSession session) {
        this.dao     = dao;
        this.session = session;
    }

    @NotNull public NeoSession getSession() {
        return session;
    }

    @NotNull public NeoDao getDao() {
        return dao;
    }
}


