package com.github.catvod.spider;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import fi.iki.elonen.NanoHTTPD;
import jcifs.smb.SmbFile;

public   class SmbProxy extends NanoHTTPD {

    public SmbProxy(int port) {
        super(port);
    }

    @Override
    public void start() throws IOException {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public Response serve(IHTTPSession session) {

        Map<String, String> header = session.getHeaders();
        String uri = session.getUri();
        if (uri.contains("?")) {
            uri = uri.substring(0, uri.indexOf('?'));
        }
        if (Method.GET == session.getMethod()) {
            List<String> param = session.getParameters().get("url");
            if (param.isEmpty()) {
                return newFixedLengthResponse("not found url!");
            }
            String smbUrl = param.get(0);
            if (uri.startsWith("/smbproxy") && !TextUtils.isEmpty(smbUrl)) {
                try {
                    smbUrl = URLDecoder.decode(smbUrl, "UTF-8");

                    Smb4nas.getContext(smbUrl);
                    SmbFile file = new SmbFile(smbUrl, Smb4nas.cifsContext);
                    file.setReadTimeout(5000);
                    file.setConnectTimeout(5000);


                    if (file.canRead() && file.exists()) {
                        return serveFile(uri, header, file, "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (uri.startsWith("/device")) {
                return newFixedLengthResponse(smbUrl);
            }

        }
        return getNotFoundResponse();
    }

    /**
     * Serves file from homeDir and its' subdirectories (only). Uses only URI,
     * ignores all headers and HTTP parameters.
     */
    Response serveFile(String uri, Map<String, String> header, SmbFile file, String mime) {
        Response res;
        try {
            // Calculate etag
            String etag = Integer.toHexString((file.getCanonicalPath() + file.lastModified() + "" + file.length()).hashCode());

            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range.substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // get if-range header. If present, it must match etag or else we
            // should ignore the range request
            String ifRange = header.get("if-range");
            boolean headerIfRangeMissingOrMatching = (ifRange == null || etag.equals(ifRange));

            String ifNoneMatch = header.get("if-none-match");
            boolean headerIfNoneMatchPresentAndMatching = ifNoneMatch != null && ("*".equals(ifNoneMatch) || ifNoneMatch.equals(etag));

            // Change return code and add Content-Range header when skipping is
            // requested
            long fileLen = file.length();

            if (headerIfRangeMissingOrMatching && range != null && startFrom >= 0 && startFrom < fileLen) {
                // range request that matches current etag
                // and the startFrom of the range is satisfiable
                if (headerIfNoneMatchPresentAndMatching) {
                    // range request that matches current etag
                    // and the startFrom of the range is satisfiable
                    // would return range from file
                    // respond with not-modified
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    // FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream fis = new BufferedInputStream(file.getInputStream());
                    fis.skip(startFrom);

                    res = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mime, fis, newLen);
                    res.addHeader("Accept-Ranges", "bytes");
                    res.addHeader("Content-Length", "" + newLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                }
            } else {

                if (headerIfRangeMissingOrMatching && range != null && startFrom >= fileLen) {
                    // return the size of the file
                    // 4xx responses are not trumped by if-none-match
                    res = newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes */" + fileLen);
                    res.addHeader("ETag", etag);
                } else if (range == null && headerIfNoneMatchPresentAndMatching) {
                    // full-file-fetch request
                    // would return entire file
                    // respond with not-modified
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else if (!headerIfRangeMissingOrMatching && headerIfNoneMatchPresentAndMatching) {
                    // range request that doesn't match current etag
                    // would return entire (different) file
                    // respond with not-modified

                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    // supply the file
                    res = newFixedFileResponse(file, mime);
                    res.addHeader("Content-Length", "" + fileLen);
                    res.addHeader("ETag", etag);
                }
            }
        } catch (IOException ioe) {
            res = getForbiddenResponse("Reading file failed.");
        }

        return res;
    }

    private Response newFixedFileResponse(SmbFile file, String mime) throws IOException {
        Response res = newFixedLengthResponse(Response.Status.OK, mime, new BufferedInputStream(file.getInputStream()), (int) file.length());

        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }


    /**
     * URL-encodes everything between "/"-characters. Encodes spaces as '%20'
     * instead of '+'.
     */
    private String encodeUri(String uri) {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if ("/".equals(tok)) {
                newUri += "/";
            } else if (" ".equals(tok)) {
                newUri += "%20";
            } else {
                try {
                    newUri += URLEncoder.encode(tok, "UTF-8");
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        }
        return newUri;
    }

    protected Response getForbiddenResponse(String s) {
        return NanoHTTPD.newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: " + s);
    }

    protected Response getInternalErrorResponse(String s) {
        return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "INTERNAL ERROR: " + s);
    }

    protected Response getNotFoundResponse() {
        return NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
    }
}