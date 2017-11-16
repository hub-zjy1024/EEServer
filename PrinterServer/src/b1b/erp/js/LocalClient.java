package b1b.erp.js;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class LocalClient {
	public static void main(String[] args) {
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress("192.168.10.65", 55555), 30 * 1000);
			System.out.println("client connected");
			socket.setSoTimeout(15*1000);
			InputStream responseIn = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(responseIn, "UTF-8"));
			OutputStream out = socket.getOutputStream();
			Scanner scan = new Scanner(System.in);
			System.out.println("输入消息：");
			String msg = scan.nextLine();
			out.write(msg.getBytes("UTF-8"));
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("recFrom Server:" + reader.readLine());
			out.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
