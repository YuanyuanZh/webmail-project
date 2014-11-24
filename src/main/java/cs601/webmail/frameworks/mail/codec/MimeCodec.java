package cs601.webmail.frameworks.mail.codec;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by yuanyuan on 11/10/14.
 */
public abstract class MimeCodec implements Codec {

    public static MimeCodec getInstance(String codecName) throws NoSuchCodecException {
        Class<? extends MimeCodec> cls = codecClassMap.get(codecName);

        if (cls == null) {
            throw new NoSuchCodecException("Not found codec which named " + codecName);
        }

        Object ret = null;
        Exception exception = null;

        try {
            Constructor constructor = cls.getConstructor();
            ret = constructor.newInstance();
        } catch (NoSuchMethodException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        }

        if (exception != null) {
            throw new IllegalStateException("Can't create instance for name: " + codecName);
        }

        return (MimeCodec) ret;
    }

    private static Map<String, Class<? extends MimeCodec>> codecClassMap;

    static {
        codecClassMap = new HashMap<String, Class<? extends MimeCodec>>();

        // scan implementation classes
        ServiceLoader<MimeCodec> loader = ServiceLoader.load(MimeCodec.class);
        Iterator<MimeCodec> it = loader.iterator();

        while (it.hasNext()) {
            it.next(); // Just load class to invoke the static block to register in this class
        }
    }

    public static void register(String encoding, Class<? extends MimeCodec> implClass) {
        codecClassMap.put(encoding, implClass);
    }

//    public abstract Object getContent(InputStream in, String type);


}
