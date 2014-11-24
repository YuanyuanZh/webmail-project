package cs601.webmail.frameworks.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class AddressParser {

    private static final String specialsNoDotNoAt = "()<>,;:\\\"[]";
    private static final String specialsNoDot = specialsNoDotNoAt + "@";
    private static final boolean ignoreBogusGroupName = true;

    /*
     * RFC822 Address parser.
     *
     * XXX - This is complex enough that it ought to be a real parser,
     *       not this ad-hoc mess, and because of that, this is not perfect.
     *
     * XXX - Deal with encoded Headers too.
     */
    static Address[] parse(String s, boolean strict,
                                   boolean parseHdr) throws AddressException {
        int start, end, index, nesting;
        int start_personal = -1, end_personal = -1;
        int length = s.length();
        boolean ignoreErrors = parseHdr && !strict;
        boolean in_group = false;	// we're processing a group term
        boolean route_addr = false;	// address came from route-addr term
        boolean rfc822 = false;		// looks like an RFC822 address
        char c;
        List v = new ArrayList();
        Address ma;

        for (start = end = -1, index = 0; index < length; index++) {
            c = s.charAt(index);

            switch (c) {
                case '(': // We are parsing a Comment. Ignore everything inside.
                    // XXX - comment fields should be parsed as whitespace,
                    //	 more than one allowed per address
                    rfc822 = true;
                    if (start >= 0 && end == -1)
                        end = index;
                    int pindex = index;
                    for (index++, nesting = 1; index < length && nesting > 0;
                         index++) {
                        c = s.charAt(index);
                        switch (c) {
                            case '\\':
                                index++; // skip both '\' and the escaped char
                                break;
                            case '(':
                                nesting++;
                                break;
                            case ')':
                                nesting--;
                                break;
                            default:
                                break;
                        }
                    }
                    if (nesting > 0) {
                        if (!ignoreErrors)
                            throw new AddressException("Missing ')'", s, index);
                        // pretend the first paren was a regular character and
                        // continue parsing after it
                        index = pindex + 1;
                        break;
                    }
                    index--;	// point to closing paren
                    if (start_personal == -1)
                        start_personal = pindex + 1;
                    if (end_personal == -1)
                        end_personal = index;
                    break;

                case ')':
                    if (!ignoreErrors)
                        throw new AddressException("Missing '('", s, index);
                    // pretend the left paren was a regular character and
                    // continue parsing
                    if (start == -1)
                        start = index;
                    break;

                case '<':
                    rfc822 = true;
                    if (route_addr) {
                        if (!ignoreErrors)
                            throw new AddressException(
                                    "Extra route-addr", s, index);

                        // assume missing comma between addresses
                        if (start == -1) {
                            route_addr = false;
                            rfc822 = false;
                            start = end = -1;
                            break;	// nope, nothing there
                        }
                        if (!in_group) {
                            // got a token, add this to our InternetAddress vector
                            if (end == -1)	// should never happen
                                end = index;
                            String addr = s.substring(start, end).trim();

                            ma = new Address();
                            ma.setAddress(addr);
                            if (start_personal >= 0) {
                                ma.encodedPersonal = unquote(
                                        s.substring(start_personal, end_personal).
                                                trim());
                            }
                            v.add(ma);

                            route_addr = false;
                            rfc822 = false;
                            start = end = -1;
                            start_personal = end_personal = -1;
                            // continue processing this new address...
                        }
                    }

                    int rindex = index;
                    boolean inquote = false;
                    outf:
                    for (index++; index < length; index++) {
                        c = s.charAt(index);
                        switch (c) {
                            case '\\':	// XXX - is this needed?
                                index++; // skip both '\' and the escaped char
                                break;
                            case '"':
                                inquote = !inquote;
                                break;
                            case '>':
                                if (inquote)
                                    continue;
                                break outf; // out of for loop
                            default:
                                break;
                        }
                    }

                    // did we find a matching quote?
                    if (inquote) {
                        if (!ignoreErrors)
                            throw new AddressException("Missing '\"'", s, index);
                        // didn't find matching quote, try again ignoring quotes
                        // (e.g., ``<"@foo.com>'')
                        outq:
                        for (index = rindex + 1; index < length; index++) {
                            c = s.charAt(index);
                            if (c == '\\')	// XXX - is this needed?
                                index++;	// skip both '\' and the escaped char
                            else if (c == '>')
                                break;
                        }
                    }

                    // did we find a terminating '>'?
                    if (index >= length) {
                        if (!ignoreErrors)
                            throw new AddressException("Missing '>'", s, index);
                        // pretend the "<" was a regular character and
                        // continue parsing after it (e.g., ``<@foo.com'')
                        index = rindex + 1;
                        if (start == -1)
                            start = rindex;	// back up to include "<"
                        break;
                    }

                    if (!in_group) {
                        start_personal = start;
                        if (start_personal >= 0)
                            end_personal = rindex;
                        start = rindex + 1;
                    }
                    route_addr = true;
                    end = index;
                    break;

                case '>':
                    if (!ignoreErrors)
                        throw new AddressException("Missing '<'", s, index);
                    // pretend the ">" was a regular character and
                    // continue parsing (e.g., ``>@foo.com'')
                    if (start == -1)
                        start = index;
                    break;

                case '"':	// parse quoted string
                    int qindex = index;
                    rfc822 = true;
                    if (start == -1)
                        start = index;
                    outq:
                    for (index++; index < length; index++) {
                        c = s.charAt(index);
                        switch (c) {
                            case '\\':
                                index++; // skip both '\' and the escaped char
                                break;
                            case '"':
                                break outq; // out of for loop
                            default:
                                break;
                        }
                    }
                    if (index >= length) {
                        if (!ignoreErrors)
                            throw new AddressException("Missing '\"'", s, index);
                        // pretend the quote was a regular character and
                        // continue parsing after it (e.g., ``"@foo.com'')
                        index = qindex + 1;
                    }
                    break;

                case '[':	// a domain-literal, probably
                    rfc822 = true;
                    int lindex = index;
                    outb:
                    for (index++; index < length; index++) {
                        c = s.charAt(index);
                        switch (c) {
                            case '\\':
                                index++; // skip both '\' and the escaped char
                                break;
                            case ']':
                                break outb; // out of for loop
                            default:
                                break;
                        }
                    }
                    if (index >= length) {
                        if (!ignoreErrors)
                            throw new AddressException("Missing ']'", s, index);
                        // pretend the "[" was a regular character and
                        // continue parsing after it (e.g., ``[@foo.com'')
                        index = lindex + 1;
                    }
                    break;

                case ';':
                    if (start == -1) {
                        route_addr = false;
                        rfc822 = false;
                        start = end = -1;
                        break;	// nope, nothing there
                    }
                    if (in_group) {
                        in_group = false;
                        /*
                         * If parsing headers, but not strictly, peek ahead.
                         * If next char is "@", treat the group name
                         * like the local part of the address, e.g.,
                         * "Undisclosed-Recipient:;@java.sun.com".
                         */
                        if (parseHdr && !strict &&
                                index + 1 < length && s.charAt(index + 1) == '@')
                            break;
                        ma = new Address();
                        end = index + 1;
                        ma.setAddress(s.substring(start, end).trim());
                        v.add(ma);

                        route_addr = false;
                        rfc822 = false;
                        start = end = -1;
                        start_personal = end_personal = -1;
                        break;
                    }
                    if (!ignoreErrors)
                        throw new AddressException(
                                "Illegal semicolon, not in group", s, index);

                    // otherwise, parsing a header; treat semicolon like comma
                    // fall through to comma case...

                case ',':	// end of an address, probably
                    if (start == -1) {
                        route_addr = false;
                        rfc822 = false;
                        start = end = -1;
                        break;	// nope, nothing there
                    }
                    if (in_group) {
                        route_addr = false;
                        break;
                    }
                    // got a token, add this to our InternetAddress vector
                    if (end == -1)
                        end = index;

                    String addr = s.substring(start, end).trim();
                    String pers = null;
                    if (rfc822 && start_personal >= 0) {
                        pers = unquote(
                                s.substring(start_personal, end_personal).trim());
                        if (pers.trim().length() == 0)
                            pers = null;
                    }

                    /*
                     * If the personal name field has an "@" and the address
                     * field does not, assume they were reversed, e.g.,
                     * ``"joe doe" (john.doe@example.com)''.
                     */
                    if (parseHdr && !strict && pers != null &&
                            pers.indexOf('@') >= 0 &&
                            addr.indexOf('@') < 0 && addr.indexOf('!') < 0) {
                        String tmp = addr;
                        addr = pers;
                        pers = tmp;
                    }
                    if (rfc822 || strict || parseHdr) {
                        if (!ignoreErrors)
                            checkAddress(addr, route_addr, false);
                        ma = new Address();
                        ma.setAddress(addr);
                        if (pers != null)
                            ma.encodedPersonal = pers;
                        v.add(ma);
                    } else {
                        // maybe we passed over more than one space-separated addr
                        StringTokenizer st = new StringTokenizer(addr);
                        while (st.hasMoreTokens()) {
                            String a = st.nextToken();
                            checkAddress(a, false, false);
                            ma = new Address();
                            ma.setAddress(a);
                            v.add(ma);
                        }
                    }

                    route_addr = false;
                    rfc822 = false;
                    start = end = -1;
                    start_personal = end_personal = -1;
                    break;

                case ':':
                    rfc822 = true;
                    if (in_group)
                        if (!ignoreErrors)
                            throw new AddressException("Nested group", s, index);
                    if (start == -1)
                        start = index;
                    if (parseHdr && !strict) {
                        /*
                         * If next char is a special character that can't occur at
                         * the start of a valid address, treat the group name
                         * as the entire address, e.g., "Date:, Tue", "Re:@foo".
                         */
                        if (index + 1 < length) {
                            String addressSpecials = ")>[]:@\\,.";
                            char nc = s.charAt(index + 1);
                            if (addressSpecials.indexOf(nc) >= 0) {
                                if (nc != '@')
                                    break;	// don't change in_group
                                /*
                                 * Handle a common error:
                                 * ``Undisclosed-Recipient:@example.com;''
                                 *
                                 * Scan ahead.  If we find a semicolon before
                                 * one of these other special characters,
                                 * consider it to be a group after all.
                                 */
                                for (int i = index + 2; i < length; i++) {
                                    nc = s.charAt(i);
                                    if (nc == ';')
                                        break;
                                    if (addressSpecials.indexOf(nc) >= 0)
                                        break;
                                }
                                if (nc == ';')
                                    break;	// don't change in_group
                            }
                        }

                        // ignore bogus "mailto:" prefix in front of an address,
                        // or bogus mail header name included in the address field
                        String gname = s.substring(start, index);
                        if (ignoreBogusGroupName &&
                                (gname.equalsIgnoreCase("mailto") ||
                                        gname.equalsIgnoreCase("From") ||
                                        gname.equalsIgnoreCase("To") ||
                                        gname.equalsIgnoreCase("Cc") ||
                                        gname.equalsIgnoreCase("Subject") ||
                                        gname.equalsIgnoreCase("Re")))
                            start = -1;	// we're not really in a group
                        else
                            in_group = true;
                    } else
                        in_group = true;
                    break;

                // Ignore whitespace
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    break;

                default:
                    if (start == -1)
                        start = index;
                    break;
            }
        }

        if (start >= 0) {
	    /*
	     * The last token, add this to our InternetAddress vector.
	     * Note that this block of code should be identical to the
	     * block above for "case ','".
	     */
            if (end == -1)
                end = length;

            String addr = s.substring(start, end).trim();
            String pers = null;
            if (rfc822 && start_personal >= 0) {
                pers = unquote(
                        s.substring(start_personal, end_personal).trim());
                if (pers.trim().length() == 0)
                    pers = null;
            }

	    /*
	     * If the personal name field has an "@" and the address
	     * field does not, assume they were reversed, e.g.,
	     * ``"joe doe" (john.doe@example.com)''.
	     */
            if (parseHdr && !strict &&
                    pers != null && pers.indexOf('@') >= 0 &&
                    addr.indexOf('@') < 0 && addr.indexOf('!') < 0) {
                String tmp = addr;
                addr = pers;
                pers = tmp;
            }
            if (rfc822 || strict || parseHdr) {
                if (!ignoreErrors)
                    checkAddress(addr, route_addr, false);
                ma = new Address();
                ma.setAddress(addr);
                if (pers != null)
                    ma.encodedPersonal = pers;
                v.add(ma);
            } else {
                // maybe we passed over more than one space-separated addr
                StringTokenizer st = new StringTokenizer(addr);
                while (st.hasMoreTokens()) {
                    String a = st.nextToken();
                    checkAddress(a, false, false);
                    ma = new Address();
                    ma.setAddress(a);
                    v.add(ma);
                }
            }
        }

        Address[] a = new Address[v.size()];
        v.toArray(a);
        return a;
    }


    /**
     * Check that the address is a valid "mailbox" per RFC822.
     * (We also allow simple names.)
     *
     * XXX - much more to check
     * XXX - doesn't handle domain-literals properly (but no one uses them)
     */
    private static void checkAddress(String addr,
                                     boolean routeAddr, boolean validate)
            throws AddressException {
        int i, start = 0;

        int len = addr.length();
        if (len == 0)
            throw new AddressException("Empty address", addr);

	/*
	 * routeAddr indicates that the address is allowed
	 * to have an RFC 822 "route".
	 */
        if (routeAddr && addr.charAt(0) == '@') {
	    /*
	     * Check for a legal "route-addr":
	     *		[@domain[,@domain ...]:]local@domain
	     */
            for (start = 0; (i = indexOfAny(addr, ",:", start)) >= 0;
                 start = i+1) {
                if (addr.charAt(start) != '@')
                    throw new AddressException("Illegal route-addr", addr);
                if (addr.charAt(i) == ':') {
                    // end of route-addr
                    start = i + 1;
                    break;
                }
            }
        }
	/*
	 * The rest should be "local@domain", but we allow simply "local"
	 * unless called from validate.
	 *
	 * local-part must follow RFC 822 - no specials except '.'
	 * unless quoted.
	 */

        char c = (char)-1;
        char lastc = (char)-1;
        boolean inquote = false;
        for (i = start; i < len; i++) {
            lastc = c;
            c = addr.charAt(i);
            // a quoted-pair is only supposed to occur inside a quoted string,
            // but some people use it outside so we're more lenient
            if (c == '\\' || lastc == '\\')
                continue;
            if (c == '"') {
                if (inquote) {
                    // peek ahead, next char must be "@"
                    if (validate && i + 1 < len && addr.charAt(i + 1) != '@')
                        throw new AddressException(
                                "Quote not at end of local address", addr);
                    inquote = false;
                } else {
                    if (validate && i != 0)
                        throw new AddressException(
                                "Quote not at start of local address", addr);
                    inquote = true;
                }
                continue;
            }
            if (inquote)
                continue;
            if (c == '@') {
                if (i == 0)
                    throw new AddressException("Missing local name", addr);
                break;		// done with local part
            }
            if (c <= 040 || c >= 0177)
                throw new AddressException(
                        "Local address contains control or whitespace", addr);
            if (specialsNoDot.indexOf(c) >= 0)
                throw new AddressException(
                        "Local address contains illegal character", addr);
        }
        if (inquote)
            throw new AddressException("Unterminated quote", addr);

	/*
	 * Done with local part, now check domain.
	 *
	 * Note that the MimeMessage class doesn't remember addresses
	 * as separate objects; it writes them out as headers and then
	 * parses the headers when the addresses are requested.
	 * In order to support the case where a "simple" address is used,
	 * but the address also has a personal name and thus looks like
	 * it should be a valid RFC822 address when parsed, we only check
	 * this if we're explicitly called from the validate method.
	 */

        if (c != '@') {
            if (validate)
                throw new AddressException("Missing final '@domain'", addr);
            return;
        }

        // check for illegal chars in the domain, but ignore domain literals

        start = i + 1;
        if (start >= len)
            throw new AddressException("Missing domain", addr);

        if (addr.charAt(start) == '.')
            throw new AddressException("Domain starts with dot", addr);
        for (i = start; i < len; i++) {
            c = addr.charAt(i);
            if (c == '[')
                return;		// domain literal, don't validate
            if (c <= 040 || c >= 0177)
                throw new AddressException(
                        "Domain contains control or whitespace", addr);
            // RFC 2822 rule
            //if (specialsNoDot.indexOf(c) >= 0)
	    /*
	     * RFC 1034 rule is more strict
	     * the full rule is:
	     *
	     * <domain> ::= <subdomain> | " "
	     * <subdomain> ::= <label> | <subdomain> "." <label>
	     * <label> ::= <letter> [ [ <ldh-str> ] <let-dig> ]
	     * <ldh-str> ::= <let-dig-hyp> | <let-dig-hyp> <ldh-str>
	     * <let-dig-hyp> ::= <let-dig> | "-"
	     * <let-dig> ::= <letter> | <digit>
	     */
            if (!(Character.isLetterOrDigit(c) || c == '-' || c == '.'))
                throw new AddressException(
                        "Domain contains illegal character", addr);
            if (c == '.' && lastc == '.')
                throw new AddressException(
                        "Domain contains dot-dot", addr);
            lastc = c;
        }
        if (lastc == '.')
            throw new AddressException("Domain ends with dot", addr);
    }


    private static String unquote(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1);
            // check for any escaped characters
            if (s.indexOf('\\') >= 0) {
                StringBuffer sb = new StringBuffer(s.length());	// approx
                for (int i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    if (c == '\\' && i < s.length() - 1)
                        c = s.charAt(++i);
                    sb.append(c);
                }
                s = sb.toString();
            }
        }
        return s;
    }

    private static int indexOfAny(String s, String any, int start) {
        try {
            int len = s.length();
            for (int i = start; i < len; i++) {
                if (any.indexOf(s.charAt(i)) >= 0)
                    return i;
            }
            return -1;
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        }
    }

}
