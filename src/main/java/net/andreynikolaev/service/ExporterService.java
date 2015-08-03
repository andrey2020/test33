package net.andreynikolaev.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import net.andreynikolaev.api.RemoteServiceTest;
import net.andreynikolaev.api.TestObject;
import net.andreynikolaev.controller.ExporterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author <a href="mailto:ich@andrey-nikolaev.net">Andrey Nikolaev</a>
 */
@Service
public class ExporterService {

    @Autowired
    private RemoteServiceTest remoteCalculator;

    private String errorMessage;

    private List<TestObject> listTestObject;

    public RemoteServiceTest getRemoteCalculator() {
        return remoteCalculator;
    }

    public void setRemoteCalculator(RemoteServiceTest remoteCalculator) {
        this.remoteCalculator = remoteCalculator;
    }

    public int getCountSessions() {
        return getListTestObject().size();
    }

    public InputStream getAllSessionAsZipInputStream() {
        byte[] result;
        errorMessage = null;
        try {
            listTestObject = getListTestObject();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                for (TestObject testObject : listTestObject) {
                    try {
                        Gson gson = new GsonBuilder().create();
                        String gsonString = gson.toJson(testObject);
                        ZipEntry zipEntry = new ZipEntry(testObject.getId() + ".json");
                        zipOutputStream.putNextEntry(zipEntry);
                        zipOutputStream.write(gsonString.getBytes());
                        zipOutputStream.closeEntry();
                    } catch (ZipException ze) {
                        result = new byte[]{};
                        errorMessage = ze.getLocalizedMessage();
                        return new ByteArrayInputStream(result);
                    }
                }

            }
            result = byteArrayOutputStream.toByteArray();

        } catch (Exception ex) {
            result = new byte[]{};
            errorMessage = ex.getLocalizedMessage();
            Logger.getLogger(ExporterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(result);
    }

    public String isServiceAvailible() {
        errorMessage = null;
        try {
            listTestObject = getRemoteCalculator().getlListObject();
        } catch (Exception e) {
            errorMessage = e.getLocalizedMessage();
        }
        return errorMessage;
    }

    public List<TestObject> getListTestObject() {
        if (listTestObject == null) {
            isServiceAvailible();
        }
        return listTestObject;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
