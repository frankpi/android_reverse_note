package com.gameassist.plugin.support;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SupportServerSocket extends Thread {
    public static String SOCKET_ADDRESS = "com.iplay.gameassist";
    private static final String TAG = "Server"; 

   
   
    @Override
    public void run() {
    	LocalServerSocket server = null;  
        LocalSocket localSocket = null;  
    	  try {
              server = new LocalServerSocket(SOCKET_ADDRESS);
              while (true) {  
            	  Log.i(TAG, "sokcet is listener");
            	  localSocket = server.accept();
//                  Credentials cre = localSocket.getPeerCredentials();  
//                  Log.i(TAG,"accept socket uid:"+cre.getUid());  
                  new InteractClientSocketThread(localSocket).start();
                  Log.i(TAG,"server has reponse");  
              }     
          } catch (IOException e) {  
              e.printStackTrace();  
          }  
          finally{  
              try {  
            	  if(localSocket!=null){
            		   localSocket.close();  
            	  }else if(server!=null){
            		  server.close();  
            	  }
               
                 
              } catch (IOException e) {  
                  e.printStackTrace();  
              }  
          }

   
    }
    
    
    
    private class InteractClientSocketThread extends Thread
    {
        private LocalSocket interactClientSocket;
        private InputStream inputstream = null;  
        private OutputStream outputstream=null;
        private String revstr="";
        private String result="";

        
        public InteractClientSocketThread(LocalSocket interactClientSocket)
        {
            this.interactClientSocket = interactClientSocket;
        }
        
        @Override
        public void run()
        {
            try
            {
				revstr = recv();
				if (!revstr.equals("")) {
//					result = coreLogic.callFunction(revstr);
                    Log.i("wx","rev:"+revstr+"--"+result);
					if(result!=null){
//						Log.i("wc", "wwqqqqq:"+result);
					send(result.getBytes());
					}
				}
            	
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d(TAG, "resolve data error !");
                send("ERROR".getBytes());
            }
            finally
            {
            	if(outputstream!=null){
            		try {
						outputstream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
                if (inputstream != null)
                {
                    try
                    {
                    	inputstream.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        public String recv() {  
            String result = null;  
            try { 
            	inputstream=interactClientSocket.getInputStream();
            	byte[] size=new byte[4];
            	int ret=inputstream.read(size);
            	if(ret>0){
            		int sizelen=byteArrayToInt(size,0);
            		byte[] data=new byte[sizelen];
            		inputstream.read(data);
            		result=new String(data);
            		 Log.d(TAG,"recv:"+new String(data)); 
            	}
            	
            } catch (IOException e) {  
                e.printStackTrace();  
            } finally {  
            }  
            return result;  
        }  
        
        public void send(byte[] data) {  
            try {  
            	byte[] size=intToByteArray1(data.length);
            	byte[] finalbyte=byteMerger(size,data);
            	outputstream =interactClientSocket.getOutputStream();
            	outputstream.write(finalbyte);
            	outputstream.flush();  
                Log.d(TAG,"send:"+new String(data));  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }
    
    public static int byteArrayToInt(byte[] b, int offset) {
	       int value= 0;
	       for (int i = 0; i < 4; i++) {
	           int shift= (4 - 1 - i) * 8;
	           value +=(b[i + offset] & 0x000000FF) << shift;//往高位游
	       }
	       return value;
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
    
   
    
}


