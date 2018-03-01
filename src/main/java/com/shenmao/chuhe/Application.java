package com.shenmao.chuhe;

import com.shenmao.chuhe.exceptions.PurposeException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.FileUpload;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Application {

    public enum UploadType {
        IMAGE, VIDEO, PDF, AUDIO
    }

    public static String UPLOAD_FOLDER = "file_uploads";        // could not use uploads: the uploads folder be deleted on app reboot
    public static String UPLOAD_FOLDER_IMAGE_PRODUCT = UPLOAD_FOLDER + "/images/product";
    public static String UPLOAD_FOLDER_VIDEO_PRODUCT = UPLOAD_FOLDER + "/videos/product";
    public static String UPLOAD_FOLDER_PDF_PRODUCT = UPLOAD_FOLDER + "/pdf/product";
    public static String UPLOAD_FOLDER_AUDIO_PRODUCT = UPLOAD_FOLDER + "/audio/product";


    static String ENV = "local";

    public static String getAppRoot () {

        URL appResource =  Application.class.getClassLoader().getResource("");
        String appResourceString = null;

        if (appResource == null) {
            appResourceString = Paths.get(Application.class.getProtectionDomain().getCodeSource().getLocation().toString()).getParent().getParent() + "";
        } else {

            appResourceString = Paths.get(appResource + "").getParent().getParent().toString() + "";
        }

        return appResourceString.substring(5);
    }

    public static Future<Void> prepare(Vertx vertx, Handler<AsyncResult<Void>> resultHandler) {

        Future<Void> future = Future.future();

        // prepare product upload folder
        mkdirp(vertx, UPLOAD_FOLDER_IMAGE_PRODUCT, null);
        //mkdirp(vertx, UPLOAD_FOLDER_VIDEO_PRODUCT, null);
        //mkdirp(vertx, UPLOAD_FOLDER_PDF_PRODUCT, null);
        //mkdirp(vertx, UPLOAD_FOLDER_AUDIO_PRODUCT, null);

        resultHandler.handle(Future.succeededFuture());

        return future;

    }

    private static void mkdirp (Vertx vertx, String path, String parent) {

        if (path == null || path.trim().isEmpty()) return;

        path = path.replaceAll("^[/]+", "");
        int pos = path.indexOf('/');
        parent = parent == null || parent.trim().isEmpty() ? "" : parent + "/";
        String pname = parent + path.substring(0, pos == -1 ? path.length() : pos);

        if (!vertx.fileSystem().existsBlocking(pname)) {
            vertx.fileSystem().mkdirBlocking(pname);
        }

        if (pos != -1)
            mkdirp(vertx, path.substring(pos + 1), pname);

    }

    public static void remoteUploadedEmptyFile(RoutingContext routingContext) {
        routingContext.getDelegate().fileUploads().stream()
                .filter(f -> f.size() == 0 )
                .forEach( f -> routingContext.vertx().fileSystem().deleteBlocking(f.uploadedFileName()));
        routingContext.next();
    }

    public static void methodOverride(RoutingContext routingContext) {

        if (routingContext.request().method() == HttpMethod.GET) {
            routingContext.next();
            return;
        }

        String realMethod = routingContext.request().getParam("_method");

        if (realMethod == null || realMethod.trim().isEmpty()) {
            routingContext.next();
            return;
        }

        if (routingContext.request().method() == HttpMethod.POST) {

            HttpMethod putOrDelete = null;

            if (realMethod.toLowerCase().equals("delete")) putOrDelete = HttpMethod.DELETE;
            if (realMethod.toLowerCase().equals("put")) putOrDelete = HttpMethod.PUT;

            if (putOrDelete != null) {
                routingContext.reroute(putOrDelete, routingContext.normalisedPath());
                return;
            }

        }

        routingContext.next();
    }

    public static List<String> moveUpload(Vertx vertx,  Set<FileUpload> fileUploads, String fieldName, UploadType type, String username) {

        if (username == null || username.trim().isEmpty()) {
            throw new PurposeException("Just log in user can upload files");
        }

        // Buffer uploadedFile = vertx.fileSystem().readFileBlocking(fileUpload.uploadedFileName());

        List<String> result = fileUploads.stream()
            .filter(fileUpload -> fileUpload.name().equals(fieldName) && fileUpload.size() > 0)
            .map( fileUpload -> {

                String uploadedDir = getUploadPath(username, type);
                String uploadedFile = uploadedDir + "/" +getUploadFileName(fileUpload.fileName());

                mkdirp(vertx, uploadedDir, null);
                vertx.fileSystem().moveBlocking(fileUpload.uploadedFileName(),  uploadedFile);

                // Use the Event Bus to dispatch the file now
                // Since Event Bus does not support POJOs by default so we need to create a MessageCodec implementation
                // and provide methods for encode and decode the bytes

                return uploadedFile.substring(UPLOAD_FOLDER.length());

            }).collect(Collectors.toList());

        return result;

    }

    public static String getUploadFileName(String uploadFilename) {

        String fileName = null;

        try {
            fileName = URLDecoder.decode(uploadFilename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new PurposeException(e.getMessage());
        }

        return fileName;
    }

    private static String getUploadPath(String username, UploadType type) {

        String path = null;

        switch (type) {
            case IMAGE:
                path = UPLOAD_FOLDER_IMAGE_PRODUCT;
                break;
            case PDF:
                path = UPLOAD_FOLDER_PDF_PRODUCT;
                break;
            case VIDEO:
                path = UPLOAD_FOLDER_VIDEO_PRODUCT;
                break;
            case AUDIO:
                path = UPLOAD_FOLDER_AUDIO_PRODUCT;
                break;
            default:
        }

        return path + "/" + (new SimpleDateFormat("yyyyMMdd")).format(new Date())
                + "/" + username + "/" + (new SimpleDateFormat("yyyyMMddHHmm")).format(new Date()) ;
    }

}
