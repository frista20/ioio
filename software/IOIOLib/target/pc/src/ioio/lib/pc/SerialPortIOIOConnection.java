/*
 * Copyright 2011 Ytai Ben-Tsvi. All rights reserved.
 *  
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 * 
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ARSHAN POURSOHI OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied.
 */
package ioio.lib.pc;

import ioio.lib.api.IOIOConnection;
import ioio.lib.api.exception.ConnectionLostException;

import java.io.InputStream;
import java.io.OutputStream;

import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;

public class SerialPortIOIOConnection implements IOIOConnection {
	// private static final String TAG = "SerialPortIOIOConnection";
	private final CommPortIdentifier identifier_;
	private SerialPort serialPort_;
	private InputStream inputStream_;
	private OutputStream outputStream_;

	public SerialPortIOIOConnection(CommPortIdentifier identifier) {
		identifier_ = identifier;
	}

	@Override
	public void waitForConnect() throws ConnectionLostException {
		try {
			synchronized (this) {
				CommPort commPort = identifier_.open(this.getClass().getName(),
						10000);
				serialPort_ = (SerialPort) commPort;
				serialPort_.enableReceiveThreshold(1);
				serialPort_.setDTR(true);
				Thread.sleep(100);
				inputStream_ = serialPort_.getInputStream();
				outputStream_ = serialPort_.getOutputStream();
			}
		} catch (Exception e) {
			if (serialPort_ != null) {
				serialPort_.close();
			}
			throw new ConnectionLostException(e);
		}
	}

	@Override
	synchronized public void disconnect() {
		if (serialPort_ != null) {
			serialPort_.close();
		}
	}

	@Override
	public InputStream getInputStream() throws ConnectionLostException {
		return inputStream_;
	}

	@Override
	public OutputStream getOutputStream() throws ConnectionLostException {
		return outputStream_;
	}

	@Override
	public boolean canClose() {
		return true;
	}
}