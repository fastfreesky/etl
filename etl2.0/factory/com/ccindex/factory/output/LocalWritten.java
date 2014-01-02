package com.ccindex.factory.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class LocalWritten implements Written {
	protected static Logger logger = Logger.getLogger(LocalWritten.class);

	private Lock lock = new ReentrantLock();
	public AtomicInteger stepFlushFile = new AtomicInteger(1);
	public AtomicInteger step = new AtomicInteger(1);

	private int level;
	private AtomicInteger bufferSize = new AtomicInteger(0);

	private int startBufferNum;
	private int maxBufferNum;

	private FileOutputStream outputFile;
	private FileChannel outChannel;

	private ByteBuffer buf;
	private String filename;

	/**
	 * 
	 * @param filename
	 * @param level
	 *            每level次 缓存区写入文件后就会升级 buffer
	 * @param startBufferNum
	 * @param maxBufferNum
	 */
	public LocalWritten(String filename, int level, int startBufferNum,
			int maxBufferNum) {
		this.filename = filename;
		this.level = level;

		this.maxBufferNum = maxBufferNum;
		this.startBufferNum = startBufferNum;

		File aFile = new File(filename);

		try {
			outputFile = new FileOutputStream(aFile, false);
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
		}

		outChannel = outputFile.getChannel();
		bufferSize.set(getBufferLevelSize());

		logger.info("createFile: " + filename + "; buffSize="
				+ bufferSize.get());

		buf = ByteBuffer.allocate(bufferSize.get());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass().equals(LocalWritten.class)) {
			LocalWritten eobj = (LocalWritten) obj;
			return eobj.filename.equals(filename);
		}
		return false;
	}

	/**
	 * 
	 * @Title: getBufferLevelSize
	 * @Description: TODO(这里用一句话描述这个方法的作用)获取buffer大小
	 * @return int
	 * @throws
	 */
	public int getBufferLevelSize() {
		int size = ((2 << (stepFlushFile.get() / level)) / 2) * startBufferNum;
		if (size > maxBufferNum || size < startBufferNum)
			size = maxBufferNum;
		return size;
	}

	public void write(String line) throws IOException {
		byte[] data = line.getBytes();

		lock.lock();
		// FileLock flock = outChannel.lock();
		try {

			if (buf.position() + data.length > buf.limit()) {
				// flushToFile();
				buf.flip();

				outChannel.write(buf);
				buf.clear();

				stepFlushFile.addAndGet(1);

				int bSize = getBufferLevelSize();
				if (bufferSize.get() != bSize) {
					logger.info("flush+add:" + filename + "; buffSize:" + bSize
							+ ";  flush_num=" + step.get());

					buf = ByteBuffer.allocate(bSize);
					bufferSize.set(bSize);
				}
			}
			// flush(data);
//			logger.debug("flush:" + filename);
			buf.put(data);
			step.addAndGet(1);

		} finally {
			lock.unlock();
			// flock.release();
		}
	}

	private void flushToFile() throws IOException {
		lock.lock();
		// FileLock flock = outChannel.lock();
		try {
			buf.flip();
			outChannel.write(buf);
			buf.clear();
			stepFlushFile.addAndGet(1);

			int bSize = getBufferLevelSize();
			if (bufferSize.get() != bSize) {
				buf = ByteBuffer.allocate(bSize);
				bufferSize.set(bSize);
			}

		} finally {
			lock.unlock();
			// flock.release();
		}
	}

	private void flush(byte[] data) throws IOException {
		lock.lock();
		// FileLock flock = outChannel.lock();
		try {
			buf.put(data);
		} finally {
			lock.unlock();
			// flock.release();
		}
	}

	public void close() throws IOException {
		flushToFile();
		logger.info("closeFile:" + filename);
		outChannel.close();
		outputFile.close();
	}

}
