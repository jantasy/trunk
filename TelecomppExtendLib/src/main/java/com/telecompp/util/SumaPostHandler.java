package com.telecompp.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.telecompp.ContextUtil;
import com.telecompp.engine.fm_sessionKeyExchange;

/**
 * 网络数据收发模块 
 * 
 * @author xiongdazhuang
 * 
 */
public class SumaPostHandler implements SumaConstants {
	public static final String RespCode_PT_PARAM_NOT_TRUE = "F991";//参数不对
	public static final String RespCode_PT_BEST_PAY_ORDER_EXIT = "F992";//订单已经存在
	public static final String RespCode_PT_BEST_PAY_ORDER_NOT_EXIT = "F993";//订单不存在
	public static final String RespCode_PT_BEST_PAY_ORDER_TERMINAL_NOT_TRUE = "F994";//终端不正确
	public static final String RespCode_PT_BEST_PAY_ORDER_IS_COMPLETED = "F995";//订单已经处理结束
	public static final String RespCode_PT_BEST_PAY_ORDER_YZF_ERROR = "F996";//翼支付侧错误
	public static final String RespCode_PT_CheckCode_Is_Null = "F997";//验证码为空
	public static final String RespCode_PT_CachedCheckCode_Is_Null = "F998";//验证码过期
	public static final String RespCode_PT_CachedCheckCode_Is_Not_True = "F999";//验证码不正确
	public static final String RespCode_PT_Login_error = "F980";//登陆失败
	public static final String RespCode_PT_Have_Registed = "F981";//已经注册过了
	public static final String RespCode_PT_Not_Have_Registed = "F982";//不是注册过
	public static final String RespCode_PT_REFUNDED = "F983";//已退款订单
	public static String getPostErr(String code){
		String retStr = "未知错误";
		if(RespCode_PT_PARAM_NOT_TRUE.equals(code)){
			retStr = "参数不对";
		}else if(RespCode_PT_BEST_PAY_ORDER_EXIT.equals(code)){
			retStr = "订单已存在";
		}else if(RespCode_PT_BEST_PAY_ORDER_NOT_EXIT.equals(code)){
			retStr = "订单不存在";
		}else if(RespCode_PT_BEST_PAY_ORDER_TERMINAL_NOT_TRUE.equals(code)){
			retStr = "终端不正确";
		}else if(RespCode_PT_BEST_PAY_ORDER_IS_COMPLETED.equals(code)){
			retStr = "订单已处理结束";
		}else if(RespCode_PT_BEST_PAY_ORDER_YZF_ERROR.equals(code)){
			retStr = "系统已受理你的退款";
		}else if(RespCode_PT_CheckCode_Is_Null.equals(code)){
			retStr = "验证码为空";
		}else if(RespCode_PT_CachedCheckCode_Is_Null.equals(code)){
			retStr = "验证码过期";
		}else if(RespCode_PT_CachedCheckCode_Is_Not_True.equals(code)){
			retStr = "验证码不正确";
		}else if(RespCode_PT_Login_error.equals(code)){
			retStr = "登录失败";
		}else if(RespCode_PT_Have_Registed.equals(code)){
			retStr = "终端已注册";
		}else if(RespCode_PT_Not_Have_Registed.equals(code)){
			retStr = "终端未注册";
		}else if(RespCode_PT_REFUNDED.equals(code)){
			retStr = "退款已受理，重复提交退款";
		}
		return retStr;
	}
    private int httpRspCode = 0;
    private String CookieID = "";
    private Map<String, Object> responseDataMap = new HashMap<String, Object>();
    private String failRespCode ;
    private static boolean isFirstConnSession = true;
    
    private LoggerHelper logger = new LoggerHelper(SumaPostHandler.class);
    
    int times = 0;
    
    public String getFailRespCode() {
		return failRespCode;
	}

	public void setFailRespCode(String failRespCode) {
		this.failRespCode = failRespCode;
	}

	private int connectTimeout = 30*1000;
    private int readTimeout = 30*1000;

    public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	 public Map<String, Object> httpPostAndGetResponse(String path, String xml,
	            int sencryptLevel) {
		 return httpPostAndGetResponse(path,xml,sencryptLevel,sencryptLevel);
	 }
	/**
     * 报文收发
     * 
     * @param path
     *            地址
     * @param xml
     *            xml报文
     * @param encryptLevel
     *            何种加密方式
     * @return 成功：收到的服务器下行xml报文 失败：null
     */



