var Mail = {};

Mail.errors = {
    '101': 'Username or password not correct.',
    '102': 'Your account was locked. Please contact the administrator.',
    '103': 'Fill out the basic information first please.',
    '201': 'passwords doesn\'t match',
    '202': 'username already exist',
    '301': 'username does not exist',
    '302': 'emailAddress or password invalid'
};

/**
 * Parse a query string to a hashed object.
 *
 * For example, query string in location is
 *      <code>?error=101&msg=Incorrect-Username' </code>
 * Will got a object like
 *      <code> { "error" : 101, "msg" : "Incorrect-Username" } </code>
 *
 * @param query Query string from location.
 * @returns {Object}
 */
Mail.parseQuery = function(query) {
    var params = {};

    if (query && query.length > 1) {

        query = query.substr(1);

        var pairs = query.split("&");

        if (pairs.length) {
            for (var i = 0, len = pairs.length; i < len; i++) {
                var kv = pairs[i].split("=");
                params[kv[0]] = kv[1];
            }
        }
    }
    return params;
};


// http://notifyjs.com/
var Noty = {
    error : function(msg) {
        $.notify(msg, "error")
    },
    info: function(msg) {
        $.notify(msg, "info")
    },
    success: function(msg) {
        $.notify(msg, "success")
    }
};

var Pagination = function() {
    this._page = 1;
    this._pages = 1;
    this._prev = 1;
    this._next = 1;
}

/**
 * @param page (int) current page number.
 * @param pageSize (int) Numbers of record each page.
 * @param total (int) Total count of records.
 */
Pagination.prototype.update = function(page, pageSize, total) {
    if (total == 0) {
        $('.page-indicator').text('0 of 0');
        return;
    }

    if (total <= pageSize) {
        this._page = 1;
    } else {
        this._page = page;
        var pages = Math.ceil(total / pageSize);  // ceil(5 / 2) = 3
        this._pages = pages;

        this._prev = (page <= 1) ? 1 : (page - 1);
        this._next = page >= pages ? pages : (page + 1);
    }

    var startPos = (page - 1) * pageSize + 1;
    var endPos = Math.min(page * pageSize, total);

    // update page indicator
    $('.page-indicator').text(startPos + "-" + endPos + " of " + total);
}

Pagination.prototype.next = function() {
    return this._next;
}

Pagination.prototype.prev = function() {
    return this._prev;
}

Pagination.prototype.page = function() {
    return this._page;
}

Pagination.prototype.hasPrev = function() {
    return this._page > 1;
}

Pagination.prototype.hasNext = function() {
    return this._page < this._pages;
}

Pagination.prototype.reset = function() {
    this._page = 1;
    this._pages = 1;
    this._prev = 1;
    this._next = 1;
}