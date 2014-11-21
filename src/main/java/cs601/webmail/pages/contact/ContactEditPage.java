package cs601.webmail.pages.contact;

import cs601.webmail.entity.Contact;
import cs601.webmail.entity.User;
import cs601.webmail.exception.NotAuthenticatedException;
import cs601.webmail.frameworks.web.PageTemplate;
import cs601.webmail.frameworks.web.RequestContext;
import cs601.webmail.pages.Page;
import cs601.webmail.service.ContactService;
import cs601.webmail.service.impl.ContactServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by yuanyuan on 11/20/14.
 */
public class ContactEditPage extends Page{

    @Override
    public void body()throws Exception{
        HttpServletRequest request= RequestContext.getCurrentInstance().getRequest();
        HttpServletResponse response=RequestContext.getCurrentInstance().getResponse();

        User user;
        try {
            user = checkUserLogin(request, response);
        } catch (NotAuthenticatedException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("x-state", "error");
            response.addHeader("x-msg", "Illegal request without user in session.");
            return;
        }

        String method = request.getMethod();
        Exception exception = null;

        try {

            // render editing template
            if ("get".equalsIgnoreCase(method)) {
                doRenderTemplate(request, response, user);
                return;
            }
            else if ("post".equalsIgnoreCase(method)) {
                doSaveContact(request, response, user);
                return;
            }

        } catch (Exception e) {
            exception = e;
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        response.setHeader("x-state", "error");
        response.setHeader("x-msg", "please use the POST method to commit changes.");

        if (exception != null) {
            response.setHeader("x-exception", exception.getMessage());
        }

    }

    private void doRenderTemplate(HttpServletRequest request,HttpServletResponse response,User user) throws IllegalAccessException, IOException{

        ContactService contactService=new ContactServiceImpl();
        Long id = Long.parseLong(request.getParameter("id"));
        Contact contact=contactService.findById(id);
        if(contact==null){
            throw  new IllegalArgumentException("Contact entity not found for this ID:" + id);
        }
        if (contact.getUserId() != user.getId()) {
            throw new IllegalAccessException("User's id don't match the target contact entity's");
        }

        PageTemplate template = new PageTemplate("/velocity/contact_edit.vm");
        template.addParam("item", contact);
        StringWriter writer = new StringWriter();
        template.merge(writer);

        response.addHeader("x-state", "ok");
        response.setContentType("text/html; charset=utf-8");

        getOut().print(writer.toString());

    }

    private void doSaveContact(HttpServletRequest request,HttpServletResponse response,User user) throws IOException{

        ContactService contactService=new ContactServiceImpl();

        try{
            Contact contact=restoreEntity(request,contactService,user);
            contactService.save(contact);
            response.addHeader("x-state","ok");

        }catch (Exception e){
            response.addHeader("x-state", "error");
            response.addHeader("x-exception", e.getMessage());
            return;
        }

    }

    private Contact restoreEntity(HttpServletRequest req, ContactService contactService, User user) throws IOException{

        try {
            Long id = Long.parseLong(req.getParameter("id"));

            Contact contact = contactService.findById(id);

            if (contact == null) {
                throw new IllegalArgumentException("Contact entity not found for this ID:" + id);
            }

            // user check failed
            if (contact.getUserId() != user.getId()) {
                throw new IllegalAccessException("User's id don't match the target contact entity's");
            }

            contact.setFullName(req.getParameter("fullName"));
            contact.setAddress(req.getParameter("address"));
            contact.setZipcode(req.getParameter("zipcode"));
            contact.setPhone(req.getParameter("phone"));
            contact.setEmail(req.getParameter("email"));

            return contact;

        } catch (Exception e) {
            throw new IOException("Can't restore contact entity");
        }
    }

}
