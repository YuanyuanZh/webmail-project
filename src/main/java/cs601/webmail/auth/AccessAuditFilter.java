package cs601.webmail.auth;

import cs601.webmail.util.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuanyuan on 11/25/14.
 */
public class AccessAuditFilter implements Filter {

    private static final Logger LOGGER = Logger.getAccessLogger();

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss,SSS Z");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String method = req.getMethod();
        String uri = req.getRequestURI();
        String remote = req.getRemoteHost();
        String protocol = req.getProtocol();
        String ua = req.getHeader("User-Agent");
        String date = SDF.format(new Date());

        try {
            chain.doFilter(request, response);
        } finally {

            // IP - [TIME] "METHOD URI PROTOCOL" statusCode "User-Agent"
            String log = String.format("%s - [%s] \"%s %s %s\" %3d \"%s\"",
                    remote,
                    date,
                    method,
                    uri,
                    protocol,
                    resp.getStatus(),
                    ua);

            LOGGER.log(log);
        }
    }

    @Override
    public void destroy() {
        // no-op
    }
}

