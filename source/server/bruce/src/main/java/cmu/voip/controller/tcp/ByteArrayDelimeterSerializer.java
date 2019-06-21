package cmu.voip.controller.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.integration.ip.tcp.serializer.AbstractPooledBufferByteArraySerializer;
import org.springframework.integration.ip.tcp.serializer.SoftEndOfStreamException;

public class ByteArrayDelimeterSerializer extends AbstractPooledBufferByteArraySerializer {
	
	protected int maxMessageSize = 20480;
	
	private static final byte[] delimeter = "@@".getBytes();

	
	@Override
	public void serialize(byte[] bytes, OutputStream outputStream) throws IOException {
		// TODO Auto-generated method stub
		outputStream.write(bytes);
		outputStream.write(delimeter);
	}

	@Override
	protected byte[] doDeserialize(InputStream inputStream, byte[] buffer) throws IOException {
		// TODO Auto-generated method stub
		int n = this.fillToDelimeter(inputStream, buffer);
		return this.copyToSizedArray(buffer, n);
	}
	
	public int fillToDelimeter(InputStream inputStream, byte[] buffer) throws IOException {
		int n = 0;
		int bite;
		if (logger.isDebugEnabled()) {
			logger.debug("Available to read: " + inputStream.available());
		}
		try {
			int delimeterCount = 0;
			
			StringBuffer buff = new StringBuffer();
			
			while (true) {
				bite = inputStream.read();
				if (bite < 0 && n == 0) {
					throw new SoftEndOfStreamException("Stream closed between payloads");
				}
				checkClosure(bite);
				
				char ch = (char)bite;
				
				buff.append(ch);
				
				if(ch == '@')
					delimeterCount++;
				
				buffer[n++] = (byte) bite;
				
				if (n >= this.maxMessageSize) {
					throw new IOException("delimeter[@@] not found before max message length: " + this.maxMessageSize);
				}
				
				if (delimeterCount == 2) {
					break;
				}
			}
			
			logger.debug("----------> Input Tcp Data : " + buff.toString());
			
			return n - 1;
		}
		catch (SoftEndOfStreamException e) {
			throw e;
		}
		catch (IOException e) {
			publishEvent(e, buffer, n);
			throw e;
		}
		catch (RuntimeException e) {
			publishEvent(e, buffer, n);
			throw e;
		}
	}
}	
