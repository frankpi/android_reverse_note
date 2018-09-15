package com.gameassist.plugin.support;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ClientConnect {  
    private static final String TAG = "ClientConnect";  
    private static String SOCKET_ADDRESS = "com.iplay.coresupport";  
    private LocalSocket Client = null;  
    private int timeout = 0;  
    private Context context;
    private InputStream inputstream = null;  
    private OutputStream os=null;
    public ClientConnect(Context context){
    	this.context=context;
    	
    }
      
    public void connect(){    
        try {  
        	if(context!=null){
        		SOCKET_ADDRESS=context.getPackageName();
        	}
            Client = new LocalSocket();  
            Client.connect(new LocalSocketAddress(SOCKET_ADDRESS));  
            Client.setSoTimeout(timeout);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
              
    public void send(byte[] data) {  
        try {  
        	byte[] size=intToByteArray1(data.length);
        	byte[] finalbyte=byteMerger(size,data);
        	os =Client.getOutputStream();
        	os.write(finalbyte);
            os.flush();  
            Log.d(TAG,"send"+new String(data));
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
    public String recv() {  
        String result = null;  
        try { 
        	inputstream=Client.getInputStream();
        	byte[] size=new byte[4];
        	int ret=inputstream.read(size);
        	if(ret>0){
        		int sizelen=byteArrayToInt(size,0);
        		byte[] data=new byte[sizelen];
        		inputstream.read(data);
        		result=new String(data);
        		Log.d(TAG,"recv:"+result);
        	}
        	
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
        }  
        return result;  
    }  
      
    public void close() {  
        try {  
        	inputstream.close();  
            os.close();  
            Client.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 

    
    public static byte[] intToByteArray1(int i) {   
		  byte[] result = new byte[4];   
		  result[0] = (byte)((i >> 24) & 0xFF);
		  result[1] = (byte)((i >> 16) & 0xFF);
		  result[2] = (byte)((i >> 8) & 0xFF); 
		  result[3] = (byte)(i & 0xFF);
		  return result;
		}
    
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }  
    public static int byteArrayToInt(byte[] b, int offset) {
	       int value= 0;
	       for (int i = 0; i < 4; i++) {
	           int shift= (4 - 1 - i) * 8;
	           value +=(b[i + offset] & 0x000000FF) << shift;//往高位游
	       }
	       return value;
	 }
	
}  