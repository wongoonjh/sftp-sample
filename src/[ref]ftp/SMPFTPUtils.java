package ftp.ref;

import java.io.File;

import SMP.cmn.common.cache.ftp.FTPUtils;
import SMP.cmn.common.cache.ftp.FTPValue.FTPInfo;
import SMP.cmn.common.ftp.imp.FTPType;
import SMP.cmn.common.ftp.imp.SMPFTPClient;
import SMP.cmn.common.ftp.imp.SMPSFTPClient;
import SMP.cmn.common.util.SMPMessageUtils;
import SMP.cmn.common.util.StringUtils;

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

public class SMPFTPUtils {

	/**
	 * ���ݼ����� ���� ���������� ��ȯ�Ѵ�.
	 * @param file
	 * @param code
	 * @param dtlCode
	 * @return
	 * @throws Exception 
	 */
	public static boolean isRemoteFileExists(String file, String code, String dtlCode) throws Exception{
		return isRemoteFileExists(file, code, dtlCode, file);
	}
	
	/**
	 * ���ݼ����� ���� ���������� ��ȯ�Ѵ�.
	 *  - ���ݼ����� ���ϸ��� file�� �ٸ� ��� ���ϸ�(remoteName)�� ����Ѵ�.
	 * @param file
	 * @param code
	 * @param dtlCode
	 * @param remoteName
	 * @return
	 * @throws Exception 
	 */
	public static boolean isRemoteFileExists(String file, String code, String dtlCode, String remoteName) throws Exception{
		return isRemoteFileExists(file, code, dtlCode, remoteName, null);
	}
	
	public static boolean isRemoteFileExists(String file, String code, String dtlCode, String remoteName, String param) throws Exception{
		boolean isMatch = false;
		try {
			FTPInfo ftpInfo = FTPUtils.getInstance().getFTPInfo(code, dtlCode);
			if(ftpInfo == null) return false;
			String remote = ftpInfo.getRemotePath();
			remote = SMPMessageUtils.getDateParser(remote, param);
			
			FTPType ftpType = ftpInfo.getProtocol();
			switch (ftpType) {
			case FTP:
				SMPFTPClient ftpClient = new SMPFTPClient();
				try {
					ftpClient.connect(ftpInfo);
					for(int i=0; i<1; i++){
						ftpClient.login(ftpInfo.getUserId(), ftpInfo.getUserPwd());
					}
					isMatch = ftpClient.isExist(remote, remoteName);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(ftpClient != null){
						ftpClient.close();
					}
				}
				break;
			case SFTP:
				SMPSFTPClient sftpClient = new SMPSFTPClient();
				try {
					sftpClient.connect(ftpInfo.getAddress(), ftpInfo.getUserId(), ftpInfo.getUserPwd(), ftpInfo.getPort());
					isMatch = sftpClient.isExist(remote, remoteName);
				} catch (Exception e) {
					throw e;
				} finally {
					if(sftpClient != null){
						sftpClient.disconnect();
					}
				}
				break;
			default:
				throw new Exception("�˼� ���� �����Դϴ�.["+ftpType+"]");
			} // switch statement ended
		} catch (Exception e) {
			throw e;
		}  // try statement ended
		return isMatch;
	}

	
	/**
	 * Ư�� ������ ������ ���ε��Ѵ�.
	 * @param file
	 * @param code
	 * @param dtlCode
	 * @return
	 */
	public static boolean upload(String file, String code, String dtlCode) throws Exception{
		return upload(file, code, dtlCode, file);
	}
	
	/**
	 * 
	 * @param file
	 * @param code
	 * @param dtlCode
	 * @param remoteName
	 *   - ���ݼ����� ������ ���ϸ��� file�� �ٸ� ��� ���ϸ��� ����Ѵ�.
	 * @return
	 * @throws Exception
	 */
	public static boolean upload(String file, String code, String dtlCode, String remoteName) throws Exception{
		return upload(file, code, dtlCode, remoteName, null);
	}
	
