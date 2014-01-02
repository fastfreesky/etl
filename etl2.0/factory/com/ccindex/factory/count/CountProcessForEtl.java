package com.ccindex.factory.count;

/**
 * 
 * @ClassName: CountProcessForEtl
 * @Description: TODO(这里用一句话描述这个类的作用)清洗日志入库对应的类
 * @author tianyu.yang
 * @date 2013-1-11 下午2:28:31
 * 
 */
public class CountProcessForEtl {

	// 清洗类型,来源后缀,来源ISP,来源省,来源设备编号,清洗开始时间,清洗最后结束时间,匹配版本,目标设备编号
	private String etl_type = null, src_file_comp_type = null, src_isp = null,
			src_province = null, src_node = null, etl_start = null,
			etl_end = null, match_type = null, accept_node = null;

	// 运行时间累加值,最小单位为min,匹配累加长度,清洗累加来源行数,清洗累计来源大小,匹配累加行数
	private long etl_run_time = 0, match_len = 0, src_file_num = 0,
			src_file_size = 0, match_num = 0;

	private String etlDate;

	public String getEtlDate() {
		return etlDate;
	}

	public void setEtlDate(String etlDate) {
		this.etlDate = etlDate;
	}

	public String getEtl_type() {
		return etl_type;
	}

	public void setEtl_type(String etl_type) {
		this.etl_type = etl_type;
	}

	public String getSrc_file_comp_type() {
		return src_file_comp_type;
	}

	public void setSrc_file_comp_type(String src_file_comp_type) {
		this.src_file_comp_type = src_file_comp_type;
	}

	public String getSrc_isp() {
		return src_isp;
	}

	public void setSrc_isp(String src_isp) {
		this.src_isp = src_isp;
	}

	public String getSrc_province() {
		return src_province;
	}

	public void setSrc_province(String src_province) {
		this.src_province = src_province;
	}

	public String getSrc_node() {
		return src_node;
	}

	public void setSrc_node(String src_node) {
		this.src_node = src_node;
	}

	public String getEtl_start() {
		return etl_start;
	}

	public void setEtl_start(String etl_start) {
		this.etl_start = etl_start;
	}

	public String getEtl_end() {
		return etl_end;
	}

	public void setEtl_end(String etl_end) {
		this.etl_end = etl_end;
	}

	public String getMatch_type() {
		return match_type;
	}

	public void setMatch_type(String match_type) {
		this.match_type = match_type;
	}

	public long getMatch_num() {
		return match_num;
	}

	public void setMatch_num(long match_num) {
		this.match_num += match_num;
	}

	public String getAccept_node() {
		return accept_node;
	}

	public void setAccept_node(String accept_node) {
		this.accept_node = accept_node;
	}

	public long getEtl_run_time() {
		return etl_run_time;
	}

	public void setEtl_run_time(long etl_run_time) {
		this.etl_run_time += etl_run_time;
	}

	public long getMatch_len() {
		return match_len;
	}

	public void setMatch_len(long match_len) {
		this.match_len += match_len;
	}

	public long getSrc_file_num() {
		return src_file_num;
	}

	public void setSrc_file_num(long src_file_num) {
		this.src_file_num += src_file_num;
	}

	public long getSrc_file_size() {
		return src_file_size;
	}

	public void setSrc_file_size(long src_file_size) {
		this.src_file_size += src_file_size;
	}

	/**
	 * 格式化输出
	 * 
	 * @return
	 */
	public String format() {

		StringBuffer format_out = new StringBuffer();

		format_out.append(etl_type).append('\t');
		format_out.append(etlDate).append('\t');
		format_out.append(accept_node).append('\t');
		format_out.append(src_node).append('\t');
		format_out.append(src_file_comp_type).append('\t');
		format_out.append(src_file_size).append('\t');
		format_out.append(match_len).append('\t');
		format_out.append(src_file_num).append('\t');
		format_out.append(match_num).append('\t');
		format_out.append(src_province).append('\t');
		format_out.append(src_isp).append('\t');
		format_out.append(etl_start).append('\t');
		format_out.append(etl_end).append('\t');
		format_out.append(etl_run_time).append('\t');
		format_out.append(match_type);

		return format_out.toString();
	}
}
