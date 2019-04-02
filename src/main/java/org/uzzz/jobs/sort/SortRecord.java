package org.uzzz.jobs.sort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

public class SortRecord implements DBWritable, Writable, Serializable {

	private static final long serialVersionUID = 5283103382129613704L;

	// post id
	protected long id;

	// 浏览数
	protected int views;

	// 点赞数
	protected int favors;

	// 评论数
	protected int comments;

	// 创建时间
	protected Date created;

	// 计算分值
	protected double score;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getFavors() {
		return favors;
	}

	public void setFavors(int favors) {
		this.favors = favors;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public void readFields(DataInput input) throws IOException {
		this.id = input.readLong();
		this.views = input.readInt();
		this.favors = input.readInt();
		this.comments = input.readInt();
		this.created = Date.valueOf(Text.readString(input));
		this.score = input.readDouble();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.id);
		output.writeInt(this.views);
		output.writeInt(this.favors);
		output.writeInt(this.comments);
		Text.writeString(output, this.created.toString());
		output.writeDouble(this.score);
	}

	@Override
	public void readFields(ResultSet set) throws SQLException {
		this.id = set.getLong(1);
		this.views = set.getInt(2);
		this.favors = set.getInt(3);
		this.comments = set.getInt(4);
		this.created = set.getDate(5);

		// 计算分值
		this.score = computeScore();
	}

	@Override
	public void write(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setLong(1, id);
		preparedStatement.setInt(2, views);
		preparedStatement.setInt(3, favors);
		preparedStatement.setInt(4, comments);
		preparedStatement.setDate(5, created);
	}

	private double computeScore() {

		double numerator = Math.log(views + 2 * favors + 5 * comments + Math.E);

		int ms2day = 60 * 60 * 1000 * 24;
		long now = System.currentTimeMillis() / ms2day;
		long created_at = this.created.getTime() / ms2day;
		long updated_at = this.created.getTime() / ms2day;

		double denominator = Math.pow(1 + (now - created_at) / 2 + (now - updated_at) / 2, Math.E / 2);

		return numerator / denominator;
	}

	public static class ScoreComparator extends DoubleWritable.Comparator {
		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
			return -super.compare(b1, s1, l1, b2, s2, l2);
		}
	}
}
