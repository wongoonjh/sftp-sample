package SMP.cmn.common.cache.ftp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import SMP.cmn.common.ftp.imp.FTPType;

/*******************************************************************************
 * <pre>
 * ��   ��  �� : 
 * ���� ������ : 
 * ��       �� : 
 * ��   ��  �� : ebizp061
 * ��   ��  �� : 2015. 2. 2.
 * ���� ���̺� :
 * ���� ����   :
 * Copyright �� SMP ������. All Right Reserved
 *******************************************************************************
 * �����ۼ� (1.0/2015. 2. 2./ebizp061)
 * �����̷� (����/�����Ͻ�/�ۼ���)
 * </pre>
 ******************************************************************************/

public class FTPValue implements Serializable{

	private static final long serialVersionUID = -1669965221572358048L;
	private String code = null;
	private Map<String, FTPInfo> ftpMap = null;
	
	public void setCode(String code){
		this.code = code;
	}
	public String getCode(){
		return this.code;
	}
	
	public void setFtpMap(String key, FTPInfo value){
		if(ftpMap == null) ftpMap = new HashMap<String, FTPValue.FTPInfo>();
		ftpMap.put(key, value);
	}
	
	public Map<String, FTPInfo> getFtpMap(){
		return this.ftpMap;
	}
	
	public FTPInfo getFTPInfo(String key){
		return (this.ftpMap == null || this.ftpMap.get(key) == null ? new FTPInfo() : this.ftpMap.get(key));
	}
	
	public class FTPInfo{
		private String dtlCode = null;
		private FTPType ftpType = null;
		private String address = null;
		private int port = 0;
		private String remotePath = null;
		private String localPath = null;
		private String userId = null;
		private String userPwd = null;
		private String name = null;
		private String customPath = null;
		private String encoding = null;
		private String timeout = null;
		
		public FTPType getFtpType() {
			return ftpType;
		}
		public void setFtpType(FTPType ftpType) {
			this.ftpType = ftpType;
		}
		
		public String getEncoding() {
			return encoding;
		}
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}
		
		public String getTimeout() {
			return timeout;
		}
		public void setTimeout(String timeout) {
			this.timeout = timeout;
		}
		
		public String getCustomPath() {
			return customPath;
		}
		public void setCustomPath(String customPath) {
			this.customPath = customPath;
		}
		public String getDtlCode() {
			return dtlCode;
		}
		public void setDtlCode(String dtlCode) {
			this.dtlCode = dtlCode;
		}
		public FTPType getProtocol() {
			return ftpType;
		}
		public void setProtocol(String protocol) {
			this.ftpType = FTPType.getType(protocol);
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public String getRemotePath() {
			return remotePath;
		}
		public void setRemotePath(String remotePath) {
			this.remotePath = remotePath;
		}
		public String getLocalPath() {
			return localPath;
		}
		public void setLocalPath(String localPath) {
			this.localPath = localPath;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getUserPwd() {
			return userPwd;
		}
		public void setUserPwd(String userPwd) {
			this.userPwd = userPwd;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}


