import com.jcraft.jsch.JSchException;


public class test {
	public static void main(String[] args) {
		SFTPClient sftp = new SFTPClient("127.0.0.1", 10022, "BTS");
		try {
			System.out.println("------------------------------");
			//sftp.authPassword("123123!!");
			sftp.authKey("C://Users/BTS/Documents/certificate.crt", "123123!!");
			
			System.out.println("------------------------------");
			sftp.uploadFile("./a", "/");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
}
