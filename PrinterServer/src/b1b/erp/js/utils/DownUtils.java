package b1b.erp.js.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 张建宇 on 2017/6/29.
 */

public class DownUtils {
	private FTPClient mClient = null;
	private String hostname;
	private int port;
	private String username;
	private String password;
	private int timeout = 15 * 1000;

	public DownUtils(String hostname, int port, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
	} // 登录 /** * FTP登陆 * @throws IOException */

	public synchronized void login() throws Exception {
		mClient = new FTPClient();
		// mClient.configure(getFTPClientConfig());
		mClient.setDefaultPort(port);
		mClient.setConnectTimeout(timeout);
		mClient.connect(hostname);
		mClient.setControlEncoding("UTF-8");
		if (!mClient.login(username, password))
			throw new Exception("FTP登陆失败，请检测登陆用户名和密码是否正确!");
		mClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		mClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
		mClient.enterLocalPassiveMode();
	}

	/**
	 * 得到配置 * @return
	 */
	private FTPClientConfig getFTPClientConfig() { /* 创建配置对象 */
		FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
		conf.setServerLanguageCode("zh");
		return conf;
	}

	/**
	 * 关闭FTP服务器
	 */
	public void closeServer() {
		try {
			if (mClient != null) {
				mClient.logout();
				mClient.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 链接是否已经打开 * @return
	 */
	public synchronized boolean serverIsOpen() {
		if (mClient == null) {
			return false;
		}
		return mClient.isConnected();
	}

	/**
	 * 列表FTP文件 * @param regEx * @return
	 */
	public String[] listFiles(String regEx) {
		String[] names;
		try {
			names = mClient.listNames(regEx);
			if (names == null)
				return new String[0];
			return names;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[0];
	}

	/**
	 * 取得FTP操作类的句柄 *
	 * 
	 * @return
	 */
	public synchronized FTPClient getmClient() {
		return mClient;
	}

	/**
	 * 上传 * @throws Exception
	 * 
	 * @param localFilePath
	 * @param remoteFilePath
	 *            以“/”开头，文件名结尾（带后缀）
	 * @return
	 * @throws Exception
	 */
	public boolean upload(String localFilePath, String remoteFilePath) throws Exception {
		boolean state = false;
		File localFile = new File(localFilePath);
		state = upload(localFile, remoteFilePath);
		return state;
	}

	/**
	 * 上传 * @throws Exception
	 */
	public boolean upload(File localFile, String remoteFilePath) throws Exception {
		boolean state = false;
		if (!localFile.isFile() || localFile.length() == 0) {
			return state;
		}
		FileInputStream localIn = new FileInputStream(localFile);
		return upload(localIn, remoteFilePath);
	}

	/**
	 * 上传 *
	 * 
	 * @param localIn
	 * @param remoteFilePath
	 *            以“/”开头，文件名结尾（带后缀）
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean upload(InputStream localIn, String remoteFilePath) throws Exception {
		int last = remoteFilePath.lastIndexOf("/");
		if (last != 0&& last != -1) {
			String path = remoteFilePath.substring(0, last);
			toTargetDir(path);
		}
		return mClient.storeFile(remoteFilePath, localIn);
	}

	private void toTargetDir(String path) throws IOException {
		int nextSeperator = path.indexOf("/", 1);
		String currentPath = null;
		if (nextSeperator == -1) {
			currentPath = path.substring(1, path.length());
			createDir(currentPath, "");
		} else {
			currentPath = path.substring(1, nextSeperator);
			createDir(currentPath, "");
			toTargetDir(path.substring(nextSeperator));
		}
	}

	private void createDir(String path, String s) throws IOException {
		if (!mClient.changeWorkingDirectory(path)) {
			mClient.makeDirectory(path);
			mClient.changeWorkingDirectory(path);
		}
	}

	/**
	 * 是否存在FTP目录 * @param dir * @param mClient * @return
	 */
	public boolean isDirExist(String dir) {
		try {
			int retCode = mClient.cwd(dir);
			return FTPReply.isPositiveCompletion(retCode);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 创建FTP多级目录 * @param remoteFilePath * @throws IOException
	 */
	public void createDir(String dir) throws IOException {
		if (!isDirExist(dir)) {
			File file = new File(dir);
			createDir(file.getParent());
			mClient.makeDirectory(dir);
		}
	}

	/**
	 * 删除文件 * @param remoteFilePath
	 */
	public boolean delFile(String remoteFilePath) {
		try {
			return mClient.deleteFile(remoteFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 下载 * @throws Exception
	 */
	public void download(String localFilePath, String remoteFilePath) throws Exception {
		OutputStream localOut = new FileOutputStream(localFilePath);
		this.download(localOut, remoteFilePath);
		localOut.close();
	}

	/**
	 * 下载 * @throws Exception
	 */
	public void download(OutputStream localOut, String remoteFilePath) throws Exception {
		boolean result = mClient.retrieveFile(remoteFilePath, localOut);
		if (!result) {
			throw new Exception("文件下载失败!");
		}
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
