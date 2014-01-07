package org.shiwa.repository.toolkit.util;

import java.io.*;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.validation.SignaturePair;
import org.shiwa.desktop.data.description.workflow.InputPort;
import org.shiwa.desktop.data.description.workflow.OutputPort;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.repository.toolkit.transferobjects.PortRTO;
import org.shiwa.repository.toolkit.transferobjects.SignatureRTO;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.shiwa.desktop.data.util.properties.Locations;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 * @author Dave
 * @version 1.0.0 15/11/11
 */
public class RepoUtil {
    
    public static final String HTTP_LOCATION = "Location";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String MIME_PLAIN = "text/plain";
    public static final String BUNDLE_TEMP_LOCATION = Locations.getDefaultTempLocation() + File.separator + "SHIWADesktop";

    public static final String UNIT   = Character.toString((char) 30);
    public static final String RECORD = Character.toString((char) 31);

    public static final int SIG_STRONG_MATCH = 2;
    public static final int SIG_WEAK_MATCH   = 1;
    public static final int SIG_NO_MATCH     = 0;

    public static void write(InputStream in, OutputStream out) throws IOException {
        int c;
        byte[] bytes = new byte[16384];
        while ((c = in.read(bytes)) != -1) {
            out.write(bytes, 0, c);
        }
        out.flush();
        in.close();
        out.close();
    }

    public static String getServerPath(HttpServletRequest req) {
        String path = req.getScheme() + "://" + req.getServerName() +
                getPort(req) + req.getContextPath();
        if (!path.endsWith("/")) {
            path += "/";
        }
        return path;
    }

    private static String getPort(HttpServletRequest req) {
        if (HTTP.equalsIgnoreCase(req.getScheme()) && req.getServerPort() != 80 ||
                HTTPS.equalsIgnoreCase(req.getScheme()) && req.getServerPort() != 443) {
            return (":" + req.getServerPort());
        } else {
            return "";
        }
    }

    public static Map<String, String> getQuery(String queryString) {
        Map<String, String> q = new HashMap<String, String>();
        if (queryString == null || queryString.length() == 0) {
            return q;
        }
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int eq = pair.indexOf("=");
            if (eq > 0 && eq < pair.length()) {
                try {
                    q.put(pair.substring(0, eq), URLDecoder.decode(pair.substring(eq + 1, pair.length()), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return q;
    }

    public static List<SignaturePair> validateSignature(SignatureRTO parent, SignatureRTO child) throws SHIWADesktopIOException {
        List<SignaturePair> pairs = new ArrayList<SignaturePair>();

        List<PortRTO> remainingInputs = new ArrayList<PortRTO>();
        remainingInputs.addAll(child.getInputPorts());

        for (PortRTO parentPort : parent.getInputPorts()) {
            SignaturePair pair = new SignaturePair();

            ReferableResource parentResource = new InputPort(parentPort.getName());
            parentResource.setTitle(parentPort.getValue());
            parentResource.setDescription(parentPort.getDescription());
            parentResource.setDataType(parentPort.getType());
            DataUtils.setDataType(parentResource);

            pair.setPrimary(parentResource);
            PortRTO match = null;
            for (PortRTO childPort : remainingInputs) {
                if (parentPort.getName().equals(childPort.getName())) {
                    ReferableResource childResource = new InputPort(childPort.getName());
                    childResource.setTitle(childPort.getValue());
                    childResource.setDescription(childPort.getDescription());
                    childResource.setDataType(childPort.getType());
                    DataUtils.setDataType(childResource);
                    pair.setSecondary(childResource);
                    match = childPort;
                }

                if (match != null) break;
            }

            pairs.add(pair);
            remainingInputs.remove(match);
        }

        for (PortRTO childPort : remainingInputs) {
            SignaturePair pair = new SignaturePair();
            ReferableResource childResource = new InputPort(childPort.getName());
            childResource.setTitle(childPort.getValue());
            childResource.setDescription(childPort.getDescription());
            childResource.setDataType(childPort.getType());
            DataUtils.setDataType(childResource);
            pair.setSecondary(childResource);
            pairs.add(pair);
        }

        List<PortRTO> remainingOutputs = new ArrayList<PortRTO>();
        remainingOutputs.addAll(child.getOutputPorts());

        for (PortRTO parentPort : parent.getOutputPorts()) {
            SignaturePair pair = new SignaturePair();

            ReferableResource parentResource = new OutputPort(parentPort.getName());
            parentResource.setTitle(parentPort.getValue());
            parentResource.setDescription(parentPort.getDescription());
            parentResource.setDataType(parentPort.getType());
            DataUtils.setDataType(parentResource);

            pair.setPrimary(parentResource);
            PortRTO match = null;
            for (PortRTO childPort : remainingOutputs) {
                if (parentPort.getName().equals(childPort.getName())) {
                    ReferableResource childResource = new OutputPort(childPort.getName());
                    childResource.setTitle(childPort.getValue());
                    childResource.setDescription(childPort.getDescription());
                    childResource.setDataType(childPort.getType());
                    DataUtils.setDataType(childResource);
                    pair.setSecondary(childResource);
                    match = childPort;
                }

                if (match != null) break;
            }

            pairs.add(pair);
            remainingOutputs.remove(match);
        }

        for (PortRTO childPort : remainingOutputs) {
            SignaturePair pair = new SignaturePair();
            ReferableResource childResource = new OutputPort(childPort.getName());
            childResource.setTitle(childPort.getValue());
            childResource.setDescription(childPort.getDescription());
            childResource.setDataType(childPort.getType());
            DataUtils.setDataType(childResource);
            pair.setSecondary(childResource);
            pairs.add(pair);
        }

        return pairs;
    }
    
    public static int getUserId(HttpServletRequest request, ApplicationFacadeLocal af) {
        String remoteUser = request.getRemoteUser();
        RepoUser user = new RepoUser(af);
        user.setUserId(remoteUser);
        return user.getId();
    } 

    public static boolean isNotEmpty(Collection<?> implementations) {
        return implementations != null && implementations.size() > 0;
    }
}