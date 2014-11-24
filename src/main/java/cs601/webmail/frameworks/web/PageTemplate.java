package cs601.webmail.frameworks.web;

import cs601.webmail.util.Strings;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 *
 * How to use? Just read the code or read test cases.
 *
 * Created by yuanyuan on 11/6/14.
 */
public class PageTemplate {

    public static final String VELOCITY_PROPERTIES = "/velocity/config/velocity.properties";
    private String templateName;
    private VelocityContext velocityContext;

    public PageTemplate(String templateName) {

        if (!Strings.haveLength(templateName)) {
            throw new IllegalArgumentException();
        }

        this.templateName = templateName;

        initVelocity();
    }

    private void initVelocity() {

        Properties p = new Properties();
        InputStream is = PageTemplate.class.getResourceAsStream(VELOCITY_PROPERTIES);

        ToolManager toolManager = new ToolManager();
        toolManager.configure("/velocity/config/tools.xml");
        ToolContext toolContext = toolManager.createContext();

        this.velocityContext = new VelocityContext(toolContext);

        try {
            p.load(is);
            Velocity.init(p);

        } catch (Exception e) {
            throw new IllegalStateException("init velocity failed.", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public String merge() throws IOException {
        StringWriter writer = new StringWriter();

        merge(writer);

        return writer.toString();
    }

    public void merge(Writer out) throws IOException {
        Template template;

        try {
            template = Velocity.getTemplate(templateName);
        } catch (Exception e) {
            throw new IOException("Template not found. [" + this.templateName + "]", e);
        }

        template.merge(velocityContext, out);
    }

    public PageTemplate addParam(String key, Object val) {
        this.velocityContext.put(key, val);
        return this;
    }

    public PageTemplate addParams(Map<String, Object> params) {
        if (params == null || params.size() == 0)
            return this;

        Iterator<String> it = params.keySet().iterator();
        for (;it.hasNext();) {
            String k = it.next();
            addParam(k, params.get(k));
        }

        return this;
    }

}
