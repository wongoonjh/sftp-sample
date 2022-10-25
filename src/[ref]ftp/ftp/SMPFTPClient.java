package SMP.cmn.common.ftp.imp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import SMP.cmn.common.cache.ftp.FTPValue.FTPInfo;
import SMP.cmn.common.util.LogUtil;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/*******************************************************************************
 * <pre>
 * ��   ��  �� : 
 * ���� ������ : 
 * ��       �� : 
 * ��   ��  �� : ebizp061
 * ��   ��  �� : 2015. 2. 3.
 * ���� ���̺� :
 * ���� ����   :
 * Copyright �� SMP ������. All Right Reserved
 *******************************************************************************
 * �����ۼ� (1.0/2015. 2. 3./ebizp061)
 * �����̷� (����/�����Ͻ�/�ۼ���)
 * </pre>
 ******************************************************************************/

public class SMPFTPClient {

	private FTPClient ftpClient = null;
	private static final boolean IS_DEBUG = false;
	
	public boolean connect(FTPInfo ftpInfo) throws Exception{
		ftpClient = new FTPClient();
		boolean isCompleted = true;
		String error = "";
		try {
			ftpClient.setControlEncoding(ftpInfo.getEncoding());
//			ftpClient.setSoTimeout(60000);
			ftpClient.connect(ftpInfo.getAddress(), ftpInfo.getPort());
		} catch (Exception e) {
			isCompleted = false;
			error = LogUtil.getStackTraceString(e);
			throw e;
		} finally {
			if(!isCompleted){
				ftpClient.disconnect();
				ftpClient = null;
			}
		}
		return isCompleted;
	}
	
	public boolean login(String user, String pass) throws Exception{
		boolean isCompleted = true;
		String error = "";
		try {
			for(int i=0; i<1; i++){
				ftpClient.login(user, pass);
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (Exception e) {
			isCompleted = false;
			error = LogUtil.getStackTraceString(e);
			throw e;
		} finally {
		}
		return isCompleted;
	}
	
	/**
	 * ���� ���ε�
	 * @param localFile
	 * @param remoteFile
	 * @return
	 * @throws Exception
	 */
	public boolean storeFile(File localFile, String remoteFile, String remoteName) throws Exception{
		boolean isCompleted = true;
		InputStream is = null;
		OutputStream os = null;
		try {
			ftpClient.makeDirectory(remoteFile);
			is = new FileInputStream(localFile);
			os = ftpClient.storeFileStream(remoteFile + remoteName);
			//���� ���͸� ����
			if(os == null){
				boolean isMakeDir = makeDirectorys(remoteFile);
				if(!isMakeDir){
					if(!isMakeDir){
						if(IS_DEBUG) System.out.println("[SMPFTPClient]���� ������ ���͸� ���� ����");
						throw new Exception("���͸� ���� �� ������ �߻��Ͽ����ϴ�.");
					} else {
						System.err.println("[SMPFTPClient]���� ������ �ش��ϴ� ���͸��� �������� ����. ���͸� ���� �Ϸ�");
					}
				}
				os = ftpClient.storeFileStream(remoteFile + remoteName);
			}
	        byte[] bytesIn = new byte[4096];
	        int read = 0;
	        while ((read = is.read(bytesIn)) != -1) {
	        	os.write(bytesIn, 0, read);
	        	os.flush();
	        } // while statement ended
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(is != null) is.close();
		        if(os != null) os.close();
			} catch (Exception e2) {
			}
		}
		return isCompleted;
	}
	
	private boolean makeDirectorys(String directory) throws IOException{
		String[] pathElements = null;
		pathElements = directory.split("[/]");
		if(pathElements != null && pathElements.length > 0){
			StringBuffer buff = new StringBuffer();
			for(String dir : pathElements){
				if(dir.equals("")) continue;
				buff.append("/").append(dir);
				boolean existed = ftpClient.changeWorkingDirectory(buff.toString());
				if(IS_DEBUG) System.out.println("[SMPFTPClient]���ݼ����� ������ ���͸� ["+buff+"]. ���� ���� ["+existed+"]");
				if(!existed){
					boolean created = ftpClient.makeDirectory(buff.toString());
					if(created){
						if(IS_DEBUG) System.out.println("[SMPFTPClient]storeFile ���͸� �����Ϸ� ["+buff+"]");
					} else {
						System.err.println("[SMPFTPClient]storeFile ���͸� ��������.["+buff+"]");
						return false;
					}//if statement ended
				}//if statement ended
			}//for statement ended
		}
		return true;
	}
	
	/**
	 * ���� �ٿ�ε�
	 * @param localFile
	 * @param remoteFile
	 * @return
	 * @throws Exception
	 */
	public boolean retrieveFile(File localFile, String remoteFile) throws Exception{
		boolean isCompleted = true;
		OutputStream os = null;
		InputStream is = null;
		try {
	        is = ftpClient.retrieveFileStream(remoteFile);
	        if(is == null){
	        	throw new Exception("�ٿ�ε� ������ �������� �ʽ��ϴ�.");
	        }
	        os = new BufferedOutputStream(new FileOutputStream(localFile));
	        byte[] bytesArray = new byte[4096];
	        int bytesRead = -1;
	        while ((bytesRead = is.read(bytesArray)) != -1) {
	        	os.write(bytesArray, 0, bytesRead);
	        	os.flush();
	        }
	        isCompleted = ftpClient.completePendingCommand();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(is != null) is.close();
		        if(os != null) os.close();
		        closeServer();
			} catch (Exception e2) {
			}
		}
		return isCompleted;
	}
	
	/**
	 * FTP ������ ������ �����ϴ��� Ȯ���Ѵ�.
	 * @param remoteFile
	 * @param remoteFileName
	 * @return
	 * @throws Exception
	 */
	public boolean isExist(String remoteFile, String remoteFileName) throws Exception{
		try {
			boolean isMatch = false;
			ftpClient.changeWorkingDirectory(remoteFile);
			String fileNames[] = ftpClient.listNames();
			if(fileNames == null || fileNames.length == 0){
				return false;
			}// if statement ended
			
			for(String fileName : fileNames){
				if(fileName.equals(remoteFileName)){
					isMatch = true;
					break;
				}//if statement ended
			}//for statement ended
			return isMatch;
		} catch (Exception e) {
			throw e;
		} finally {
			if(ftpClient != null){
				ftpClient.logout();
				ftpClient.disconnect();
				ftpClient = null;
			} // if statement ended
		} // try statement ended
	}
	
	private boolean closeServer() throws IOException{
		boolean isClose = true;
		try {
			close();
		} catch (Exception e) {
			isClose = false;
		}
		return isClose;
	}
	
	public void close() throws IOException{
		if(ftpClient != null){
			if (ftpClient.isConnected()) {
				ftpClient.logout();
				ftpClient.disconnect();
			} // if statement ended
			ftpClient = null;
		} // if statement ended
	}
}


