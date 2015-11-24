package cn.yjt.oa.app.imageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class FastNetwork implements Network {

    static final String TAG = "FastNetwork";
    private static ThreadPoolExecutor networkExecutorService = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(2);

    private static NetworkFilter filter = new NetworkFilterImpl();

    private static List<DownloadTask> waitQueue = new ArrayList<DownloadTask>();
    private static List<DownloadTask> workQueue = new ArrayList<DownloadTask>();

    public FastNetwork(Context context) {
    }

    @Override
    public void performRequest(final String url, final File file,
                               NetworkListener listener) {
        filter.filt(url, listener);
        if (containsInWorkQueue(url)) {
            DownloadTask task = getTaskInWorkQueue(url);
            listener.onStarted(url, (int) task.getMax(),
                    (int) task.getProgress());
        } else if (containsInWaitQueue(url)) {
            listener.onWait(url);
        } else {
            if (workQueue.size() == 2) {
                listener.onWait(url);
            }
            DownloadTask task = new DownloadTask(url, file);
            waitQueue.add(task);
            networkExecutorService.submit(task);
        }

    }

    private boolean containsInWorkQueue(String url) {
        synchronized (waitQueue) {
            for (DownloadTask task : workQueue) {
                if (task.equals(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    private DownloadTask getTaskInWorkQueue(String url) {
        synchronized (waitQueue) {
            for (DownloadTask task : workQueue) {
                if (task.equals(url)) {
                    return task;
                }
            }
        }
        return null;
    }

    private boolean containsInWaitQueue(String url) {
        synchronized (waitQueue) {
            for (DownloadTask task : waitQueue) {
                if (task.equals(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void delete(File file) {
        File deletedFile = new File(file.getAbsolutePath()
                + System.currentTimeMillis());
        file.renameTo(deletedFile);
        deletedFile.delete();
    }

    private class DownloadTask implements Runnable {
        private String url;
        private File file;
        private long progress;
        private long max;

        public DownloadTask(String url, File file) {
            super();
            this.url = url;
            this.file = file;
        }

        @Override
        public void run() {
            synchronized (waitQueue) {
                waitQueue.remove(this);
                workQueue.add(this);
            }
            try {
                download(url, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (waitQueue) {
                workQueue.remove(this);
            }
        }

        public long getMax() {
            return max;
        }

        public long getProgress() {
            return progress;
        }

        private void download(String url, File file) {
            filter.onStart(url);
            final File tempFile = new File(file.getAbsolutePath() + ".temp");
            if (file.exists()) {
                delete(file);
            }
            FileOutputStream out = null;
            InputStream in = null;
            try {
                URL _Url = new URL(url);
                HttpURLConnection conn;
                if(url.startsWith("https")) {
                    conn = (HttpsURLConnection) _Url.openConnection();
                    ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                    SSLContext sslcontext = SSLContext.getInstance("TLS");
                    sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);
                    ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
                } else {
                    conn = (HttpURLConnection) _Url.openConnection();
                }
//				HttpURLConnection conn = (HttpURLConnection) _Url
//						.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    out = new FileOutputStream(tempFile);
                    in = conn.getInputStream();
                    this.max = conn.getContentLength();
                    long progress = 0;
                    int len = 0;
                    byte[] buffer = new byte[1024];
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                        progress += len;
                        this.progress = progress;
                        filter.onProgress(url, progress,
                                conn.getContentLength());
                    }

                    try {
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    boolean renameTo = tempFile.renameTo(file);
                    Log.d(TAG, tempFile.getAbsolutePath() + " rename to "
                            + file.getAbsolutePath() + " - :" + renameTo);
                    filter.onSuccess(url, file);
                } else {
                    filter.onError(url,
                            new Exception("Connect failure responseCode="
                                    + conn.getResponseCode()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                    }
                }
                filter.onError(url, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof DownloadTask) {
                return ((DownloadTask) o).url.equals(url);
            } else if (o instanceof String) {
                return o.equals(url);
            }
            return super.equals(o);
        }

    }

    private static X509TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }

    };
}
