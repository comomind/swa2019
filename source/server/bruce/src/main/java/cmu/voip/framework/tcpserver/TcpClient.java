package cmu.voip.framework.tcpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
 
 
public class TcpClient {
 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
 
		try {
			Socket socket = new Socket("127.0.0.1", 8888);
 
			// 입력 스트림
			// 서버에서 보낸 데이터를 받음
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
 
			// 출력 스트림
			// 서버에 데이터를 송신
			OutputStream out = socket.getOutputStream();
 
			// 서버에 데이터 송신
			out.write("1234567\r\n".getBytes());
			out.flush();
			System.out.println("데이터를 송신 하였습니다.\n");
 
			String line = in.readLine();
			System.out.println("서버로 부터의 응답 : "+line);
 
			// 서버 접속 끊기
			in.close();
			out.close();
			socket.close();
 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
 
}