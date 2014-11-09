package cs601.webmail.frameworks.web;

import cs601.webmail.frameworks.web.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yuanyuan on 11/3/14.
 */
class DefaultRequestContext extends RequestContext {

    DefaultRequestContext(HttpServletRequest _request, HttpServletResponse _response) {
        this._request = _request;
        this._response = _response;
    }

    private HttpServletRequest _request;

    private HttpServletResponse _response;

    @Override
    public void init() {
    }

    @Override
    public void release() {
    }

    @Override
    public HttpServletRequest getRequest() {
        return _request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return _response;
    }

}
