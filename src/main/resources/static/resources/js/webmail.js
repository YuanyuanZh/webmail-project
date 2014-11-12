var Mail = {};

Mail.errors = {
    '101': 'Username or password not correct.',
    '102': 'Your account was locked. Please contact the administrator.',
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