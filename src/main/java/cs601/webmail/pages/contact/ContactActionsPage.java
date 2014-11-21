package cs601.webmail.pages.contact;

import cs601.webmail.entity.Contact;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.Page;
import cs601.webmail.service.ContactService;
import cs601.webmail.service.impl.ContactServiceImpl;
import cs601.webmail.util.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yuanyuan on 11/20/14.
 */
public class ContactActionsPage extends Page {
    @Override
    public void body() throws Exception{
        RequestContext context=RequestContext.getCurrentInstance();

        HttpServletRequest req=context.getRequest();
        HttpServletResponse resp=context.getResponse();

        String action =req.getParameter("action");

        ActionType actionType=ActionType.fromString(action);

        if(actionType==null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setHeader("x-state","error");
            resp.setHeader("x-msg","param 'action' not found ");
            return;
        }

        final User user;
        try{
            user=checkUserLogin(req,resp);
        }catch (NotAuthenticatedException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("x-state", "error");
            resp.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }

        try {
            switch (actionType) {
                case delete: {
                    doAction(req, resp, new IActionController<Contact>() {
                        @Override
                        public void doAction(Contact contact) {
                            if (contact.getUserId() == user.getId())
                                contact.setFlagDel(Contact.FLAG_DELETE);
                        }
                    });
                    break;
                }
                case disable: {
                    doAction(req, resp, new IActionController<Contact>() {
                        @Override
                        public void doAction(Contact contact) {
                            if (contact.getUserId() == user.getId())
                                contact.setFlagDel(Contact.FLAG_DISABLED);
                        }
                    });
                    break;
                }
                case enable: {
                    doAction(req, resp, new IActionController<Contact>() {
                        @Override
                        public void doAction(Contact contact) {
                            if (contact.getUserId() == user.getId())
                                contact.setFlagDel(Contact.FLAG_NO);
                        }
                    });
                    break;
                }
                case star: {
                    doAction(req, resp, new IActionController<Contact>() {
                        @Override
                        public void doAction(Contact contact) {
                            if (contact.getUserId() == user.getId())
                                contact.setFlagFav(Contact.FLAG_YES);
                        }
                    });
                    break;
                }
                case unstar: {
                    doAction(req, resp, new IActionController<Contact>() {
                        @Override
                        public void doAction(Contact contact) {
                            if (contact.getUserId() == user.getId())
                                contact.setFlagFav(Contact.FLAG_NO);
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
    private static interface IActionController<T> {

        void doAction(T object);

    }

    private void doAction(HttpServletRequest req,HttpServletResponse resp,IActionController controller){

        final ContactService contactService=new ContactServiceImpl();
        String id=req.getParameter("id");

        if(!Strings.haveLength(id)){
            throw new IllegalArgumentException("'id' not found!");
        }

        //multi-ids
        if (id.indexOf(',') > -1) {

            String[] ids = id.split(",");

            for (String _id : ids) {

                Contact contact = contactService.findById(Long.parseLong(_id));

                if (contact == null) {
                    throw new IllegalStateException(String.format("Contact not found which id is %s", id));
                }

                controller.doAction(contact);

                contactService.save(contact);
            }

        }else {
            Contact contact = contactService.findById(Long.parseLong(id));

            if (contact == null) {
                throw new IllegalStateException(String.format("Contact not found which id is %s", id));
            }

            controller.doAction(contact);

            contactService.save(contact);
        }
    }

    private static enum ActionType{
        delete,

        enable,

        disable,

        star,

        unstar;

        static ActionType fromString(String action) {

            if (action == null || action.length() == 0)
                return null;

            for (ActionType t : values()) {
                if (action.equals(t.toString())) {
                    return t;
                }
            }

            return null;
        }

    }

}