	public static boolean upload(String file, String code, String dtlCode, String remoteName, String param) throws Exception{
		boolean isCompleted = true;
		try {
			FTPInfo ftpInfo = FTPUtils.getInstance().getFTPInfo(code, dtlCode);
			if(ftpInfo == null) return false;
			String remote = ftpInfo.getRemotePath();
			remote = SMPMessageUtils.getDateParser(remote, param);
			if(ftpInfo != null){
				FTPType ftpType = ftpInfo.getProtocol();
				switch (ftpType) {
					case FTP:
						// ���� ���ε�
						isCompleted = ftpUpload(remote, ftpInfo, file, remoteName);
						break;
					case SFTP:
						// ���� ���ε�
						isCompleted = sftpUpload(remote, ftpInfo, file, remoteName);
						break;
					default:
						throw new Exception("�˼� ���� �����Դϴ�.["+ftpType+"]");
				} // switch statement ended
			}//if statement ended
		} catch (Exception e) {
			throw e;
		}// try statement ended
		return isCompleted;
	}

	
	/**
	 * ���� ���ε�
	 * @param ftpInfo
	 * @param file
	 * @param remoteName
	 * @return
	 * @throws Exception
	 */
	private static boolean ftpUpload(String remotePath, FTPInfo ftpInfo, String file, String remoteName) throws Exception{
		boolean isCompleted = false;
		SMPFTPClient ftpClient = new SMPFTPClient();
		try {
			// ftp ����
			ftpClient.connect(ftpInfo);
			// ftp �α���
			for(int i=0; i<1; i++){
				ftpClient.login(ftpInfo.getUserId(), ftpInfo.getUserPwd());
			}
			
			// ���ε� �� ��� ���� ��ġ
			String localFile = StringUtils.nvl(ftpInfo.getLocalPath());
			if(localFile.equals("")) throw new Exception("���� ��ΰ� ��ϵ��� �ʾҽ��ϴ�.");
			// ���ε� ���� ��ġ
			String remoteFile = StringUtils.nvl(remotePath);
			if(remoteFile.equals("")) throw new Exception("���ε� ��ΰ� ��ϵ��� �ʾҽ��ϴ�.");
			if(!localFile.endsWith("/")) localFile = localFile + "/" + file;
			else localFile = localFile + file;
			
			if(!remoteFile.endsWith("/")) remoteFile = remoteFile + "/";
			
			// ���ϰ˻�
			File local = new File(localFile);
			if(!local.exists()) throw new Exception("������ �������� �ʽ��ϴ�. ["+localFile+"]");
			// ���� ���ε�
			isCompleted = ftpClient.storeFile(new File(localFile), remoteFile, remoteName);
		} catch (Exception e) {
			throw e;
		} finally {
			ftpClient.close();
		} // try statement ended
		return isCompleted;
	}
	
	/**
	 * ���� ���ε�
	 * @param ftpInfo
	 * @param file
	 * @param remoteName
	 * @return
	 * @throws Exception
	 */
	private static boolean sftpUpload(String remote, FTPInfo ftpInfo, String file, String remoteName) throws Exception{
		boolean isCompleted = true;
		SMPSFTPClient sftpClient = new SMPSFTPClient();
		try {
			// sftp ����
			sftpClient.connect(ftpInfo.getAddress(), ftpInfo.getUserId(), ftpInfo.getUserPwd(), ftpInfo.getPort());
			// ���ε� �� ��� ���� ��ġ
			String localFile = StringUtils.nvl(ftpInfo.getLocalPath());
			if(localFile.equals("")) throw new Exception("���� ��ΰ� ��ϵ��� �ʾҽ��ϴ�.");
			// ���ε� ���� ��ġ
			String remoteFile = StringUtils.nvl(remote);
			if(remoteFile.equals("")) throw new Exception("���ε� ��ΰ� ��ϵ��� �ʾҽ��ϴ�.");
			if(!localFile.endsWith("/")) localFile = localFile + "/" + file;
			if(!remoteFile.endsWith("/")) remoteFile = remoteFile + "/" + remoteName;
			
			// ���ϰ˻�
			File local = new File(localFile);
			if(!local.exists()) throw new Exception("������ �������� �ʽ��ϴ�. ["+localFile+"]");
			// ���� ���ε�
			isCompleted = sftpClient.uploadFile(localFile, remoteFile);
		} catch (Exception e) {
			throw e;
		} finally {
			sftpClient.disconnect();
		} // try statement ended
		return isCompleted;
	}
	
	/**
	 * Ư�� �������� ������ �ٿ�ε� �Ѵ�.
	 * @param downloadFileName
	 * @param code
	 * @param dtlCode
	 * @return
	 * @throws Exception 
	 */
	public static boolean download(String downloadFileName, String code, String dtlCode) throws Exception{
		return download(downloadFileName, code, dtlCode, downloadFileName);
	}
	
	/**
	 * Ư�� �������� ������ �ٿ�ε� �Ѵ�.
	 *  - ���ݼ������� �ٿ�ε� �� ���ϸ��� downloadFileName�� �ٸ� ��� ���ϸ��� ����Ѵ�.
	 * @param file
	 * @param code
	 * @param dtlCode
	 * @return
	 * @throws Exception 
	 */
	public static boolean download(String downloadFileName, String code, String dtlCode, String remoteName) throws Exception{
		return download(downloadFileName, code, dtlCode, remoteName, null);
	}
	
