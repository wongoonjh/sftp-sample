package SMP.cmn.common.cache.ftp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SMP.cmn.common.cache.AbstractSMPCache;
import SMP.cmn.common.cache.ftp.FTPValue.FTPInfo;
import SMP.cmn.common.util.StringUtils;

import com.initech.ubiz30.common.db.DbClient;

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

public class FTPUtils extends AbstractSMPCache{

	private static FTPUtils instance = null;
	private static final String CMN_FTP_INF = "CMN_FTP_INF.qryFtpSystemInfo";
	private static final String CMN_FTP_PROPERTY = "CMN_FTP_INF.qryFtpSystemProperty";
	
	public FTPUtils(){}
	
	/**
	 * FTPUtils �ν��Ͻ��� ��ȯ�Ѵ�.
	 * @return
	 * @throws Exception 
	 */
	public static FTPUtils getInstance() throws Exception{
		if(instance == null){
			throw new Exception("FTPUtils�� �ʱ�ȭ ���� �ʾҽ��ϴ�.");
		} // if statement ended
		return instance;
	}
	
	/**
	 * CodeCache�� �����Ѵ�.
	 */
	@Override
	public void reloadAll() {
		super.reload(super.getKey());
	}

	public static void init(DbClient dbClient){
		if(instance == null){
			instance = new FTPUtils();
			instance.dbClient = dbClient;
			instance.setKey();
			instance.init();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		try {
			List<HashMap<String, String>> rows = dbClient.queryForList(CMN_FTP_INF);
			if(rows != null){
				Map<String, FTPValue> data = new HashMap<String, FTPValue>();
				for(HashMap<String, String> map : rows){
					// ���� �ý��� �ڵ�
					String connSysCode = map.get("CONN_SYS_CODE");
					FTPValue ftpValue = data.get(connSysCode);
					if(ftpValue == null) ftpValue = new FTPValue();
					
					// ���� ���ڵ� ����
					String connDtlCode = map.get("CONN_SYS_DTL_CODE");
					FTPInfo ftpInfo = ftpValue.getFTPInfo(connDtlCode);
					ftpInfo.setDtlCode(connDtlCode);
					ftpInfo.setName(map.get("CONN_SYS_DTL_NAME"));
					ftpInfo.setAddress(map.get("ADDRESS"));
					ftpInfo.setPort(Integer.parseInt(map.get("PORT")));
					ftpInfo.setProtocol(map.get("TRANS_PROTOCOL"));
					ftpInfo.setLocalPath(map.get("LOCAL_PATH"));
					ftpInfo.setRemotePath(map.get("REMOTE_PATH"));
					ftpInfo.setUserId(map.get("ACCOUNT_ID"));
					ftpInfo.setUserPwd(map.get("ACCOUNT_PWD"));
					String encoding = StringUtils.nvl(map.get("ENCODING"));
					String timeout = StringUtils.nvl(map.get("TIME_OUT"));
					
					// ���� �� ������Ƽ ������ ���´�.
					String propertyId = StringUtils.nvl(map.get("PROPERTY_ID"));
					if(!propertyId.equals("")){
						HashMap<String, String> param = new HashMap<String, String>();
						param.put("PROPERTY_ID", propertyId);
						Map<String, String> propertyMap = (Map<String, String>) dbClient.queryForObject(CMN_FTP_PROPERTY, param);
						if(propertyMap != null){
							encoding = StringUtils.nvl(propertyMap.get("ENCODING"));
							timeout = StringUtils.nvl(propertyMap.get("TIME_OUT"));
						} // if statement ended
					} // if statement ended
					
					ftpInfo.setEncoding(encoding);
					ftpInfo.setTimeout(timeout);
					
					ftpValue.setFtpMap(connDtlCode, ftpInfo);
					
					// �������� ����
					data.put(connSysCode, ftpValue);
				} // for statement ended
				super.remove(super.getKey());
				super.set(super.getKey(), data);
			} // if statement ended
		} catch (Exception e) {
			// TODO : ����ó��
			e.printStackTrace();
		} // try statement ended
	}

	@Override
	protected void setKey() {
		super.KEY = "FTP_KEY";
	}
	
	/**
	 * FTP ������ ��ȯ�Ѵ�.
	 * @param code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public FTPValue getFTPValue(String code){
		Map<String, FTPValue> data = (Map<String, FTPValue>) super.get(super.getKey());
		if(data == null) return null;
		else return data.get(code);
	}
	
	/**
	 * FTP �������� ��ȯ�Ѵ�.
	 * @param code
	 * @param dtlCode
	 * @return
	 */
	public FTPInfo getFTPInfo(String code, String dtlCode){
		FTPValue value = getFTPValue(code);
		if(value == null) return null;
		else return value.getFTPInfo(dtlCode);
	}
}


