package SMP.cmn.common.ftp.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.sftp.SftpClientFactory;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


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

public class SMPSFTPClient {

	private ChannelSftp command;

	private Session session;
	
	public void disconnect() {
		if (command != null) {
			command.exit();
		}
		if (session != null) {
			session.disconnect();
		}
		session = null;
		command = null;
	}
	
	public boolean connect(String host, String login, String password, int port) throws JSchException {
		if (command != null) {
			disconnect();
		}
		FileSystemOptions fso = new FileSystemOptions();
		try {
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(fso, "no");
			session = SftpClientFactory.createConnection(host, port, login.toCharArray(), password.toCharArray(), fso);
			Channel channel = session.openChannel("sftp");
			channel.connect();
			command = (ChannelSftp) channel;

		} catch (FileSystemException e) {
			e.printStackTrace();
			return false;
		}
		return command.isConnected();
	}
	
	/**
	 * ���� ���ε�
	 * @param localPath
	 * @param remotePath
	 * @return
	 * @throws Exception
	 */
	public boolean uploadFile(String localPath, String remotePath) throws Exception {
		boolean isCompleted = true;
		FileInputStream fis = new FileInputStream(localPath);
		try {
			String remoteDir = remotePath.substring(0, remotePath.lastIndexOf("/"));
			String path[] = remoteDir.split("[/]");
			StringBuffer buf = new StringBuffer();
			for ( int i=0; i<path.length; i++){
				buf.append(path[i]+"/");
				try {
					command.cd(buf.toString());
				} catch (Exception e) {
					if ( e instanceof SftpException){
						command.mkdir(buf.toString());
					}
				}
			}
			command.put(fis, remotePath);
		} catch (SftpException e) {
			e.printStackTrace();
			isCompleted = false;
			throw new Exception(e);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		return isCompleted;
	}

	/**
	 * ���� �ٿ�ε�
	 * @param dir
	 * @param downloadFileName
	 * @param path
	 * @throws Exception
	 */
	public boolean download(String dir, String downloadFileName, String path) throws Exception {
		boolean isCompleted = true;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
        	command.cd(dir);
            is = command.get(downloadFileName);
        } catch (SftpException e) {
            e.printStackTrace();
        }
        try {
            fos = new FileOutputStream(new File(path));
            int i;
            while ((i = is.read()) != -1) {
                fos.write(i);
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            isCompleted = false;
            throw new Exception(e);
        } finally {
            try {
            	if(fos != null) fos.close();
                if(is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isCompleted;
    }
	
	@SuppressWarnings("rawtypes")
	public boolean isExist(String remoteFile, String remoteFileName) throws Exception {
		try {
			String path[] = remoteFile.split("[/]");
			StringBuffer buf = new StringBuffer();
			for ( int i=0; i<path.length; i++){
				buf.append(path[i]+"/");
			}
			boolean isMatch = false;
			Vector vector = command.ls(buf.toString());
			if(vector != null){
				String regExp = "[\\s]+";
				String[] dirList = new String[vector.size()];
				for(int i=0; i<vector.size(); i++){
					dirList[i] = vector.elementAt(i).toString().replaceAll(regExp, "^");
					if(dirList[i].endsWith(remoteFileName)){
						isMatch = true;
						break;
					}
				}
			}
			return isMatch;
		} catch (SftpException e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			disconnect();
		}
	}
}


