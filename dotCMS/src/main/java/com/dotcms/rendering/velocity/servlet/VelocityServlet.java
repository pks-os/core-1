package com.dotcms.rendering.velocity.servlet;

import static com.dotmarketing.filters.TimeMachineFilter.TM_DATE_VAR;

import com.dotcms.business.CloseDB;
import com.dotcms.rendering.velocity.viewtools.RequestWrapper;

import com.dotcms.repackage.com.fasterxml.jackson.databind.ObjectMapper;
import com.dotcms.repackage.com.fasterxml.jackson.databind.ObjectWriter;
import com.dotcms.rest.api.v1.page.PageResource;
import com.dotcms.rest.api.v1.page.PageResourceHelper;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.filters.Constants;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.PageMode;

import java.io.IOException;
import java.net.URLDecoder;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class VelocityServlet extends HttpServlet {


    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    @Override
    @CloseDB
    protected final void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        RequestWrapper request = new RequestWrapper(req);
        final String uri = URLDecoder.decode((request.getAttribute(Constants.CMS_FILTER_URI_OVERRIDE) != null)
                ? (String) request.getAttribute(Constants.CMS_FILTER_URI_OVERRIDE)
                : request.getRequestURI(), "UTF-8");

        if (uri == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "VelocityServlet called without running through the CMS Filter");
            Logger.error(this.getClass(),
                    "You cannot call the VelocityServlet without passing the requested url via a  requestAttribute called  "
                            + Constants.CMS_FILTER_URI_OVERRIDE);
            return;
        }

        final boolean comeFromSomeWhere = request.getHeader("referer") != null;

        if (APILocator.getLoginServiceAPI().isLoggedIn(request) && !comeFromSomeWhere){
            goToEditPage(uri, response);
        } else {
            request.setRequestUri(uri);
            final PageMode mode = PageMode.getWithNavigateMode(request);

            try {
                VelocityModeHandler.modeHandler(mode, request, response).serve();
            } catch (ResourceNotFoundException rnfe) {
                Logger.error(this, "ResourceNotFoundException" + rnfe.toString(), rnfe);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (ParseErrorException pee) {
                Logger.error(this, "Template Parse Exception : " + pee.toString(), pee);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template Parse Exception");
            } catch (MethodInvocationException mie) {
                Logger.error(this, "MethodInvocationException" + mie.toString(), mie);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "MethodInvocationException Error on template");
            } catch (Exception e) {
                Logger.error(this, "Exception" + e.toString(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Exception Error on template");
            }
        }
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        Logger.info(this.getClass(), "Initing VelocityServlet");


    }

    private void goToEditPage(final String requestURI, final HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        final String url = String.format("/dotAdmin/#/edit-page/content?url=%s", requestURI);
        response.sendRedirect(url);
    }

}
