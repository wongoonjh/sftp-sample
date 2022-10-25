package SMP.cmn.common.ftp.imp;

/*******************************************************************************
 * <pre>
 * ��   ��  �� : 
 * ���� ������ : 
 * ��       �� : 
 * ��   ��  �� : ebizp061
 * ��   ��  �� : 2015. 2. 23.
 * ���� ���̺� :
 * ���� ����   :
 * Copyright �� SMP ������. All Right Reserved
 *******************************************************************************
 * �����ۼ� (1.0/2015. 2. 23./ebizp061)
 * �����̷� (����/�����Ͻ�/�ۼ���/����)
 * </pre>
 ******************************************************************************/

public enum FTPType {
	FTP,
	SFTP,
	UNKNOWN;
	
	/**
	 * FTP Type�� ��ȯ�Ѵ�.
	 * @param type
	 * @return
	 */
	public static FTPType getType(String type){
		if(type == null || type.trim().equals("")) return UNKNOWN;
		else if(type.equalsIgnoreCase("FTP")) return FTP;
		else if(type.equalsIgnoreCase("SFTP")) return SFTP;
		else return UNKNOWN;
	}
}


