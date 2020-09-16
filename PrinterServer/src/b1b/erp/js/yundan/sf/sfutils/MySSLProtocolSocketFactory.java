package b1b.erp.js.yundan.sf.sfutils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MySSLProtocolSocketFactory extends SSLSocketFactory implements HostnameVerifier {
	private SSLContext sslContext;
	private SSLSocketFactory mFactory;

	public MySSLProtocolSocketFactory() {
		super();
		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { tm }, null);
			mFactory = sslContext.getSocketFactory();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return new String[0];
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return new String[0];
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
			throws IOException, UnknownHostException {
		return mFactory.createSocket(socket, host, port, autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return mFactory.createSocket();
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return mFactory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
		return mFactory.createSocket(host, port, localHost, localPort);
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return mFactory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress,
			int localPort) throws IOException {
		return mFactory.createSocket(address, port, localAddress, localPort);
	}

	//自定义私有类
	public static class TrustAnyTrustManager implements X509TrustManager {

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {

		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	@Override
	public boolean verify(String arg0, SSLSession arg1) {
		return true;
	}
}
