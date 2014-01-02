package com.ccindex.factory.output;

import java.io.IOException;

public interface Written {
	public void write(String line) throws IOException;

	public void close() throws IOException;
}
