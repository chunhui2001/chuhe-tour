package com.shenmao.chuhe;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.FileUpload;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

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
            System.out.println(pname + ", dddd 1");
            vertx.fileSystem().mkdirBlocking(pname);
        } else {
            System.out.println(pname + ", dddd 2");
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

    public static List<String> moveUpload(Vertx vertx, FileUpload fileUpload, UploadType type) {

        // Buffer uploadedFile = vertx.fileSystem().readFileBlocking(fileUpload.uploadedFileName());

        List<String> returnUploadPath = new ArrayList<>();

        String fileName = null;

        try {
            fileName = URLDecoder.decode(fileUpload.fileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        String uploadedDir = UPLOAD_FOLDER_IMAGE_PRODUCT + "/" + (new SimpleDateFormat("yyyyMMdd")).format(new Date());
        String uploadedFile = uploadedDir + "/" + fileName;

        mkdirp(vertx, uploadedDir, null);
        vertx.fileSystem().copyBlocking(fileUpload.uploadedFileName(), uploadedFile);

        returnUploadPath.add(uploadedFile);
        System.out.println(uploadedFile + ", move upload file fileName");

        return returnUploadPath;

    }

}
