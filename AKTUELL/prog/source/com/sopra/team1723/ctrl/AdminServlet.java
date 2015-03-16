package com.sopra.team1723.ctrl;

import javax.servlet.http.*;
import com.sopra.team1723.data.*;

/**
 * Steuert Administrator-Funktionalitäten
 */
public class AdminServlet extends ServletController {

    /**
     * Steuert Administrator-Funktionalitäten
     */
    public AdminServlet() {
    }

    /**
     * Liest aus der Request die BenutzerID/Emailadresse und die neue
     * Rolle aus. Anschlieend wird mit dem Datenbankmanager die Rolle
     * in der Datenbank geupdated. Der betroene Benutzer hat nun neue
     * Rechte. Gibt "trueuruck, wenn bei diesem Ablauf keine Fehler
     * auftreten, und "false"bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean adminRolleAendern(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

}