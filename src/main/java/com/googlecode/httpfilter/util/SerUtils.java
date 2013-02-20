package com.googlecode.httpfilter.util;

import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerUtils {

	public static <T extends Serializable> byte[] encode(T serializable)
			throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(serializable);
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			try {
				if (null != oos) {
					oos.close();
				}
			} catch (Exception e) {
				// donothing
			}
		}
		return baos.toByteArray();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T decode(byte[] bytes) throws IOException {
		final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (IOException e) {
			throw new IOException(e);
		} catch (ClassNotFoundException cnfe) {
			throw new IOException(cnfe);
		} finally {
			try {
				if (null != ois) {
					ois.close();
				}
			} catch (Exception e) {
				// donothing
			}
		}
	}

}
