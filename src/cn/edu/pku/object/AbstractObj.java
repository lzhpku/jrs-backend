package cn.edu.pku.object;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import cn.edu.pku.conf.DatabaseConf;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;
import cn.edu.pku.util.HashMapSorter;
import cn.edu.pku.util.SegmenterUtil;

public class AbstractObj {

	protected String postitle = new String ();
	protected String posSalary = new String ();
	protected String posLocation = new String ();
	protected String posPublishDate = new String ();
	protected String posType = new String ();
	protected String posExperience = new String ();
	protected String posDegree = new String ();
	protected String posRecruitNum = new String ();
	protected String posCategory = new String ();
	protected String posDescription = new String ();
	protected String posUrl = new String ();
	protected String posKeyword = new String();
	protected String comName = new String ();
	protected String comScale = new String ();
	protected String comType = new String ();
	protected String comIndustry = new String ();
	protected String comHost = new String ();
	protected String comLocation = new String ();
	
	protected int hasTag = 0;	
	protected String source = new String ();
	protected String snapshotUrl = new String ();
	protected String displayContent = new String ();
	
	/**
	 * 将数据导入到conf文件中
	 * @param outputPath 导入文件路径
	 * @param sources 数据来源
	 * @param data 数据时间范围
	 * @param field 数据域
	 * */
	public static void feildsToConf(String outputPath,
			String[] sources,
			String[] date,
			String field,
			String key,
			String value
			) {
		
		String sql = "select ";
		
		//fields
		if (field == null || field.length() == 0) {
			System.out.println("info: no fields selected");
			return;
		}
		sql += field;
		
		//sources
		if (sources == null || sources.length == 0) {
			System.out.println("info: no sources selected");
			return;
		}
		sql += " from "
				+ DatabaseConf.getPositiontable()
				+ " where ("
				+ "source = '"
				+ sources[0] + "'";
		for (int i = 1; i < sources.length; i ++) {
			sql += " or source = '" + sources[i] + "'";
		}
		sql += ")";
		
		//key field
		if (key == null || key.length() == 0
				|| value == null || value.length() == 0) {
			System.out.println("info: no other key specified");
		} else {
			sql += " and (" + key + " = '" + value + "')";
		}
		
		//date
		if (date == null || date.length == 0) {
			System.out.println("info: no date specified");
			return;
		}
		sql += " and (pos_publish_date > '" + date[0] + "'";
		if (date.length == 2) {
			sql += " and pos_publish_date <= '" + date[1] + "'";
		}
		sql += ");";
		System.out.println(sql);
		
		List list = new ArrayList<>();
		String url = DatabaseConf.getDatebaseurl();
		try {
			Class.forName(DatabaseConf.getClassname());
			Connection conn;
			try {
				conn = DriverManager.getConnection(url);

				PreparedStatement stmt;
				try {
					stmt = conn.prepareStatement(sql);
					ResultSet rs = stmt.executeQuery(sql);
					try {
						list = convertList(rs);
//						System.out.println(list.size());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sql = new String();
		
		int counter = 0;
		HashMap<String, Integer> dup = new HashMap<String, Integer> ();
		FileOutput fo = new FileOutput(outputPath);
		Iterator it = list.iterator();
		while(it.hasNext()) {
		    Map hm = (Map)it.next();
		    String content = hm.get(field).toString();
		    if (dup.containsKey(content)) {
		    	dup.put(content, dup.get(content) + 1);
		    } else {
		    	dup.put(content, 1);
		    }
		    counter ++;
		}
		System.out.println("info : " + counter + " data processed");
		Map.Entry[] set = HashMapSorter.getSortedHashMapByValue(dup, 0);

		try {
			if (fo.t3 != null) {
				for (int i = 0; i < set.length; i ++) {
					String k = set[i].getKey().toString();
					int v = (int) set[i].getValue();
					fo.t3.write(k + "	" + v);
					fo.t3.newLine();
					if (k.length() == 0) {
						continue;
					}
					sql += "insert into industry (industry, num) values('"
							+ k + "', " + v + ");";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
//		sql = "insert into industry (industry, num) values('基金/证券/期货/投资', 101755);insert into industry (industry, num) values('互联网/电子商务', 88773);insert into industry (industry, num) values('教育/培训/院校', 52455);insert into industry (industry, num) values('房地产/建筑/建材/工程', 51098);insert into industry (industry, num) values('媒体/出版/影视/文化传播', 47954);insert into industry (industry, num) values('计算机软件', 45195);insert into industry (industry, num) values('专业服务/咨询(财会/法律/人力资源等)', 29661);insert into industry (industry, num) values('医药/生物工程', 22413);insert into industry (industry, num) values('IT服务(系统/数据/维护)', 17887);insert into industry (industry, num) values('保险', 14318);insert into industry (industry, num) values('广告/会展/公关', 10342);insert into industry (industry, num) values('医疗/护理/美容/保健/卫生服务', 5442);insert into industry (industry, num) values('通信/电信运营、增值服务', 4827);insert into industry (industry, num) values('医疗设备/器械', 4817);insert into industry (industry, num) values('家居/室内设计/装饰装潢', 4676);insert into industry (industry, num) values('银行', 4068);insert into industry (industry, num) values('信托/担保/拍卖/典当', 3927);insert into industry (industry, num) values('计算机硬件', 3641);insert into industry (industry, num) values('贸易/进出口', 3560);insert into industry (industry, num) values('网络游戏', 3266);insert into industry (industry, num) values('跨领域经营', 3155);insert into industry (industry, num) values('电子技术/半导体/集成电路', 3142);insert into industry (industry, num) values('酒店/餐饮', 2858);insert into industry (industry, num) values('环保', 2486);insert into industry (industry, num) values('其他', 2476);insert into industry (industry, num) values('学术/科研', 2285);insert into industry (industry, num) values('仪器仪表及工业自动化', 2176);insert into industry (industry, num) values('物业管理/商业中心', 2099);insert into industry (industry, num) values('娱乐/体育/休闲', 2093);insert into industry (industry, num) values('快速消费品（食品/饮料/烟酒/日化）', 2084);insert into industry (industry, num) values('通信/电信/网络设备', 1961);insert into industry (industry, num) values('旅游/度假', 1827);insert into industry (industry, num) values('农/林/牧/渔', 1522);insert into industry (industry, num) values('礼品/玩具/工艺美术/收藏品/奢侈品', 1472);insert into industry (industry, num) values('零售/批发', 1350);insert into industry (industry, num) values('耐用消费品（服饰/纺织/皮革/家具/家电）', 1318);insert into industry (industry, num) values('汽车/摩托车', 1258);insert into industry (industry, num) values('能源/矿产/采掘/冶炼', 1171);insert into industry (industry, num) values('中介服务', 1170);insert into industry (industry, num) values('电气/电力/水利', 1153);insert into industry (industry, num) values('大型设备/机电设备/重工业', 1019);insert into industry (industry, num) values('外包服务', 962);insert into industry (industry, num) values('政府/公共事业/非盈利机构', 955);insert into industry (industry, num) values('交通/运输', 898);insert into industry (industry, num) values('石油/石化/化工', 887);insert into industry (industry, num) values('加工制造（原料加工/模具）', 876);insert into industry (industry, num) values('航空/航天研究与制造', 747);insert into industry (industry, num) values('租赁服务', 550);insert into industry (industry, num) values('检验/检测/认证', 431);insert into industry (industry, num) values('物流/仓储', 267);insert into industry (industry, num) values('印刷/包装/造纸', 153);insert into industry (industry, num) values('办公用品及设备', 110);";
//		sql = "";
//		//industry-industry-industry-industry-industry
//		System.out.println(sql);
//		url = DatabaseConf.getDatebaseurl();
//		try {
//			Class.forName(DatabaseConf.getClassname());
//			Connection conn;
//			try {
//				conn = DriverManager.getConnection(url);
//
//				PreparedStatement stmt;
//				try {
//					stmt = conn.prepareStatement(sql);
//					stmt.executeUpdate();
//					stmt.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				conn.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 将数据导入到文件中
	 * @param outputPath 导入文件路径
	 * @param sources 数据来源
	 * @param data 数据时间范围
	 * @param fields 数据域
	 * @param maxLimit 提取的数据最大数目
	 * */
	public static void feildsToText(
			String outputPath,
			String outputSeperator,
			String[] sources,
			String[] date,
			String key,
			String value,
			String[] fields,
			int maxLimit
			) {
		
		String sql = "select ";
		
		//fields
		if (fields == null || fields.length == 0) {
			System.out.println("info: no fields selected");
			return;
		}
		sql += fields[0];
		for (int i = 1; i < fields.length; i ++) {
			sql += ", " + fields[i];
		}
		
		//sources
		if (sources == null || sources.length == 0) {
			System.out.println("info: no sources selected");
			return;
		}
		sql += " from "
				+ DatabaseConf.getPositiontable()
				+ " where ("
				+ "source = '"
				+ sources[0] + "'";
		for (int i = 1; i < sources.length; i ++) {
			sql += " or source = '" + sources[i] + "'";
		}
		sql += ")";
		
		//key field
		if (key == null || key.length() == 0) {
			System.out.println("info: no industries selected");
			return;
		}
		sql += " and (" + key + " = '" + value + "')";
		
		//date
		if (date == null || date.length == 0) {
			System.out.println("info: no date specified");
			return;
		}
		sql += " and (pos_publish_date > '" + date[0] + "'";
		if (date.length == 2) {
			sql += " and pos_publish_date <= '" + date[1] + "'";
		}
		sql += ");";
		System.out.println(sql);
		
		List list = new ArrayList<>();
		String url = DatabaseConf.getDatebaseurl();
		try {
			Class.forName(DatabaseConf.getClassname());
			Connection conn;
			try {
				conn = DriverManager.getConnection(url);

				PreparedStatement stmt;
				try {
					stmt = conn.prepareStatement(sql);
					ResultSet rs = stmt.executeQuery(sql);
					try {
						list = convertList(rs);
//						System.out.println(list.size());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (maxLimit == -1) {
			maxLimit = 0x7fffffff;
		}
		int counter = 0;
		HashSet<String> dup = new HashSet<String> ();
		FileOutput fo = new FileOutput(outputPath);
		try {
			if (fo.t3 != null) {
				Iterator it = list.iterator();
				while(it.hasNext()) {
					if (counter >= maxLimit) {
						break;
					}
				    Map hm = (Map)it.next();
				    String content = new String();
				    for (int i = 0; i < fields.length; i ++) {
				    	if (fields[i].equals(fields[fields.length - 1])) {
					    	String str = hm.get(fields[i]).toString();
					    	content += str;
				    	}
				    }
				    if (dup.contains(content)) {
				    	continue;
				    }
				    dup.add(content);
				    for (int i = 0; i < fields.length; i ++) {
				    	String str = hm.get(fields[i]).toString();
				    	//这里是一个补丁，由于之前处理的数据没有考虑职位描述
				    	//最后可能接额外的工作地址、公司网址
				    	int index = str.indexOf("公司网址：");
				    	if (index != -1) {
				    		str = str.substring(0, index);
				    	}
				    	index = str.indexOf("工作地址：");
				    	if (index != -1) {
				    		str = str.substring(0, index);
				    	}
				    	fo.t3.write(str + outputSeperator);
				    }
				    fo.t3.newLine();
				    counter ++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
		System.out.println("info : " + counter + " data processed");
	}

	/**
	 * 将数据导入到文件中
	 * @param outputPath 导入文件路径
	 * @param sources 数据来源
	 * @param data 数据时间范围
	 * @param industries 行业类别
	 * @param fields 数据域
	 * @param maxLimit 提取的数据最大数目
	 * */
	public static void feildsToText(
			String outputPath,
			String outputSeperator,
			String[] sources,
			String[] date,
			String[] industries,
			String[] fields,
			int maxLimit
			) {
		
		String sql = "select ";
		
		//fields
		if (fields == null || fields.length == 0) {
			System.out.println("info: no fields selected");
			return;
		}
		sql += fields[0];
		for (int i = 1; i < fields.length; i ++) {
			sql += ", " + fields[i];
		}
		
		//sources
		if (sources == null || sources.length == 0) {
			System.out.println("info: no sources selected");
			return;
		}
		sql += " from "
				+ DatabaseConf.getPositiontable()
				+ " where ("
				+ "source = '"
				+ sources[0] + "'";
		for (int i = 1; i < sources.length; i ++) {
			sql += " or source = '" + sources[i] + "'";
		}
		sql += ")";
		
		//industries
		if (industries == null || industries.length == 0) {
			System.out.println("info: no industries selected");
			return;
		}
		sql += " and (com_industry = '" + industries[0] + "'";
		for (int i = 1; i < industries.length; i ++) {
			sql += " or com_industry = '" + industries[i] + "'";
		}
		sql += ")";
		
		//date
		if (date == null || date.length == 0) {
			System.out.println("info: no date specified");
			return;
		}
		sql += " and (pos_publish_date > '" + date[0] + "'";
		if (date.length == 2) {
			sql += " and pos_publish_date <= '" + date[1] + "'";
		}
		sql += ");";
		System.out.println(sql);
		
		List list = new ArrayList<>();
		String url = DatabaseConf.getDatebaseurl();
		try {
			Class.forName(DatabaseConf.getClassname());
			Connection conn;
			try {
				conn = DriverManager.getConnection(url);

				PreparedStatement stmt;
				try {
					stmt = conn.prepareStatement(sql);
					ResultSet rs = stmt.executeQuery(sql);
					try {
						list = convertList(rs);
//						System.out.println(list.size());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (maxLimit == -1) {
			maxLimit = 0x7fffffff;
		}
		int counter = 0;
		HashSet<String> dup = new HashSet<String> ();
		FileOutput fo = new FileOutput(outputPath);
		try {
			if (fo.t3 != null) {
				Iterator it = list.iterator();
				while(it.hasNext()) {
					if (counter >= maxLimit) {
						break;
					}
				    Map hm = (Map)it.next();
				    String content = new String();
				    for (int i = 0; i < fields.length; i ++) {
				    	if (fields[i].equals(fields[fields.length - 1])) {
					    	String str = hm.get(fields[i]).toString();
					    	content += str;
				    	}
				    }
				    if (dup.contains(content)) {
				    	continue;
				    }
				    dup.add(content);
				    for (int i = 0; i < fields.length; i ++) {
				    	String str = hm.get(fields[i]).toString();
				    	//这里是一个补丁，由于之前处理的数据没有考虑职位描述
				    	//最后可能接额外的工作地址、公司网址
				    	int index = str.indexOf("公司网址：");
				    	if (index != -1) {
				    		str = str.substring(0, index);
				    	}
				    	index = str.indexOf("工作地址：");
				    	if (index != -1) {
				    		str = str.substring(0, index);
				    	}
				    	fo.t3.write(str + outputSeperator);
				    }
				    fo.t3.newLine();
				    counter ++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
		System.out.println("info : " + counter + " data processed");
	}
	
	/**
	 * 将结果保存为list，因为:
	 * 1.ResultSet可能有最大数量限制；
	 * 2.需要在遍历的同时删除，以便节省空间;
	 * */
	public static List convertList(ResultSet rs) throws SQLException{
		List list = new ArrayList();
		ResultSetMetaData md = rs.getMetaData();//获取键名
		int columnCount = md.getColumnCount();//获取行的数量
		while (rs.next()) {
			Map rowData = new HashMap();//声明Map
			for (int i = 1; i <= columnCount; i ++) {
				rowData.put(md.getColumnName(i), rs.getObject(i));//获取键名及值
			}
			list.add(rowData);
		}
		return list;
	}
	
	public AbstractObj () {
		super();
	}	
	
	public AbstractObj(String postitle, String posSalary, String posLocation, String posPublishDate, String posType,
			String posExperience, String posDegree, String posRecruitNum, String posCategory, String posDescription,
			String posUrl, String posKeyword, String comName, String comScale, String comType, String comIndustry, String comHost, String comLocation,
			int hasTag, String source, String snapshotUrl, String displayContent) {
		super();
		this.postitle = postitle;
		this.posSalary = posSalary;
		this.posLocation = posLocation;
		this.posPublishDate = posPublishDate;
		this.posType = posType;
		this.posExperience = posExperience;
		this.posDegree = posDegree;
		this.posRecruitNum = posRecruitNum;
		this.posCategory = posCategory;
		this.posDescription = posDescription;
		this.posUrl = posUrl;
		this.posKeyword = posKeyword;
		this.comName = comName;
		this.comScale = comScale;
		this.comType = comType;
		this.comIndustry = comIndustry;
		this.comHost = comHost;
		this.comLocation = comLocation;
		this.hasTag = hasTag;
		this.source = source;
		this.snapshotUrl = snapshotUrl;
		this.displayContent = displayContent;
	}
	
	public AbstractObj(AbstractObj a) {
		this.postitle = a.postitle;
		this.posSalary = a.posSalary;
		this.posLocation = a.posLocation;
		this.posPublishDate = a.posPublishDate;
		this.posType = a.posType;
		this.posExperience = a.posExperience;
		this.posDegree = a.posDegree;
		this.posRecruitNum = a.posRecruitNum;
		this.posCategory = a.posCategory;
		this.posDescription = a.posDescription;
		this.posUrl = a.posUrl;
		this.posKeyword = a.posKeyword;
		this.comName = a.comName;
		this.comScale = a.comScale;
		this.comType = a.comType;
		this.comIndustry = a.comIndustry;
		this.comHost = a.comHost;
		this.comLocation = a.comLocation;
		this.hasTag = a.hasTag;
		this.source = a.source;
		this.snapshotUrl = a.snapshotUrl;
		this.displayContent = a.displayContent;
	}

	public String getPostitle() {
		return postitle;
	}
	
	public void setPostitle(String postitle) {
		this.postitle = postitle;
	}
	
	public String getPosSalary() {
		return posSalary;
	}
	
	public void setPosSalary(String posSalary) {
		this.posSalary = posSalary;
	}
	
	public String getPosLocation() {
		return posLocation;
	}
	
	public void setPosLocation(String posLocation) {
		this.posLocation = posLocation;
	}
	
	public String getPosPublishDate() {
		return posPublishDate;
	}
	
	public void setPosPublishDate(String posPublishDate) {
		this.posPublishDate = posPublishDate;
	}
	
	public String getPosType() {
		return posType;
	}
	
	public void setPosType(String posType) {
		this.posType = posType;
	}
	
	public String getPosExperience() {
		return posExperience;
	}
	
	public void setPosExperience(String posExperience) {
		this.posExperience = posExperience;
	}
	
	public String getPosDegree() {
		return posDegree;
	}
	
	public void setPosDegree(String posDegree) {
		this.posDegree = posDegree;
	}
	
	public String getPosRecruitNum() {
		return posRecruitNum;
	}
	
	public void setPosRecruitNum(String posRecruitNum) {
		this.posRecruitNum = posRecruitNum;
	}
	
	public String getPosCategory() {
		return posCategory;
	}
	
	public void setPosCategory(String posCategory) {
		this.posCategory = posCategory;
	}
	
	public String getPosDescription() {
		return posDescription;
	}
	
	public void setPosDescription(String posDescription) {
		this.posDescription = posDescription;
	}
	
	public String getPosUrl() {
		return posUrl;
	}
	
	public void setPosUrl(String posUrl) {
		this.posUrl = posUrl;
	}
	
	public String getPosKeyword() {
		return posKeyword;
	}
	
	public void setPosKeyword(String posKeyword) {
		this.posKeyword = posKeyword;
	}
	
	public String getComName() {
		return comName;
	}

	public void setComName(String comName) {
		this.comName = comName;
	}

	public String getComScale() {
		return comScale;
	}
	
	public void setComScale(String comScale) {
		this.comScale = comScale;
	}
	
	public String getComType() {
		return comType;
	}
	
	public void setComType(String comType) {
		this.comType = comType;
	}
	
	public String getComIndustry() {
		return comIndustry;
	}
	
	public void setComIndustry(String comIndustry) {
		this.comIndustry = comIndustry;
	}
	
	public String getComHost() {
		return comHost;
	}
	
	public void setComHost(String comHost) {
		this.comHost = comHost;
	}
	
	public String getComLocation() {
		return comLocation;
	}
	
	public void setComLocation(String comLocation) {
		this.comLocation = comLocation;
	}
	
	public int getHasTag() {
		return hasTag;
	}
	
	public void setHasTag(int hasTag) {
		this.hasTag = hasTag;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getSnapshotUrl() {
		return snapshotUrl;
	}
	
	public void setSnapshotUrl(String snapshotUrl) {
		this.snapshotUrl = snapshotUrl;
	}
	
	public String getDisplayContent() {
		return displayContent;
	}
	
	public void setDisplayContent(String displayContent) {
		this.displayContent = displayContent;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comHost == null) ? 0 : comHost.hashCode());
		result = prime * result + ((comIndustry == null) ? 0 : comIndustry.hashCode());
		result = prime * result + ((comLocation == null) ? 0 : comLocation.hashCode());
		result = prime * result + ((comName == null) ? 0 : comName.hashCode());
		result = prime * result + ((comScale == null) ? 0 : comScale.hashCode());
		result = prime * result + ((comType == null) ? 0 : comType.hashCode());
		result = prime * result + ((displayContent == null) ? 0 : displayContent.hashCode());
		result = prime * result + hasTag;
		result = prime * result + ((posCategory == null) ? 0 : posCategory.hashCode());
		result = prime * result + ((posDegree == null) ? 0 : posDegree.hashCode());
		result = prime * result + ((posDescription == null) ? 0 : posDescription.hashCode());
		result = prime * result + ((posExperience == null) ? 0 : posExperience.hashCode());
		result = prime * result + ((posLocation == null) ? 0 : posLocation.hashCode());
		result = prime * result + ((posPublishDate == null) ? 0 : posPublishDate.hashCode());
		result = prime * result + ((posRecruitNum == null) ? 0 : posRecruitNum.hashCode());
		result = prime * result + ((posSalary == null) ? 0 : posSalary.hashCode());
		result = prime * result + ((posType == null) ? 0 : posType.hashCode());
		result = prime * result + ((posUrl == null) ? 0 : posUrl.hashCode());
		result = prime * result + ((postitle == null) ? 0 : postitle.hashCode());
		result = prime * result + ((snapshotUrl == null) ? 0 : snapshotUrl.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractObj other = (AbstractObj) obj;
		if (comHost == null) {
			if (other.comHost != null)
				return false;
		} else if (!comHost.equals(other.comHost))
			return false;
		if (comIndustry == null) {
			if (other.comIndustry != null)
				return false;
		} else if (!comIndustry.equals(other.comIndustry))
			return false;
		if (comLocation == null) {
			if (other.comLocation != null)
				return false;
		} else if (!comLocation.equals(other.comLocation))
			return false;
		if (comName == null) {
			if (other.comName != null)
				return false;
		} else if (!comName.equals(other.comName))
			return false;
		if (comScale == null) {
			if (other.comScale != null)
				return false;
		} else if (!comScale.equals(other.comScale))
			return false;
		if (comType == null) {
			if (other.comType != null)
				return false;
		} else if (!comType.equals(other.comType))
			return false;
		if (displayContent == null) {
			if (other.displayContent != null)
				return false;
		} else if (!displayContent.equals(other.displayContent))
			return false;
		if (hasTag != other.hasTag)
			return false;
		if (posCategory == null) {
			if (other.posCategory != null)
				return false;
		} else if (!posCategory.equals(other.posCategory))
			return false;
		if (posDegree == null) {
			if (other.posDegree != null)
				return false;
		} else if (!posDegree.equals(other.posDegree))
			return false;
		if (posDescription == null) {
			if (other.posDescription != null)
				return false;
		} else if (!posDescription.equals(other.posDescription))
			return false;
		if (posExperience == null) {
			if (other.posExperience != null)
				return false;
		} else if (!posExperience.equals(other.posExperience))
			return false;
		if (posLocation == null) {
			if (other.posLocation != null)
				return false;
		} else if (!posLocation.equals(other.posLocation))
			return false;
		if (posPublishDate == null) {
			if (other.posPublishDate != null)
				return false;
		} else if (!posPublishDate.equals(other.posPublishDate))
			return false;
		if (posRecruitNum == null) {
			if (other.posRecruitNum != null)
				return false;
		} else if (!posRecruitNum.equals(other.posRecruitNum))
			return false;
		if (posSalary == null) {
			if (other.posSalary != null)
				return false;
		} else if (!posSalary.equals(other.posSalary))
			return false;
		if (posType == null) {
			if (other.posType != null)
				return false;
		} else if (!posType.equals(other.posType))
			return false;
		if (posUrl == null) {
			if (other.posUrl != null)
				return false;
		} else if (!posUrl.equals(other.posUrl))
			return false;
		if (postitle == null) {
			if (other.postitle != null)
				return false;
		} else if (!postitle.equals(other.postitle))
			return false;
		if (snapshotUrl == null) {
			if (other.snapshotUrl != null)
				return false;
		} else if (!snapshotUrl.equals(other.snapshotUrl))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	
}
