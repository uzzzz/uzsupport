package org.uzzz.jobs.semblance;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

import lombok.Data;

@Data
public class SemblanceRecord implements DBWritable, Writable, Serializable {

	private static final long serialVersionUID = -3687664190079656781L;

	protected long id;

	protected String title;

//	protected String content;

	@Override
	public void readFields(DataInput input) throws IOException {
		this.id = input.readLong();
		this.title = input.readUTF();

//		int size = input.readInt();
//		byte[] bytes = new byte[size];
//		input.readFully(bytes);
//		this.content = new String(bytes, "UTF-8");
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.id);
		output.writeUTF(this.title);

//		byte[] bytes = this.content == null ? new byte[0] : this.content.getBytes("UTF-8");
//		output.writeInt(bytes.length);
//		output.write(bytes);
	}

	@Override
	public void readFields(ResultSet set) throws SQLException {
		this.id = set.getLong(1);
		this.title = set.getString(2);
//		this.content = set.getString(3);
	}

	@Override
	public void write(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setLong(1, id);
		preparedStatement.setString(2, title);
//		preparedStatement.setString(3, content);
	}
}
