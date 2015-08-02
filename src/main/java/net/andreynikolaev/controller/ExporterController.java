package net.andreynikolaev.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import net.andreynikolaev.service.ExporterService;
import net.andreynikolaev.WebApplication;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ExporterController {

    @Autowired
    private ExporterService exporterService;

    private transient StreamedContent fileExport;

    /**
     *
     */
    public void stopApp() {
        Thread stopThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ExporterController.class.getName()).log(Level.SEVERE, null, ex);
            }
            WebApplication.stopApp();
        });
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Session Exporter will be shut down"));

        stopThread.start();

    }

    /**
     *
     */
    public void exportSessions() {
        String fileName = "Sessions_" + getCurenDate() + ".zip";
        fileExport = new DefaultStreamedContent(getExporterService().getAllSessionAsZipInputStream(), "application/zip", fileName);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The file: " + fileName + " was generated."));
    }

    public StreamedContent getFileExport() {
        return fileExport;
    }

    public boolean getSessionExportStartDisable() {
        boolean result = getExporterService().getCountSessions() < 1;
        return result;
    }

    public int getSessionCount() {
        return getExporterService().getCountSessions();
    }

    public ExporterService getExporterService() {
        return exporterService;
    }

    public void setExporterService(ExporterService exporterService) {
        this.exporterService = exporterService;
    }

    private String getCurenDate() {
        String result = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss").format(new Date());

        return result;
    }
}
