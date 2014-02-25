
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author benoit
 * @param <T>
 */
public class JaxbWrapper<T> {

    private JAXBContext jaxbContext = null;
    private final Schema schema;

    public JaxbWrapper(URL schemaUrl, Class<?>... clazz) throws JAXBException, SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        this.schema = schemaFactory.newSchema(schemaUrl);
        this.jaxbContext = JAXBContext.newInstance(clazz);
    }

    public JaxbWrapper(File schemaFile, Class<?>... clazz) throws JAXBException, SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        this.schema = schemaFactory.newSchema(schemaFile);
        this.jaxbContext = JAXBContext.newInstance(clazz);
    }

    public String objectToXml(T t) throws JAXBException {
        StringWriter writer = new StringWriter();
        objectToXml(t, writer);
        return writer.toString();
    }

    public void objectToXml(T t, Writer writer) throws JAXBException {
        createMarshaller().marshal(t, writer);
    }

    public void objectToXml(T t, OutputStream outputStream) throws JAXBException {
        createMarshaller().marshal(t, outputStream);
    }

    public void objectToXml(T t, File file) throws JAXBException {
        createMarshaller().marshal(t, file);
    }

    public void validate(T t) throws JAXBException {
        createMarshaller().marshal(t, new StringWriter());
    }

    @SuppressWarnings("unchecked")
    public T xmlToObject(InputStream is) throws JAXBException {
        return (T) createUnmarshaller().unmarshal(is);
    }

    @SuppressWarnings("unchecked")
    public T xmlToObject(String xml) throws JAXBException {
        StringReader stringReader = new StringReader(xml);
        return (T) createUnmarshaller().unmarshal(stringReader);
    }

    private Marshaller createMarshaller() throws JAXBException, PropertyException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(schema);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return marshaller;
    }

    private Unmarshaller createUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);
        return unmarshaller;
    }
}