    public void safeDisConnect(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            // conn = null;
        }
    }

    /**
     * 通用解析接口 使用XML格式： <SumaPos> <Header> <MsgID>xxxx</MsgID>
     * <MsgType>xxxx</MsgType> </Header> <Content>
     * <TransMsgResp>xxxx</TransMsgResp> <TransMsgResp1>xxxx</TransMsgResp1>
     * </Content> </SumaPos>
     * 
     * @param xmlData
     * @return
     */
    public Map<String, Object> parseXML(byte[] xmlData) {

        String nodeNameString = "";
        String mapKey = "";
        String mapValue = "";

        Map<String, Object> dataMap = new HashMap<String, Object>();

        if (xmlData == null || (xmlData.length <= 0)) {
            return null;
        }

        DocumentBuilder documentBuilder;
        try {
            // 构建转换Factory
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            documentBuilder = docBuilderFactory.newDocumentBuilder();

            // 输入数据转换为Document
            ByteArrayInputStream bais = new ByteArrayInputStream(xmlData);
            Document doc = (Document) documentBuilder.parse(bais);

            // 获取第一节点
            NodeList nodeListLv1 = doc.getChildNodes();

            if (1 == nodeListLv1.getLength()) {
                Node node = nodeListLv1.item(0);

                // 获取第二节点
                NodeList nodeListLv2 = node.getChildNodes();
                // 循环数据成员
                for (int i = 0; i < nodeListLv2.getLength(); i++) {

                    Node nodeChild = nodeListLv2.item(i);
                    nodeNameString = nodeChild.getNodeName().trim();

                    // 遍历数据
                    NodeList dataListChild = nodeChild.getChildNodes();
                    if (dataListChild.getLength() > 0) {
                        for (int dI = 0; dI < dataListChild.getLength(); dI++) {
                            Node dataNode = dataListChild.item(dI);
                            nodeNameString = dataNode.getNodeName().trim();

                            if ("#text".equals(nodeNameString)) {
                                mapKey = nodeChild.getNodeName();
                                mapValue = nodeChild.getTextContent();

                            } else if (nodeNameString != null
                                    && !"".equals(nodeNameString)) {
                                mapKey = dataNode.getNodeName();
                                mapValue = dataNode.getTextContent();
                            } else {
                                continue;
                            }
                            dataMap.put(mapKey, mapValue);
                        }
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return dataMap;
    }

    /**
     * 特殊解析接口01
     */
    public Map<String, Object> parseXML1(byte[] xmlData) {
        String RespCode = "";
        String InterfaceType = "";
        String TransMsgResp = "";
        String TransMsgRespSign = "";
        String PublicTipCount = "";
        String MsgID = "";
        String MsgType = "";
        String CSN = "";
        String ICCID = "";
        String TerminalModelNum = "";
        String VerNo = "";
        String VerStatus = "";
        String ParamVerStatus = "";

        Map<String, Object> dataMap = new HashMap<String, Object>();
        Map<String, Object> testMap = null;

        ArrayList<Map<String, Object>> test = new ArrayList<Map<String, Object>>();
        ArrayList<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
        ByteArrayInputStream is = new ByteArrayInputStream(xmlData);
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, "GBK");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("Header")) {

                    } else if (parser.getName().equals("MsgID")) {
                        eventType = parser.next();
                        MsgID = parser.getText();
                        if (MsgID == null) {
                            MsgID = "";
                        }
                    } else if (parser.getName().equals("MsgType")) {
                        eventType = parser.next();
                        MsgType = parser.getText();
                        if (MsgType == null) {
                            MsgType = "";
                        }
                    } else if (parser.getName().equals("CSN")) {
                        eventType = parser.next();
                        CSN = parser.getText();
                        if (CSN == null) {
                            CSN = "";
                        }
                    } else if (parser.getName().equals("ICCID")) {
                        eventType = parser.next();
                        ICCID = parser.getText();
                        if (ICCID == null) {
                            ICCID = "";
                        }
                    } else if (parser.getName().equals("TerminalModelNum")) {
                        eventType = parser.next();
                        TerminalModelNum = parser.getText();
                        if (TerminalModelNum == null) {
                            TerminalModelNum = "";
                        }
                    } else if (parser.getName().equals("VerNo")) {
                        eventType = parser.next();
                        VerNo = parser.getText();
                        if (VerNo == null) {
                            VerNo = "";
                        }
                    } else if (parser.getName().equals("VerStatus")) {
                        eventType = parser.next();
                        VerStatus = parser.getText();
                        if (VerStatus == null) {
                            VerStatus = "";
                        }
                    } else if (parser.getName().equals("ParamVerStatus")) {
                        eventType = parser.next();
                        ParamVerStatus = parser.getText();
                        if (ParamVerStatus == null) {
                            ParamVerStatus = "";
                        }
                    } else if (parser.getName().equals("test")) {
                        testMap = new HashMap<String, Object>();
                    } else if (parser.getName().equals("RespCode")) {
                        eventType = parser.next();
                        RespCode = parser.getText();
                        if (RespCode == null) {
                            RespCode = "";
                        }
                    } else if (parser.getName().equals("InterfaceType")) {
                        eventType = parser.next();
                        InterfaceType = parser.getText();
                        if (InterfaceType == null) {
                            InterfaceType = "";
                        }
                    } else if (parser.getName().equals("TransMsgResp")) {
                        eventType = parser.next();
                        TransMsgResp = parser.getText();
                        if (TransMsgResp == null) {
                            TransMsgResp = "";
                        }
                    } else if (parser.getName().equals("TransMsgRespSign")) {
                        eventType = parser.next();
                        TransMsgRespSign = parser.getText();
                        if (TransMsgRespSign == null) {
                            TransMsgRespSign = "";
                        }
                    } else if (parser.getName().equals("PublicTipCount")) {
                        eventType = parser.next();
                        PublicTipCount = parser.getText();
                        if (PublicTipCount == null) {
                            PublicTipCount = "";
                        }
                        dataMap.put("PublicTipCount", PublicTipCount);
                        
                    } else if (parser.getName().equals("PublicTipInfo")) {
                        testMap = new HashMap<String, Object>();
                    } else if (parser.getName().equals("PublicTipTitle")) {

                        if (testMap == null) {
                            return null;
                        }

                        eventType = parser.next();

                        String strData = parser.getText();
                        if (strData == null) {
                            strData = "";
                        }
                        testMap.put("PublicTipTitle", strData);
                    } else if (parser.getName().equals("PublicTipContent")) {

                        if (testMap == null) {
                            return null;
                        }

                        eventType = parser.next();

                        String strData = parser.getText();
                        if (strData == null) {
                            strData = "";
                        }
                        testMap.put("PublicTipContent", strData);
                    } else if (parser.getName().equals("PublicTipHref")) {
                        if (testMap == null) {
                            return null;
                        }
                        eventType = parser.next();
                        String strData = parser.getText();
                        if (strData == null) {
                            strData = "";
                        }
                        testMap.put("PublicTipHref", strData);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("Header")) {
                        if ((MsgID == null) || (MsgType == null)
                                || (CSN == null) || (ICCID == null)
                                || (TerminalModelNum == null)
                                || (VerNo == null) || (VerStatus == null)
                                || (ParamVerStatus == null)) {
                            return null;
                        }
                        dataMap.put("MsgID", MsgID);
                        dataMap.put("MsgType", MsgType);
                        dataMap.put("CSN", CSN);
                        dataMap.put("ICCID", ICCID);
                        dataMap.put("TerminalModelNum", TerminalModelNum);
                        dataMap.put("VerNo", VerNo);
                        dataMap.put("VerStatus", VerStatus);
                        dataMap.put("ParamVerStatus", ParamVerStatus);
                    } else if (parser.getName().equals("Content")) {
                        if (test == null) {
                            return null;// 解析失败
                        }
                        dataMap.put("test", test);
                    } else if (parser.getName().equals("test")) {
                        if ((testMap == null) || (RespCode == null)
                                || (InterfaceType == null)
                                || (TransMsgResp == null)
                                || (TransMsgRespSign == null)) {
                            return null;// 解析失败
                        }
                        testMap.put("RespCode", RespCode);
                        testMap.put("InterfaceType", InterfaceType);
                        testMap.put("TransMsgResp", TransMsgResp);
                        testMap.put("TransMsgRespSign", TransMsgRespSign);
                        test.add(testMap);
                    } else if (parser.getName().equals("PublicTipInfo")) {
                        listData.add(testMap);

                        dataMap.put("PublicTipInfo", listData);
                    }
                    break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return dataMap;
    }
    private static final int MAX = 800000;
    public Map<String, Object> httpPostAndGetResponse(String path, String xml,int sencryptLevel,int rencryptLevel) {

    	TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "URL "+path);
        fm_sessionKeyExchange sessionKeyExchangeHandler = new fm_sessionKeyExchange();
        String xmlSenddedSting = sessionKeyExchangeHandler
                .postDataEncWithSessionKey(xml, sencryptLevel);
        if (xmlSenddedSting == null) {
            return null;
        }
        InputStream input = null;
        HttpPost httpPost = new HttpPost(path);
        HttpEntity entity;
        try {
        	entity = new StringEntity(xmlSenddedSting, "GBK");
			httpPost.setEntity(entity);
			String cookidString = TerminalDataManager.getCookieId();
            if (cookidString == null) {
            } else {
            	httpPost.setHeader("Cookie", cookidString);
            }
            // 设置文件类型
            httpPost.setHeader("Content-Type", "text/xml; charset=GBK");
            httpPost.setHeader("Accept-Encoding", "identity");
			HttpClient hc = new DefaultHttpClient();
			HttpParams params = hc.getParams();
			//	设置超时
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60 * 1000);
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);
//			HttpConnectionParams.setConnectionTimeout(params, getConnectTimeout());
//			HttpConnectionParams.setSoTimeout(params, getReadTimeout());
			HttpResponse resp = hc.execute(httpPost);
			if(resp.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				//获取数据成功
				//判断数据量是否过大
				HttpEntity het = resp.getEntity();
				if(het !=null){
					if(het.getContentLength()>=MAX){
						return null;
					}else{
						Header[] headers = resp.getHeaders("set-cookie");
						if(headers!=null&&headers.length>0){
							CookieID = headers[0].getValue();
						}
						input = het.getContent();
						//读取全部内容
						het.getContentLength();//获取数据长度
						byte[]buf = new byte[(int)het.getContentLength()];
						byte[]buffer = new byte[4096];
						int len = input.read(buffer);
						int pos = 0;
						while(len>0){
							System.arraycopy(buffer, 0, buf, pos, len);
							pos += len;
							len = input.read(buffer);
						}
						String responseString = new String(buf);
						if (responseString.length() == 4) {
		                	setFailRespCode(responseString);
		                    // 是否需要强制更新主控密钥或参数下载
		                    Log.d(TerminalDataManager.DEBUG_LOGCAT_FILTER,
		                            responseString);
		                    if (responseString.equals("F305")) {
		                        // masterKey
		                        Context c = ContextUtil.getInstance();
		                    }else if (responseString.equals("F311")) {
		                        // ParamVerStatus
		                        Context c = ContextUtil.getInstance();
		                    }else if (responseString.equals("F312")) {
		                        // ParamVerStatus
		                        Context c = ContextUtil.getInstance();
		                    }else if(responseString.equals("F003"))
		                    {
		                    	TerminalDataManager.setExceptionCode(responseString);
		                    	/*
		                    	if(isFirstConnSession)
		                    	{
		                    		isFirstConnSession = false;
		                    		boolean result = ConnSessionUtil.masterKeyUpdate(ContextUtil.getInstance());
		                    		if(result)
		                    		{
		                    			if(times < 3)
		                    			{
		                    				times++;
		                    				isFirstConnSession = true;
		                    				responseDataMap = httpPostAndGetResponse(path,xml,sencryptLevel);
		                    				new ContextUtil().post2UiThread(new Runnable() {
		                    					
		                    					@Override
		                    					public void run() {
		                    						Toast.makeText(ContextUtil.getInstance(),
		                    								"会话已更新", Toast.LENGTH_LONG).show();
		                    					}
		                    				});
		                    			}
		                    			else
		                    			{
		                    				return null;
		                    			}
		                    			return responseDataMap;
		                    		}
		                    		else
		                    		{
		                    			isFirstConnSession = true;
		                    			ContextUtil contextUtil = new ContextUtil();
		                    			contextUtil.post2UiThread(new Runnable() {
		                    				
		                    				@Override
		                    				public void run() {
		                    					Toast.makeText(ContextUtil.getInstance(),
		                    							"会话失效，请重新登录客户端", Toast.LENGTH_LONG).show();
		                    				}
		                    			});
		                    		}
		                    	}
		                    */}
		                    //	对接翼机通 时使用的错误提示
		                    ResponseExceptionInfo.setHttpResponseCode(responseString);
		                    TerminalDataManager
		                            .setGeneralCodeFromService(responseString);
		                    TerminalDataManager.setExceptionCode(responseString);
		                    return null;
		                }else if(responseString.length()>4){
		                	String encdataString = sessionKeyExchangeHandler
		                            .postDataDecWithSessionKey(responseString,
		                                    rencryptLevel);
		                    if (encdataString == null) {
		                        return null;
		                    }
		                    responseDataMap.put(MAP_HTTP_RESPONSE_RESPONSE,
		                            encdataString);
		                    System.out.println("receive:"+encdataString);
		                    responseDataMap.put(MAP_HTTP_RESPONSE_COOKIE_ID, CookieID);
		                    return responseDataMap;
		                }else{
		                	TerminalDataManager.setExceptionCode("A0002");
		                    return null;
		                }
					}
		        }
				return null;
			}else{
				//请求网络出错
				System.out.println("网络异常错误代码"+resp.getStatusLine().getStatusCode());
				return null;
			}
		} catch (Exception e1) {
			Log.d(TerminalDataManager.DEBUG_LOGCAT_FILTER, "A0002");
            TerminalDataManager.setExceptionCode("A0002");
            logger.printLogOnSDCard(e1.getStackTrace().toString());
            e1.printStackTrace();
            return null;
		}finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				input = null;
			}
		}
    }

}
