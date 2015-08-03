package net.andreynikolaev.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import net.andreynikolaev.service.ExporterService;
import net.andreynikolaev.WebApplication;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ExporterController {

    @Autowired
    private ExporterService exporterService;

    private transient StreamedContent fileExport;
    private boolean serviceAvailible;
    private boolean stopFlag;

    /**
     *
     */
    public void stopApp() {
        Thread stopThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ExporterController.class.getName()).log(Level.SEVERE, null, ex);
            }
            WebApplication.stopApp();
        });
        setMessage(FacesMessage.SEVERITY_WARN, "Session Exporter will be shut down!");
        this.stopFlag = true;
        stopThread.start();

    }

    /**
     *
     */
    public void exportSessions() {

        String fileName = "Sessions_" + getCurenDate() + ".zip";
        this.fileExport = new DefaultStreamedContent(getExporterService().getAllSessionAsZipInputStream(), "application/zip", fileName);
        if (getExporterService().getErrorMessage() != null) {
            setMessage(FacesMessage.SEVERITY_ERROR, getExporterService().getErrorMessage());
            fileExport = null;
        } else {
            setMessage("The file: " + fileName + " was generated.");
        }

    }

    public StreamedContent getFileExport() {
        StreamedContent result = this.fileExport;
        this.fileExport = null;
        return result;
    }

    public boolean getSessionExportStartDisable() {
        boolean result = getExporterService().getCountSessions() < 1;
        return result;
    }

    public int getSessionCount() {
        return getExporterService().getCountSessions();
    }

    public ExporterService getExporterService() {
        return this.exporterService;
    }

    public void setExporterService(ExporterService exporterService) {
        this.exporterService = exporterService;
    }

    public boolean isServiceAvailible() {
        return this.serviceAvailible && !this.stopFlag;
    }

    public void checkRemoteService() {
        this.fileExport = null;
        String errorMessage = getExporterService().isServiceAvailible();
        this.serviceAvailible = errorMessage == null;
        if (!this.serviceAvailible && !this.stopFlag) {
            setMessage(FacesMessage.SEVERITY_FATAL, errorMessage);
        } else if(this.serviceAvailible && !this.stopFlag){
            setMessage(getSessionCount() + " sessions available for download.");
        }
        if(this.stopFlag){
            setMessage(FacesMessage.SEVERITY_WARN, "Session Exporter will be shut down!");
        }
    }

    private void setMessage(Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, "", message));
    }

    private void setMessage(String message) {
        setMessage(FacesMessage.SEVERITY_INFO, message);
    }

    private String getCurenDate() {
        String result = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss").format(new Date());
        return result;
    }

    public boolean getSessionExportDownloadDisable() {
        return this.fileExport == null;
    }

    public void setStopFlag() {
        this.stopFlag = true;
    }

    public boolean isStopFlag() {
        return this.stopFlag;
    }
}
