package com.jsonengine.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jsonengine.common.JEUtils;
import com.jsonengine.query.QueryFilter;
import com.jsonengine.query.QueryRequest;
import com.jsonengine.query.QueryService;

/**
 * Provides REST API for jsonengine query operations.
 * 
 * @author @kazunori_279
 */
public class QueryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String PARAM_COND = "cond";

    public static final String PARAM_SORT = "sort";

    public static final String PARAM_LIMIT = "limit";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // add QueryFilters for "cond" parameter
        final QueryRequest qReq = createQueryRequest(req);
        final String[] conds = req.getParameterValues(PARAM_COND);
        if (conds != null) {
            for (String cond : conds) {
                final String[] tokens = cond.split("\\.");
                final String propName = tokens[0];
                final QueryFilter.Comparator cp =
                    QueryFilter.parseComparator(tokens[1]);
                final String propValue = tokens[2];
                final QueryFilter condFilter =
                    new QueryFilter.CondFilter(
                        qReq.getDocType(),
                        propName,
                        cp,
                        propValue);
                qReq.addQueryFilter(condFilter);
            }
        }

        // add QueryFilters for "sort"
        final String sortParam = req.getParameter(PARAM_SORT);
        if (sortParam != null) {
            final String[] sortTokens = sortParam.split("\\.");
            final String propName = sortTokens[0];
            final QueryFilter.SortOrder so =
                QueryFilter.parseSortOrder(sortTokens[1]);
            final QueryFilter sortFilter =
                new QueryFilter.SortFilter(qReq.getDocType(), propName, so);
            qReq.addQueryFilter(sortFilter);
        }

        // add QueryFilters for "limit"
        final String limitParam = req.getParameter(PARAM_LIMIT);
        if (limitParam != null) {
            final int limit = Integer.parseInt(limitParam);
            final QueryFilter limitFilter =
                new QueryFilter.LimitFilter(qReq.getDocType(), limit);
            qReq.addQueryFilter(limitFilter);
        }

        // execute query
        final String resultJson = QueryService.i.query(qReq);

        // return the result
        resp.setContentType(CRUDServlet.RESP_CONTENT_TYPE);
        final PrintWriter pw = resp.getWriter();
        pw.append(resultJson);
        pw.close();
    }

    private QueryRequest createQueryRequest(HttpServletRequest req)
            throws UnsupportedEncodingException {

        // set charset for reading parameters
        req.setCharacterEncoding(CRUDServlet.CHARSET);

        // parse URI and put docType and docId into jeReq
        final QueryRequest qReq = new QueryRequest();
        final String[] tokens = req.getRequestURI().split("/");
        if (tokens.length < 3) {
            throw new IllegalArgumentException("No docType found");
        }
        if (tokens.length >= 3) {
            qReq.setDocType(tokens[2]);
        }

        // set Google account info, timestamp, and checkConflict flag
        if (req.getUserPrincipal() != null) {
            qReq.setRequestedBy(req.getUserPrincipal().getName());
        }
        qReq.setRequestedAt(JEUtils.i.getGlobalTimestamp());
        return qReq;
    }
}