	public static boolean download(String downloadFileName, String code, String dtlCode, String remoteName, String param) throws Exception{
		boolean isCompleted = true;
		
		try {
			FTPInfo ftpInfo = FTPUtils.getInstance().getFTPInfo(code, dtlCode);
			if(ftpInfo == null) return false;
			String remote = ftpInfo.getRemotePath();
			remote = SMPMessageUtils.getDateParser(remote, param);
			if(ftpInfo != null){
				FTPType ftpType = ftpInfo.getProtocol();
				switch (ftpType) {
					case FTP:
						// �ٿ�ε�
						isCompleted = ftpDownload(remote, ftpInfo, downloadFileName, remoteName);
						break;
					case SFTP:
						isCompleted = sftpDownload(remote, ftpInfo, downloadFileName, remoteName);
						break;
					default:
						throw new Exception("�˼� ���� �����Դϴ�.["+ftpType+"]");
				} // swith statement ended
			} // if statement ended
		} catch (Exception e) {
			throw e;
		}// try statement ended
		return isCompleted;
	}

	
	private static boolean ftpDownload(String remote, FTPInfo ftpInfo, String downloadFileName, String remoteName) throws Exception{
		boolean isCompleted = true;
		SMPFTPClient ftpClient = new SMPFTPClient();
		try {
			// ���� ����
			ftpClient.connect(ftpInfo);
			
			// �α���
			for(int i=0; i<1; i++){
				ftpClient.login(ftpInfo.getUserId(), ftpInfo.getUserPwd());
			}
			
			// ���ε� �� ��� ���� ��ġ
			String localFile = StringUtils.nvl(ftpInfo.getLocalPath());
			
			if(localFile.equals("")) throw new Exception("���� ��ΰ� ��ϵ��� �ʾҽ��ϴ�.");

			// ���ε� ���� ��ġ
			String remoteFile = StringUtils.nvl(remote);
			if(remoteFile.equals("")) throw new Exception("���ε� ��ΰ� ��ϵ��� �ʾҽ��ϴ�.");
			
			File f = new File(localFile);
			if(!f.exists()){
				f.mkdirs();
			} // if statement ended
			
			if(!localFile.endsWith("/")) localFile = localFile + "/" + downloadFileName;
			else localFile = localFile + downloadFileName;
			if(!remoteFile.endsWith("/")) remoteFile = remoteFile + "/" + remoteName;
			else remoteFile = remoteFile + remoteName;
			
			// �ٿ�ε�
			isCompleted = ftpClient.retrieveFile(new File(localFile), remoteFile);
		} catch (Exception e) {
			throw e;
		} finally {
			ftpClient.close();
		} // try statement ended
		return isCompleted;
	}
	
	private static boolean sftpDownload(String remote, FTPInfo ftpInfo, String downloadFileName, String remoteName) throws Exception{
		boolean isCompleted = true;
		SMPSFTPClient sftpClient = new SMPSFTPClient();
		try {
			// ���� ���ε�
			// sftp ����
			sftpClient.connect(ftpInfo.getAddress(), ftpInfo.getUserId(), ftpInfo.getUserPwd(), ftpInfo.getPort());
			// ���ε� �� ��� ���� ��ġ
			String localFile = StringUtils.nvl(ftpInfo.getLocalPath());
			if(localFile.equals("")) throw new Exception("���� ��ΰ� ��ϵ��� �ʾҽ��ϴ�.");
			// ���ε� ���� ��ġ
			String remoteFile = StringUtils.nvl(remote);
			if(remoteFile.equals("")) throw new Exception("���ε� ��ΰ� ��ϵ��� �ʾҽ��ϴ�.");
			
			File f = new File(localFile);
			if(!f.exists()){
				f.mkdirs();
			} // if statement ended
			
			if(!localFile.endsWith("/")) localFile = localFile + "/" + remoteName;
			else localFile = localFile + remoteName;
			
			isCompleted = sftpClient.download(remoteFile, downloadFileName, localFile);
		} catch (Exception e) {
			throw e;
		} finally {
			sftpClient.disconnect();
		} // try statement ended

		return isCompleted;
	}
	
	/**
	 * ���� ������ ��ġ�� ��ȯ�Ѵ�.
	 * @param file
	 * @param code
	 * @param dtlCode
	 * @return
	 * @throws Exception 
	 */
	public static File getLocalFile(String file, String code, String dtlCode) throws Exception{
		return getLocalFile(file, code, dtlCode, null);
	}
	
	/**
	 * Ư�� ���͸��� ��ġ�� ��ȯ�Ѵ�.
	 * @param file
	 * @param code
	 * @param dtlCode
	 * @param usePath
	 * @return
	 * @throws Exception 
	 */
	public static File getLocalFile(String fileName, String code, String dtlCode, String usePath) throws Exception{
		FTPInfo ftpInfo = FTPUtils.getInstance().getFTPInfo(code, dtlCode);
		if(ftpInfo == null) return null;
		String localFile = StringUtils.nvl(ftpInfo.getLocalPath());
		
		if(localFile.equals("")){
			throw new Exception("���� ��ΰ� ��ϵ��� �ʾҽ��ϴ�.");
		} // if statement ended
		
		File file = null;
		if(!localFile.endsWith("/")){
			if(StringUtils.nvl(usePath).equals("")) file = new File(localFile + "/" + fileName);
			else file = new File(localFile + "/" + (!usePath.endsWith("/") ? usePath+"/" : "") + fileName);
		} else {
			if(StringUtils.nvl(usePath).equals("")) file = new File(localFile + "/" + fileName);
			else file = new File(localFile + "/" + (!usePath.endsWith("/") ? usePath+"/" : "") + fileName);
		} // if statement ended
		return file;
	}
	
}


