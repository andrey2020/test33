package net.andreynikolaev.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
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

    public RemoteServiceTest getRemoteCalculator() {
        return remoteCalculator;
    }

    public void setRemoteCalculator(RemoteServiceTest remoteCalculator) {
        this.remoteCalculator = remoteCalculator;
    }

    public int getCountSessions() {
        return getRemoteCalculator().getlListObject().size();
    }

    public InputStream getAllSessionAsZipInputStream() {
        byte[] result;

        try {
            List<TestObject> listTestObject = getRemoteCalculator().getlListObject();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                for (TestObject listTestObject1 : listTestObject) {
                    Gson gson = new GsonBuilder().create();
                    String gsonString = gson.toJson(listTestObject1);
                    ZipEntry zipEntry = new ZipEntry(listTestObject1.getId() + ".json");
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(gsonString.getBytes());
                    zipOutputStream.closeEntry();
                }

            }
            result = byteArrayOutputStream.toByteArray();

        } catch (IOException ex) {
            result = new byte[]{-1};
            Logger.getLogger(ExporterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(result);
    }

}
