package program;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Scanner;
import java.util.Vector;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class MySFTPClient {
	public void start() {
		// https://lahuman.jabsiri.co.kr/152
		// DH알고리즘을 쓰기위한 코드
		Security.addProvider(new BouncyCastleProvider());
		try {
			KeyPairGenerator.getInstance("DH");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		try {
			KeyAgreement.getInstance("DH");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		Session session = null;
		Channel channel = null;
		JSch jsch = new JSch();

		Scanner scanner = new Scanner(System.in);
		System.out.print("계정 입력: ");
		String username = scanner.nextLine();
		System.out.print("호스트 주소 입력: ");
		String host = scanner.nextLine();
		System.out.print("비밀번호 입력: ");
		String password = scanner.nextLine();

		try {
			// 세션 객체 생성
			session = jsch.getSession(username, host, 22);
			// 비밀번호설정
			session.setPassword(password);
			// 호스트 정보를 검사하지 않음
			session.setConfig("StrictHostKeyChecking", "no");
			// 세션접속
			session.connect();
			// sftp채널열기
			channel = session.openChannel("sftp");
			// 채널접속
			channel.connect();
			System.out.println("Connected to user@" + host);
		} catch (JSchException e) {
			e.printStackTrace();
			System.out.println("접속에 실패했습니다.");
			// 실패시 시스템 종료
			System.exit(0);
		}
		ChannelSftp channelSftp = (ChannelSftp) channel;
		while (true) {
			System.out.print("sftp> ");
			String str = "";
			str = scanner.nextLine();

			String[] params = str.split(" ");
			String command = params[0];

			if (command.equals("cd")) {
				String p1 = params[1];// 스플릿의 과부하를 줄인다..왜? 변수명으로 바꼈자나
				try {
					channelSftp.cd(p1);
				} catch (SftpException e) {
					System.out.println("Couldn't stat remote file: No such file or directory");
				}
			} // end cd
			else if (command.equals("lcd")) {
				// lcd C:\Users\solulink
				String p1 = params[1];
				try {
					channelSftp.lcd(p1);
				} catch (SftpException e) {

					System.out.println("Couldn't change local directory to " + p1 + ": No such file or directory");
				}
			} // end lcd
			else if (command.equals("pwd")) {
				try {
					System.out.println("Remote working directory: " + channelSftp.pwd());
				} catch (SftpException e) {
					e.printStackTrace();
				}
			} // end pwd
			else if (command.equals("lpwd")) {
				// lpwd
				System.out.println("Local working directory: " + channelSftp.lpwd());
			} // end lpwd
			else if (command.equals("get")) {
				try {
					if (params.length == 2) {
						channelSftp.get(params[1]);
					} else {
						channelSftp.get(params[1], params[2]);
					}
				} catch (SftpException e) {
					System.out.println("Ex)get centos.txt C:\\Users\\solulink");
				}
			} // end get
			else if (command.equals("put")) {
				String p1 = str.split(" ")[1];
				try {
					channelSftp.put(p1);

				} catch (SftpException e) {
					System.out.println("Ex)put window.txt");
				}
			} // end put
			else if (command.equals("ls") || command.equals("dir")) {
				String path = ".";
				try {
					// 가변길이의 배열
					Vector vector = channelSftp.ls(path);
					if (vector != null) {
						for (int i = 0; i < vector.size(); i++) {
							Object obj = vector.elementAt(i);
							if (obj instanceof ChannelSftp.LsEntry) {
								System.out.println(((ChannelSftp.LsEntry) obj).getLongname());
							}
						}
					}
				} catch (SftpException e) {
					System.out.println(e.toString());
				}
			} // end ls
			else if (command.equals("rm")) {
				try {
					String p1 = str.split(" ")[1];
					channelSftp.rm(p1);
				} catch (SftpException e) {
					System.out.println("Couldn't delete file: No such file or directory");
				}
			} // end rm
			else if (command.equals("mkdir")) {
				String p1 = str.split(" ")[1];
				try {
					channelSftp.mkdir(p1);
				} catch (SftpException e) {
					e.printStackTrace();
				}
			} // end mkdir
			else if (command.equals("rmdir")) {
				String p1 = str.split(" ")[1];
				try {
					channelSftp.rmdir(p1);
				} catch (SftpException e) {
					System.out.println("Couldn't remove diretory: No such file or directory");
				}
			} // end rmdir
			else if (command.equals("chmod")) {
				// 접근권한 설정
				// chmod 777 window.txt(rwx:7 x:1 wx:3 r-x:5)
				String p1 = str.split(" ")[1];
				String p2 = str.split(" ")[2];
				try {
					channelSftp.chmod(Integer.parseInt(p1), p2);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (SftpException e) {
					e.printStackTrace();
				}
			} // end chmod
			else if (command.equals("chown")) {
				// 파일소유권변경->일반계정에 root권한 부여(vi /etc/passwd->UID와GID변경)
				// 리눅스에서 cat /etc/passwd
				// jinpyolee : 1000 sftpuser : 1004
				// chown 1000 window.txt
				String p1 = str.split(" ")[1];
				String p2 = str.split(" ")[2];
				try {
					channelSftp.chown(Integer.parseInt(p1), p2);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (SftpException e) {
					e.printStackTrace();
				}
			} // end chown
			else if (command.equals("ln") || (command.equals("symlink"))) {
				// 링크파일 생성(rwxrwxrwx, 리눅스에서 하늘색으로 나옴)
				// ln window.txt win.txt
				String p1 = str.split(" ")[1];
				String p2 = str.split(" ")[2];
				try {
					channelSftp.symlink(p1, p2);
				} catch (SftpException e) {
					e.printStackTrace();
				}
			} // end ln
			else if (command.equals("quit")) {
				channelSftp.quit();
				// 반복문 나가서 종료
				break;
			} // end quit
			else {
				System.out.println("Invalid command.");
			}
		} // end while
			// 연결해제
		channelSftp.disconnect();
		// 스캐너자원반납
		scanner.close();
		// 시스템종료
		System.exit(0);
	}
}// end class
출처: https://thefif19wlsvy.tistory.com/5 [FIF's 코딩팩토리:티스토리]