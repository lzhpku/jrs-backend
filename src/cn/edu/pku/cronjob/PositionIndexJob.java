package cn.edu.pku.cronjob;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.edu.pku.conf.ZhilianConf;
import cn.edu.pku.crawler.Zhilian;
import cn.edu.pku.service.PositionIndexService;
import cn.edu.pku.util.RuntimeLog;
import cn.edu.pku.util.TimeUtil;

@Component
public class PositionIndexJob {
	
	ZhilianJob zhilianJob;
	BdwmJob bdwmJob;
	QianchengJob qianchengJob;
	PositionIndexService positionIndexservice;

	public PositionIndexService getPositionIndexservice() {
		return positionIndexservice;
	}

	@Resource
	public void setPositionIndexservice(PositionIndexService positionIndexservice) {
		this.positionIndexservice = positionIndexservice;
	}
	
	public QianchengJob getQianchengJob() {
		return qianchengJob;
	}

	@Resource
	public void setQianchengJob(QianchengJob qianchengJob) {
		this.qianchengJob = qianchengJob;
	}

	public BdwmJob getBdwmJob() {
		return bdwmJob;
	}

	@Resource
	public void setBdwmJob(BdwmJob bdwmJob) {
		this.bdwmJob = bdwmJob;
	}

	public ZhilianJob getZhilianJob() {
		return zhilianJob;
	}
	
	@Resource
	public void setZhilianJob(ZhilianJob zhilianJob) {
		this.zhilianJob = zhilianJob;
	}

	@Scheduled(cron = "0 4 11 * * ?")
	public void executePipeline() {
		
//		zhilianJob.executePipeline();
//		bdwmJob.executePipeline();
//		qianchengJob.executePipeline();
		
		//构建索引
		System.out.println("info:	开始 构建职位索引	"
				+ TimeUtil.getCurrentTime("yyyy/MM/dd HH:mm:ss"));
		RuntimeLog.init("runtimelog.txt");
		RuntimeLog.write("数据索引");
		positionIndexservice.create();
		RuntimeLog.write("数据索引");
		RuntimeLog.close();
		System.out.println("info:	完成 构建职位索引	"
				+ TimeUtil.getCurrentTime("yyyy/MM/dd HH:mm:ss"));
	}
}