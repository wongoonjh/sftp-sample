import com.jcraft.jsch.*;

import java.util.Properties;

public final class SFTPClient {
    private final String      host;
    private final int         port;
    private final String      username;
    private final JSch        jsch;
    private       ChannelSftp channel;
    private       Session     session;

    /**
     * @param host     remote host
     * @param port     remote port
     * @param username remote username
     */
    public SFTPClient(String host, int port, String username) {
        this.host     = host;
        this.port     = port;
        this.username = username;
        jsch          = new JSch();
    }

    /**
     * Use default port 22
     *
     * @param host     remote host
     * @param username username on host
     */
    public SFTPClient(String host, String username) {
        this(host, 22, username);
    }


    /**
     * 접속
     * @param password
     * @throws JSchException
     */
    public void authPassword(String password) throws JSchException {
        session = jsch.getSession(username, host, port);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(password);
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }


    /**
     * 키패어 접속
     * @param keyPath
     * @param pass
     * @throws JSchException
     */
    public void authKey(String keyPath, String pass) throws JSchException {
        jsch.addIdentity(keyPath, pass);
        session = jsch.getSession(username, host, port);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }
    
    
    /**
     * 파일 업로드
     * @param localPath
     * @param remotePath
     * @throws JSchException
     * @throws SftpException
     */
    public void uploadFile(String localPath, String remotePath) throws JSchException, SftpException {
        System.out.println("Uploading ...[" + localPath + "] to [" + remotePath + "]\n\n");
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.put(localPath, remotePath);
    }
    
    /**
     *  다운로드
     * @param remotePath
     * @param localPath
     * @throws SftpException
     */
    public void downloadFile(String remotePath, String localPath) throws SftpException {
        System.out.println("Downloading ...[" + remotePath + "] to [" + localPath + "]\n\n");
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.get(remotePath, localPath);
    }
    
    
 
    /*
    public void listFiles(String remoteDir) throws SftpException, JSchException {
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        System.out.println("Listing ...[" + remoteDir + "]\n\n");
        
        channel.cd(remoteDir);
        Vector<ChannelSftp.LsEntry> files = channel.ls(".");
        for (ChannelSftp.LsEntry file : files) {
            var name        = file.getFilename();
            var attrs       = file.getAttrs();
            var permissions = attrs.getPermissionsString();
            var size        = Util.humanReadableByteCount(attrs.getSize());
            if (attrs.isDir()) {
                size = "PRE";
            }
            System.out.printf("[%s] %s(%s)%n", permissions, name, size);
        }
    }
    */

}
