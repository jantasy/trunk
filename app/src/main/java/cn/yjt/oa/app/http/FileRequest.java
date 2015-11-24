package cn.yjt.oa.app.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import cn.yjt.oa.app.app.utils.LogUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class FileRequest extends Request<File> {
	
	private Listener<File> listener;
	private File folder;

	public FileRequest(String url, Listener<File> listener,File folder,ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		this.listener = listener;
		this.folder = folder;
	}

	@Override
	protected void deliverResponse(File file) {
		listener.onResponse(file);
	}

	@Override
	public void deliverError(VolleyError error) {
		super.deliverError(error);
		LogUtils.i("deliverError:"+error);
	}
	
	@Override
	protected Response<File> parseNetworkResponse(NetworkResponse networkResponse) {
		LogUtils.i("parseNetworkResponse:"+networkResponse.headers);
		LogUtils.i("parseNetworkResponse:"+networkResponse.headers.keySet());
		String fileName = "temp";
		String contentDisposition = networkResponse.headers.get("Content-Disposition");
		String[] contents = contentDisposition.split(";");
		for (int i = 0; i < contents.length; i++) {
			String content = contents[i].trim();
			if(content.startsWith("filename=")){
				String string = content.substring("filename=".length()+1,content.length()-1);
				try {
					fileName = URLDecoder.decode(string, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		LogUtils.i(fileName);
		FileOutputStream out = null;
		File file = new File(folder, fileName);
		File parentFile = file.getParentFile();
		if(!parentFile.exists()){
			parentFile.mkdirs();
		}
		try {
			out = new FileOutputStream(file);
			 out.write(networkResponse.data);
			 out.flush();
		}  catch (Exception e) {
			e.printStackTrace();
			return Response.error(new VolleyError(e));
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Response.success(file, null);
	}

}
