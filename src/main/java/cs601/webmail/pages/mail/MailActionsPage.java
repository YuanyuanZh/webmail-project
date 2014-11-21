package cs601.webmail.pages.mail;

import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.Page;
import cs601.webmail.service.MailService;
import cs601.webmail.service.impl.MailServiceImpl;
import cs601.webmail.util.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yuanyuan on 11/15/14.
 */
public class MailActionsPage extends Page {

    @Override
    public void body() throws Exception {

        RequestContext context = RequestContext.getCurrentInstance();

        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();

        String action = req.getParameter("action");

        MailActionType actionType = MailActionType.fromString(action);

        if (actionType == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            resp.setHeader("x-state", "error");
            resp.setHeader("x-msg", "param 'action' not found.");
            return;
        }

        try {
            switch (actionType) {
                case delete: {
                    doAction(req, resp, new MailController() {
                        @Override
                        public void doAction(Mail mail) {
                            mail.setFlagDel(Mail.FLAG_DELETE);
                        }
                    });
                    break;
                }
                case trash: {
                    doAction(req, resp, new MailController() {
                        @Override
                        public void doAction(Mail mail) {
                            mail.setFlagDel(Mail.FLAG_TRASH);
                        }
                    });
                    break;
                }
                case untrash: {
                    doAction(req, resp, new MailController() {
                        @Override
                        public void doAction(Mail mail) {
                            mail.setFlagDel(Mail.FLAG_NO);
                        }
                    });
                    break;
                }
                case star: {
                    doAction(req, resp, new MailController() {
                        @Override
                        public void doAction(Mail mail) {
                            mail.setFlagFav(Mail.FLAG_YES);
                        }
                    });
                    break;
                }
                case unstar: {
                    doAction(req, resp, new MailController() {
                        @Override
                        public void doAction(Mail mail) {
                            mail.setFlagFav(Mail.FLAG_NO);
                        }
                    });
                    break;
                }
                case read: {
                    doAction(req, resp, new MailController() {
                        @Override
                        public void doAction(Mail mail) {
                            mail.setFlagUnread(Mail.FLAG_NO);
                        }
                    });
                    break;
                }
                case unread: {
                    doAction(req, resp, new MailController() {
                        @Override
                        public void doAction(Mail mail) {
                            mail.setFlagUnread(Mail.FLAG_YES);
                        }
                    });
                    break;
                }
                default: {
                    // no-op
                }
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("x-state", "ok");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("x-state", "error");
            resp.setHeader("x-exception", e.getMessage());
        }
    }

    private void doAction(HttpServletRequest req, HttpServletResponse resp, MailController controller) {

        final MailService mailService = new MailServiceImpl();

        String id = req.getParameter("id");

        if (!Strings.haveLength(id)) {
            throw new IllegalArgumentException("'id' not found!");
        }

        // multi-ids
        // e.g.  201,203,204
        if (id.indexOf(',') > -1) {

            String[] ids = id.split(",");

            for (String _id : ids) {

                Mail mail = mailService.findById(Long.parseLong(_id));

                if (mail == null) {
                    throw new IllegalStateException(String.format("Mail not found which id is %s", id));
                }

                controller.doAction(mail);

                mailService.save(mail);
            }
        }
        // one
        else {
            Mail mail = mailService.findById(Long.parseLong(id));

            if (mail == null) {
                throw new IllegalStateException(String.format("Mail not found which id is %s", id));
            }

            controller.doAction(mail);

            mailService.save(mail);
        }
    }

    static interface MailController {

        void doAction(Mail mail);

    }

    static enum  MailActionType {

        trash,

        // go back to INBOX
        untrash,

        delete,

        star,

        unstar,

        read,

        unread;

        static MailActionType fromString(String action) {

            if (action == null || action.length() == 0)
                return null;

            for (MailActionType t : values()) {
                if (action.equals(t.toString())) {
                    return t;
                }
            }

            return null;
        }

    }

}
