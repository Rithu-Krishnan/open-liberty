/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package oidc.client.base.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import jakarta.json.JsonObject;
import jakarta.security.enterprise.identitystore.openid.AccessToken;
import jakarta.security.enterprise.identitystore.openid.IdentityToken;
import jakarta.security.enterprise.identitystore.openid.OpenIdClaims;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.security.enterprise.identitystore.openid.RefreshToken;
import jakarta.servlet.ServletOutputStream;

public class OpenIdContextLogger {

    protected String caller = null;
    protected OpenIdContext context = null;

    public OpenIdContextLogger(String callingClass, OpenIdContext openidContext) {

        caller = callingClass;
        context = openidContext;

    }

    public void logContext(ServletOutputStream ps) throws IOException {

        printLine(ps, caller, "Recording the content of the OpenIdContext");

        if (context == null) {
            printLine(ps, caller, "OpenIdContext: null");
            return;

        }

        logSubject(ps);

        logAccessTokenClaims(ps);

        logIdTokenClaims(ps);

        logRefreshTokenClaims(ps);

        logClaims(ps);

        logJsonClaims(ps);

        logExpiresIn(ps);

        printLine(ps, caller, "Token Type: " + context.getTokenType());

        // TODO compare atClaims to jsonClaims - should they be the same?

        // TODO what can we validate with getStoredValue???
        //context.getStoredValue(null, null, claimsSub);
    }

    protected void logSubject(ServletOutputStream ps) throws IOException {

        String claimsSub = null;
        String contextSub = context.getSubject();
        printLine(ps, caller, "OpenIdContext subject: " + contextSub);

        OpenIdClaims claims = context.getClaims();
        if (claims != null) {
            claimsSub = claims.getSubject();
            printLine(ps, caller, "Claims Subject: " + claimsSub);
        } else {
            printLine(ps, caller, "OpenIdContext subjects do NOT match since there are no claims");
        }

        // compare context subject to cliams subject???
        if (contextSub == null && claimsSub == null) {
            printLine(ps, caller, "OpenIdContext subjects are null");
        } else {
            if (claimsSub == null) {
                printLine(ps, caller, "OpenIdContext subjects do NOT match: claimsSub is null and does not match the contextSub:" + contextSub);
            } else {
                if (claimsSub.equals(contextSub)) {
                    printLine(ps, caller, "OpenIdContext subjects match");
                } else {
                    printLine(ps, caller, "OpenIdContext subjects do NOT match: claimsSub: " + claimsSub + " does not match contextSub: " + contextSub);
                }
            }
        }

    }

    protected void logAccessTokenClaims(ServletOutputStream ps) throws IOException {

        AccessToken accessToken = context.getAccessToken();
        if (accessToken != null) {
            Map<String, Object> atClaims = accessToken.getClaims();
            printLine(ps, caller, "Access Token: " + accessToken.toString());
            for (Map.Entry<String, Object> entry : atClaims.entrySet()) {
                printLine(ps, caller, "Access Token: Claim: Key: " + entry.getKey() + " Value: " + entry.getValue());
            }
        } else {
            printLine(ps, caller, "Access Token: null");
        }
    }

    protected void logIdTokenClaims(ServletOutputStream ps) throws IOException {

        IdentityToken idToken = context.getIdentityToken();
        if (idToken != null) {
            Map<String, Object> idClaims = idToken.getClaims();
            printLine(ps, caller, "Identity Token: " + idToken.toString());
            for (Map.Entry<String, Object> entry : idClaims.entrySet()) {
                printLine(ps, caller, "Identity Token: Claim: Key: " + entry.getKey() + " Value: " + entry.getValue());
            }
        } else {
            printLine(ps, caller, "Identity Token: null");
        }
    }

    protected void logRefreshTokenClaims(ServletOutputStream ps) throws IOException {

        Optional<RefreshToken> refreshToken = context.getRefreshToken();
        if (refreshToken != null) {
            RefreshToken tokenContent = refreshToken.get();
            printLine(ps, caller, "Refresh Token (raw): " + tokenContent.toString());
            printLine(ps, caller, "Refresh Token: " + tokenContent.getToken());
        } else {
            printLine(ps, caller, "Refresh Token: null");
        }
    }

    protected void logJsonClaims(ServletOutputStream ps) throws IOException {

        JsonObject claimsJson = context.getClaimsJson();
        if (claimsJson != null) {
            printLine(ps, caller, "Json Claims: " + claimsJson.toString());
            // TODO update once I can see what the data really looks like
            //for (String key : claims.get)
        } else {
            printLine(ps, caller, "Json Claims: null");
        }
    }

    protected void logExpiresIn(ServletOutputStream ps) throws IOException {

        Optional<Long> expiresIn = context.getExpiresIn();
        if (expiresIn != null) {
            printLine(ps, caller, "Expires In: " + expiresIn.toString());
            // TODO update once we see what're we're getting and what else we could to with the value.
        } else {
            printLine(ps, caller, "Expires In: null");
        }

    }

    protected void logClaims(ServletOutputStream ps) throws IOException {

        // TODO - do something with this
        OpenIdClaims claims = context.getClaims();
        if (claims == null) {
            printLine(ps, caller, "Claims are null");
        }
        // TODO - do something with the claims

    }

    protected void printLine(ServletOutputStream ps, String caller, String msg) throws IOException {

        printLine(ps, caller + ": " + msg);

    }

    public void printLine(ServletOutputStream ps, String msg) throws IOException {

        System.out.println(msg);
        ps.println(msg);

    }
}